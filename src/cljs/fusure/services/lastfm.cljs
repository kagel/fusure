(ns fusure.services.lastfm
  (:require-macros [cljs.core.async.macros :refer (go)])
  (:require [cljsjs.lastfm.api]
            [cljsjs.lastfm.cache]
            [fusure.utils.jaro-winkler :refer [jaro-winkler]]
            [cljs.core.async :refer [<! >!]]))

(def lfm-app-id "76f94baf960ebd9932d609827595e9bc")
(def lfm-secure-key "caf8c1566e3e08e3a85f340ad90c0202")
(def lfm-redirect-uri "")

(def lastfm-cache (js/LastFMCache.))

(def lastfm
  (js/LastFM. (js-obj
                "apiKey" lfm-app-id
                "apiSecret" lfm-secure-key
                "cache" lastfm-cache)))

(defn get-artist [name]
  (.. lastfm -artist (getInfo (js-obj "artist" name)
                              (js-obj "success" (fn [data]
                                                  (.log js/console data))
                                      "error" (fn [code message]
                                                (.log js/console code message))))))

(defn gen-table [response query]
  (let [data     (-> (js->clj response :keywordize-keys true)
                     (get-in [:results :artistmatches :artist]))
        query-lc (.toLowerCase query)]
    (sort-by first (fn [left right]
                     (let [left-lc  (.toLowerCase left)
                           right-lc (.toLowerCase right)]
                       (> (jaro-winkler left-lc query-lc)
                          (jaro-winkler right-lc query-lc))))
             (mapv (fn [{:keys [name listeners mbid]}]
                     [name listeners mbid (jaro-winkler (.toLowerCase name) query-lc)])
                   data))))

(defn find-artist [chan name]
  (go
    (.. lastfm -artist (search (js-obj "artist" name)
                               (js-obj "success" (fn [data]
                                                   (go (>! chan (gen-table data name)))
                                                   (.log js/console data))
                                       "error" (fn [code message]
                                                 (.log js/console code message)))))))