(ns fusure.state
  (:require [cljs.core.async :refer [chan pub sub]]))

(defn gen-table
  [size]
  (mapv (fn [i] [i
                 (rand-int 1000)
                 (rand)
                 (rand-nth ["Here" "There" "Nowhere" "Somewhere"])])
        (range 1 (inc size))))

(defonce app-state
  (atom {:navigation nil
         :channels   {:pub-sub          (chan)
                      :services-channel (chan)}
         :config     nil
         :table      (gen-table 10)}))

(defn state [] @app-state)

(def services-channel-path [:channels :services-channel])
(def pub-sub-path [:channels :pub-sub])

(defn services-channel [app] (get-in app services-channel-path))
(defn pub-sub-publisher [app] (get-in app pub-sub-path))

