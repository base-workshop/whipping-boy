(ns whipping-boy.data
  (:require [clj-time.core :as time]
            [monger.joda-time]
            [validateur.validation :refer [presence-of
                                           valid? validation-set]]
            [slingshot.slingshot :refer [throw+]]
            [korma.db :as korma]
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
