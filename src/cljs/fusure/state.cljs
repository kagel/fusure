(ns fusure.state
  (:require [cljs.core.async :refer [chan pub sub]]))

(defonce app-state
         (atom {:navigation nil
                :channels   {:pub-sub          (chan)
                             :services-channel (chan)}
                :config     nil
                :table      nil
                :services   {:lastfm {:session nil}}}))

(defn state [] @app-state)

(def services-channel-path [:channels :services-channel])
(def lastfm-session-path [:services :lastfm :session])
(def pub-sub-path [:channels :pub-sub])

(defn services-channel [app] (get-in app services-channel-path))
(defn pub-sub-publisher [app] (get-in app pub-sub-path))

(defn lastfm-session [app] (get-in app lastfm-session-path))