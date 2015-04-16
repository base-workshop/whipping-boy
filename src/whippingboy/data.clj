(ns whippingboy.data
  (:use [whippingboy.helpers])
  (:require [clj-time.core :as time]
            [validateur.validation :refer [presence-of
                                           valid? validation-set]]
            [slingshot.slingshot :refer [throw+]]
            [korma.db :as korma]
            [korma.core :as sql]
            [lobos.connectivity :as lobos])
  (:import org.bson.types.ObjectId))

;;postgres
(def db-connection-info
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :user "postgres"
   :password "zaq12wsx"
   :subname "//localhost:5432/workshop"})

; set up korma
(korma/defdb db db-connection-info)
; set up lobos
(lobos/open-global db-connection-info)

;; Utility Functions
;;
(defn created-now
  [model]
  (assoc model :created_at (time/now)))

(defn updated-now
  [model]
  (assoc model :updated_at (time/now)))

;;
;; Validation Functions
;; (Inspired by http://stackoverflow.com/questions/1640311/should-i-use-a-function-or-a-macro-to-validate-arguments-in-clojure)
;;
(defmulti validate* (fn [val val-type] val-type))

(defn validate
  "Execute a sequence of validation tests"
  [& tests]
  (doseq [test tests] (apply validate* test)))

;; entities
(defentity websites)
(defentity icons)

(defn get-websites []
  (sql/select websites (sql/limit 1000)))

(defmethod validate* ::WebSite
  [website _]
  (let [v (validation-set (presence-of :url))
        message (v website)]
    (if-not (empty? message)
      (throw+ {:type ::invalid} message))))

(defn create-website [website]
  (let [new-website (created-now (updated-now website))]
    (validate [new-website ::WebSite])
    (sql/insert websites
                (sql/values (select-keys new-website [:url :rank :created_at :updated_at])))))

(defn get-icon-for-website [website-id]
  (first (sql/select icons (sql/where {:web_site_id website-id}))))

(defn website-with-icon [website]
  (let [website-id (:id website)
        icon (get-icon-for-website website-id)]
    (assoc website :has_icon (not (nil? icon)))))

(defn get-websites-with-icons []
  (let [websites (get-websites)]
    (assoc {} :items (map website-with-icon websites))))
