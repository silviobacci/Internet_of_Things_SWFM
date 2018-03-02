// ----------------------------
// MAIN.JS
// It contains js function
// to prepare home dashboard page
// ------------------------------

// ----------------------------
// PAGE CODE
// ----------------------------

$(document).ready(function(){
	$('#modal').on('shown.bs.modal', function (e) {
		get_user_data();
		texture_constructor("texture_map.txt", $("#canvas-ov"), $("#canvas-left-container"), 30);
		wave_constructor($("#river-sec"), $("#canvas-right-container"), 30);
		draw_texture();
		draw_wave();
		create_handlers();
	});
});

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

function contains(rect, x, y) {
	return (x >= rect.x && x <= rect.x + rect.w && y >= rect.y && y <= rect.y + rect.h)
}

function create_handlers() {
	$('#river-ov').mousemove(function(ev) {
		var x = ev.pageX - $(this).offset().left;
		var y = ev.pageY - $(this).offset().top;

		if(contains(d0, x, y))
			$(this).css('cursor', 'pointer');
		else if (contains(d1, x, y))
			$(this).css('cursor', 'pointer');
		else if (contains(d2, x, y))
			$(this).css('cursor', 'pointer');
		else if (contains(d3, x, y))
			$(this).css('cursor', 'pointer');
		else if (contains(mote[0], x, y))
			$(this).css('cursor', 'pointer');
		else if (contains(mote[1], x, y))
			$(this).css('cursor', 'pointer');
		else if (contains(mote[2], x, y))
			$(this).css('cursor', 'pointer');
		else if (contains(mote[3], x, y))
			$(this).css('cursor', 'pointer');
		else if (contains(mote[4], x, y))
			$(this).css('cursor', 'pointer');
		else if (contains(mote[5], x, y))
			$(this).css('cursor', 'pointer');
		else
			$(this).css('cursor', 'default');
	});
	
	$('#river-ov').click(function(ev) {
		var x = ev.pageX - $(this).offset().left;
		var y = ev.pageY - $(this).offset().top;

		if(contains(d0, x, y))
			dam(0, false);
		else if (contains(d1, x, y))
			dam(1, true);
		else if (contains(d2, x, y))
			dam(2, false);
		else if (contains(d3, x, y))
			dam(3, true);
		 else if (contains(mote[0], x, y))
			set_water_level(0, 15);
		 else if (contains(mote[1], x, y))
			set_water_level(1, 15);
		 else if (contains(mote[2], x, y))
			set_water_level(2, 15);
		 else if (contains(mote[3], x, y))
			set_water_level(3, 15);
		 else if (contains(mote[4], x, y))
			set_water_level(4, 15);
		 else if (contains(mote[5], x, y))
			set_water_level(5, 15);
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
