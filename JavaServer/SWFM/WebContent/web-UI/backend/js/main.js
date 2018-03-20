// ----------------------------
// MAIN.JS
// It contains js function
// to prepare home dashboard page
// ------------------------------

// ----------------------------
// PAGE CODE
// ----------------------------
var is_admin;
var map;

$(document).ready(function(){
	get_user_data();
	
	map = new MAP(10000);
	
	var googleapi = "https://maps.googleapis.com/maps/api/js?key=AIzaSyDQVpIU4EdpO_4ZI5mU2gTDKOsLRSeFUW8&callback=draw_map";
	var script_type = "text/javascript";
	map.container.append("<script type=" + script_type + " src=" + googleapi + "/>");
});

function draw_map() {
	map.map = new google.maps.Map(map.container[0], {zoom: 6});
	map.req = window.requestAnimationFrame(function(timestamp){map.refresh(timestamp, true);});
}

// ----------------------------
// GET USER FUNCTIONS
// ----------------------------

// AJAX-REQ
// Change navbar link if already logged in
function get_user_data() {
    ajax_req(
    		redirect, 
        "",     
        get_succ, 
        get_err
    );
}

// AJAX-REP
// Action done in case of success
function get_succ(reply) {
    if (reply.error == false)
        prepare_page(reply.message);
    else
        window.location.replace(rel_fron_path);
}

// AJAX-ERR
// Action done in case of failure
function get_err(reply) {
	alert("Server unreachable.");
    window.location.replace(rel_fron_path);
}

// AJAX-REP
// Action done in case of success
function logout_succ(reply) {
	window.location.replace(rel_fron_path);
}

// AJAX-ERR
// Action done in case of failure
function logout_err(reply) {
	alert("Server unreachable.");
}

// Prepare page with custom user data
function prepare_page(userdata) {
	$('#btn-logout > a').click(function (){ajax_req(logout, "null", logout_succ, logout_err);});
	$('.nav-avatar').attr("src", img_svr_path + userdata.avatar);
	$('.card-avatar').attr("src", img_svr_path + userdata.avatar);
	$('.cover-img').css('background-image', 'url(' + img_svr_path + userdata.cover + ')');
	$('.card-name').html(userdata.name + " " + userdata.surname);
	$('.navbar-brand').attr("href", rel_fron_path);
	is_admin = userdata.admin;
	
	if(is_admin == true){
		$('.card-text').html("You are an administrator. You can act directly on our dams in order control the water flows.");
		$('.admin-to-hide').show();
	}
	else{
		$('.card-text').html("You are a standard user so we can simply observe an overview of the current state of the water flows.");
		$('.admin-to-hide').hide();
	}
}

function request_to_server() {
	$.ajax({
		   type: 'GET',
		   url: 'http://localhost:8080/JavaServer/MyServer',
		   contentType: 'text/plain',
		   xhrFields: {withCredentials: false},
		   headers: {},
		   success: function(data) {
		   set_water_level(0, data);
		   },
		   error: function(data) {
		   console.log(data);
		   }
		 });
}

// -------------------------------------
// UTILITY
// -------------------------------------

// Send an ajax req
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
