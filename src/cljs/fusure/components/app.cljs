(ns fusure.components.app
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [fusure.services.lastfm :refer [get-artist]]
            [fusure.services.musicbrainz :refer [search]]))

(defn app-view [_ _]
  (reify
    om/IRender
    (render [_]
      (html
        [:div
         (get-artist "The Ex")
         (search "release" "Wings of Lead Over Dormant Seas")]))))
