// ----------------------------
// MAIN.JS
// It contains js function
// to prepare home dashboard page
// ------------------------------

// ----------------------------
// PAGE CODE
// ----------------------------

get_user_data();

var texture = [];
var tile_width = 16;
var tile_height = 16;
var row_number;
var col_number;
var context;

create_texture();

var dam_open = [false, false, false, false];

var animation_position = [0, 0, 0, 0];
var animation_timer = [0, 0, 0, 0];
var animation_speed = 50;

var sd = [0, 24, 0, 16];
var ed = [6, 29, 12, 29];
var td = [3, 3, 22, 25];
var bd = [4, 4, 23, 26];

var d0 = {x: (tile_width - 1) * ed[0], y: tile_height * td[0], w: tile_width * 2, h: tile_height * 3};
var d1 = {x: tile_width * sd[1], y: tile_height * td[1], w: tile_width * 2, h: tile_height * 3};
var d2 = {x: (tile_width - 1) * ed[2], y: tile_height * td[2], w: tile_width * 2, h: tile_height * 3};
var d3 = {x: tile_width * sd[3], y: tile_height * td[3], w: tile_width * 2, h: tile_height * 3};

var g, l, w, r, u, b, w1, w2, w3, w4, g1, g2, g3, g4, u2, b2, o1, o2, o3, o4, o5, o6, o7, o8, o9, o0;
var t1, t2, t3, m1, m2, m3, m4, m5, m6, m7, m8, f2, f5;

$(document).ready(function(){
	g = $('#g')[0];
	l = $('#l')[0];
	w = $('#w')[0];
	r = $('#r')[0];
	u = $('#u')[0];
	b = $('#b')[0];
	w1 = $('#1')[0];
	w2 = $('#2')[0];
	w3 = $('#3')[0];
	w4 = $('#4')[0];
	g1 = $('#5')[0];
	g2 = $('#6')[0];
	g3 = $('#7')[0];
	g4 = $('#8')[0];
	u2 = $('#u2')[0];
	b2 = $('#b2')[0];
	o1 = $('#o1')[0];
	o2 = $('#o2')[0];
	o3 = $('#o3')[0];
	o4 = $('#o4')[0];
	o5 = $('#o5')[0];
	o6 = $('#o6')[0];
	o7 = $('#o7')[0];
	o8 = $('#o8')[0];
	o9 = $('#o9')[0];
	o0 = $('#o0')[0];
	t1 = $('#t1')[0];
	t2 = $('#t2')[0];
	t3 = $('#t3')[0];
	m1 = $('#m1')[0];
	m2 = $('#m2')[0];
	m3 = $('#m3')[0];
	m4 = $('#m4')[0];
	m5 = $('#m5')[0];
	m6 = $('#m6')[0];
	m7 = $('#m7')[0];
	f2 = $('#f2')[0];
	f5 = $('#f5')[0];
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

// Prepare page with custom user data
function prepare_page(userdata) {
	$('#btn-logout > a').attr("href", php_logout);
	$('.nav-avatar').attr("src", img_svr_path + userdata.avatar);
	$('.card-avatar').attr("src", img_svr_path + userdata.avatar);
	$('.cover-img').css('background-image', 'url(' + img_svr_path + userdata.cover + ')');
	$('.card-name').html(userdata.name + " " + userdata.surname);
	if(userdata.admin == "1")
		$('.card-text').html("You are an administrator. You can act directly on our dams in order control the water flows.");
	else
		$('.card-text').html("You are a standard user so we can simply observe an overview of the current state of the water flows.");
		
    $('.navbar-brand').attr("href", rel_fron_path);
}

// -------------------------------------
// MAP INITIALIZATION FUNCTION
// -------------------------------------

function init_map() {
	var river = {lat: 43.843176, lng: 10.734928};
	var map = new google.maps.Map(document.getElementById('map'), {zoom: 6, center: river});
	var marker = new google.maps.Marker({position: river, map: map});
	
	marker.addListener('click', function() {$('#modal').modal('show'); draw_texture();});
}

// -------------------------------------
// CANVAS RIVER OVERVIEW
// -------------------------------------

function create_texture() {
	$.get(texture_svr_path + "texture_map.txt", function(data) {
		var lines = data.split("\n");
		for(var i = 0; i < lines.length - 1; i++) {
		  	var chars = lines[i].split("");
			texture[i] = [];
		  	for(var j = 0; j < chars.length; j++) {
		  		texture[i][j] = chars[j];
		  	}
		}
		row_number = texture.length;
		col_number = texture[1].length;
	});
}

function open_dam_left(index_dam) {
	var start_dam = sd[index_dam];
	var end_dam = ed[index_dam];
	var top_dam = td[index_dam];
	var bottom_dam = bd[index_dam];
	var current_index = end_dam - animation_position[index_dam];
	
	switch(current_index) {
		case end_dam:
			context.drawImage(o4, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(o5, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position[index_dam]++;
			break;
		case end_dam - 1:
			context.drawImage(g3, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(g2, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context.drawImage(o1, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(o3, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position[index_dam]++;
			break;
		case start_dam - 1:
			context.drawImage(u, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(b, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			dam_open[index_dam] = !dam_open[index_dam];
			animation_position[index_dam] = 0;
			clearInterval(animation_timer[index_dam]);
			break;
		default:
			context.drawImage(u, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(b, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context.drawImage(o1, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(o3, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position[index_dam]++;
			break;
	}
}

function close_dam_left(index_dam) {
	var start_dam = sd[index_dam];
	var end_dam = ed[index_dam];
	var top_dam = td[index_dam];
	var bottom_dam = bd[index_dam];
	var current_index = start_dam + animation_position[index_dam];
	
	switch(current_index) {
		case start_dam:
			context.drawImage(o1, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(o3, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position[index_dam]++;
			break;
		case end_dam:
			context.drawImage(u2, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(b2, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context.drawImage(l, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(l, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			dam_open[index_dam] = !dam_open[index_dam];
			animation_position[index_dam] = 0;
			clearInterval(animation_timer[index_dam]);
			break;
		default:
			context.drawImage(u2, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(b2, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context.drawImage(o1, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(o3, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position[index_dam]++;
			break;
	}
}

function open_dam_right(index_dam) {
	var start_dam = sd[index_dam];
	var end_dam = ed[index_dam];
	var top_dam = td[index_dam];
	var bottom_dam = bd[index_dam];
	var current_index = start_dam + animation_position[index_dam];
	
	switch(current_index) {
		case start_dam:
			context.drawImage(o6, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(o8, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position[index_dam]++;
			break;
		case start_dam + 1:
			context.drawImage(g4, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(g1, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context.drawImage(o9, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(o0, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position[index_dam]++;
			break;
		case end_dam + 1:
			context.drawImage(u, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(b, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			dam_open[index_dam] = !dam_open[index_dam];
			animation_position[index_dam] = 0;
			clearInterval(animation_timer[index_dam]);
			break;
		default:
			context.drawImage(u, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(b, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context.drawImage(o9, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(o0, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position[index_dam]++;
			break;
	}
}

function close_dam_right(index_dam) {
	var start_dam = sd[index_dam];
	var end_dam = ed[index_dam];
	var top_dam = td[index_dam];
	var bottom_dam = bd[index_dam];
	var current_index = end_dam - animation_position[index_dam];
	
	switch(current_index) {
		case end_dam:
			context.drawImage(o9, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(o9, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position[index_dam]++;
			break;
		case start_dam:
			context.drawImage(u2, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(b2, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context.drawImage(r, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(r, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			dam_open[index_dam] = !dam_open[index_dam];
			animation_position[index_dam] = 0;
			clearInterval(animation_timer[index_dam]);
			break;
		default:
			context.drawImage(u2, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(b2, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context.drawImage(o9, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context.drawImage(o0, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position[index_dam]++;
			break;
	}
}

function dam(index_dam, right) {
	if(right == true) {
		if (dam_open[index_dam] == true)
			animation_timer[index_dam] = setInterval(close_dam_right, animation_speed, index_dam);
		else
			animation_timer[index_dam] = setInterval(open_dam_right, animation_speed, index_dam);
	}
	else {
		if (dam_open[index_dam] == true)
			animation_timer[index_dam] = setInterval(close_dam_left, animation_speed, index_dam);
		else
			animation_timer[index_dam] = setInterval(open_dam_left, animation_speed, index_dam);
	}
}

function contains(rect, x, y) {
	return (x >= rect.x && x <= rect.x + rect.w && y >= rect.y && y <= rect.y + rect.h)
}

function draw_texture() {
	var canvas = $('#river-ov')[0];
	canvas.width = tile_width * col_number;
	canvas.height = tile_height * row_number;
	
	context = canvas.getContext("2d");
	
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
	});
	
	for(var i = 0; i < row_number; i++) {
		for(var j = 0; j < col_number; j++) {
			switch(texture[i][j]) {
				case "r":
					context.drawImage(r, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "w":
					context.drawImage(w, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "l":
					context.drawImage(l, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "u":
					context.drawImage(u, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "b":
					context.drawImage(b, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "1":
					context.drawImage(w1, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "2":
					context.drawImage(w2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "3":
					context.drawImage(w3, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "4":
					context.drawImage(w4, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "5":
					context.drawImage(g1, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "6":
					context.drawImage(g2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "7":
					context.drawImage(g3, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "8":
					context.drawImage(g4, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "9":
					context.drawImage(u2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "0":
					context.drawImage(b2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "a":
					context.drawImage(o1, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "c":
					context.drawImage(o2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "d":
					context.drawImage(o3, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "e":
					context.drawImage(o4, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "f":
					context.drawImage(o5, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "h":
					context.drawImage(o6, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "i":
					context.drawImage(o7, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "j":
					context.drawImage(o8, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "k":
					context.drawImage(o9, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "m":
					context.drawImage(o0, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "n":
					context.drawImage(t1, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "o":
					context.drawImage(t2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "p":
					context.drawImage(t3, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "q":
					context.drawImage(m1, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "s":
					context.drawImage(m2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "t":
					context.drawImage(m3, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "v":
					context.drawImage(m4, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "x":
					context.drawImage(m5, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "y":
					context.drawImage(m6, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "z":
					context.drawImage(m7, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "$":
					context.drawImage(f2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "!":
					context.drawImage(f5, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				default:
					context.drawImage(g, j*tile_width, i*tile_height, tile_width, tile_height);
			}
		}
	}
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
