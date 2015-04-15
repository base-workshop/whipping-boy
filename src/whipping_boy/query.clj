(ns whipping-boy.query
  (:use [whipping-boy.helpers])
  (:require [whipping-boy.data :as data]
            [clj-time.core :as time]
            [korma.core :as sql]))

(defentity web_sites)

(defn get-websites []
  (sql/select web_sites))

(defn create-website [website]
  (let [new-website (data/created-now (data/updated-now website))]
    (sql/insert web_sites
      (sql/values (select-keys new-website [:url :rank :snippet :created_at :updated_at])))))
