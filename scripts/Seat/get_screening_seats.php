<?php
require "../DataBase.php";
require "Seat.php";

$db = new DataBase();
$seat = new Seat($db);

if (isset($_POST['ScreeningId'])) {
    if ($db->dbConnect()) {
        $seats = $seat->getScreeningSeats($_POST['ScreeningId']);
        if ($seats) {
            echo $seats;
        } else echo "Get Screening Seats Failed";
    } else echo "Error: Database connection";
} else echo "ScreeningId is required";
