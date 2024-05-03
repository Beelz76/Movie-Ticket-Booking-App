<?php
require "../DataBase.php";
require "Ticket.php";

$db = new DataBase();
$ticket = new Ticket($db);

if (!isset($_POST['UserId'])) {
    echo "UserId is required";
    exit;
}

if (!$db->dbConnect()) {
    echo "Error: Database connection";
    exit;
}

try {
    $userId = $_POST['UserId'];
    $tickets = $ticket->getUserTickets($userId);

    if ($tickets) {
        echo $tickets;
    } else {
        echo "Get User Tickets Failed";
    }
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}