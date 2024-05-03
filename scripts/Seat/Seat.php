<?php
class Seat
{
    private $db;

    public function __construct($db)
    {
        $this->db = $db;
    }

    public function getScreeningSeats($screeningId) {
        $screeningId = $this->db->prepareData($screeningId);

        $this->db->sql = 
            "SELECT s.SeatId, s.Row, s.Number, 
            CASE WHEN t.TicketId IS NOT NULL THEN 1 ELSE 0 END AS IsTaken 
            FROM Seat s
            LEFT JOIN Ticket t ON s.SeatId = t.SeatId AND t.ScreeningId = '" . $screeningId . "'
            WHERE s.HallId = (SELECT HallId FROM Screening WHERE ScreeningId = '" . $screeningId . "')
            ORDER BY s.SeatId";

        $result = mysqli_query($this->db->connect, $this->db->sql);
        $seats = array();
    
        while($row = mysqli_fetch_assoc($result)) {
            $seats[] = $row;
        }
    
        if (!empty($seats)) {
            return json_encode($seats);
        }
                
        return null;
    }
    
}