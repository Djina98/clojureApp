(ns project.pages
  (:require [hiccup.page :refer [html5]]))

(defn template [& body]
  (html5
    [:head [:title "Bee organic"]]
    [:body
     [:a {:href "/"} [:h1 "Bee organic"]]
     body]))

(defn index [producers]
  (template
    (for [p producers]
      [:h2 [:a {:href (str "/producers/" (:_id p))} (:name p)]])))

(defn producer [p]
  (template
    [:small (:address p)]
    [:h1 (:title p)]
    [:p (:description p)]
    [:small (:contact p)]))
