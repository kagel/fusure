(ns fusure.components.table
  (:require-macros [cljs.core.async.macros :refer (go)])
  (:require [om.core :as om :include-macros true]
            [fusure.components.play :refer [play-view]]
            [sablono.core :refer-macros [html]]
            [fusure.state :refer [services-channel]]
            [cljs.core.async :refer [<! >!]]))

(defn table-row-view [row]
  (reify
    om/IRender
    (render [_]
      (let [[artist title url] row]
        (html [:tr
               [:td artist]
               [:td title]
               [:td {:style {:-webkit-user-select "none"}}
                (om/build play-view {:url url})]])))))

(defn table-view [chan owner]
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
                              (om/build table-row-view row))]]]))))
