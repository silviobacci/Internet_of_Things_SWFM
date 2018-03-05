// ----------------------------
// MAIN.JS
// It contains js function
// to prepare home dashboard page
// ------------------------------

// ----------------------------
// PAGE CODE
// ----------------------------
var is_admin;

$(document).ready(function(){
	get_user_data();
	create_texture("texture_map.txt");
	//$('#modal').modal('show');
	map_constructor($("#map"));
	create_map_placeholder($("#map"));
	
	$('#modal').on('shown.bs.modal', function (e) {
		texture_constructor($("#river-ov"), $("#canvas-left-container"), $("#canvas-river-ov"), 50);
		wave_constructor($("#river-sec"), $("#canvas-right-container"), $("#canvas-river-sec"), 30);
		history_constructor($("#river-sec"), $("#chart-history"));
		
		draw_texture();
		create_default_motes();
		create_texture_handlers(is_admin);
		create_overlay($("#river-ov"), $("#overlay"));
		//create_texture_placeholder($("#river-ov"));
		
		//draw_wave();
		//create_wave_placeholder($("#river-sec"));
		create_wave_click_to_open($("#river-sec"));
		
		//build_chart([{x:0, y:10}, {x:10, y:10}]);
		//create_history_placeholder($("#river-sec"), $("#chart-history"));
		create_history_click_to_open($("#river-sec"), $("#chart-history"));
		
		//draw_threshold_alarm($("#river-sec"), $("#threshold"), $("#alert"));
		//create_slider_handler();
		//create_threshold_alarm_placeholder($("#river-sec"), $("#threshold"));
		create_threshold_alarm_click_to_open($("#river-sec"), $("#threshold"));
	});
});

function create_default_motes() {
	var default_motes_columns = [11, 27, 14, 26, 18, 18];
	var default_motes_rows = [1, 1, 11, 11, 21, 28];
	create_motes(default_motes_rows, default_motes_columns);
}

function draw_threshold_alarm(canvas, container, container2) {
	container.css("width", canvas[0].width + 4);
	container.css("height", canvas[0].height + 4);
	container2.css("width", canvas[0].width + 4);
}

function create_threshold_alarm_placeholder(canvas, container) {
	$('.unable').css("display","none");
	$('<img />', {src: $("#unable_rect")[0].src, width: canvas[0].width, height : canvas[0].height}).appendTo(container);
}

function create_threshold_alarm_click_to_open(canvas, container) {
	$('.click').css("display","none");
	$('<img />', {src: $("#click")[0].src, width: canvas[0].width, height : canvas[0].height}).appendTo(container);
}

function create_slider_handler() {
	$('#slider').slider({
		formatter: function(value) {
			$('#new_th').html("NEW THRESHOLD: " + value + " cm");
		}
	});
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
	$('#btn-logout > a').click(function (){
		ajax_req(logout, "null", logout_succ, logout_err);
	});
	$('.nav-avatar').attr("src", img_svr_path + userdata.avatar);
	$('.card-avatar').attr("src", img_svr_path + userdata.avatar);
	$('.cover-img').css('background-image', 'url(' + img_svr_path + userdata.cover + ')');
	$('.card-name').html(userdata.name + " " + userdata.surname);
	$('.navbar-brand').attr("href", rel_fron_path);
	is_admin = userdata.admin;
	
	if(is_admin== true){
		$('.card-text').html("You are an administrator. You can act directly on our dams in order control the water flows.");
		$('.to-hidden').css("display","default");
	}
	else{
		$('.card-text').html("You are a standard user so we can simply observe an overview of the current state of the water flows.");
		$('.to-hidden').css("display","none");
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
