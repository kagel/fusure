(ns fusure.services.lastfm
  (:require [cljsjs.lastfm.api]
            [cljsjs.lastfm.cache]))

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