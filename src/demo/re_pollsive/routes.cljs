(ns re-pollsive.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [secretary.core :as secretary]
            [goog.events :as gevents]
            [goog.history.EventType :as EventType]
            [re-frame.core :as rf]
            [re-pollsive.events :as events]
            [re-pollsive.subs :as subs]
            [re-pollsive.core :as poll]
            ))


(defn hook-browser-navigation! []
  (doto (History.)
    (gevents/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here
  (defroute "/" []
    (rf/dispatch [::events/set-active-panel :home-panel])

    (rf/dispatch [::events/poll-stop])
    (rf/dispatch [::poll/set-rules
                  {:rules [{:interval                 4
                            :event                    [::events/log "POLL (every 4)"]
                            :poll-when                [::subs/poll?]
                            :dispatch-event-on-start? true}

                           {:interval                 6
                            :event                    [::events/log "POLL (every 6)"]
                            :poll-when                [::subs/poll?]
                            :dispatch-event-on-start? true}
                           ]}]))

  (defroute "/about" []
    (rf/dispatch [::events/set-active-panel :about-panel]))


  ;; --------------------
  (hook-browser-navigation!))
