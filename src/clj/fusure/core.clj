(ns fusure.core
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [ring.util.response :refer [resource-response]]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]])
  (:gen-class))

(defroutes app
           (GET "/" [] (resource-response "index.html" {:root "public"}))
           (route/resources "/")
           (route/not-found "No."))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

(comment
  (def server (-main)))