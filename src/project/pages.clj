(ns project.pages
  (:require [hiccup.page :refer [html5]]
            [hiccup.form :as form]
            [markdown.core :as markdown]
            [project.db :as db]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

;Header and navbar
(defn template [& body]
  (html5
    [:head [:title "Bee organic"]
     [:link {:rel "stylesheet"
             :href "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
             :integrity "sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
             :crossorigin "anonymous"}]]
    [:body
      ;[:nav.navbar.navbar-expand-lg.navbar-light.bg-light
      [:nav.navbar.navbar-expand-sm.bg-success.navbar-dark
       [:div.container-fluid
        [:ul.navbar-nav
         [:li.nav-item
          [:a.navbar-brand {:href "/" :style "font-weight:bold"} "Bee organic"]]
         [:li.nav-item
          [:a.nav-link {:href "/producers/new"} "Dodaj novog proizvođača"]]
         [:li.nav-item
          [:a.nav-link {:href "/admin/login"} "Uloguj se"]]
         [:li.nav-item
          [:a.nav-link {:href "/admin/logout"} "Odjavi se"]]]]]
     [:div.container {:style "margin-top:30px"} body]]))

(def preview-length 270)

;Private function to display description on home page with maximum of 270 characters
(defn- cut-description [description]
  (if (> (.length description) preview-length)
    (str (subs description 0 preview-length) "...")
    description))

;Page with all producers, 3 producers per row
(defn index [producers]
  (template
    [:div {:class "row" :style "padding:15px"}
    (for [p producers]
      [:div {:class "col-sm-4"}
      [:div {:class "card" :style "margin-bottom:30px"}
       [:div {:class "card-body"}
        ;[:h5 {:class "card-text"} [:a {:href (str "/producers/" (:_id p))} (:name p)]]
        [:div {:class "row"}
         [:div {:class "col text-center"}
          [:a.btn.btn-outline-success {:href (str "/producers/" (:_id p)) :style "font-weight:bold"} (:name p)]]]
        [:p {:class "card-text"} (-> p :description cut-description markdown/md-to-html-string)]
        ]]]
      )]
    [:br]
    ))

;Producer page
(defn producer [p]
  (template
    [:small ((into {} (db/get-certified-by-value (:certified_id p))) :name)]
    [:hr]
    [:h1 (:name p)]
    [:small (:address p)]
    [:br]
    [:small (:contact p)]
    [:p (-> p :description markdown/md-to-html-string)]
    [:hr]
    (form/form-to
      [:delete (str "/producers/" (:_id p))]
      (anti-forgery-field)
      [:a.btn.btn-secondary {:href (str "/") :style "margin-right:15px"} "Nazad"]
      [:a.btn.btn-primary {:href (str "/producers/" (:_id p) "/edit") :style "margin-right:15px"} "Izmeni"]
      (form/submit-button {:class "btn btn-danger"} "Obriši"))
    ))

;Edit producer and Add new producer page
(defn edit-producer [p]
  (template
    (form/form-to
      [:post (if p
               (str "/producers/" (:_id p))
               "/producers")
       [:br]]
      [:div.form-group
       (form/label "name" "Naziv proizvođača")
       (form/text-field {:class "form-control"} "name" (:name p))
       [:br]]

      [:div.form-group
       (form/label "description" "Kratak opis")
       (form/text-area {:class "form-control"} "description" (:description p))
       [:br]]

      [:div.form-group
       (form/label "address" "Adresa")
       (form/text-field {:class "form-control"} "address" (:address p))
       [:br]]

      [:div.form-group
       (form/label "contact" "Kontakt")
       (form/text-field {:class "form-control"} "contact" (:contact p))
       [:br]]

      [:div.form-group
       (form/drop-down {:class "form-control"} "certified_id" [["Sertifikovan organski" 1]["U periodu konverzije" 2] ["Nema sertifikat" 3]]  (:certified_id p))
       [:br]]

      (anti-forgery-field)

      (form/submit-button {:class "btn btn-primary"} "Sačuvaj izmene"))))

;Admin login page
(defn admin-login [& [message]]
  (template
    (when message
      [:div.alert.alert-danger message])
    (form/form-to
      [:post "/admin/login"]

      [:div {:class "row" :style "margin-top:100px"}
       [:div {:class "col-md-4 offset-md-4"}
        [:div.form-group
         (form/label "username" "Email adresa")
         (form/text-field  {:class "form-control"} "username")]

        [:div.form-group
         (form/label "password" "Lozinka")
         (form/password-field {:class "form-control"} "password")
         [:br]]]]

      [:div {:class "row"}
       [:div {:class "col text-center"}
        (anti-forgery-field)
        (form/submit-button {:class "btn btn-primary"} "Uloguj se")]])))


