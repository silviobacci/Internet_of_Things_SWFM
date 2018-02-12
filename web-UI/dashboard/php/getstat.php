<?php

// -------------------------
// STAT.PHP
// It contains PHP code to
// fill up stat cards 
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
//logged_user_check();

if(isset($_POST['init']) && isset($_POST['last']))
    retrieve_stats_dates($_POST['init'], $_POST['last']);
else if(isset($_POST['date']))
    get_stat_list($_POST['date']);
else if(isset($_POST['trip_id']))
    get_stat_info($_POST['trip_id']);
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
// GET STAT DATE LIST
// -------------------------------------

// Retrieve and send crash dates to client
function retrieve_stats_dates($init_date, $last_date) {
    global $my_database;

    $query_string = "SELECT DATE_FORMAT(starttime, '%m/%d/%Y') as date
                     FROM (`swfm_trip` AS t INNER JOIN `swfm_stat` AS c ON t.`id` = c.`id`)
                     WHERE `email` = '" . get_user_email() . "'
                     ORDER BY `date` ASC;";
        
    $my_result = $my_database->send_query($query_string);
    
    if ($my_result->num_rows == 0)
        launch_error("No stats found.");
    
    send_stats_dates($my_result);
}

// -------------------------------------
// GET STAT LIST PER DATE
// -------------------------------------

function get_stat_list($date) {
    global $my_database;

    $query_string = "SELECT t.id, DATE_FORMAT(starttime, '%H:%i') as  triptime
                     FROM (`swfm_trip` AS t INNER JOIN `swfm_stat` AS s ON t.`id` = s.`id`)
                     WHERE `email` = '" . get_user_email() . "'
                     AND DATE_FORMAT(starttime, '%m/%d/%Y') = '" . $date . "'
                     ORDER BY triptime DESC";
    
    $my_result = $my_database->send_query($query_string);

    if ($my_result->num_rows == 0)
        launch_error("No stats in the selected day.");
    
    send_stats_list($my_result);
}

// -------------------------------------
// GET STAT INFO PER ID
// -------------------------------------

function get_stat_info($trip_id) {
    global $my_database;

    $query_string = "SELECT *
                     FROM `swfm_stat`
                     WHERE `id` = '" . $trip_id . "'";
    
    $my_result = $my_database->send_query($query_string);

    if ($my_result->num_rows == 0)
        launch_error("No stats related to the id.");
    
    send_stat_info($my_result->fetch_array());
}
