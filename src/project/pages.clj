(ns project.pages
  (:require [hiccup.page :refer [html5]]
            [hiccup.form :as form]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn template [& body]
  (html5
    [:head [:title "Bee organic"]
     [:link {:rel "stylesheet"
             :href "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
             :integrity "sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
             :crossorigin "anonymous"}]]
    [:body
     [:div.container
      [:nav.navbar.navbar-expand-lg.navbar-light.bd-light
       [:a.navbar-brand {:href "/"} "Bee organic"]
       [:div.navbar-nav.ml-auto
        [:a.nav-item.nav-link {:href "/producers/new"} "Dodaj novog proizvođača"]
        [:a.nav-item.nav-link {:href "/admin/login"} "Uloguj se"]
        [:a.nav-item.nav-link {:href "/admin/logout"} "Odjavi se"]
        ]]
      body]]))

(def preview-length 270)

(defn- cut-description [description]
  (if (> (.length description) preview-length)
    (str (subs description 0 preview-length) "...")
    description))

(defn index [producers]
  (template
    (for [p producers]
      [:div
       [:h2 [:a {:href (str "/producers/" (:_id p))} (:name p)]]
       [:p (-> p :description cut-description)]
       ]
      )))

(defn producer [p]
  (template
    [:a {:href (str "/producers/" (:_id p) "/edit")} "Izmeni"]
    [:hr]
    [:small (:address p)]
    [:h1 (:name p)]
    [:p (:description p)]
    [:small (:contact p)]))

(defn edit-producer [p]
  (template
    (form/form-to
      [:post (if p
               (str "/producers/" (:_id p))
               "/producers")]
      (form/label "name" "Naziv proizvođača")
      (form/text-field "name" (:name p))

      (form/label "description" "Kratak opis")
      (form/text-area "description" (:description p))

      (form/label "address" "Adresa")
      (form/text-field "address" (:address p))

      (form/label "contact" "Kontakt")
      (form/text-field "contact" (:contact p))

      (anti-forgery-field)

      (form/submit-button "Sačuvaj izmene"))))

(defn admin-login [& [message]]
  (template
    (when message
      [:div.alert.alert-danger message])
    (form/form-to
      [:post "/admin/login"]

      [:div.form-group
       (form/label "username" "Email adresa")
       (form/text-field  {:class "form-control"} "username")]

      [:div.form-group
       (form/label "password" "Lozinka")
       (form/password-field {:class "form-control"} "password")
       [:br]]

      (anti-forgery-field)
      (form/submit-button {:class "btn btn-primary"} "Uloguj se"))))
