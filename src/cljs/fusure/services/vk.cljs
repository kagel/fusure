(ns fusure.services.vk
  (:require [cljsjs.vk]
            [fusure.utils.jsonp :refer [jsonp]]))

(def api-id "4667976")
(def redirect-url "https://oauth.vk.com/blank.html")
(def api-version 5.27)

(defn get-access-token []
  "")
(defn get-user-id []
  "")

(defn vk-init []
  (.init js/VK (js-obj
                 "apiId" api-id)))

(defn vk-call [method params callback]
  (.call (.-Api js/VK) method params callback))

(defn vk-oauth-url [] (str "https://oauth.vk.com/authorize?"
                           "client_id=" api-id "&"
                           "scope=8&"
                           "redirect_uri=" redirect-url "&"
                           "display=page&"
                           "v=" api-version "&"
                           "response_type=token"))

(defn url-encode [s]
  s)

(defn make-query-string [m]
  (->> (for [[k v] m]
         (str (url-encode k) "=" (url-encode v)))
       (interpose "&")
       (apply str)))

(defn vk-method [method params]
  (str "https://api.vk.com/method/" method
       "?uid=" (get-user-id) "&"
       (make-query-string params)
       "&access_token=" (get-access-token)))

(defn callback [x]
  (.log js/console x))

(defn call [method params]
  (jsonp (vk-method method params)
         :content {:callback "fusure.services.vk.callback"}
         :on-success (fn [data]
                       (.log js/console (clj->js data)))
         :on-error (fn [data]
                     (.log js/console (clj->js data)))))

(defn audio-search [query]
  (call "audio.search" {"q" query}))
