(ns fusure.components.app
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]))

(defn app-view [_ _]
  (reify
    om/IRender
    (render [_]
      (html
        [:h1 "Hi."]))))
