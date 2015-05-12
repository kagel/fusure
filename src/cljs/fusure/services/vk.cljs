(ns fusure.services.vk
  (:require-macros [cljs.core.async.macros :refer (go)])
  (:require [cljsjs.vk]
            [fusure.utils.jsonp :refer [jsonp]]
            [cljs.core.async :refer [<! >!]]
            [fusure.state :refer [services-channel]]))

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
  (.log js/console "callback " x))

(defn call [method params on-success]
  (.log js/console (vk-method method params))
  (jsonp (vk-method method params)
         :content {:callback "fusure.services.vk.callback"}
         :on-success on-success
         :on-error (fn [data]
                     (.log js/console (clj->js data)))))

(defn gen-table [response]
  (let [data (:response (js->clj response))]
    (mapv (fn [{:keys [artist title]}]
            [artist title])
          data)))

(defn audio-search [app query]
  (call "audio.search"
        {"q" query "performer_only" 1}
        (fn [data]
          (go (>! (services-channel app) (gen-table data)))
          (.log js/console (clj->js data))))
  "")
