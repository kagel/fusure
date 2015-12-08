(ns fusure.utils.common)

(defn enter? [event] (= 13 (.-keyCode event)))
