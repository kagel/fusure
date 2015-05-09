(ns fusure.components.table
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [cljsjs.fixed-data-table]
           ;[cljsjs.react-table]
            ))

(def Table (js/React.createFactory js/FixedDataTable.Table))
(def Column (js/React.createFactory js/FixedDataTable.Column))

(defn getter [k row] (get row k))

(defn table-view [{:keys [table]}]
  (reify om/IRender
    (render [_]
      (html [:div (Table
                    #js {:width        600
                         :height       400
                         :rowHeight    30
                         :rowGetter    #(get table %)
                         :rowsCount    (count table)
                         :headerHeight 30}
                    (Column
                      #js {:label "Number" :dataKey 0 :cellDataGetter getter :width 100})
                    (Column
                      #js {:label "Amount" :dataKey 1 :cellDataGetter getter :width 100})
                    (Column
                      #js {:label "Coeff" :dataKey 2 :cellDataGetter getter :width 200})
                    (Column
                      #js {:label "Store" :dataKey 3 :cellDataGetter getter :width 200}))]))))
