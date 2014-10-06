require 'open-uri'
require 'json'
require 'csv'

API_KEY = ENV['MOVIE_DB_KEY']

CSV.open("all-crews.csv", 'wb') do |f|
  f << ["MovieId", "CrewId", "Name", "Role"]
end

CSV.foreach("all-movies.csv", :headers => true) do  |row|
  movie_id = row[1]
  content = open("http://api.themoviedb.org/3/movie/#{movie_id}?api_key=#{API_KEY}&append_to_response=casts").read
  result = JSON.parse(content)

  CSV.open("all-crews.csv", 'ab') do |f|
    result["casts"]["crew"].select { |row| ["Producer", "Director"].include? row["job"] }.each do |row|
      f << [movie_id, row["id"], row["name"], row["job"]]
    end
  end
end
