(ns fusure.services.musicbrainz
  (:require-macros [cljs.core.async.macros :refer (go)])
  (:require [cljsjs.musicbrainz]
            [cljs.core.async :refer [<! >!]]))

(def mbz js/MBz)

(defn search [chan resource query]
  (.search mbz (js-obj "resource" resource
                       "query" query
                       "results" (fn [data]
                                   (go (>! chan data))
                                   (.log js/console data)))))