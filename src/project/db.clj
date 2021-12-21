(ns project.db
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer [$set]])
  (:import
    [org.bson.types ObjectId]))

(def db-connection-uri (or (System/getenv "PROJECT_MONGO_URI")
                           "mongodb://127.0.0.1/project"))
(def db (-> db-connection-uri
            mg/connect-via-uri
            :db))

(def producers-collection "producers")
(def certified-collection "certified")
(def products-collection "products")

(defn create-producer [name address contact description certified_id]
  (mc/insert db producers-collection
             {:name name
              :address address
              :contact contact
              :description description
              :certified_id certified_id}))

(defn update-producer [producer-id name address contact description certified_id]
  (mc/update-by-id db producers-collection (ObjectId. producer-id)
             {$set
                {:name name
                 :address address
                 :contact contact
                 :description description
                 :certified_id certified_id}}))

(defn delete-producer [producer-id]
  (mc/remove-by-id db producers-collection (ObjectId. producer-id)))

(defn get-producers []
  (mc/find-maps db producers-collection))

(defn get-producer-by-id [producer-id]
  (mc/find-map-by-id db producers-collection (ObjectId. producer-id)))

(defn get-certified-by-value [value]
  (mc/find-maps db certified-collection  {:value value}))

(defn create-product [name description amount price packaging type producer_id]
  (mc/insert db products-collection
             {:name name
              :description description
              :amount amount
              :price price
              :packaging packaging
              :type type
              :producer_id producer_id}))

(defn update-product [product-id name description amount price packaging type producer_id]
  (mc/update-by-id db products-collection (ObjectId. product-id)
                   {$set
                    {:name name
                     :description description
                     :amount amount
                     :price price
                     :packaging packaging
                     :type type
                     :producer_id producer_id}}))

(defn delete-product [product-id]
  (mc/remove-by-id db products-collection (ObjectId. product-id)))

(defn get-products []
  (mc/find-maps db products-collection))

(defn get-product-by-id [product-id]
  (mc/find-map-by-id db products-collection (ObjectId. product-id)))

