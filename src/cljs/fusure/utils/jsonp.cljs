(ns fusure.utils.jsonp
  (:import [goog.net Jsonp]
           [goog Uri]))


(defn jsonp [uri & opts]
  (let [{:keys [on-success on-timeout content callback-name callback-value timeout-ms]} opts
        req        (goog.net.Jsonp. uri callback-name)
        data       (when content (clj->js content))
        on-success (when on-success #(on-success (js->clj % :keywordize-keys true)))
        on-timeout (when on-timeout #(on-timeout (js->clj % :keywordize-keys true)))]
    (when timeout-ms (.setRequestTimeout req timeout-ms))
    (.send req data on-success on-timeout callback-value)))

