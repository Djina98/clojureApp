(ns project.db
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer [$set]])
  (:import
    [org.bson.types ObjectId]))

(defmacro ^{:private true} defoperator
  [operator]
  `(def ^{:const true} ~(symbol (str operator)) ~(str operator)))

(defoperator $regex)
(defoperator $toLower)
(defoperator $or)
(defoperator $sort)

;Connection parametars
(def db-connection-uri (or (System/getenv "PROJECT_MONGO_URI")
                           "mongodb://127.0.0.1/project"))
(def db (-> db-connection-uri
            mg/connect-via-uri
            :db))

;Collections
(def certified-collection "certified")
(def packaging-collection "packaging")
(def producers-collection "producers")
(def products-collection "products")
(def producer-reviews-collection "producer_reviews")
(def product-reviews-collection "product_reviews")

;Certified collection
(defn get-certified []
  (mc/find-maps db certified-collection))
(defn get-certified-by-id [certified-id]
  (mc/find-map-by-id db certified-collection (ObjectId. certified-id)))
(defn get-certified-by-name [name]
  (mc/find-maps db certified-collection  {:name name}))

;Packaging collection
(defn get-packaging []
  (mc/find-maps db packaging-collection))
(defn get-packaging-by-id [packaging-id]
  (mc/find-map-by-id db packaging-collection (ObjectId. packaging-id)))
(defn get-packaging-by-name [name]
  (mc/find-maps db packaging-collection  {:name name}))

;Producers collection
(defn create-producer [name address contact description certified_id]
  (mc/insert db producers-collection
             {:name name
              :address address
              :contact contact
              :description description
              :certified_id (str (get (into {} (get-certified-by-name certified_id)) :_id))}))

(defn update-producer [producer-id name address contact description certified_id]
  (mc/update-by-id db producers-collection (ObjectId. producer-id)
             {$set
                {:name name
                 :address address
                 :contact contact
                 :description description
                 :certified_id (str (get (into {} (get-certified-by-name certified_id)) :_id))}}))

;Search producers
(defn search-producers [keyword]
  (mc/find-maps db producers-collection {$or [{:name {$regex (str ".*" keyword ".*")}}
                                              {:address {$regex (str ".*" keyword ".*")}}
                                              {:contact {$regex (str ".*" keyword ".*")}}
                                              {:description {$regex (str ".*" keyword ".*")}}]
                                         }))

(defn delete-producer [producer-id]
  (mc/remove-by-id db producers-collection (ObjectId. producer-id)))

(defn get-producers []
  (mc/find-maps db producers-collection))

(defn get-producer-by-id [producer-id]
  (mc/find-map-by-id db producers-collection (ObjectId. producer-id)))

(defn get-producer-by-name [name]
  (mc/find-maps db producers-collection  {:name name}))

(defn get-producers-by-certified [certified-id]
  (mc/find-maps db producers-collection {:certified_id certified-id}))

;Products collection
(defn create-product [name description amount price type producer_id packaging_id]
  (mc/insert db products-collection
             {:name name
              :description description
              :amount amount
              :price price
              :type type
              :producer_id (str (get (into {} (get-producer-by-name producer_id)) :_id))
              :packaging_id (str (get (into {} (get-packaging-by-name packaging_id)) :_id))}))

(defn update-product [product-id name description amount price type producer_id packaging_id]
  (mc/update-by-id db products-collection (ObjectId. product-id)
                   {$set
                    {:name name
                     :description description
                     :amount amount
                     :price price
                     :type type
                     :producer_id (str (get (into {} (get-producer-by-name producer_id)) :_id))
                     :packaging_id (str (get (into {} (get-packaging-by-name packaging_id)) :_id))}}))

(defn delete-product [product-id]
  (mc/remove-by-id db products-collection (ObjectId. product-id)))

(defn get-products []
  (mc/find-maps db products-collection))

(defn get-product-by-id [product-id]
  (mc/find-map-by-id db products-collection (ObjectId. product-id)))

(defn get-products-by-packaging [packaging-id]
  (mc/find-maps db products-collection  {:packaging_id packaging-id}))

(defn get-product-by-name [name]
  (mc/find-maps db products-collection  {:name name}))

(defn get-products-by-producer [producer-id]
  (mc/find-maps db products-collection  {:producer_id producer-id}))

;Search products
(defn search-products [keyword]
  (mc/find-maps db products-collection {$or [{:name {$regex (str ".*" keyword ".*")}}
                                             {:description {$regex (str ".*" keyword ".*")}}
                                             {:amount {$regex (str ".*" keyword ".*")}}
                                             {:price {$regex (str ".*" keyword ".*")}}
                                             {:type {$regex (str ".*" keyword ".*")}}
                                             {:producer_id (str (:_id (into {} (get-producer-by-name {$regex (str ".*" keyword ".*")}))))}]
                                        }))

;Producer-reviews collection
(defn create-producer-review [producer-id review rating]
  (mc/insert db producer-reviews-collection
             {:producer_id producer-id
              :review review
              :rating rating}))

(defn get-reviews-for-producer [producer-id]
  (mc/find-maps db producer-reviews-collection {:producer_id producer-id}))

(defn get-producer-reviews-by-rating [rating]
  (mc/find-maps db producer-reviews-collection {:rating rating}))

(defn get-all-producer-reviews []
  (mc/find-maps db producer-reviews-collection))

;Search producer-reviews
(defn search-producer-reviews [keyword]
  (mc/find-maps db producer-reviews-collection {$or [{:review {$regex (str ".*" keyword ".*")}}
                                                     {:rating {$regex (str ".*" keyword ".*")}}
                                                     {:producer_id (str (:_id (into {} (get-producer-by-name {$regex (str ".*" keyword ".*")}))))}]}))

;Product-reviews collection
(defn create-product-review [product-id review rating]
  (mc/insert db product-reviews-collection
             {:product_id product-id
              :review review
              :rating rating}))

(defn get-reviews-for-product [product-id]
  (mc/find-maps db product-reviews-collection {:product_id product-id}))

(defn get-product-reviews-by-rating [rating]
  (mc/find-maps db product-reviews-collection {:rating rating}))

(defn get-all-product-reviews []
  (mc/find-maps db product-reviews-collection))

;Search product-reviews
(defn search-product-reviews [keyword]
  (mc/find-maps db product-reviews-collection {$or [{:review {$regex (str ".*" keyword ".*")}}
                                                     {:rating {$regex (str ".*" keyword ".*")}}
                                                     {:product_id (str (:_id (into {} (get-product-by-name {$regex (str ".*" keyword ".*")}))))}]}))

(defn delete-producer-review [producer-review-id]
  (mc/remove-by-id db producer-reviews-collection (ObjectId. producer-review-id)))

