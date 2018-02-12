<?php

// -----------------------------
// GETEVALUATION.PHP
// It contains PHP code to
// fill up evaluation page cards 
// -----------------------------

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

// retrieve all evaluation data
retrieve_evaluation_data();

// -------------------------------------
// UTILITY
// -------------------------------------

// Check if user is already logged in
function logged_user_check() {
    if(!is_user_logged())
        launch_error("Not yet logged.");        
}

// -------------------------------------
// GET EVALUATION DATE LIST
// -------------------------------------

// Retrieve and send crash dates to client
function retrieve_evaluation_data() {
    global $my_database;

    $query_string = "SELECT *
                    FROM `swfm_trip` INNER JOIN `swfm_evaluation` ON (`swfm_trip`.`id` = `swfm_evaluation`.`id`)
                    WHERE `email` = '" . get_user_email() . "'
                    ORDER BY `swfm_trip`.`starttime` ASC
                    LIMIT 0,10";
    
    $my_result = $my_database->send_query($query_string);
    
    if ($my_result->num_rows == 0)
        launch_error("No trip found.");

    send_evaluation_data($my_result);
}
