(ns project.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as response]
            [project.db :as db]
            [project.pages :as pages]))

(defroutes app-routes
  (GET "/" [] (pages/index (db/get-producers)))
           (GET "/producers/new" [] (pages/edit-producer nil))
           (POST "/producers" [name address contact description]
             (do (db/create-producer name address contact description)
                 (response/redirect "/")))

           (GET "/producers/:producer-id/edit" [producer-id] (pages/edit-producer (db/get-producer-by-id producer-id)))
           (POST "/producers/:producer-id" [producer-id name address contact description]
             (do (db/update-producer producer-id name address contact description)
                 (response/redirect (str "/producers/" producer-id))))

           (GET "/producers/:producer-id" [producer-id] (pages/producer (db/get-producer-by-id producer-id)))
           (route/not-found "Not Found"))


(def app
  (wrap-defaults app-routes site-defaults))
