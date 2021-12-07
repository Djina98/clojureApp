(ns project.web
 (:require
  [ring.adapter.jetty :as jetty]
  [compojure.handler :as handler]
  [project.handler :as project])
  (:gen-class))

(defn -main [& args]
  (let [port (Integer. (or (System/getenv "PROJECT_PORT")
                           3000))]
    (jetty/run-jetty (handler/site #'project/app)
                     {:port port
                      :join? false})))
