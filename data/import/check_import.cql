match (a)-[r:ACTED_IN]->(m)
WITH  length(r.roles) as numberOfRoles, a,m, r
WHERE numberOfRoles > 1
RETURN a, m, r
