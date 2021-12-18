(ns project.pages
  (:require [hiccup.page :refer [html5]]
            [hiccup.form :as form]
            [markdown.core :as markdown]
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
    [:br]
    [:div {:class "row"}
    (for [p producers]
      [:div {:class "col-sm-4"}
      [:div {:class "card border-dark"}
       [:div {:class "card-body"}
        [:h5 {:class "card-text"} [:a {:href (str "/producers/" (:_id p))} (:name p)]]
        [:p {:class "card-text"} (-> p :description cut-description markdown/md-to-html-string)]
        ]]]
      )]
    ))

(defn producer [p]
  (template
    (form/form-to
      [:delete (str "/producers/" (:_id p))]
      (anti-forgery-field)
      [:a.btn.btn-primary {:href (str "/producers/" (:_id p) "/edit")} "Izmeni"]
      (form/submit-button {:class "btn btn-danger"} "Obriši"))
    [:hr]
    [:small (:address p)]
    [:h1 (:name p)]
    [:small (:certified p)]
    [:p (-> p :description markdown/md-to-html-string)]
    [:small (:contact p)]))

(defn edit-producer [p]
  (template
    (form/form-to
      [:post (if p
               (str "/producers/" (:_id p))
               "/producers")]
      [:div.form-group
       (form/label "name" "Naziv proizvođača")
       (form/text-field {:class "form-control"} "name" (:name p))]

      [:div.form-group
       (form/label "description" "Kratak opis")
       (form/text-area {:class "form-control"} "description" (:description p))]

      [:div.form-group
       (form/label "address" "Adresa")
       (form/text-field {:class "form-control"} "address" (:address p))]

      [:div.form-group
       (form/label "contact" "Kontakt")
       (form/text-field {:class "form-control"} "contact" (:contact p))]

      [:div.form-group
       (form/label "certified" "Organski sertifikat?")
       (form/text-field {:class "form-control"} "certified" (:certified p))
       [:br]]

      (anti-forgery-field)

      (form/submit-button {:class "btn btn-primary"} "Sačuvaj izmene"))))

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
