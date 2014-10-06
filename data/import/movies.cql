// Create unique constraints
CREATE CONSTRAINT ON (m:Movie) ASSERT m.id IS UNIQUE;
CREATE CONSTRAINT ON (g:Genre) ASSERT g.id IS UNIQUE;
CREATE CONSTRAINT ON (p:Person) ASSERT p.id IS UNIQUE;
CREATE CONSTRAINT ON (k:Keyword) ASSERT k.id IS UNIQUE;

// Movie titles
LOAD CSV WITH HEADERS FROM "https://raw.githubusercontent.com/neo4j-meetups/clojure-hackathon/master/data/all-movies.csv" AS line

MERGE (m:Movie {id: line.Id})
ON CREATE SET m.title = line.Title, m.releaseDate = line.ReleaseDate;

// Genres
LOAD CSV WITH HEADERS FROM "https://raw.githubusercontent.com/neo4j-meetups/clojure-hackathon/master/data/all-genres.csv" AS line

MATCH (m:Movie {id: line.MovieId})
MERGE (g:Genre {id: line.GenreId})
ON CREATE SET g.name = line.GenreName

MERGE (m)-[:HAS_GENRE]->(g);

// Casts
LOAD CSV WITH HEADERS FROM "https://raw.githubusercontent.com/neo4j-meetups/clojure-hackathon/master/data/all-casts.csv" AS line

MATCH (m:Movie {id: line.MovieId})

MERGE (p:Person {id: line.ActorId})
ON CREATE SET p.name = line.ActorName

MERGE (p)-[:ACTED_IN {roles: line.Character}]->(m);

// take 2
LOAD CSV WITH HEADERS FROM "https://raw.githubusercontent.com/neo4j-meetups/clojure-hackathon/master/data/all-casts.csv" AS line

MATCH (m:Movie {id: line.MovieId})

MERGE (p:Person {id: line.ActorId})
ON CREATE SET p.name = line.ActorName

WITH *

OPTIONAL MATCH (p)-[r:ACTED_IN]->(m)

WITH CASE WHEN r IS NULL THEN [1] ELSE [] END AS addNewRel,
     CASE WHEN NOT r IS NULL THEN [1] ELSE [] END AS updateExistingRel,
     m, p, line

FOREACH(ignoreMe IN addNewRel |
  MERGE (p)-[:ACTED_IN {roles: [line.Character]}]->(m))
FOREACH(ignoreMe IN updateExistingRel |
  MERGE (p)-[r:ACTED_IN]->(m)
  SET r.roles = [r IN r.roles WHERE r <> line.Character] + line.Character);

// Key words
LOAD CSV WITH HEADERS FROM "https://raw.githubusercontent.com/neo4j-meetups/clojure-hackathon/master/data/all-keywords.csv" AS line

MERGE (k:Keyword {id: line.KeyWordId})
ON CREATE SET k.name = line.KeyWord

MERGE (m:Movie {id: line.MovieId})

MERGE (m)-[:HAS_KEYWORD]->(k);
