(ns project.admin)

(def admin-username (or (System/getenv "PROJECT_ADMIN_USERNAME")
                        "admin"))
(def admin-password (or (System/getenv "PROJECT_ADMIN_PASSWORD")
                        "admin"))

(defn authorize-admin [username password]
  (and (= username admin-username)
       (= password admin-password)))
