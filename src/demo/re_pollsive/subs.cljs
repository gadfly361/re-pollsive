(ns re-pollsive.subs
  (:require [re-frame.core :as rf]))


(rf/reg-sub
 ::name
 (fn [db]
   (:name db)))

(rf/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))


(rf/reg-sub
 ::poll?
 (fn [db _]
   (boolean (:poll? db))
   ))
