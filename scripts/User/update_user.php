<?php
require "../DataBase.php";
require "User.php";

$db = new DataBase();
$user = new User($db);

if (!isset($_POST['UserId']) && !isset($_POST['Fullname']) || !isset($_POST['Login']) || !isset($_POST['Password'])) {
    echo "All fields are required";
    exit;
}

if (!$db->dbConnect()) {
    echo "Error: Database connection";
    exit;
}

try {
    $userId = $_POST['UserId'];
    $fullname = $_POST['Fullname'];
    $login = $_POST['Login'];
    $password = $_POST['Password'];

    if (strlen($login) < 4) {
        echo "Login must be at least 4 characters long";
        exit;
    }
    
    if ($user->updateUserInfo("user", $userId, $fullname, $email, $login, $password)) {
        echo "User Update Success";
    } else {
        echo "User Update Failed";
    }
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}