(ns re-pollsive.impl
  (:require
   [clojure.set :as set]
   [re-frame.core :as rf]
   ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(def ns-root "re-pollsive.core/")



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Util

(defn ns-keyword
  ([]
   (ns-keyword nil))
  ([suffix]
   (keyword
    (str ns-root
         "polling"
         suffix))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Subs

(defn register-subs []
  (rf/reg-sub
   (ns-keyword "-counter")
   (fn [db _]
     (get-in db [(ns-keyword) :counter])))

  (rf/reg-sub
   (ns-keyword "-rules")
   (fn [db _]
     (get-in db [(ns-keyword) :rules])))
  )



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Events

(defn inc-counter!
  [{:keys [db]} _]
  {:db
   (-> db
       (update-in [(ns-keyword) :counter] inc))})

(defn rule-started-at!
  [{:keys [db]} [_ {:keys [started-at
                           stopped-time-since-last-poll
                           rule-index]}]]
  (let [rules         (get-in db [(ns-keyword) :rules])
        rules-updated (some-> rules
                              (assoc-in [rule-index :started-at] started-at)
                              (assoc-in [rule-index :stopped-time-since-last-poll] stopped-time-since-last-poll)
                              (assoc-in [rule-index :started-before?] true)
                              )]
    {:db
     (-> db
         (assoc-in [(ns-keyword) :rules] rules-updated))}))


(defn register-events []
  (rf/reg-event-fx
   (ns-keyword "-inc-counter")
   inc-counter!)

  (rf/reg-event-fx
   (ns-keyword "-rule-started-at")
   rule-started-at!)
  )



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Effects

(defn set-interval-handler []
  (let [counter        (or @(rf/subscribe [(ns-keyword "-counter")])
                           0) ;; default counter to 0
        rules          @(rf/subscribe [(ns-keyword "-rules")])]

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; DEBUG
    #_(js/console.log counter)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

    ;; 1. traverse rules
    (doseq [[i rule] (map-indexed vector rules)]
      (let [{:keys [;; defined by user
                    interval
                    event
                    dispatch-event-on-start?
                    poll-when
                    ;; populated by re-pollsive
                    started-at
                    started-before? ;; goes with dispatch-event-on-start?
                    stopped-time-since-last-poll]
             :or   {dispatch-event-on-start? false}
             } rule

            poll? (if poll-when
                    @(rf/subscribe poll-when)
                    true ;; default: poll immediately
                    )

            time-elapsed (- counter
                            started-at)

            started-at-effective (- counter
                                    stopped-time-since-last-poll)

            interval-rem (rem time-elapsed
                              interval)]


        ;; a) if polling, but don't have started-at, set started-at
        (when (and poll?
                   (not started-at))
          (rf/dispatch-sync [(ns-keyword "-rule-started-at")
                             {:started-at                   started-at-effective
                              :stopped-time-since-last-poll nil
                              :rule-index                   i}])

          ;; and also handle dispatching event on start
          (when (and dispatch-event-on-start?
                     (not started-before?))
            (rf/dispatch event)))


        ;; b) once started-at is set, see if you should dispatch the event
        (when started-at
          (cond
            ;; if not polling, erase started-at and track how much
            ;; time has elapsed
            (not poll?)
            (do
              (rf/dispatch-sync [(ns-keyword "-rule-started-at")
                                 {:started-at                   nil
                                  ;; using `dec` to restart where left off
                                  :stopped-time-since-last-poll (dec interval-rem)
                                  :rule-index                   i}])
              ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
              ;; DEBUG
              #_(rf/dispatch-sync [:re-pollsive.events/log
                                 (str "stopped after: " interval-rem " seconds")])
              ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
              )

            ;; dispatch event if interval is met
            (let [interval-met? (= 0
                                   interval-rem)]
              interval-met?)
            (rf/dispatch event)

            ;; otherwise do nothing
            :else

            nil
            ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            ;; DEBUG
            #_(rf/dispatch [:re-pollsive.events/log
                          (str "since last poll: " interval-rem)])
            ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
            ))))

    ;; 2. increment counter
    (rf/dispatch-sync [(ns-keyword "-inc-counter")])
    ))


(rf/reg-fx
 (ns-keyword "-init")

 (fn [opts]
   (register-subs)
   (register-events)

   (js/window.setInterval
    set-interval-handler
    1000 ;; 1 second
    )))
