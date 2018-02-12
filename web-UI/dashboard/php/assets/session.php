<?php
// -------------------------------------
// SESSION.PHP
// contains php script to set/reset
// super global $_SESSION array
// -------------------------------------

// Set $_SESSION array with logged user data
function set_session_array($email, $username, $name, $surname, $avatar, $cover, $admin) {
    $_SESSION['email'] = $email;
    $_SESSION['username'] = $username;
    $_SESSION['name'] = $name;
    $_SESSION['surname'] = $surname;
    $_SESSION['avatar'] = $avatar;
    $_SESSION['cover'] = $cover;
	$_SESSION['admin'] = $admin;
}

// Check if user is logged in
function is_user_logged() {
    if(isset($_SESSION['username']))
        return true;
    else
        return false;
}

// Return email of logged user
function get_user_email() {
    return $_SESSION['email'];
}

// Return username of logged user
function get_user_username() {
    return $_SESSION['username'];
}

// Return name of logged user
function get_user_name() {
    return $_SESSION['name'];
}

// Return surname of logged user
function get_user_surname() {
    return $_SESSION['surname'];
}

// Return avatar of logged user
function get_user_avatar() {
    return $_SESSION['avatar'];
}

// Return cover of logged user
function get_user_cover() {
    return $_SESSION['cover'];
}
	
// Return bio of logged user
function get_user_admin() {
	return $_SESSION['admin'];
}

?>
