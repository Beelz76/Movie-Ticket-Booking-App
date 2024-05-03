<?php
require "../DataBase.php";
require "Movie.php";

$db = new DataBase();
$movie = new Movie($db);

if ($db->dbConnect()) {
    $movies = $movie->getMovies();
    if ($movies) {
        echo $movies;
    } else echo "Get Movies Failed";
} else echo "Error: Database connection";
