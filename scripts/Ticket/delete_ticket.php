<?php
require "../DataBase.php";
require "Ticket.php";

$db = new DataBase();
$ticket = new Ticket($db);

if (!isset($_POST['TicketId'])) {
    echo "TicketId is required";
    exit;
}

if (!$db->dbConnect()) {
    echo "Error: Database connection";
    exit;
}

try {
    $ticketId = $_POST['TicketId'];

    if ($ticket->deleteTicket("ticket", $ticketId)) {
        echo "Delete Ticket Success";
    } else { 
        echo "Delete Ticket Failed";
    }
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
