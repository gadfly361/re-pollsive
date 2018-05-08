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
| :poll-when                | re-frame subscription |           | **yes**   |
| :dispatch-event-on-start? | boolean               | false     | **no**    |

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
