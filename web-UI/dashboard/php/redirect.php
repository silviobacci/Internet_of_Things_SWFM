<?php
// -------------------------------------
// REDIRECT.PHP
// contains php script to redirect
// not logged user
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
        send_user_data(
            get_user_username(),
            get_user_name(),
            get_user_surname(),
            get_user_avatar(),
            get_user_cover(),
			get_user_admin()
        );
    else
        launch_error("Not yet logged.");
}
