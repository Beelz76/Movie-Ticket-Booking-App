<?php
require "DataBaseConfig.php";

class DataBase
{
    public $connect;
    public $data;
    public $sql;
    protected $servername;
    protected $login;
    protected $password;
    protected $databasename;

    public function __construct()
    {
        $this->connect = null;
        $this->data = null;
        $this->sql = null;
        $dbc = new DataBaseConfig();
        $this->servername = $dbc->servername;
        $this->login = $dbc->login;
        $this->password = $dbc->password;
        $this->databasename = $dbc->databasename;
    }

    public function dbConnect()
    {
        $this->connect = mysqli_connect($this->servername, $this->login, $this->password, $this->databasename);
        return $this->connect;
    }

    public function prepareData($data)
    {
        return mysqli_real_escape_string($this->connect, stripslashes(htmlspecialchars($data)));
    }
}

