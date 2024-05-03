<?php
class User
{
    private $db;

    public function __construct($db)
    {
        $this->db = $db;
    }

    public function logIn($table, $login, $password)
    {
        $login = $this->db->prepareData($login);
        $password = $this->db->prepareData($password);

        $this->db->sql = "SELECT * FROM " . $table . " WHERE Login = '" . $login . "'";
        $result = mysqli_query($this->db->connect, $this->db->sql);
        $row = mysqli_fetch_assoc($result);

        if (mysqli_num_rows($result) != 0) {
            $dblogin = $row['Login'];
            $dbpassword = $row['Password'];
            if ($dblogin == $login && password_verify($password, $dbpassword)) {
                return true;
            } 
            return false;
        } 

        return false;
    }

    public function signUp($table, $login, $fullname, $email, $password)
    {
        $fullname = $this->db->prepareData($fullname);
        $login = $this->db->prepareData($login);
        $password = $this->db->prepareData($password);
        $email = $this->db->prepareData($email);
        $password = password_hash($password, PASSWORD_DEFAULT);

        $this->db->sql =
            "INSERT INTO " . $table . " (Login, Fullname, Password, Email) 
            VALUES ('" . $login . "','" . $fullname . "','" . $password . "','" . $email . "')";

        if (mysqli_query($this->db->connect, $this->db->sql)) {
            return true;
        } 

        return false;
    }

    public function updateUserInfo($table, $userId, $fullname, $email, $login, $password) {
        $userId = $this->db->prepareData($userId);
        $fullname = $this->db->prepareData($fullname);
        $login = $this->db->prepareData($login);
        $password = $this->db->prepareData($password);
        $email = $this->db->prepareData($email);
        $password = password_hash($password, PASSWORD_DEFAULT);

        $this->db->sql = 
            "UPDATE " . $table . " SET Fullname = '" . $fullname . "', 
            Login = '" . $login . "', Password = '" . $password . "', Email = '" . $email . 
            "' WHERE UserId = '" . $userId . "'";
        
        if (mysqli_query($this->db->connect, $this->db->sql)) {
            return true;
        } 

        return false;
    }

    public function getUserInfo($table, $userId)
    {
        $userId = $this->db->prepareData($userId);

        $this->db->sql = "SELECT * FROM " . $table . " WHERE UserId = '" . $userId . "'";
        $result = mysqli_query($this->db->connect, $this->db->sql);
        $row = mysqli_fetch_assoc($result);

        if (mysqli_num_rows($result) != 0) {
            return json_encode($row);
        }

        return null;
    }

    public function getUserIdByLogin($table, $login)
    {
        $login = $this->db->prepareData($login);

        $this->db->sql = "SELECT * FROM " . $table . " WHERE Login = '" . $login . "'";
        $result = mysqli_query($this->db->connect, $this->db->sql);
        $row = mysqli_fetch_assoc($result);

        if (mysqli_num_rows($result) != 0) {
            $userId = $row['UserId'];
            return $userId;
        } 

        return null;
    } 
}
