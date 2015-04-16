(ns whippingboy.query
  (:use [whippingboy.helpers])
  (:require [whippingboy.data :as data]
            [clj-time.core :as time]
            [korma.core :as sql]))

(defentity web_sites)

(defn get-websites []
  (sql/select web_sites))

(defn create-website [website]
  (let [new-website (data/created-now (data/updated-now website))]
    (sql/insert web_sites
      (sql/values (select-keys new-website [:url :rank :snippet :created_at :updated_at])))))

;; (defn websites_with_icons []
;;   (let [websites (get-websites)]
;;     (map website_with_icon websites)))

;; (defn get_icon_for_website [website_id]
;;   (first (sql/select icons (where {:web_site_id website_id}))))

;; (defn website_with_icon [website]
;;   (let [website_id (:id website)
;;         icon (get_icon_for_website website_id)]
;;     (assoc website :icon (not (nil? icon)))))
