<?php
// -------------------------------------
// JSONRESPONSE.PHP
// contains php function to echo
// a JSON response
// -------------------------------------

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

?>