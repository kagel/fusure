(ns fusure.services.vk
  (:require-macros [cljs.core.async.macros :refer (go)])
  (:require [cljsjs.vk]
            [cljs.core.async :refer [<! >!]]
            [fusure.state :refer [services-channel]]))

(def api-id "4667976")

(defn vk-init []
  (.init js/VK (js-obj
                 "apiId" api-id)))

(defn vk-login []
  (.. js/VK -Auth (login (fn [response]
                           (when (.-session response)
                             (.log js/console "logged in")))
                         8)))

(defn vk-call [method params success error]
  (.. js/VK -Api (call method (clj->js params) success error)))

(defn gen-table [response] p
  (let [data (:response (js->clj response :keywordize-keys true))]
    (mapv (fn [{:keys [artist title url]}]
            [artist title url])
          (drop 1 data))))

(defn audio-search [chan query]
  (vk-call "audio.search"
           {"q" query "performer_only" 1}
           (fn [data]
             (go (>! chan (gen-table data)))
             (.log js/console (clj->js data)))
           (fn [error]
             (.log js/console error))))

