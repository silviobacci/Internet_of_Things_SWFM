<?php
// -------------------------------------
// LOGIN.PHP
// contains php script to add user in DB
// and validate data
// -------------------------------------

// -------------------------------------
// REQUIRE
// -------------------------------------
require_once "assets/dbmanager.php";
require_once "assets/session.php";
require_once "assets/jsonresponse.php";
	
session_start();

// sanitize input to prevent code injection and try to strip HTML tags
$data = filter_field();

// encrypt password
encrypt_password($data);

// authenticate and get user data
$user = authenticate($data['username'], $data['password']);

// set session array
session_data_init($user);

// login successfull
launch_response("Login completed.");

// -------------------------------------
// DATA FILTER/VALIDATE FUNCTIONS
// -------------------------------------

// Try to filter data to prevent code injection
function filter_field() {
    if(!isset($_POST['username']) || !isset($_POST['password']))
        launch_error("You have left a field empty.");

    $data['username'] = addslashes(filter_var($_POST['username'], FILTER_SANITIZE_STRING));
    $data['password'] = addslashes(strip_tags($_POST['password']));

    foreach ($data as $key => $value) {
        if($value != null && $value === false)
            launch_error("Problem with your data, try changing them.");
    }

    return $data;
}

// -------------------------------------
// PASSWORD/ENCRYPTION FUNCTIONS
// -------------------------------------

// Encrypt the password with a WEAK md5 algorithm
function encrypt_password(&$data) {
    $data['password'] = md5($data['password']);
}

// -------------------------------------
// PHP SESSION FUNCTIONS
// -------------------------------------

function session_data_init(&$user) {
    set_session_array(
        $user['email'],
        $user['username'], 
        $user['name'], 
        $user['surname'],
        $user['avatar'], 
        $user['cover'],
		$user['admin']);
}

// -------------------------------------
// DB FUNCTIONS
// -------------------------------------

// Permit to authenticate user with username and password
function authenticate($username, $password) {   
    global $my_database;

    $query_string = "SELECT * FROM `swfm_user` WHERE username = '" . $username . "' AND password='" . $password . "'";
    $my_result = $my_database->send_query($query_string);
    
    if ($my_result->num_rows != 1)
        launch_error("Authentication failed.");
    
    $user = $my_result->fetch_assoc();
    $my_database->close_connection();
    return $user;
}

?>
