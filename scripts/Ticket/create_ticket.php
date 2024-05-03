<?php
require "../DataBase.php";
require "Ticket.php";

$db = new DataBase();
$ticket = new Ticket($db);

if (!isset($_POST['UserId']) && !isset($_POST['ScreeningId']) && !isset($_POST['SeatId'])) {
    echo "All fields are required";
    exit;
}

if (!$db->dbConnect()) {
    echo "Error: Database connection";
    exit;
}

try {
    $userId = $_POST['UserId'];
    $screeningId = $_POST['ScreeningId'];
    $seatId = $_POST['SeatId'];

    if ($ticket->isSeatTaken($screeningId, $seatId)) {
        echo "Seat Already Taken";
        exit;
    }

    if ($ticket->createTicket("ticket", $userId, $screeningId, $seatId)) {
        echo "Create Ticket Success";
    } else {
        echo "Create Ticket Failed";
    }
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}