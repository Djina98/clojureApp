(ns project.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as response]
            [ring.middleware.session :as session]
            [project.db :as db]
            [project.pages :as pages]
            [project.admin :as admin]))

(defroutes app-routes
           (GET "/" [] (pages/index (db/get-producers)))

           (GET "/producers/:producer-id" [producer-id] (pages/producer (db/get-producer-by-id producer-id)))

           (GET "/admin/login" [:as {session :session}]
             (if (:admin session)
               (response/redirect "/")
               (pages/admin-login)))

           (POST "/admin/login" [username password]
             (if(admin/authorize-admin username password)
               (-> (response/redirect "/")
                   (assoc-in [:session :admin] true))
               (pages/admin-login "Pogrešna email adresa ili lozinka!")))

           (GET "/admin/logout" []
             (-> (response/redirect "/")
                 (assoc-in [:session :admin] false)))

           (route/not-found "Not Found"))

(defroutes admin-routes
           (GET "/producers/new" [] (pages/edit-producer nil))

           (POST "/producers" [name address contact description]
             (do (db/create-producer name address contact description)
                 (response/redirect "/")))

           (GET "/producers/:producer-id/edit" [producer-id] (pages/edit-producer (db/get-producer-by-id producer-id)))

           (POST "/producers/:producer-id" [producer-id name address contact description]
             (do (db/update-producer producer-id name address contact description)
                 (response/redirect (str "/producers/" producer-id))))

           (DELETE "/producers/:producer-id" [producer-id]
             (do (db/delete-producer producer-id)
                 (response/redirect "/"))))

(defn wrap-admin-only [handler]
  (fn [request]
    (if (-> request :session :admin)
      (handler request)
      (response/redirect "/admin/login"))))

(def app
  (-> (routes (wrap-routes admin-routes wrap-admin-only)
              app-routes)
      (wrap-defaults site-defaults)
      session/wrap-session))
