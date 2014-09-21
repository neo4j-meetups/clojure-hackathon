(ns clojure-neo4j-hackathon.routes.home
  (:require [compojure.core :refer :all]
            [clojurewerkz.neocons.rest        :as nr]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [clojure-neo4j-hackathon.layout :as layout]
            [clojure-neo4j-hackathon.util :as util]
            [clojure.walk :as walk]))

(def conn (nr/connect "http://localhost:7474/db/data/"))

(def all-movies-query "MATCH (m:Movie)
                       RETURN m
                       LIMIT {limit}")

(defn get-movies
  [limit]
  (let [result (->> (cy/tquery conn all-movies-query {:limit limit})
                    walk/keywordize-keys)]
    result))

(defn home-page []
  (let [result (get-movies 50)]
    (println result)
    (layout/render
     "home.html"
     {:result (map  #(->> % :m :data) result) })))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))
