<?php
// -------------------------------------
// SIGNUP.PHP
// contains php script to add user in DB
// and validate data
// -------------------------------------

// -------------------------------------
// REQUIRE
// -------------------------------------
require_once "assets/dbmanager.php";
require_once "assets/session.php";
require_once "assets/imagevalidation.php";
require_once "assets/jsonresponse.php";

// start a session to check if user is already logged
session_start();

// if user is already logged, return a JSON object and die
logged_user_check();

// if one or more required field are not set, return a JSON object and die
empty_field_check();

// sanitize input to prevent code injection and try to strip HTML tags
$data = filter_field();

// validate images and upload it
validate_image($data, $_FILES['avatar'], 'avatar');
validate_image($data, $_FILES['cover'], 'cover');
upload_image_to_server($data, 'avatar');
upload_image_to_server($data, 'cover');

// encrypt password
encrypt_password($data);

// send all data to db
send_data_to_db($data);

// sign up successfull
launch_response("Sign up successfull.");


// -------------------------------------
// USER SESSION VALIDATE FUNCTIONS
// -------------------------------------

// Send a query to database and check if an email is already in DB
function is_user_registered() {
    global $my_database;
    
    $query_string = "SELECT * FROM `swfm_user` WHERE `email` = '" . $_POST['email'] . "'";
    $my_result = $my_database->send_query($query_string);
    $my_database->close_connection();
    
    if ($my_result->num_rows == 0)
        launch_error("This mail is already present in our database.");
}

// Check if user is already logged in
function logged_user_check() {
    if(is_user_logged())
        launch_error("You are already logged in.");
}

// -------------------------------------
// DATA FILTER/VALIDATE FUNCTIONS
// -------------------------------------

// Check if all required fields are not empty
function empty_field_check() {
    $all_full =
        isset($_POST['email']) &&
        isset($_POST['username']) &&
        isset($_POST['password']) &&
        isset($_POST['name']) &&
        isset($_POST['surname']);
    
    if(!$all_full)
        launch_error("Some fields are empty.");
}

// Try to filter data to prevent code injection
function filter_field() {
    $data['email'] = addslashes(filter_var($_POST['email'], FILTER_VALIDATE_EMAIL));
    $data['username'] = addslashes(filter_var($_POST['username'], FILTER_SANITIZE_STRING));
    $data['password'] = addslashes(strip_tags($_POST['password']));
    $data['name'] = addslashes(filter_var($_POST['name'], FILTER_SANITIZE_STRING));
    $data['surname'] = addslashes(filter_var($_POST['surname'], FILTER_SANITIZE_STRING));
	if (isset($_POST['admin']))
		$data['admin'] = 1;
	else
		$data['admin'] = 0;
	

    foreach ($data as $key => $value) {
        if($value != null && $value === false)
            launch_error("Problem with your data, try changing them.");
    }

    return $data;
}

// Check if the image is valid one
function validate_image(&$data, &$image, $key) {    
    if(
        $image["error"] != UPLOAD_ERR_NO_FILE &&
        check_real_image($image) &&
        check_image_size($image) && 
        check_image_extension($image))

        $data[$key] = $image;
    else
        $data[$key] = null;

}

// -------------------------------------
// PASSWORD/ENCRYPTION FUNCTIONS
// -------------------------------------

// Encrypt the password with a WEAK md5 algorithm
function encrypt_password(&$data) {
    $data['password'] = md5($data['password']);
}

// -------------------------------------
// IMAGE UPLOAD FUNCTIONS
// -------------------------------------

// Upload an image to server
function upload_image_to_server(&$data, $key) {
    $target_dir = "../../dashboard/img/uploads/";
    $target_file = $key . "-" . $data['username'] . "." . image_get_extension($data[$key]);
    $target_uri = $target_dir . $target_file;

    if(check_image_not_exists($target_uri) && upload_image($data[$key], $target_uri))
        $data[$key] = $target_file;
    else
        $data[$key] = null;
}

// -------------------------------------
// DB FUNCTIONS
// -------------------------------------

function send_data_to_db(&$data) {
    global $my_database;
    
    $columns = "";
    $values = "";

    foreach ($data as $key => $value) {
        if($value != null) {
            $columns = $columns . " `" . $key . "`,";
            $values = $values . " '" . $value . "',";
        }
    }

    $columns = substr($columns, 0, -1);
    $values = substr($values, 0, -1);

    $query_string = "INSERT INTO `swfm_user` (" . $columns . ")";
    $query_string = $query_string . " VALUES (" . $values . ")";

    // send query and close connection
    $my_result = $my_database->send_query($query_string);
    $my_database->close_connection();

    if (!$my_result)
        launch_error("There are some problem with your registration.");
}



?>
