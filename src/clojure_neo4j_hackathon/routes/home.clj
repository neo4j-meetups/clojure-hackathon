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
  (->> (cy/tquery conn all-movies-query {:limit limit})
                    walk/keywordize-keys))

(defn home-page []
  (let [result (get-movies 50)]
    (layout/render
     "home.html"
     {:result (map  #(->> % :m :data) result) })))

(defn about-page []
  (layout/render "about.html"))

(def movie-query "MATCH (m:Movie {id: {id}})
                  RETURN m")

(defn get-movie [movie-id]
  (->> (cy/tquery conn movie-query {:id movie-id})
       walk/keywordize-keys))

(defn movies-page [movie-id]
  (layout/render "movies.html"
                 {:result (first (map #(->> % :m :data) (get-movie movie-id))) }))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/movies/:id" [id] (movies-page id))
  (GET "/about" [] (about-page)))
