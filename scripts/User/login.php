<?php
require "../DataBase.php";
require "User.php";

$db = new DataBase();
$user = new User($db);

if (!isset($_POST['Login']) || !isset($_POST['Password'])) {
    echo "All fields are required";
    exit;
}

if (!$db->dbConnect()) {
    echo "Error: Database connection";
    exit;
}

try {
    $login = $_POST['Login'];
    $password = $_POST['Password'];

    if ($user->logIn("user", $login, $password)) {
        $userId = $user->getUserIdByLogin("user", $login);
        echo $userId . " Login Success";
    } else {
        echo "Username or Password wrong";
    }
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
