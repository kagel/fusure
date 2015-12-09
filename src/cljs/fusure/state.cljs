(ns fusure.state
  (:require [cljs.core.async :refer [chan pub sub]]))

(defonce app-state
         (atom {:navigation    nil
                :channels      {:pub-sub          (chan)
                                :services-channel (chan)}
                :config        nil
                :table         nil
                :services      {:lastfm {:session nil}}
                :last-scrobble nil}))

(defn state [] @app-state)

(def services-channel-path [:channels :services-channel])
(def lastfm-session-path [:services :lastfm :session])
(def last-scrobble-path [:last-scrobble])
(def pub-sub-path [:channels :pub-sub])

(defn services-channel [app] (get-in app services-channel-path))
(defn pub-sub-publisher [app] (get-in app pub-sub-path))

(defn lastfm-session [] (get-in (state) lastfm-session-path))
(defn last-scrobble [] (get-in (state) last-scrobble-path))
