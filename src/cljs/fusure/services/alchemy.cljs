(ns fusure.services.alchemy
  (:require [fusure.utils.jsonp :refer [jsonp]]))

(def non-searchable "b233d332e207ba07975d8eec9c8cf77340c74f21")

(def entities-endpoint "http://access.alchemyapi.com/calls/text/TextGetRankedNamedEntities")
(def sentiment-endpoint "http://access.alchemyapi.com/calls/text/TextGetTextSentiment")

(defn callback [data]
  (.log js/console data))

(defn entities [text]
  (jsonp entities-endpoint
         :content {:apikey             non-searchable
                   :text               text
                   :outputMode         "json"
                   :structuredEntities "1"
                   :jsonp              "fusure.services.alchemy.callback"}
         :on-success (fn [data]
                       (.log js/console data))
         :on-error (fn [data]
                     (.log js/console data)))
  "")

(defn sentiments [text]
  (jsonp sentiment-endpoint
         :content {:apikey             non-searchable
                   :text               text
                   :outputMode         "json"
                   :structuredEntities "1"
                   :jsonp              "fusure.services.alchemy.callback"}
         :on-success (fn [data]
                       (.log js/console data))
         :on-error (fn [data]
                     (.log js/console data)))
  "")
