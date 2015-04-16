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

(defmigration add-labels-table
  (up [] (create
          (tbl :labels
               (varchar :name 255)
               (varchar :country 255))))
  (down [] (drop (table :labels))))

(defmigration add-artists-table
  (up [] (create
          (tbl :artists
               (refer-to :labels)
               (varchar :name 255))))
  (down [] (drop (table :artists))))

(defmigration add-albums-table
  (up [] (create
          (tbl :albums
               (refer-to :artists)
               (refer-to :labels)
               (varchar :name 255)
               (integer :year))))
  (down [] (drop (table :albums))))


(defmigration add-tracks-table
  (up [] (create
          (tbl :tracks
               (refer-to :albums)
               (refer-to :artists)
               (varchar :name 255)
               (integer :number))))
  (down [] (drop (table :tracks))))

(defmigration add-covers-table
  (up [] (create
          (tbl :covers
               (refer-to :albums)
               (varchar :url 255))))
  (down [] (drop (table :covers))))
