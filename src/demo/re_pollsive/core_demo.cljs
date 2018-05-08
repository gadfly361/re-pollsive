(ns re-pollsive.core_demo
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [re-pollsive.effects :as effects]
            [re-pollsive.events :as events]
            [re-pollsive.routes :as routes]
            [re-pollsive.views :as views]
            [re-pollsive.config :as config]
            [re-pollsive.core :as poll]
            ))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::poll/init])
  (routes/app-routes)
  (dev-setup)
  (mount-root))
