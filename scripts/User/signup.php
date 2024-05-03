<?php
require "../DataBase.php";
require "User.php";

$db = new DataBase();
$user = new User($db);

if (isset($_POST['Fullname']) && isset($_POST['Login']) && isset($_POST['Password'])) {
    if (strlen($_POST['Login']) < 4) {
        echo "Login must be at least 4 characters long";
        exit;
    }
    if ($db->dbConnect()) {
        if ($user->signUp("user", $_POST['Login'], $_POST['Fullname'], $_POST['Email'], $_POST['Password'])) {
            $userId = $user->getUserIdByLogin("user", $_POST['Login']);
            echo $userId . " Sign Up Success";
        } else echo "Sign up Failed";
    } else echo "Error: Database connection";
} else echo "All fields are required";
