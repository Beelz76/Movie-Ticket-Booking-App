<?php
require "../DataBase.php";
require "Ticket.php";

$db = new DataBase();
$ticket = new Ticket($db);

if (isset($_POST['TicketId'])) {
    if ($db->dbConnect()) {
        if ($ticket->deleteTicket("ticket", $_POST['TicketId'])) {
            echo "Delete Ticket Success";
        } else echo "Delete Ticket Failed";
    } else echo "Error: Database connection";
} else echo "TicketId is required";
