(ns fusure.components.vk-login
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [fusure.services.vk :refer [vk-login]]))

(defn vk-login-view []
  (reify
    om/IRender
    (render [_]
      (html [:button
             {:onClick (fn [_]
                         (vk-login))}]))))
