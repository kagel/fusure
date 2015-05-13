(ns fusure.components.search
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [fusure.components.table :refer [table-view]]
            [fusure.services.vk :refer [audio-search]]))

(defn enter? [event] (= 13 (.-keyCode event)))

(defn search-view [chan owner]
  (reify om/IRender
    (render [_]
      (html
        [:input.form-control {:type    "text"
                 :ref     "search"
                 :onKeyUp (fn [e]
                            (when (enter? e)
                              (let [query (-> (om/get-node owner "search")
                                              .-value)]
                                (audio-search chan query))))}]))))
