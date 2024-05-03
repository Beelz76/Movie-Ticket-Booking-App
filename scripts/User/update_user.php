<?php
require "../DataBase.php";
require "User.php";

$db = new DataBase();
$user = new User($db);

if (isset($_POST['UserId']) && isset($_POST['Fullname']) && isset($_POST['Login']) && isset($_POST['Password'])) {
    if (strlen($_POST['Login']) < 4) {
        echo "Login must be at least 4 characters long";
        exit;
    }
    if ($db->dbConnect()) {
        if ($user->updateUserInfo("user", $_POST['UserId'], $_POST['Fullname'], $_POST['Email'], $_POST['Login'], $_POST['Password'])) {
            echo "User Update Success";
        } else echo "User Update Failed";
    } else echo "Error: Database connection";
} else echo "All fields are required";
