(ns re-pollsive.events
  (:require
   [re-frame.core :as rf]
   [re-pollsive.db :as db]
   [re-pollsive.effects :as effects]
   ))

(rf/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(rf/reg-event-db
 ::set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))


(rf/reg-event-fx
 ::alert
 (fn [_ [_ message]]
   {::effects/alert message}))

(rf/reg-event-fx
 ::log
 (fn [_ [_ message]]
   {::effects/log message}))



(rf/reg-event-db
 ::poll-start
 (fn [db _]
   (assoc db :poll? true)))

(rf/reg-event-db
 ::poll-stop
 (fn [db _]
   (assoc db :poll? false)))
