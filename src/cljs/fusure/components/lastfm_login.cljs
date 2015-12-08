(ns fusure.components.lastfm-login
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [fusure.services.lastfm :refer [login]]
            [fusure.utils.common :refer [enter?]]))

(defn lastfm-login-view [{:keys [artists-chan]} owner]
  (reify om/IRender
    (render [_]
      (html
        (let [login-fn (fn []
                         (let [user (-> (om/get-node owner "user") .-value)
                               password (-> (om/get-node owner "password") .-value)]
                           (login user password)))]
          [:div
           [:input.form-control {:type "text"
                                 :ref  "user"}]
           [:input.form-control {:type    "password"
                                 :ref     "password"
                                 :onKeyUp (fn [e]
                                            (when (enter? e)
                                              (login-fn)))}]
           [:button
            {:onClick login-fn}]])))))
