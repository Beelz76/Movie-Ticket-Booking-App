<?php

class DataBaseConfig
{
    public $servername;
    public $login;
    public $password;
    public $databasename;

    public function __construct()
    {
        $this->servername = 'localhost';
        $this->login = 'root';
        $this->password = '';
        $this->databasename = 'cinemadb';
    }
}
