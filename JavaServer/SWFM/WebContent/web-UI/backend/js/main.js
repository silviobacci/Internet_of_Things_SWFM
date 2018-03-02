// ----------------------------
// MAIN.JS
// It contains js function
// to prepare home dashboard page
// ------------------------------

// ----------------------------
// PAGE CODE
// ----------------------------

$(document).ready(function(){
	get_user_data();
	create_texture("texture_map.txt");
	
	$('#modal').on('shown.bs.modal', function (e) {
		texture_constructor($("#river-ov"), $("#canvas-left-container"), 30);
		wave_constructor($("#river-sec"), $("#canvas-right-container"), 30);
		draw_texture();
		draw_wave();
		create_default_motes();
		create_texture_handlers();
	});
});

function create_default_motes() {
	var default_motes_columns = [11, 27, 14, 26, 18, 18];
	var default_motes_rows = [1, 1, 11, 11, 21, 28];
	create_motes(default_motes_rows, default_motes_columns);
}

// ----------------------------
// GET USER FUNCTIONS
// ----------------------------

// AJAX-REQ
// Change navbar link if already logged in
function get_user_data() {
    ajax_req(
        php_redir, 
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
		ajax_req(php_logout, "null", logout_succ, logout_err);
	});
	$('.nav-avatar').attr("src", img_svr_path + userdata.avatar);
	$('.card-avatar').attr("src", img_svr_path + userdata.avatar);
	$('.cover-img').css('background-image', 'url(' + img_svr_path + userdata.cover + ')');
	$('.card-name').html(userdata.name + " " + userdata.surname);
	if(userdata.admin == true)
		$('.card-text').html("You are an administrator. You can act directly on our dams in order control the water flows.");
	else
		$('.card-text').html("You are a standard user so we can simply observe an overview of the current state of the water flows.");
		
    $('.navbar-brand').attr("href", rel_fron_path);
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
