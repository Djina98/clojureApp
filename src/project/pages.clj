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
             :href "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"}]
     [:link {:rel "stylesheet"
             :href "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
             :integrity "sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
             :crossorigin "anonymous"}]
     [:link {:rel "stylesheet"
             :href "/style.css"}]

     [:script "function searchInProducers() {
          var keyword = document.getElementById(\"searchProducers\").value;
          window.location.href = \"/producers/keyword/\" + keyword;}"]
     [:script "function searchInProducts() {
          var keyword = document.getElementById(\"searchProducts\").value;
          window.location.href = \"/products/keyword/\" + keyword;}"]
     ]
    [:body
     [:nav.navbar.navbar-expand-sm.bg-success.navbar-dark
     [:div.container-fluid
      [:ul.navbar-nav
       [:li.nav-item
        [:a.navbar-brand {:href "/" :style "font-weight:bold;font-size:20px"} "Bee organic"]]
       [:li.nav-item
        [:div.dropdown
         [:button.dropbtn "Proizvođači\n      " [:i.fa.fa-caret-down]]
         [:div.dropdown-content
          [:a {:href "/"} "Pregled"]
          [:a {:href "/producers/new"} "Dodaj novog"]
          ]]]
       [:li.nav-item
        [:div.dropdown
         [:button.dropbtn "Proizvodi\n      " [:i.fa.fa-caret-down]]
         [:div.dropdown-content
          [:a {:href "/products"} "Pregled"]
          [:a {:href "/products/new"} "Dodaj novi"]
          ]]]]
       [:div.d-flex.align-items-center
        [:ul.navbar-nav
         [:li.nav-item
          [:a {:href "/admin/login"} "Uloguj se"]]
         [:li.nav-item
          [:a {:href "/admin/logout"} "Odjavi se"]]]]]]
     [:div.container {:style "margin-top:30px"}
      body]
     [:script {:src "/search.js"}]]))

(def preview-length 270)

;Private function to display description on home page with maximum of 270 characters
(defn- cut-description [description]
  (if (> (.length description) preview-length)
    (str (subs description 0 preview-length) "...")
    description))

;Page with all producers, 3 producers per row
(defn producers [producers]
  (template
    [:div {:class "row" :style "padding:15px;margin-left:10px"}
     [:div {:class "col"}
      [:a {:href (str "/") :type "button" :class "btn btn-outline-success" :style "margin-right:10px"} "Svi proizvođači"]
      [:a {:href (str "/producers/certified/" (str (get (into {} (db/get-certified-by-name "Sertifikovan organski")) :_id))) :type "button" :class "btn btn-outline-success" :style "margin-right:10px"} "Sertifikovani organski"]
      [:a {:href (str "/producers/certified/" (str (get (into {} (db/get-certified-by-name "Nema sertifikat")) :_id))) :type "button" :class "btn btn-outline-success" :style "margin-right:10px"} "Bez sertifikata"]
      [:a {:href (str "/producers/certified/" (str (get (into {} (db/get-certified-by-name "U periodu konverzije")) :_id))) :type "button" :class "btn btn-outline-success" :style "margin-right:10px"} "U periodu konverzije"]]
     [:div {:class "input-group rounded" :style "margin-top:20px"}
      [:input {:type "search"
               :id "searchProducers"
               :class "form-control rounded"
               :placeholder "Pretraži proizvođače"
               :aria-label "Pretraga"
               :aria-describedby "search-addon"}]
      [:button {:type "button" :id "btnSearchProducers" :class "btn btn-success" :onClick "searchInProducers()"}
       [:i {:class "fa fa-search"}]]]
     [:br]]
    [:div {:class "row" :style "padding:15px"}
    (for [p producers]
      [:div {:class "col-sm-4"}
      [:div {:class "card" :style "margin-bottom:30px"}
       [:div {:class "card-body"}
        [:div {:class "row"}
         [:div {:class "col text-center"}
          [:a.btn.btn-outline-success {:href (str "/producers/" (:_id p)) :style "font-weight:bold"} (:name p)]
          ;[:a.btn.btn-outline-success {:href (str "/products/producers/" (:_id p)) :style "font-weight:bold"} "Ponuda"]
          ]]

        [:p {:class "card-text"} (-> p :description cut-description markdown/md-to-html-string)]
        ]]]
      )]
    [:br]
    ))

;Producer page
(defn producer [p]
  (template
    [:hr]
    [:h1 (:name p)]
    [:br]
    [:small ((into {} (db/get-certified-by-id (:certified_id p))) :name)]
    [:br]
    [:small (str "Adresa: " (:address p))]
    [:br]
    [:small (str "Kontakt: " (:contact p))]
    [:p (-> p :description markdown/md-to-html-string)]
    [:hr]
    (form/form-to
      [:delete (str "/producers/" (:_id p))]
      (anti-forgery-field)
      [:a.btn.btn-secondary {:href (str "/") :style "margin-right:15px"} "Nazad"]
      [:a.btn.btn-primary {:href (str "/producers/" (:_id p) "/edit") :style "margin-right:15px"} "Izmeni"]
      (form/submit-button {:class "btn btn-danger"} "Obriši"))
    ))

;Edit producer and Add new producer form
(defn edit-producer [p]
  (template
    [:div {:class "card"}
     [:div {:class "card-body" :style "margin-bottom:20px"}
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
         (form/text-area {:class "form-control" :rows "6"} "description" (:description p))
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
         (form/label "certified_id" "Sertifikat")
         (form/drop-down {:class "form-control"} "certified_id"  (into [] (for [c (db/get-certified)] (get c :name)))  (if p (get (into {} (db/get-certified-by-id (:certified_id p))) :name)))
         [:br]]

        (anti-forgery-field)

        (form/submit-button {:class "btn btn-primary"} "Sačuvaj izmene"))]]))

;Page with all products, 3 products per row
(defn products [products]
  (template
    [:div {:class "row" :style "padding:15px;margin-left:10px"}
     [:div {:class "col"}
      [:a {:href (str "/products") :type "button" :class "btn btn-outline-success" :style "margin-right:10px"} "Svi proizvodi"]
      [:a {:href (str "/products/packaging/" (str (get (into {} (db/get-packaging-by-name "Staklena teglica")) :_id))) :type "button" :class "btn btn-outline-success" :style "margin-right:10px"} "Proizvodi u staklenoj ambalaži"]
      [:a {:href (str "/products/packaging/" (str (get (into {} (db/get-packaging-by-name "Plastična boca")) :_id))) :type "button" :class "btn btn-outline-success" :style "margin-right:10px"} "Proizvodi u plastičnoj ambalaži"]]
     [:div {:class "input-group rounded" :style "margin-top:20px"}
      [:input {:type "search"
               :id "searchProducts"
               :class "form-control rounded"
               :placeholder "Pretraži proizvode"
               :aria-label "Pretraga"
               :aria-describedby "search-addon"}]
      [:button {:type "button" :id "btnSearchProducts" :class "btn btn-success" :onClick "searchInProducts()"}
       [:i {:class "fa fa-search"}]]]
     [:br]]
    [:div {:class "row" :style "padding:15px"}
     (for [p products]
         [:div {:class "col-sm-4" :id "div-glass"}
           [:div {:class "card" :style "margin-bottom:30px"}
            [:div {:class "card-body"}
             [:div {:class "row"}
              [:div {:class "col text-center"}
               [:a.btn.btn-outline-success {:href (str "/products/" (:_id p)) :style "font-weight:bold"} (:name p)]]]
             [:p {:class "card-text"} (-> p :description cut-description markdown/md-to-html-string)]
             ]]])]
    [:br]))

;Product page
(defn product [p]
  (template
    [:small (str "Proizvođač: " ((into {} (db/get-producer-by-id (:producer_id p))) :name))]
    [:br]
    [:small (str "Vrsta meda: " (:type p))]
    [:hr]
    [:h1 (:name p)]
    [:small (str "Količina u gramima: " (:amount p)) ]
    [:br]
    [:small (str "Cena u dinarima: " (:price p))]
    [:br]
    [:small (str "Ambalaža: " ((into {} (db/get-packaging-by-id (:packaging_id p))) :name))]
    [:p (-> p :description markdown/md-to-html-string)]
    [:hr]
    (form/form-to
      [:delete (str "/products/" (:_id p))]
      (anti-forgery-field)
      [:a.btn.btn-secondary {:href (str "/products") :style "margin-right:15px"} "Nazad"]
      [:a.btn.btn-primary {:href (str "/products/" (:_id p) "/edit") :style "margin-right:15px"} "Izmeni"]
      (form/submit-button {:class "btn btn-danger"} "Obriši"))
    ))

;Edit product and Add new product form
(defn edit-product [p]
  (template
    [:div {:class "card"}
     [:div {:class "card-body" :style "margin-bottom:20px"}
      (form/form-to
        [:post (if p
                 (str "/products/" (:_id p))
                 "/products")
         [:br]]
        [:div.form-group
         (form/label "name" "Naziv proizvoda")
         (form/text-field {:class "form-control"} "name" (:name p))
         [:br]]

        [:div.form-group
         (form/label "description" "Kratak opis")
         (form/text-area {:class "form-control" :rows "5"} "description" (:description p))
         [:br]]

        [:div.form-group
         (form/label "amount" "Količina")
         (form/text-field {:class "form-control"} "amount" (:amount p))
         [:br]]

        [:div.form-group
         (form/label "price" "Cena")
         (form/text-field {:class "form-control"} "price" (:price p))
         [:br]]

        [:div.form-group
         (form/label "type" "Vrsta meda")
         (form/text-field {:class "form-control"} "type" (:type p))
         [:br]]

        [:div.form-group
         (form/label "producer_id" "Proizvođač")
         (form/drop-down {:class "form-control"} "producer_id"  (into [] (for [p (db/get-producers)] (get p :name)))  (if p (get (into {} (db/get-producer-by-id (:producer_id p))) :name)))
         [:br]]

        [:div.form-group
         (form/label "packaging_id" "Ambalaža")
         (form/drop-down {:class "form-control"} "packaging_id"  (into [] (for [pkg (db/get-packaging)] (get pkg :name)))  (if p (get (into {} (db/get-packaging-by-id (:packaging_id p))) :name)))
         [:br]]

        (anti-forgery-field)

        (form/submit-button {:class "btn btn-primary"} "Sačuvaj izmene"))]]
    ))

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


