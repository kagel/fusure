(ns fusure.components.play
  (:require-macros [cljs.core.async.macros :refer (go)])
  (:require [om.core :as om]
            [cljs.core.async :refer [<! >! chan]]
            [sablono.core :refer-macros [html]]))

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
(defn toggle-playback [owner]
  (let [playback (om/get-state owner :playback)
        toggle   (get-in playback-controls [playback :toggle])]
    (set-playback-status owner toggle)))

(defn play-view [{:keys [url]} owner]
  (reify
    om/IInitState
    (init-state [_]
      {:chan     (chan)
       :playback :stopped})
    om/IWillMount
    (will-mount [_]
      (let [chan (om/get-state owner :chan)]
        (go (while true
              (let [msg (<! chan)]
                (when (= msg :toggle-playback)
                  (toggle-playback owner)))))))
    om/IWillReceiveProps
    (will-receive-props [_ {:keys [url]}]
      (let [new-url (:url (om/get-props owner))]
        (when (not= new-url url)
          (set-playback-status owner stop))))
    om/IRenderState
    (render-state [_ {:keys [chan playback]}]
      (html [:div
             {:class   (get-in playback-controls [playback :class])
              :onClick (fn [_]
                         (go (>! chan :toggle-playback)))}
             [:audio {:preload "none" :ref "audio"}
              [:source {:type "audio/mpeg" :src url}]]]))))
