(ns fusure.core
  (:require [cljsjs.jquery]
            [cljsjs.bootstrap]
            [goog.dom :as gdom]
            [om.core :as om :include-macros true]
            [fusure.state :as state]
            [fusure.components.app :refer [app-view]]))

(defn main []
  (om/root app-view state/app-state {:target (gdom/getElement "app")}))
