<?php
require "../DataBase.php";
require "User.php";

$db = new DataBase();
$user = new User($db);

if (!isset($_POST['UserId'])) {
    echo "UserId is required";
    exit;
}

if (!$db->dbConnect()) {
    echo "Error: Database connection";
    exit;
}

try {
    $userId = $_POST['UserId'];
    $userInfo = $user->getUserInfo("user", $userId);

    if ($userInfo) {
        echo $userInfo . " Get User Info Success";
    } else {
        echo "Get User Info Failed";
    }
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}