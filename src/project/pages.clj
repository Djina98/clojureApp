(ns project.pages
  (:require [hiccup.page :refer [html5]]
            [hiccup.form :as form]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn template [& body]
  (html5
    [:head [:title "Bee organic"]]
    [:body
     [:a {:href "/"} [:h1 "Bee organic"]]
     [:a {:href "producers/new"} "Dodaj novog proizvođača"]
     [:hr]
     body]))

(defn index [producers]
  (template
    (for [p producers]
      [:h2 [:a {:href (str "/producers/" (:_id p))} (:name p)]])))

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
