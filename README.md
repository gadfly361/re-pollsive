# re-pollsive

> "Everywhere I look, I see the repulsive sight of hundreds, thousands of revolting little children."
> - Grand High Witch, The Witches

re-pollsive is a library that handles polling events
for [re-frame](https://github.com/Day8/re-frame) applications.

**NOTE: NOT YET PUBLISHED TO CLOJARS** ... but it will be very soon.

```clojure
[re-pollsive "0.1.0"]
```

And in your ns:
```clojure
(ns your-ns
  (:require [re-pollsive.core :as poll]))
```
  
**Note**: For now, this library should be considered *alpha quality*,
as the api is still settling.

# The Problem

If you aren't careful, it is easy to add a bunch of `setInterval`s
scattered throughout your application.  When these `setInterval`s
collide, this can lead to unexpected and hard to debug behavior.

In addition, if you are using a hot-reloading tool (such
as [figwheel](https://github.com/bhauman/lein-figwheel)), you will
need to proactively defend against unintended `setInterval`s starting
on reload.

# Re-pollsive's solution

With re-pollsive, you only set up one `setInterval` when your
application starts, by dispatching `::poll/init`.  This will start a
counter that counts up *every second*.

Re-pollsive allows you to dynamically define polling rules by
dispatching `::poll/set-rules`.  An *interval* is defined for each
rule, and will be tracked against the aforementioned counter.

In addition, because you are anchoring off of only one `setInterval`,
you can freely use hot-reloading tools (such as figwheel) without any
problems.

# API

### `::poll/init`

`::poll/init` starts a counter for your polling rules to hang off of.

Note: This needs to be dispatched **only once**, when the application *first* loads.

```
(re-frame/dispatch-sync [::poll/init])
```

### `::poll/set-rules`

`::poll/set-rules` takes a hash-map of the following:

| key                       | type                  | default   | required? |
|---------------------------|-----------------------|-----------|-----------|
| :interval                 | int (seconds)         |           | **yes**   |
| :event                    | re-frame event        |           | **yes**   |
| :poll-when                | re-frame subscription |           | no        |
| :dispatch-event-on-start? | boolean               | false     | no        |

`:poll-when` is a re-frame subscription vector
(e.g. `[:should-i-be-polling?]`), and its value should be a boolean.
`:poll-when` can be used to effectively *start* and *stop* the poller.
If you do not supply `:poll-when`, then the poller will always run.

`:dispatch-event-on-start?` is a way to dispatch the event at time 0.
Say you have an interval of 30, by setting `:dispatch-event-on-start?`
to true, then the event will dispatch at 0 seconds, 30 seconds, 60
seconds, etc.  If `:dispatch-event-on-start?` is false, then the event
will dispatch at 30 seconds, 60 seconds, etc. (and not at time 0).

# Usage

Create a new re-frame application.

```
lein new re-frame foo
```

Add the following to the `:dependencies` vector of your *project.clj*
file.

```clojure
[re-pollsive "0.1.0"]
```

Then require re-pollisve in the core namespace, and add the
`::poll/init` event.

```clojure
(ns foo.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]

            ;; Add this (1 of 2)
            [re-pollisve.core :as poll]

            [foo.events :as events]
            [foo.views :as views]
            [foo.config :as config]
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

  ;; And this (2 of 2)
  (re-frame/dispatch-sync [::poll/init])

  (dev-setup)
  (mount-root))
```

Next, you will need to dispatch a `::poll/set-rules` event somewhere.
Personally, I like dispatching this in my routes file (because I may
want to handle polling events differently on each page).

# Non-goals

- *A perfectly accurate counter*. The internal counter is not intended
  to be perfectly accurate. For example, if you set an interval to
  `30`, expect the event to be dispatched roughly every 30 seconds,
  but not exactly. In other words, don't use re-pollsive for a clock.
- *Handling really quick events*.  The choice of having the `:interval`
  be measured in seconds as opposed to milliseconds was intentional.
  If you want to dispatch events with millisecond granularity, you are
  likely better off using a one-off setTimeout.

# Questions

If you have questions, I can usually be found hanging out in
the [clojurians](http://clojurians.net/) #reagent slack channel (my
handle is [@gadfly361](https://twitter.com/gadfly361)).

# License

Copyright © 2018 Matthew Jaoudi

Distributed under the The MIT License (MIT).
