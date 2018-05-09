(ns re-pollsive.core
  (:require
   [re-frame.core :as rf]
   [re-pollsive.impl]
   ))



(rf/reg-event-fx
 ::init
 (fn [_ _]
   {::polling-init nil}))


(rf/reg-event-fx
 ::set-rules
 (fn [{:keys [db]}
      [_ rules]]
   {:db (-> db
            (assoc-in [::polling :rules] rules))}))
