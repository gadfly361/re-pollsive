(ns re-pollsive.views
  (:require
   [re-frame.core :as rf]
   [re-pollsive.subs :as subs]
   [re-pollsive.events :as events]
   [re-pollsive.core :as rp]

   ))



;; home

(defn home-panel []
  (let [name    @(rf/subscribe [::subs/name])
        poll? @(rf/subscribe [::subs/poll?])
        ]
    [:div (str "Hello from " name ". This is the Home Page.")
     [:div
      [:button
       {:style    {:background-color (if poll? "red" "green")
                   :text-align       "center"
                   :width            "80px"
                   :padding          "8px"
                   :margin           "8px"}
        :on-click #(if poll?
                     (rf/dispatch [::events/poll-stop])
                     (rf/dispatch [::events/poll-start]))
        }
       (if poll? "stop poll" "start poll")]
      ]


     [:div [:a {:href "#/about"} "go to About Page"]]]))


;; about

(defn about-panel []
  [:div "This is the About Page."
   [:div [:a {:href "#/"} "go to Home Page"]]])


;; main

(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :about-panel [about-panel]
    [:div]))


(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (rf/subscribe [::subs/active-panel])]
    [show-panel @active-panel]))
