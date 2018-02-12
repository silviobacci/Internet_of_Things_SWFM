<?php
// -------------------------------------
// JSONRESPONSE.PHP
// contains php function to echo
// a JSON response
// -------------------------------------

// ----------------------
// USED IN: ALL PAGES
// ----------------------

// Retrieve a JSON object with user data
function send_user_data($username, $name, $surname, $avatar, $cover, $admin) {
    echo '{  
        "error" : false, 
        "message" : { 
            "username" : "' . $username . '",
            "name" : "' . $name . '",
            "surname" : "' . $surname . '",
            "avatar" : "' . $avatar . '",
            "cover" : "' . $cover . '",
			"admin" : "' . $admin . '"}
        }';
    exit(0);
}

// Retrieve a JSON object with an error message
function launch_error($message) {
    echo '{ "error" : true, "message" : "' . $message . '" }';
    die(-1);
}

// Retrieve a JSON object with a success message
function launch_response($message) {
    echo '{ "error" : false, "message" : "' . $message .'" }';
    exit(0);
}

// ----------------------
// USED IN: HOME
// ----------------------

// Retrieve a JSON object with last trip data
function send_trip_info($query_result) {
    echo '{  
        "error" : false, 
        "lasttripdata" : { 
            "starttime" : "' . $query_result['starttime'] . '",
            "secondslength" : "' . $query_result['secondslength'] . '",
            "pointstotal" : "' . $query_result['pointstotal'] . '",
            "pointsacceleration" : "' . $query_result['pointsacceleration'] . '",
            "pointsbraking" : "' . $query_result['pointsbraking'] . '",
            "pointssteering" : "' . $query_result['pointssteering'] . '"}
        }';
    exit(0);
}

// ----------------------
// USED IN: EVALUATION
// ----------------------

// Retrieve a JSON object with last trip data
function send_evaluation_data($query_result) {
    $response_str = 
    '{
        "error" : false, 
        "evaluationdata": [';

    for($i = 0; $i < mysqli_num_rows($query_result); $i++) {
        $row = $query_result->fetch_array();
        $response_str = $response_str .
            '{
                "number" : ' . ($i+1) . ',
                "starttime" : "' . $row['starttime'] . '",
                "pointstotal" : "' . $row['pointstotal'] . '",
                "pointsacceleration" : "' . $row['pointsacceleration'] . '",
                "pointsbraking" : "' . $row['pointsbraking'] . '",
                "pointssteering" : "' . $row['pointssteering'] . '"
            },';
    }
        
    $response_str = substr($response_str, 0, -1);
    $response_str = $response_str . ']}';

    echo $response_str;
    exit(0);
}

// ----------------------
// USED IN: STATS
// ----------------------

// Retrieve a JSON object with stats data
function send_stats_dates($query_result) {
    $response_str = 
    '{
        "error" : false, 
        "statsdates": [';

    for($i = 0; $i < mysqli_num_rows($query_result); $i++) {
        $row = $query_result->fetch_array();
        $response_str = $response_str . '"' . $row[0] . '",';
    }
        
    $response_str = substr($response_str, 0, -1);
    $response_str = $response_str . ']}';

    echo $response_str;
    exit(0);
}

// Retrieve a JSON object with stat id and time
function send_stats_list($query_result) {
    $response_str = 
    '{
        "error" : false, 
        "statlist": [';

    for($i = 0; $i < mysqli_num_rows($query_result); $i++) {
        $row = $query_result->fetch_array();
        $response_str = $response_str .
            '{
                "number" : ' . ($i+1) . ',
                "id" : "' . $row['id'] . '",
                "time" : "' . $row['triptime'] . '"
            },';
    }
        
    $response_str = substr($response_str, 0, -1);
    $response_str = $response_str . ']}';

    echo $response_str;
    exit(0);
}

// Retrieve a JSON object with stat data
function send_stat_info($query_result) {
    echo '{  
        "error" : false, 
        "statdata" : { 
            "numberacc" : "' . $query_result['numberacc'] . '",
            "worstacc" : "' . $query_result['worstacc'] . '",
            "numberbra" : "' . $query_result['numberbra'] . '",
            "worstbra" : "' . $query_result['worstbra'] . '",
            "numbercur" : "' . $query_result['numbercur'] . '",
            "worstcur" : "' . $query_result['worstcur'] . '"}
        }';
    exit(0);
}

// ----------------------
// USED IN: CRASH
// ----------------------

// Retrieve a JSON object with crashes data
function send_crash_dates($query_result) {
    $response_str = 
    '{
        "error" : false, 
        "crashdates": [';

    for($i = 0; $i < mysqli_num_rows($query_result); $i++) {
        $row = $query_result->fetch_array();
        $response_str = $response_str . '"' . $row[0] . '",';
    }
        
    $response_str = substr($response_str, 0, -1);
    $response_str = $response_str . ']}';

    echo $response_str;
    exit(0);
}

// Retrieve a JSON object with crashes data
function send_crash_infos($query_result) {
    $response_str = 
    '{
        "error" : false, 
        "crashinfos": [';

    for($i = 0; $i < mysqli_num_rows($query_result); $i++) {
        $row = $query_result->fetch_array();
        $response_str = $response_str .
            '{
                "number" : ' . ($i+1) . ',
                "crashtime" : "' . $row['crashtime'] . '",
                "intensity" : "' . $row['intensity'] . '",
                "stationary" : ' . $row['stationary'] . '
            },';
    }
        
    $response_str = substr($response_str, 0, -1);
    $response_str = $response_str . ']}';

    echo $response_str;
    exit(0);
}

?>
