// -----------------------------------------
// MAIN.JS
// It contains js code to permit login/signup
// ------------------------------------------

$(document).ready(function(){
	scroll_page_toggle();
    check_if_logged();
    $("#form-login").vindicate("init");
    $("#form-signup").vindicate("init");
});

// -------------------------------------
// LOGGED/NOT-LOGGED USER FUNCTION
// -------------------------------------

// Make an ajax req to know if user is logged or not
function check_if_logged() {
    ajax_req(php_redir, "", check_succ, check_err);
}

// Hide login/signup button and show dashboard one
function check_succ(reply) {
    if (reply.error == false) {
        $("#btn-login").hide();
        $("#btn-signup").hide();
    } else {
        $("#btn-dashboard").hide();
    }
}

// Hide dashboard button (server not reachable)
function check_err() {
    $("#btn-dashboard").hide();
}

// -------------------------------------
// SUBMIT LOGIN FORM SUCCESS
// -------------------------------------

// Submit validation form login
function submit_login() {
    $("#form-login").vindicate("validate");
	
    ajax_req(
        php_login,
        $("#form-login").serialize(),     
        login_succ, 
        login_err
    );
}

// Redirect to dashboard if login was successful or display error
function login_succ(reply) {
	console.log(reply);
    if (reply.error == false) {
        window.location.replace(rel_dash_path);
    } else {
        $(".login-error-title").html("Error: ");
        $(".login-error-text").html(reply.message);
        show_alert(".alert-login");
    }
}

// Show an alert if login was not possible
function login_err(reply) {
	console.log(reply);
    $(".login-error-title").html("Error: ");
    $(".login-error-text").html("server unreachable.");
    show_alert(".alert-login");
}

// -------------------------------------
// SUBMIT SIGNUP FORM SUCCESS
// -------------------------------------

// Submit validation form sign up
function submit_signup() {
    $("#form-signup").vindicate("validate");
    data = new FormData(document.getElementById('form-signup'));

    ajax_req_file(
        php_signup,
        data,     
        signup_succ, 
        signup_err
    );
}

// Action done in case of success
function signup_succ(reply) {
    if (reply.error == false) {
		data = $("#form-signup").serialize();
        $(".signup-success-title").html("Success: ");
        $(".signup-success-text").html(reply.message);
        show_alert(".alert-signup-succ");
        $("#form-signup :input").prop('disabled', true);
        $(".btn-secondary").prop('disabled', false);
		
		ajax_req(
			 php_login,
			 data,
			 login_succ,
			 login_err
		 );
    } else {
        $(".signup-error-title").html("Error: ");
        $(".signup-error-text").html(reply.message);
        show_alert(".alert-signup");
    }
}

// Action done in case of failure
function signup_err(reply) {
    $(".signup-error-title").html("Error: ");
    $(".signup-error-text").html("server unreachable.");
	show_alert(".alert-signup");
}

// -------------------------------------
// UTILITY
// -------------------------------------

// toggle on full height scroll on big devices
function scroll_page_toggle() {
	if(window.matchMedia("(min-width: 768px)").matches){
		$('.mobile-div').remove();
		$('#fullpage').fullpage();
	}
	else
		$('.row-heading:not(:first)').hide();
}

function yes_scroll() {
	$.fn.fullpage.setMouseWheelScrolling(true);
	$.fn.fullpage.setAllowScrolling(true);
}

function no_scroll() {
	$.fn.fullpage.setMouseWheelScrolling(false);
	$.fn.fullpage.setAllowScrolling(false);
}

function ajax_req(dest, info, succ, err) {
    $.ajax({
        type: "POST",
        url: dest,
        xhrFields: { withCredentials: true },
        data: info,
        dataType: "json",
        success: succ,
        error: err
    });
}

function ajax_req_file(dest, info, succ, err) {
    $.ajax({
        type: "POST",
        url: dest,
        xhrFields: { withCredentials: true },
        data: info,
        dataType: "json",
        processData: false,
        contentType: false,
        success: succ,
        error: err
    });
}

function show_alert(htmlclass) {
    $(htmlclass).removeClass("d-none");
    $(htmlclass).addClass("show");
}
