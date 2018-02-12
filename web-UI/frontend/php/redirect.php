<?php
// -------------------------------------
// REDIRECT.PHP
// contains php script to redirect
// already logged user server
// -------------------------------------

// -------------------------------------
// REQUIRE
// -------------------------------------
require_once "assets/session.php";
require_once "assets/jsonresponse.php";

// start the current session
session_start();

// if user is already logged, return a JSON object and die
logged_user_check();

// Check if user is already logged in
function logged_user_check() {
    if(is_user_logged())
        launch_response("Login already done.");
    else
        launch_error("Not yet logged.");
}
