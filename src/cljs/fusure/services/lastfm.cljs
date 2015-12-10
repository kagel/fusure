(ns fusure.services.lastfm
  (:require-macros [cljs.core.async.macros :refer (go)])
  (:require [cljsjs.lastfm.api]
            [cljsjs.lastfm.cache]
            [fusure.utils.jaro-winkler :refer [jaro-winkler]]
            [cljs.core.async :refer [<! >!]]
            [fusure.state :as state]
            [cljs-time.core :refer [now]]))

(def lfm-app-id "76f94baf960ebd9932d609827595e9bc")
(def lfm-secure-key "caf8c1566e3e08e3a85f340ad90c0202")

(def lastfm-cache (js/LastFMCache.))

(def lastfm
  (js/LastFM. (js-obj
                "apiKey" lfm-app-id
                "apiSecret" lfm-secure-key
                "cache" lastfm-cache)))

(defn login [user password]
  (go
    (.. lastfm -auth (getMobileSession
                       (js-obj "username" user "password" password)
                       (js-obj "success" (fn [data]
                                           (when-let [session (.-key (.-session data))]
                                             (.log js/console (str "logged in, session is: " session))
                                             (swap! state/app-state assoc-in state/lastfm-session-path session)
                                             (.log js/console (str "logged in, session is: " (state/lastfm-session)))))
                               "error" (fn [code message]
                                         (.log js/console code message)))))))

(defn get-artist [name]
  (.. lastfm -artist (getInfo (js-obj "artist" name)
                              (js-obj "success" (fn [data]
                                                  (.log js/console data))
                                      "error" (fn [code message]
                                                (.log js/console code message))))))

(defn gen-table [response query]
  (let [data (-> (js->clj response :keywordize-keys true)
                 (get-in [:results :artistmatches :artist]))
        query-lc (.toLowerCase query)]
    (sort-by :name (fn [left right]
                     (let [left-lc (.toLowerCase left)
                           right-lc (.toLowerCase right)]
                       (> (jaro-winkler left-lc query-lc)
                          (jaro-winkler right-lc query-lc))))
             (->> (mapv (fn [{:keys [name listeners mbid]}]
                          {:name        name
                           :listeners   listeners
                           :mbid        mbid
                           :jw-distance (jaro-winkler (.toLowerCase name) query-lc)})
                        data)
                  (filter (fn [x] (> (:jw-distance x) 0.7)))))))

(defn find-artist [chan name]
  (go
    (.. lastfm -artist (search (js-obj "artist" name)
                               (js-obj "success" (fn [data]
                                                   (go (>! chan (gen-table data name)))
                                                   (.log js/console data))
                                       "error" (fn [code message]
                                                 (.log js/console code message)))))))


(defn submit-scrobble [url artist album track duration]
  (go
    (.. lastfm -track (scrobble (js-obj "artist" artist
                                        "album" album
                                        "track" track
                                        "timestamp" (/ (.getTime (js/Date.)) 1000)
                                        "duration" duration)
                                (js-obj "key" (state/lastfm-session))
                                (js-obj "success" (fn [data]
                                                    (.log js/console (str "Scrobble result: " data))
                                                    (swap! state/app-state assoc-in state/last-scrobble-path url)
                                                    (swap! state/app-state update-in state/scrobbles-path conj url))
                                        "error" (fn [code message]
                                                  (.log js/console code message)))))))