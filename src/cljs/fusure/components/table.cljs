(ns fusure.components.table
  (:require-macros [cljs.core.async.macros :refer (go)])
  (:require [om.core :as om :include-macros true]
            [fusure.components.play :refer [play-view]]
            [fusure.services.vk :refer [audio-search]]
            [sablono.core :refer-macros [html]]
            [fusure.state :refer [services-channel]]
            [cljs.core.async :refer [<! >!]]))

(defn table-track-view [row]
  (reify
    om/IRender
    (render [_]
      (let [[artist title url] row]
        (html [:tr
               [:td artist]
               [:td title]
               [:td {:style {:-webkit-user-select "none"}}
                (om/build play-view {:url url})]])))))

(defn tracks-table-view [chan owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (go (while true
            (let [table (<! chan)]
              (om/set-state! owner {:table table})))))
    om/IRenderState
    (render-state [_ {:keys [table]}]
      (html [:div
             [:table.table [:tbody
                            (for [row table]
                              (om/build table-track-view row))]]]))))

(defn table-artist-view [row]
  (reify
    om/IRender
    (render [_]
      (let [{:keys [name listeners mbid jw-distance]} row]
        (html [:tr
               [:td name]
               [:td listeners]
               [:td {:style {:font-size 7}} mbid]
               [:td jw-distance]])))))

(defn artists-table-view [{:keys [artists-chan tracks-chan]} owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (go (while true
            (let [table (<! artists-chan)]
              (do
                (audio-search tracks-chan (-> table first :name))
                (om/set-state! owner {:table table}))))))
    om/IRenderState
    (render-state [_ {:keys [table]}]
      (html [:div {:style {:font-size 9}}
             [:table.table [:tbody
                            (for [row table]
                              (om/build table-artist-view row))]]]))))
