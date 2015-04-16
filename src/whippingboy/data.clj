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
(declare websites icons)

(sql/defentity icons)
(sql/defentity websites (sql/has-one icons {:fk :website_id}))
(sql/defentity labels)
(sql/defentity artists)
(sql/defentity albums)
(sql/defentity tracks)
(sql/defentity covers)

(defn get-fixed-websites []
  (sql/select websites (sql/with icons) (sql/limit 1000)))

(defn get-websites []
  (sql/select websites (sql/limit 1000)))

(defn get-labels []
  (sql/select labels (sql/limit 1000)))

(defn get-artists []
  (sql/select artists (sql/limit 1000)))

(defn get-albums []
  (sql/select albums (sql/limit 1000)))

(defn get-tracks []
  (sql/select tracks (sql/limit 1000)))

(defn get-covers []
  (sql/select covers (sql/limit 1000)))

(defn get-label [id]
  (first (sql/select labels (sql/where {:id id}))))

(defn get-artist [id]
  (first (sql/select artists (sql/where {:id id}))))

(defn get-album [id]
  (first (sql/select albums (sql/where {:id id}))))

(defn get-track [id]
  (first (sql/select tracks (sql/where {:id id}))))

(defn get-cover [id]
  (first (sql/select covers (sql/where {:id id}))))

(defmethod validate* ::WebSite
  [website _]
  (let [v (validation-set (presence-of :url))
        message (v website)]
    (if-not (empty? message)
      (throw+ {:type ::invalid} message))))

(defmethod validate* ::Artist
  [artist _]
  (let [v (validation-set (presence-of :name))
        message (v artist)]
    (if-not (empty? message)
      (throw+ {:type ::invalid} message))))

(defmethod validate* ::Label
  [label _]
  (let [v (validation-set (presence-of :name)
                          (presence-of :country))
        message (v label)]
    (if-not (empty? message)
      (throw+ {:type ::invalid} message))))

(defmethod validate* ::Album
  [album _]
  (let [v (validation-set (presence-of :name)
                          (presence-of :year)
                          (presence-of :artist_id))
        message (v album)]
    (if-not (empty? message)
      (throw+ {:type ::invalid} message))))

(defmethod validate* ::Track
  [track _]
  (let [v (validation-set (presence-of :name)
                          (presence-of :number)
                          (presence-of :album_id))
        message (v track)]
    (if-not (empty? message)
      (throw+ {:type ::invalid} message))))

(defmethod validate* ::Cover
  [cover _]
  (let [v (validation-set (presence-of :url)
                          (presence-of :album_id))
        message (v cover)]
    (if-not (empty? message)
      (throw+ {:type ::invalid} message))))

(defn create-website [website]
  (validate [website ::WebSite])
  (sql/insert websites
              (sql/values (select-keys website [:url :rank]))))

(defn create-artist [artist]
  (validate [artist ::Artist])
  (sql/insert artists
              (sql/values (select-keys artist [:name :label_id]))))

(defn create-label [label]
  (validate [label ::Label])
  (sql/insert labels
              (sql/values (select-keys label [:name :country]))))

(defn create-album [album]
  (validate [album ::Album])
  (sql/insert albums
              (sql/values (select-keys album [:name :year :artist_id :label_id]))))

(defn create-track [track]
  (validate [track ::Track])
  (sql/insert tracks
              (sql/values (select-keys track [:name :number :album-id :artist_id]))))

(defn create-cover [cover]
  (validate [cover ::Cover])
  (sql/insert covers
              (sql/values (select-keys cover [:url :album_id]))))

(defn get-icon-for-website [website-id]
  (first (sql/select icons (sql/where {:website_id website-id}))))

(defn website-with-icon [website]
  (let [website-id (:id website)
        icon (get-icon-for-website website-id)]
    (assoc website :has_icon (not (nil? icon)))))

(defn get-websites-with-icons []
  (let [websites (get-websites)]
    (assoc {} :items (map website-with-icon websites))))

(defn get-fixed-websites-with-icons []
  (assoc {} :items
         (for [{:keys [:website_id] :as website}
               (get-fixed-websites)]
           (-> website
               (select-keys [:url :rank :id])
               (assoc :has_icon (not (nil? website_id)))))))
