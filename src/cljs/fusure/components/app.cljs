(ns fusure.components.app
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [fusure.services.lastfm :refer [get-artist]]))

(defn app-view [_ _]
  (reify
    om/IRender
    (render [_]
      (html
        (get-artist "The Ex")))))
