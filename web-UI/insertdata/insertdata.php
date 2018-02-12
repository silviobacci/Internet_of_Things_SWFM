<?php
// -------------------------------------
// INSERTDATA.PHP
// permit to insert raw user data in
// SQL database
// -------------------------------------

header('Content-type: application/json');

// -----------------------------------
// JSON OBJECT EXAMPLE
// -----------------------------------
/*
    '{  
        "email" : 
            "user@example.it",
        "trip" : {
            "starttime" : "2018-01-07 07:30:00",
            "secondslength" : 120 
        },
        "stat" : {
            "numberacc" : 2,
            "worstacc" : 5,
            "numberbra" : 1,
            "worstbra" : 3,
            "numbercur" : 8,
            "worstcur" : 4  
        },
        "evaluation" : {
            "pointstotal" : 2,
            "pointsacceleration" : 5,
            "pointsbraking" : 1,
            "pointssteering" : 3
        },
        "crash" : [
            {
                "crashtime" : "2018-01-07 07:30:00",
                "intensity" : 5,
                "stationary" : 0
            },
            {
                "crashtime" : "2018-01-10 10:25:00",
                "intensity" : 3,
                "stationary" : 1
            },
        ]
    }';
*/

// -------------------------------------
// REQUIRE
// -------------------------------------
require_once "assets/dbmanager.php";
require_once "assets/jsonresponse.php";

// -------------------------------------
// PHP SERVER MAIN CODE
//      GET DATA & DECODE
//      DECODED SUCCESSFULL?
//      INSERT TRIP & RETRIEVE ID
//      INSERT STAT
//      INSERT EVALUATION
//      INSERT CRASH (NOT MANDATORY)
//      RETRIEVE RESUME
//      CALCULATE NEW RESUME
//      UPDATE RESUME
//      CLOSE & SUCCESS REPLY
// -------------------------------------

// get data from POST req and decode it
$data_array = json_decode($_POST['content'], true);

// check if decoded json had correct format
check_decoded_json($data_array);

// insert new trip into the db and retrieve the inserted id
$trip_id = insert_trip($data_array['email'], $data_array['trip']);

// insert stats related to last trip
insert_stat($trip_id, $data_array['stat']);

// insert evaluations related to last trip
insert_evaluation($trip_id, $data_array['evaluation']);

// insert crashes related to last trip (if present)
insert_crash($trip_id, $data_array['crash']);

// retrieve total resume point of the user
$resume = get_resume_points($data_array['email']);

// calculate new total resume point of the user
$new_resume = calculate_new_resume($resume, $data_array['trip'], $data_array['evaluation']);

// update new total resume point of the user
update_resume_data($data_array['email'], $new_resume);

// reply to the client
close_and_reply();

// -----------------------------------
// JSON STRING FUNCTIONS
// -----------------------------------

// Check if JSON decoded with success
function check_decoded_json($data_array) {
    if($data_array == null)
        launch_error("JSON arrived to server is not correct.");
}

// -------------------------------------
// DB QUERY FUNCTIONS
// -------------------------------------

// Insert trip data and return the trip id
function insert_trip($email, $trip) {
    global $my_database;

    $query_string = 
        "INSERT INTO `xdr_trip` (`id`, `email`, `starttime`, `secondslength`) 
        VALUES (NULL, 
                '" . $email . "', 
                '" . $trip['starttime'] . "',
                '" . $trip['secondslength'] . "');";

    $my_result = $my_database->send_query($query_string);

    if (!$my_result)
        launch_error("Problem inserting trip data, maybe user is not registered.");

    return $my_id = $my_database->get_last_id();
}

// Insert stat data into DB
function insert_stat($trip_id, $stat) {
    global $my_database;

    $query_string = 
        "INSERT INTO `xdr_stat` (`id`, `numberacc`, `worstacc`, `numberbra`, `worstbra`, `numbercur`, `worstcur`) 
        VALUES ('" . $trip_id . "', 
                '" . $stat['numberacc'] . "', 
                '" . $stat['worstacc'] . "', 
                '" . $stat['numberbra'] . "',
                '" . $stat['worstbra'] . "',
                '" . $stat['numbercur'] . "',
                '" . $stat['worstcur'] . "');";

    $my_result = $my_database->send_query($query_string);

    if (!$my_result)
        launch_error("Problem inserting stat data.");
}

// Insert evaluation data into db
function insert_evaluation($trip_id, $evaluation) {
    global $my_database;

    $query_string = 
        "INSERT INTO `xdr_evaluation` (`id`, `pointstotal`, `pointsacceleration`, `pointsbraking`, `pointssteering`) 
        VALUES ('" . $trip_id . "', 
                '" . $evaluation['pointstotal'] . "', 
                '" . $evaluation['pointsacceleration'] . "', 
                '" . $evaluation['pointsbraking'] . "',
                '" . $evaluation['pointssteering'] . "');";

    $my_result = $my_database->send_query($query_string);

    if (!$my_result)
        launch_error("Problem inserting evaluation data.");
}

// Insert crash data into db if crash data is not null
function insert_crash($trip_id, $crash) {
    global $my_database;

    if($crash == null)
        return;

    $query_string = "INSERT INTO `xdr_crash` (`id`, `crashtime`, `intensity`, `stationary`) VALUES ";

    if(count($crash) == 1) {
        $query_string = $query_string . "('" . $trip_id . "', 
                                        '" . $crash['crashtime'] . "', 
                                        '" . $crash['intensity'] . "', 
                                        '" . $crash['stationary'] . "'), ";
    } else {
        for($i = 0; $i < count($crash); $i++) {
            $query_string = $query_string . "('" . $trip_id . "', 
                                            '" . $crash[$i]['crashtime'] . "', 
                                            '" . $crash[$i]['intensity'] . "', 
                                            '" . $crash[$i]['stationary'] . "'), ";
        }
    }

    $query_string = substr($query_string, 0, -2);
    $query_string = $query_string . ';';

    $my_result = $my_database->send_query($query_string);

    if (!$my_result)
        launch_error("Problem inserting crash data.");
}

// Get all resume info of a user
function get_resume_points($email) {
    global $my_database;

    $query_string = "SELECT * FROM `xdr_user` WHERE `email` = '" . $email . "'";
    $my_result = $my_database->send_query($query_string);
    
    if ($my_result->num_rows != 1)
        launch_error("Read user data failed.");
    
    $resume = $my_result->fetch_array();
    return $resume;
}

// Calculate new resume data starting from new trip and old resume data
function calculate_new_resume($resume, $trip, $evaluation) {
    $driven_hour = $resume['drivenhours'];
    $triplen_hour = $trip['secondslength'] / 3600;
    $total_driven_hour = $driven_hour + $triplen_hour;

    $new_resume['pointstotal'] = ($resume['pointstotal'] * $driven_hour +
                                  $evaluation['pointstotal'] * $triplen_hour) 
                                    / $total_driven_hour;

    $new_resume['pointsacceleration'] = ($resume['pointsacceleration'] * $driven_hour +
                                         $evaluation['pointsacceleration'] * $triplen_hour) 
                                            / $total_driven_hour;
    
    $new_resume['pointsbraking'] = ($resume['pointsbraking'] * $driven_hour +
                                    $evaluation['pointsbraking'] * $triplen_hour) 
                                        / $total_driven_hour;

    $new_resume['pointssteering'] = ($resume['pointssteering'] * $driven_hour +
                                     $evaluation['pointssteering'] * $triplen_hour) 
                                        / $total_driven_hour;

    $new_resume['drivenhours'] = $total_driven_hour;

    return $new_resume;
}

// Update resume data of user
function update_resume_data($email, $new_resume) {
    global $my_database;

    $query_string = "UPDATE `xdr_resume` SET 
                        `pointstotal` = '" . $new_resume['pointstotal'] . "', 
                        `pointsacceleration` = '" . $new_resume['pointsacceleration'] . "',
                        `pointsbraking` = '" . $new_resume['pointsbraking'] . "',
                        `pointssteering` = '" . $new_resume['pointssteering'] . "',
                        `drivenhours` = '" . $new_resume['drivenhours'] . "'
                    WHERE `xdr_resume`.`email` = '" . $email . "';";
    
    $my_result = $my_database->send_query($query_string);

    if (!$my_result)
        launch_error("Problem updating resume data.");
}

// Reply to script caller & close the connection with db
function close_and_reply() {
    launch_response("Data inserted with success!");
    $my_database->close_connection();
}
