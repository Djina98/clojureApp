(ns project.pages
  (:require [hiccup.page :refer [html5]]
            [hiccup.form :as form]
            [markdown.core :as markdown]
            [project.db :as db]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn calculate-average-rating [producer-reviews]
  (def sum (atom 0))
  (for [r producer-reviews]
    (double (/ (swap! sum #(+ % (Integer/parseInt (:rating r)))) (count producer-reviews)))
    )
  )
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
     [:script "function searchInProducerReviews() {
          var keyword = document.getElementById(\"searchProducerReviews\").value;
          window.location.href = \"/producer-reviews/keyword/\" + keyword;}"]
     [:script "function searchInProductReviews() {
          var keyword = document.getElementById(\"searchProductReviews\").value;
          window.location.href = \"/product-reviews/keyword/\" + keyword;}"]
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
         [:a {:href "/"} "Dostupni proizvođači"]
         [:a {:href "/producers/new"} "Dodaj novog proizvođača"]
         [:a {:href "/producer-reviews"} "Utisci o proizvođačima"]
         ]]]
       [:li.nav-item
       [:div.dropdown
        [:button.dropbtn "Proizvodi\n      " [:i.fa.fa-caret-down]]
        [:div.dropdown-content
         [:a {:href "/products"} "Dostupni proizvodi"]
         [:a {:href "/products/new"} "Dodaj novi proizvod"]
         [:a {:href "/product-reviews"} "Utisci o proizvodima"]
         ]]]
      ]
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
      [:div.dropdown {:style "margin-right:20px"}
       [:button {:class "btn btn-outline-success" :style "font-weight:bold"} "Filtriraj po sertifikatima\n      " [:i.fa.fa-caret-down]]
       [:div.dropdown-content
        [:a {:href (str "/")} "Svi proizvođači"]
        [:a {:href (str "/producers/certified/" (str (get (into {} (db/get-certified-by-name "Sertifikovan organski")) :_id)))} "Sertifikovan organski"]
        [:a {:href (str "/producers/certified/" (str (get (into {} (db/get-certified-by-name "Nema sertifikat")) :_id)))} "Nema sertifikat"]
        [:a {:href (str "/producers/certified/" (str (get (into {} (db/get-certified-by-name "U periodu konverzije")) :_id)))} "U periodu konverzije"]
        ]]]
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
     [:p {:style "text-align:center;margin-bottom:20px"} (if (empty? producers) "Još uvek nema dostupnih proizvođača")]

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
(defn producer [p reviews]
  (template
    [:div {:style "text-align:center"}
     [:div {:class "row"}
      [:div {:class "col"}
       [:br]
       [:a.btn.btn-outline-success {:href (str "/producer-reviews/" (:_id p) "/new") :style "margin-right:15px;font-weight:bold"} "Ostavi utisak o ovom proizvođaču"]
       ]
      [:div {:class "col" :style "border:1px solid black;"}
       [:h6 {:style "margin-top:10px"} "Prosečna ocena:"]
       [:h1 (if (not-empty (calculate-average-rating reviews)) (nth (into [] (calculate-average-rating reviews)) (dec (count (calculate-average-rating reviews)))))]
       [:p (if (empty? (calculate-average-rating reviews)) "Ocena nije dostupna")]
       ]
      ]
    [:hr]
    [:h1 (:name p)]
    [:br]
    [:small ((into {} (db/get-certified-by-id (:certified_id p))) :name)]
    [:br]
    [:small (str "Adresa: " (:address p))]
    [:br]
    [:small (str "Kontakt: " (:contact p))]
    [:p (-> p :description markdown/md-to-html-string)]
    (form/form-to
      [:delete (str "/producers/" (:_id p))]
      (anti-forgery-field)
      [:a.btn.btn-secondary {:href (str "/") :style "margin-right:15px"} "Nazad"]
      [:a.btn.btn-primary {:href (str "/producers/" (:_id p) "/edit") :style "margin-right:15px"} "Izmeni"]
      (form/submit-button {:class "btn btn-danger"} "Obriši"))]
    [:hr]
    [:div {:class "row" :style "padding:15px"}
     [:h3 {:style "text-align:center;margin-bottom:20px"} "Utisci"]
     [:p {:style "text-align:center;margin-bottom:20px"} (if (empty? reviews) "Još uvek nema dostupnih utisaka")]

     (for [r reviews]
       [:div {:class "col-sm-4"}
        [:div {:class "card" :style "margin-bottom:30px"}
         [:div {:class "card-body"}
          [:div {:class "row"}
           [:div {:class "col text-center"}
            [:a {:style "font-weight:bold;color:black"} (str "Ocena: " (:rating r))]
            ]]

          [:p {:class "card-text"} (:review r)]
          ]]]
       )]
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

        [:div {:style "text-align:center"}
         (form/submit-button {:class "btn btn-primary"} "Sačuvaj")])]]))

;Page with all products, 3 products per row
(defn products [products]
  (template
    [:div {:class "row" :style "padding:15px;margin-left:10px"}
     [:div {:class "col"}
      [:div.dropdown {:style "margin-right:20px"}
       [:button {:class "btn btn-outline-success" :style "font-weight:bold"} "Filtriraj po ambalaži\n      " [:i.fa.fa-caret-down]]
       [:div.dropdown-content
        [:a {:href (str "/products")} "Svi proizvodi"]
        [:a {:href (str "/products/packaging/" (str (get (into {} (db/get-packaging-by-name "Staklena teglica")) :_id)))} "Proizvodi u staklenoj ambalaži"]
        [:a {:href (str "/products/packaging/" (str (get (into {} (db/get-packaging-by-name "Plastična boca")) :_id)))} "Proizvodi u plastičnoj ambalaži"]
        ]]

      [:div.dropdown {:style "margin-right:20px"}
       [:button {:class "btn btn-outline-success" :style "font-weight:bold"} "Filtriraj po proizvođaču\n      " [:i.fa.fa-caret-down]]
       [:div.dropdown-content
        [:a {:href (str "/products")} "Svi proizvodi"]
        (for [p products]
          [:a {:href (str "/products/producer/" (:producer_id p))} (get (into {} (db/get-producer-by-id (:producer_id p))) :name)])
        ]]]

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
     [:p {:style "text-align:center;margin-bottom:20px"} (if (empty? products) "Još uvek nema dostupnih proizvoda")]

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
(defn product [p reviews]
  (template
    [:div {:style "text-align:center"}
     [:div {:class "row"}
      [:div {:class "col"}
       [:br]
       [:a.btn.btn-outline-success {:href (str "/product-reviews/" (:_id p) "/new") :style "margin-right:15px;font-weight:bold"} "Ostavi utisak o ovom proizvodu"]
       ]
      [:div {:class "col" :style "border:1px solid black;"}
       [:h6 {:style "margin-top:10px"} "Prosečna ocena:"]
       [:h1 (if (not-empty (calculate-average-rating reviews)) (nth (into [] (calculate-average-rating reviews)) (dec (count (calculate-average-rating reviews)))))]
       [:p (if (empty? (calculate-average-rating reviews)) "Ocena nije dostupna")]
       ]
      ]
     [:hr]
    [:a {:href (str "/producers/" (:producer_id p)) :style "color:black;font-weight:bold"} (str "Proizvođač: " ((into {} (db/get-producer-by-id (:producer_id p))) :name))]
    [:br]
    [:small (str "Vrsta meda: " (:type p))]
    [:h1 (:name p)]
    [:small (str "Količina u gramima: " (:amount p)) ]
    [:br]
    [:small (str "Cena u dinarima: " (:price p))]
    [:br]
    [:small (str "Ambalaža: " ((into {} (db/get-packaging-by-id (:packaging_id p))) :name))]
    [:p (-> p :description markdown/md-to-html-string)]

    (form/form-to
      [:delete (str "/products/" (:_id p))]
      (anti-forgery-field)
      [:a.btn.btn-secondary {:href (str "/products") :style "margin-right:15px"} "Nazad"]
      [:a.btn.btn-primary {:href (str "/products/" (:_id p) "/edit") :style "margin-right:15px"} "Izmeni"]
      (form/submit-button {:class "btn btn-danger"} "Obriši"))
     [:hr]

     [:div {:class "row" :style "padding:15px"}
      [:h3 {:style "text-align:center;margin-bottom:20px"} "Utisci"]
      [:p {:style "text-align:center;margin-bottom:20px"} (if (empty? reviews) "Još uvek nema dostupnih utisaka")]

      (for [r reviews]
        [:div {:class "col-sm-4"}
         [:div {:class "card" :style "margin-bottom:30px"}
          [:div {:class "card-body"}
           [:div {:class "row"}
            [:div {:class "col text-center"}
             [:a {:style "font-weight:bold;color:black"} (str "Ocena: " (:rating r))]
             ]]
           [:p {:class "card-text"} (:review r)]]]])]]
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
         (form/text-field {:class "form-control" :type "number" :min "1"} "amount" (:amount p))
         [:br]]

        [:div.form-group
         (form/label "price" "Cena")
         (form/text-field {:class "form-control" :type "number" :min "1"} "price" (:price p))
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

        [:div {:style "text-align:center"}
         (form/submit-button {:class "btn btn-primary"} "Sačuvaj")])]]))

;Page with all producer-reviews - 3 reviews per row
(defn producer-reviews [reviews]
  (template
    [:div {:class "row" :style "padding:15px;margin-left:10px"}
     [:div {:class "col"}
      [:div.dropdown {:style "margin-right:20px"}
       [:button {:class "btn btn-outline-success" :style "font-weight:bold"} "Filtriraj po oceni\n      " [:i.fa.fa-caret-down]]
       [:div.dropdown-content
        [:a {:href (str "/producer-reviews")} "Svi utisci"]
        [:a {:href (str "/producer-reviews/rating/5")} "Ocena 5"]
        [:a {:href (str "/producer-reviews/rating/4")} "Ocena 4"]
        [:a {:href (str "/producer-reviews/rating/3")} "Ocena 3"]
        [:a {:href (str "/producer-reviews/rating/2")} "Ocena 2"]
        [:a {:href (str "/producer-reviews/rating/1")} "Ocena 1"]
        ]]

     [:div {:class "input-group rounded" :style "margin-top:20px"}
      [:input {:type "search"
               :id "searchProducerReviews"
               :class "form-control rounded"
               :placeholder "Pretraži utiske"
               :aria-label "Pretraga"
               :aria-describedby "search-addon"}]
      [:button {:type "button" :id "btnSearchProducerReviews" :class "btn btn-success" :onClick "searchInProducerReviews()"}
       [:i {:class "fa fa-search"}]]]
     [:br]]]
    [:div {:class "row" :style "padding:15px"}
     (for [r reviews]
       [:div {:class "col-sm-4" :id "div-glass"}
        [:div {:class "card" :style "margin-bottom:30px"}
         [:div {:class "card-body"}
          [:div {:class "row"}
           [:div {:class "col text-center"}
            [:a.btn.btn-outline-success {:href (str "/producers/" (:producer_id r)) :style "font-weight:bold;margin-bottom:20px"} ((into {} (db/get-producer-by-id (:producer_id r))) :name)]]]
          [:p {:class "card-text" :style "text-align:center"} (str "Ocena: " (:rating r))]
          [:p {:class "card-text" :style "text-align:center"} (:review r)]
          ]]])]
    [:br]))

;Add producer-review form
(defn add-producer-review [p]
  (template
    [:div {:class "card"}
     [:div {:class "card-body" :style "margin-bottom:20px"}
      [:div {:class "card-title" :style "text-align:center"}
       [:h3 "Utisak o proizvođaču"]
       [:h1 (:name p)]]
      (form/form-to
        [:post "/producer-reviews"]
     [:br]
        [:div.form-group
         (form/text-field {:class "form-control" :type "hidden"} "producer-id" (str (:_id p)))
         [:br]]

        [:div.form-group
         (form/label "review" "Utisak")
         (form/text-area {:class "form-control" :rows "6"} "review")
         [:br]]

        [:div.form-group
         (form/label "rating" "Ocena")
         (form/text-field {:class "form-control" :type "number" :min "1" :max "5"} "rating")
         [:br]]

        (anti-forgery-field)
        [:div {:style "text-align:center"}
         (form/submit-button {:class "btn btn-primary"} "Sačuvaj")])]]))

;Page with all product-reviews - 3 reviews per row
(defn product-reviews [reviews]
  (template
    [:div {:class "row" :style "padding:15px;margin-left:10px"}
     [:div {:class "col"}
      [:div.dropdown {:style "margin-right:20px"}
       [:button {:class "btn btn-outline-success" :style "font-weight:bold"} "Filtriraj po oceni\n      " [:i.fa.fa-caret-down]]
       [:div.dropdown-content
        [:a {:href (str "/product-reviews")} "Svi utisci"]
        [:a {:href (str "/product-reviews/rating/5")} "Ocena 5"]
        [:a {:href (str "/product-reviews/rating/4")} "Ocena 4"]
        [:a {:href (str "/product-reviews/rating/3")} "Ocena 3"]
        [:a {:href (str "/product-reviews/rating/2")} "Ocena 2"]
        [:a {:href (str "/product-reviews/rating/1")} "Ocena 1"]
        ]]

      [:div {:class "input-group rounded" :style "margin-top:20px"}
       [:input {:type "search"
                :id "searchProductReviews"
                :class "form-control rounded"
                :placeholder "Pretraži utiske"
                :aria-label "Pretraga"
                :aria-describedby "search-addon"}]
       [:button {:type "button" :id "btnSearchProductReviews" :class "btn btn-success" :onClick "searchInProductReviews()"}
        [:i {:class "fa fa-search"}]]]
      [:br]]]
    [:div {:class "row" :style "padding:15px"}
     (for [r reviews]
       [:div {:class "col-sm-4" :id "div-glass"}
        [:div {:class "card" :style "margin-bottom:30px"}
         [:div {:class "card-body"}
          [:div {:class "row"}
           [:div {:class "col text-center"}
            [:a.btn.btn-outline-success {:href (str "/products/" (:product_id r)) :style "font-weight:bold;margin-bottom:20px"} ((into {} (db/get-product-by-id (:product_id r))) :name)]]]
          [:p {:class "card-text" :style "text-align:center"} (str "Ocena: " (:rating r))]
          [:p {:class "card-text" :style "text-align:center"} (:review r)]
          ]]])]
    [:br]))

;Add producer-review form
(defn add-product-review [p]
  (template
    [:div {:class "card"}
     [:div {:class "card-body" :style "margin-bottom:20px"}
      [:div {:class "card-title" :style "text-align:center"}
       [:h3 "Utisak o proizvodu"]
       [:h1 (:name p)]]
      (form/form-to
        [:post "/product-reviews"]
        [:br]
        [:div.form-group
         (form/text-field {:class "form-control" :type "hidden"} "product-id" (str (:_id p)))
         [:br]]

        [:div.form-group
         (form/label "review" "Utisak")
         (form/text-area {:class "form-control" :rows "6"} "review")
         [:br]]

        [:div.form-group
         (form/label "rating" "Ocena")
         (form/text-field {:class "form-control" :type "number" :min "1" :max "5"} "rating")
         [:br]]

        (anti-forgery-field)
        [:div {:style "text-align:center"}
         (form/submit-button {:class "btn btn-primary"} "Sačuvaj")])]]))

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


