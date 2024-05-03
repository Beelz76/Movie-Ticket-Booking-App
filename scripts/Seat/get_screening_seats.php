<?php
require "../DataBase.php";
require "Seat.php";

$db = new DataBase();
$seat = new Seat($db);

if (!isset($_POST['ScreeningId'])) {
    echo "ScreeningId is required";
    exit;
}

if (!$db->dbConnect()) {
    echo "Error: Database connection";
    exit;
}

try {
    $screeningId = $_POST['ScreeningId'];
    $seats = $seat->getScreeningSeats($screeningId);
        
    if ($seats) {
        echo $seats;
    } else {
        echo "Get Screening Seats Failed";
    }
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
