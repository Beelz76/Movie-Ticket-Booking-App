<?php
require "../DataBase.php";
require "User.php";

$db = new DataBase();
$user = new User($db);

if (!isset($_POST['Fullname']) || !isset($_POST['Login']) || !isset($_POST['Password'])) {
    echo "All fields are required";
    exit;
}

if (!$db->dbConnect()) {
    echo "Error: Database connection";
    exit;
}

try {
    $fullname = $_POST['Fullname'];
    $login = $_POST['Login'];
    $password = $_POST['Password'];
    $email =  $_POST['Email'];

    if (strlen($login) < 4) {
        echo "Login must be at least 4 characters long";
        exit;
    } else {
        if ($user->signUp("user", $login, $fullname, $email, $password)) {
            $userId = $user->getUserIdByLogin("user", $login);
            echo $userId . " Sign Up Success";
        } else {
            echo "Sign up Failed";
        }
    }
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
