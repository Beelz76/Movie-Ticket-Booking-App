<?php
require "../DataBase.php";
require "Screening.php";

$db = new DataBase();
$screening = new Screening($db);

if (!isset($_POST['MovieId'])) {
    echo "MovieId is required";
    exit;
}

if (!$db->dbConnect()) {
    echo "Error: Database connection";
    exit;
}

try {
    $movieId = $_POST['MovieId'];
    $screenings = $screening->getMovieScreenings($movieId);
        
    if ($screenings) {
        echo $screenings;
    } else {
        echo "Get Movie Screenings Failed";
    }
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}