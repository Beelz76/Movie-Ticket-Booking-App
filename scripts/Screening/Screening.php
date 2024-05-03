<?php
class Screening
{
    private $db;

    public function __construct($db)
    {
        $this->db = $db;
    }

    public function getMovieScreenings($movieId) {
        $movieId = $this->db->prepareData($movieId);

        $this->db->sql = 
            "SELECT Screening.ScreeningId, DATE(Screening.ScreeningStart) AS Date, 
            TIME(Screening.ScreeningStart) AS StartTime, TIME(Screening.ScreeningEnd) AS EndTime, 
            Hall.Name AS HallName, ScreeningPrice.Price 
            FROM Screening 
            INNER JOIN Hall ON Screening.HallId = Hall.HallId 
            INNER JOIN ScreeningPrice ON Screening.ScreeningPriceId = ScreeningPrice.ScreeningPriceId 
            WHERE Screening.MovieId = '" . $movieId . "'
            ORDER BY Date, StartTime";

        $result = mysqli_query($this->db->connect, $this->db->sql);
        $screenings = array();

        while($row = mysqli_fetch_assoc($result)) {
            $screenings[] = $row;
        }

        if (!empty($screenings)) {
            return json_encode($screenings);
        }
    
        return null;
    }
    
}