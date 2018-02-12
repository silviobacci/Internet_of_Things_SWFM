<?php

// -------------------------
// CRASH.PHP
// It contains PHP code to
// fill up crash cards 
// and calendar
// -----------------------

// -------------------------------------
// REQUIRE
// -------------------------------------
require_once "assets/dbmanager.php";
require_once "assets/session.php";
require_once "assets/jsonresponse.php";

// start the current session
session_start();

// if user is not logged launch error and die
logged_user_check();

if(isset($_POST['init']) && isset($_POST['last']))
    retrieve_crash_dates($_POST['init'], $_POST['last']);
else if(isset($_POST['date']))
    get_crashes_info($_POST['date']);
else
    launch_error("Wrong data sent to server.");

// -------------------------------------
// UTILITY
// -------------------------------------

// Check if user is already logged in
function logged_user_check() {
    if(!is_user_logged())
        launch_error("Not yet logged.");        
}

// -------------------------------------
// GET CRASH DATE LIST
// -------------------------------------

// Retrieve and send crash dates to client
function retrieve_crash_dates($init_date, $last_date) {
    global $my_database;

    $query_string = "SELECT DATE_FORMAT(crashtime, '%m/%d/%Y')
                     FROM (`swfm_trip` AS t INNER JOIN `swfm_crash` AS c ON t.`id` = c.`id`)
                     WHERE `email` = '" . get_user_email() . "'";
        
    $my_result = $my_database->send_query($query_string);
    
    if ($my_result->num_rows == 0)
        launch_error("No crashes found.");
    
    send_crash_dates($my_result);
}

// -------------------------------------
// GET CRASH INFO
// -------------------------------------

function get_crashes_info($date) {
    global $my_database;

    $query_string = "SELECT DATE_FORMAT(crashtime, '%m/%d/%Y - %H:%i:%s') as  crashtime, intensity, stationary
                     FROM (`swfm_trip` AS t INNER JOIN `swfm_crash` AS c ON t.`id` = c.`id`)
                     WHERE `email` = '" . get_user_email() . "'
                     AND DATE_FORMAT(crashtime, '%m/%d/%Y') = '" . $date . "'
                     ORDER BY crashtime DESC";
    
    $my_result = $my_database->send_query($query_string);

    if ($my_result->num_rows == 0)
        launch_error("No crashes in the selected day.");
    
    send_crash_infos($my_result);
}
