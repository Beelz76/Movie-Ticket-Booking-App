<?php
require "../DataBase.php";
require "User.php";

$db = new DataBase();
$user = new User($db);

if (isset($_POST['Login']) && isset($_POST['Password'])) {
    if ($db->dbConnect()) {
        if ($user->logIn("user", $_POST['Login'], $_POST['Password'])) {
            $userId = $user->getUserIdByLogin("user", $_POST['Login']);
            echo $userId . " Login Success";
        } else echo "Username or Password wrong";
    } else echo "Error: Database connection";
} else echo "All fields are required";
