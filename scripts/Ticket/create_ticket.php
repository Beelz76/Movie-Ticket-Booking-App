<?php
require "../DataBase.php";
require "Ticket.php";

$db = new DataBase();
$ticket = new Ticket($db);

if (isset($_POST['UserId']) && isset($_POST['ScreeningId']) && isset($_POST['SeatId'])) {
    if ($db->dbConnect()) {
        if(!$ticket->isSeatTaken( $_POST['ScreeningId'], $_POST['SeatId'])) {
            if ($ticket->createTicket("ticket", $_POST['UserId'], $_POST['ScreeningId'], $_POST['SeatId'])) {
                echo "Create Ticket Success";
            } else echo "Create Ticket Failed";
        } else echo "Seat Already Taken";
    } else echo "Error: Database connection";
} else echo "All fields are required";