<?php
require "../DataBase.php";
require "Ticket.php";

$db = new DataBase();
$ticket = new Ticket($db);

if (isset($_POST['UserId'])) {
    if ($db->dbConnect()) {
        $tickets = $ticket->getUserTickets($_POST['UserId']);
        if ($tickets) {
            echo $tickets;
        } else echo "Get User Tickets Failed";
    } else echo "Error: Database connection";
} else echo "UserId is required";
