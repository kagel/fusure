(ns fusure.services.alchemy
  (:import [goog.net Jsonp]
           [goog Uri]))

(def non-searchable "b233d332e207ba07975d8eec9c8cf77340c74f21")

(def entities-endpoint "http://access.alchemyapi.com/calls/text/TextGetRankedNamedEntities")
(def sentiment-endpoint "http://access.alchemyapi.com/calls/text/TextGetTextSentiment")

(defn jsonp [uri & opts]
  (let [{:keys [on-success on-timeout content callback-name callback-value timeout-ms]} opts
        req        (goog.net.Jsonp. uri callback-name)
        data       (when content (clj->js content))
        on-success (when on-success #(on-success (js->clj % :keywordize-keys true)))
        on-timeout (when on-timeout #(on-timeout (js->clj % :keywordize-keys true)))]
    (when timeout-ms (.setRequestTimeout req timeout-ms))
    (.log js/console (str "content: " data))
    (.send req data on-success on-timeout callback-value)))

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
