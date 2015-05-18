(ns fusure.components.search
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [fusure.components.table :refer [tracks-table-view]]
            [fusure.services.vk :refer [audio-search]]
            [fusure.services.lastfm :refer [find-artist]]
            [fusure.services.musicbrainz :refer [search]]))

(defn enter? [event] (= 13 (.-keyCode event)))

(defn search-view [{:keys [tracks-chan artists-chan]} owner]
  (reify om/IRender
    (render [_]
      (html
        [:input.form-control {:type    "text"
                              :ref     "search"
                              :onKeyUp (fn [e]
                                         (when (enter? e)
                                           (let [query (-> (om/get-node owner "search")
                                                           .-value)]
                                             (find-artist artists-chan query)
                                             (audio-search tracks-chan query))))}]))))
