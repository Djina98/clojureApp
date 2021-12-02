(ns project.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [project.db :as db]
            [project.pages :as pages]))

(defroutes app-routes
  (GET "/" [] (pages/index (db/get-producers)))
           (GET "/producers/:producer-id" [producer-id] (pages/producer (db/get-producer-by-id producer-id)))
           (route/not-found "Not Found"))


(def app
  (wrap-defaults app-routes site-defaults))
