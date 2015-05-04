(ns fusure.services.musicbrainz
  (:require [cljsjs.musicbrainz]))

(def mbz js/MBz)

(defn search [resource query]
  (.search mbz (js-obj "resource" resource
                       "query" query
                       "results" (fn [data]
                                   (.log js/console data)))))