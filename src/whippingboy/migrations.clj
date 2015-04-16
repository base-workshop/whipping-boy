(ns whippingboy.migrations
  (:refer-clojure :exclude
                  [alter drop bigint boolean char double float time complement])
  (:use [whippingboy.data]
        [whippingboy.helpers]
        [lobos [migration :only [defmigration]] core schema]))

(defn run-migrations []
  (binding [lobos.migration/*migrations-namespace* 'whippingboy.migrations]
    (migrate)))

(defmigration add-websites-table
  (up [] (create
          (tbl :websites
            (integer :rank)
            (varchar :url 255))))
  (down [] (drop (table :websites))))

(defmigration add-icons-table
  (up [] (create
          (tbl :icons
               (refer-to :websites)
               (integer :height)
               (integer :width))))
  (down [] (drop (table :icons))))

(defmigration add-teams-table
  (up [] (create
          (tbl :teams
               (varchar :name 255))))
  (down [] (drop (table :teams))))

(defmigration add-positions-table
  (up [] (create
          (tbl :positions
               (text :description)
               (varchar :name 255))))
  (down [] (drop (table :positions))))


(defmigration add-players-table
  (up [] (create
          (tbl :players
               (refer-to :teams)
               (refer-to :positions)
               (varchar :name 255))))
  (down [] (drop (table :players))))
