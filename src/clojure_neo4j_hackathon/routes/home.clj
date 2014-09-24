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
    (-> (cy/tquery conn all-movies-query {:limit limit})
        walk/keywordize-keys))

(defn home-page []
  (let [result (get-movies 50)]
    (layout/render
     "home.html"
     {:result (map  #(-> % :m :data) result) })))

(defn about-page []
  (layout/render "about.html"))

(def movie-query "MATCH (m:Movie {id: {id}})<-[r:ACTED_IN]-(a)
                  WITH m, COLLECT({actor: a.name, role: r.role}) AS actors
                  MATCH m-[:HAS_KEYWORD]->(k)
                  WITH m, actors, COLLECT(k.name) as keywords
                  MATCH m-[:HAS_GENRE]->(g)
                  RETURN m, actors, keywords, COLLECT(g.name) as genres")


(defn get-movie [movie-id]
  (-> (cy/tquery conn movie-query {:id movie-id})
       walk/keywordize-keys))

(defn movies-page [movie-id]
  (let [[result] (get-movie movie-id)
        title    (get-in result [:m :data :title])
        release  (get-in result [:m :data :releaseDate])
        actors   (:actors result)
        keywords (:keywords result)
        genres   (:genres result)]
    (layout/render "movies.html"
                   {:title     title
                    :release   release
                    :actors    actors
                    :genres    genres
                    :keywords  keywords})))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/movies/:id" [id] (movies-page id))
  (GET "/about" [] (about-page)))
