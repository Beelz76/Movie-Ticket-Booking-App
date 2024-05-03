<?php
require "../DataBase.php";
require "Movie.php";

$db = new DataBase();
$movie = new Movie($db);

if (!$db->dbConnect()) {
    echo "Error: Database connection";
    exit;
}

try {
    $movies = $movie->getMovies();

    if ($movies) {
        echo $movies;
    } else {
        echo "Get Movies Failed";
    }
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}