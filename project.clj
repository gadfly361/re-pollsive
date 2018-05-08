(defproject re-pollsive "0.1.0"
  :description "A polling events library for re-frame"
  :url "https://github.com/gadfly361/re-pollsive"
  :license {:name "MIT"}
  :scm {:name "git"
        :url  "https://github.com/gadfly361/re-pollsive"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.908"]
                 [reagent "0.7.0"]
                 [re-frame "0.10.5"]]

  :plugins [[lein-cljsbuild "1.1.5"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/main"]

  :clean-targets ^{:protect false} ["dev-resources/public/js/compiled" "target"]

  :profiles
  {:dev
   {:dependencies [[secretary "1.2.3"]
                   [binaryage/devtools "0.9.4"]
                   [day8.re-frame/re-frame-10x "0.3.2"]
                   [re-frisk "0.5.3"]]
    :plugins      [[lein-figwheel "0.5.13"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/demo" "src/main"]
     :figwheel     {:on-jsload "re-pollsive.core-demo/mount-root"}
     :compiler     {:main                 re-pollsive.core-demo
                    :output-to            "dev-resources/public/js/compiled/app.js"
                    :output-dir           "dev-resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload
                                           day8.re-frame-10x.preload
                                           re-frisk.preload]
                    :closure-defines      {"re_frame.trace.trace_enabled_QMARK_" true}
                    :external-config      {:devtools/config {:features-to-install :all}}}}

     {:id           "min"
      :source-paths ["src/demo" "src/main"]
      :compiler     {:main            re-pollsive.core-demo
                     :output-to       "dev-resources/public/js/compiled/app.js"
                     :optimizations   :advanced
                     :closure-defines {goog.DEBUG false}
                     :pretty-print    false}}
     ]}
   )
