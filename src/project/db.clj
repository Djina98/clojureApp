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

(defn create-producer [name address contact description]
  (mc/insert db producers-collection
             {:name name
              :address address
              :contact contact
              :description description}))

(defn update-producer [producer-id name address contact description]
  (mc/update-by-id db producers-collection (ObjectId. producer-id)
             {$set
                {:name name
                :address address
                :contact contact
                :description description}}))

(defn get-producers []
  (mc/find-maps db producers-collection))

(defn get-producer-by-id [producer-id]
  (mc/find-map-by-id db producers-collection (ObjectId. producer-id)))