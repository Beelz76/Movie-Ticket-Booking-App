<?php
require "../DataBase.php";
require "User.php";

$db = new DataBase();
$user = new User($db);

if (isset($_POST['UserId'])) {
    if ($db->dbConnect()) {
        $userInfo = $user->getUserInfo("user", $_POST['UserId']);
        if ($userInfo) {
            echo $userInfo . " Get User Info Success";
        } else echo "Get User Info Failed";
    } else echo "Error: Database connection";
} else echo "UserId is required";
