<?php
class Movie
{
    private $db;

    public function __construct($db)
    {
        $this->db = $db;
    }

    public function getMovies() {
        $this->db->sql = 
            "SELECT m.MovieId, m.Title, m.ReleaseYear, m.Duration, m.Description, m.Image, 
            GROUP_CONCAT(DISTINCT d.Fullname SEPARATOR ', ') AS Directors, 
            GROUP_CONCAT(DISTINCT g.Name SEPARATOR ', ') AS Genres, 
            GROUP_CONCAT(DISTINCT c.Name SEPARATOR ', ') AS Countries
            FROM Movie m
            LEFT JOIN MovieDirector md ON m.MovieId = md.MovieId
            LEFT JOIN Director d ON md.DirectorId = d.DirectorId
            LEFT JOIN MovieGenre mg ON m.MovieId = mg.MovieId
            LEFT JOIN Genre g ON mg.GenreId = g.GenreId
            LEFT JOIN MovieCountry mc ON m.MovieId = mc.MovieId
            LEFT JOIN Country c ON mc.CountryId = c.CountryId
            GROUP BY m.MovieId";
        
        $result = mysqli_query($this->db->connect, $this->db->sql);
        $movies = array();

        while($row = mysqli_fetch_assoc($result)) {
            $movies[] = $row;
        }

        if (!empty($movies)) {
            return json_encode($movies);
        }

        return null;
    }
}
