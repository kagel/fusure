(ns fusure.components.play
  (:require-macros [cljs.core.async.macros :refer (go)])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [fusure.services.lastfm :refer [submit-scrobble]]
            [fusure.state :as state]))

(defn start [audio]
  (.load audio)
  (.play audio)
  :started)

(defn play [audio]
  (.play audio)
  :started)

(defn pause [audio]
  (.pause audio)
  :paused)

(defn stop [audio]
  (.pause audio)
  :stopped)

(def playback-controls
  {:started {:toggle pause :class "fa fa-pause"}
   :paused  {:toggle play :class "fa fa-play-circle"}
   :stopped {:toggle start :class "fa fa-play-circle"}})

(defn set-playback-status [owner status]
  (om/set-state! owner :playback (->> (om/get-node owner "audio")
                                      (status))))

(defn toggle-playback [owner url artist title]
  (let [playback (om/get-state owner :playback)
        toggle (get-in playback-controls [playback :toggle])]
    (set-playback-status owner toggle)
    (let [last-scrobble (state/last-scrobble)]
      (if (or (nil? last-scrobble) (not= last-scrobble url))
        (submit-scrobble url artist "" title 0)))))

(defn play-view [{:keys [url artist title]} owner]
  (reify
    om/IInitState
    (init-state [_]
      {:playback :stopped})
    om/IWillReceiveProps
    (will-receive-props [_ {:keys [url]}]
      (let [new-url (:url (om/get-props owner))]
        (when (not= new-url url)
          (set-playback-status owner stop))))
    om/IRenderState
    (render-state [_ {:keys [playback]}]
      (html [:button
             {:class   (get-in playback-controls [playback :class])
              :style   {:font-size 36}
              :onClick (fn [_]
                         (go
                           (toggle-playback owner url artist title)))}
             [:audio {:preload "none" :ref "audio"}
              [:source {:type "audio/mpeg" :src url}]]]))))
