(ns fusure.components.table
  (:require-macros [cljs.core.async.macros :refer (go)])
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [fusure.state :refer [services-channel]]
            [cljs.core.async :refer [<! >!]]
            [cljsjs.fixed-data-table]))

(def Table (js/React.createFactory js/FixedDataTable.Table))
(def Column (js/React.createFactory js/FixedDataTable.Column))

(defn getter [k row] (get row k))

(defn table-view [app owner _]
  (reify
    om/IWillMount
    (will-mount [_]
      (go (let [table (<! (services-channel app))]
            (om/set-state! owner {:table table}))))
    om/IRenderState
    (render-state [_ {:keys [table]}]
      (html [:div (Table
                    #js {:width        600
                         :height       400
                         :rowHeight    30
                         :rowGetter    #(get table %)
                         :rowsCount    (count table)
                         :headerHeight 30}
                    (Column
                      #js {:label "Artist" :dataKey 0 :cellDataGetter getter :width 200})
                    (Column
                      #js {:label "Title" :dataKey 1 :cellDataGetter getter :width 400}))]))))
