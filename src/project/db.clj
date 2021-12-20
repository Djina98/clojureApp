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

(defn get-certified-by-id [certified-id]
  (mc/find-map-by-id db certified-collection (ObjectId. certified-id)))

(defn get-certified-by-value [value]
  (mc/find-maps db certified-collection  {:value value}))

