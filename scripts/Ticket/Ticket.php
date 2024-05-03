<?php
class Ticket
{
    private $db;

    public function __construct($db)
    {
        $this->db = $db;
    }

    public function createTicket($table, $userId, $screeningId, $seatId) {
        $userId = $this->db->prepareData($userId);
        $screeningId = $this->db->prepareData($screeningId);
        $seatId = $this->db->prepareData($seatId);

        $this->db->sql =
            "INSERT INTO " . $table . " (UserId, ScreeningId, SeatId) 
            VALUES ('" . $userId . "','" . $screeningId . "','" . $seatId . "')";

        if (mysqli_query($this->db->connect, $this->db->sql)) {
            return true;
        } 

        return false;
    }

    public function deleteTicket($table, $ticketId) {
        $ticketId = $this->db->prepareData($ticketId);

        $this->db->sql = "DELETE FROM " . $table . " WHERE ticketId = '" . $ticketId . "'";

        if (mysqli_query($this->db->connect, $this->db->sql)) {
            return true;
        }

        return false;
    }

    public function getUserTickets($userId) {
        $userId = $this->db->prepareData($userId);

        $this->db->sql = 
            "SELECT  t.TicketId, m.Title AS MovieTitle, m.Image AS MovieImage, DATE(s.ScreeningStart) AS Date, 
            TIME(s.ScreeningStart) AS StartTime, TIME(s.ScreeningEnd) AS EndTime, sp.Price AS Price, 
            h.Name AS HallName, se.Row AS SeatRow, se.Number AS SeatNumber 
            FROM Ticket t 
            INNER JOIN Screening s ON t.ScreeningId = s.ScreeningId 
            INNER JOIN Movie m ON s.MovieId = m.MovieId 
            INNER JOIN Hall h ON s.HallId = h.HallId 
            INNER JOIN ScreeningPrice sp ON s.ScreeningPriceId = sp.ScreeningPriceId 
            INNER JOIN Seat se ON t.SeatId = se.SeatId 
            WHERE t.UserId = '" . $userId . "'";

        $result = mysqli_query($this->db->connect, $this->db->sql);
        $tickets = array();

        while($row = mysqli_fetch_assoc($result)) {
            $tickets[] = $row;
        }

        if (!empty($tickets)) {
            return json_encode($tickets);
        }
            
        return null;
    }

    public function isSeatTaken($screeningId, $seatId) {
        $screeningId = $this->db->prepareData($screeningId);
        $seatId = $this->db->prepareData($seatId);

        $this->db->sql = "SELECT * FROM Ticket WHERE ScreeningId = '" . $screeningId . "' AND SeatId = '" . $seatId . "'";
        $result = mysqli_query($this->db->connect, $this->db->sql);

        if (mysqli_num_rows($result) > 0) {
            return true;
        } 

        return false;
    }
}