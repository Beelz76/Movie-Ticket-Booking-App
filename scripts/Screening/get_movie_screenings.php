<?php
require "../DataBase.php";
require "Screening.php";

$db = new DataBase();
$screening = new Screening($db);

if (isset($_POST['MovieId'])) {
    if ($db->dbConnect()) {
        $screenings = $screening->getMovieScreenings($_POST['MovieId']);
        if ($screenings) {
            echo $screenings;
        } else echo "Get Movie Screenings Failed";
    } else echo "Error: Database connection";
} else echo "MovieId is required";
