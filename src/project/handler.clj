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

           ;Routes for producers
           (GET "/" [] (pages/producers (db/get-producers)))
           (GET "/producers/certified/:certified-id" [certified-id] (pages/producers (db/get-producers-by-certified certified-id)))
           (GET "/producers/keyword/:keyword" [keyword] (pages/producers (db/search-producers keyword)))
           (GET "/producers/:producer-id" [producer-id] (pages/producer (db/get-producer-by-id producer-id) (db/get-reviews-for-producer producer-id)))

           ;Routes for producer-reviews
           (GET "/producer-reviews" [] (pages/producer-reviews (db/get-all-producer-reviews)))
           (GET "/producer-reviews/keyword/:keyword" [keyword] (pages/producer-reviews (db/search-producer-reviews keyword)))
           (GET "/producer-reviews/:producer-id/new" [producer-id] (pages/add-producer-review (db/get-producer-by-id producer-id)))
           (GET "/producer-reviews/rating/:rating" [rating] (pages/producer-reviews (db/get-producer-reviews-by-rating rating)))
           (POST "/producer-reviews" [producer-id review rating]
             (do (db/create-producer-review producer-id review rating)
                 (response/redirect (str "/producers/" producer-id))))

           ;Routes for product-reviews
           (GET "/product-reviews" [] (pages/product-reviews (db/get-all-product-reviews)))
           (GET "/product-reviews/keyword/:keyword" [keyword] (pages/product-reviews (db/search-product-reviews keyword)))
           (GET "/product-reviews/:product-id/new" [product-id] (pages/add-product-review (db/get-product-by-id product-id)))
           (GET "/product-reviews/rating/:rating" [rating] (pages/product-reviews (db/get-product-reviews-by-rating rating)))
           (POST "/product-reviews" [product-id review rating]
             (do (db/create-product-review product-id review rating)
                 (response/redirect (str "/products/" product-id))))

           ;Routes for products
           (GET "/products" [] (pages/products (db/get-products)))
           (GET "/products/packaging/:packaging-id" [packaging-id] (pages/products (db/get-products-by-packaging packaging-id)))
           (GET "/products/producer/:producer-id" [producer-id] (pages/products (db/get-products-by-producer producer-id)))
           (GET "/products/keyword/:keyword" [keyword] (pages/products (db/search-products keyword)))
           (GET "/products/:product-id" [product-id] (pages/product (db/get-product-by-id product-id) (db/get-reviews-for-product product-id)))

           ;Routes for admin login and logout
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

           ;Undefined route
           (route/not-found "Not Found"))

(defroutes admin-routes
           ;Routes for new producer
           (GET "/producers/new" [] (pages/edit-producer nil))
           (POST "/producers" [name address contact description certified_id]
             (do (db/create-producer name address contact description certified_id)
                 (response/redirect "/")))

           ;Routes for edit producer
           (GET "/producers/:producer-id/edit" [producer-id] (pages/edit-producer (db/get-producer-by-id producer-id)))
           (POST "/producers/:producer-id" [producer-id name address contact description certified_id]
             (do (db/update-producer producer-id name address contact description certified_id)
                 (response/redirect (str "/producers/" producer-id))))

           ;Route for delete producer
           (DELETE "/producers/:producer-id" [producer-id]
             (do (db/delete-producer producer-id)
                 (response/redirect "/")))

           ;Routes for new product
           (GET "/products/new" [] (pages/edit-product nil))
           (POST "/products" [name description amount price type producer_id packaging_id]
             (do (db/create-product name description amount price type producer_id  packaging_id)
                 (response/redirect "/products")))

           ;Routes for edit product
           (GET "/products/:product-id/edit" [product-id] (pages/edit-product (db/get-product-by-id product-id)))
           (POST "/products/:product-id" [product-id name description amount price type producer_id packaging_id]
             (do (db/update-product product-id name description amount price type producer_id packaging_id)
                 (response/redirect (str "/products/" product-id))))

           ;Route for delete product
           (DELETE "/products/:product-id" [product-id]
             (do (db/delete-product product-id)
                 (response/redirect "/products"))))

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

