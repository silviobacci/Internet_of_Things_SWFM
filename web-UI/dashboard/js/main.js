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

create_texture();

var dam_open = false;

var animation_position = 0;
var animation_timer;

var top_dam;
var bottom_dam;
var start_dam;
var end_dam;

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

function open_dam_left() {
	var current_index = end_dam - animation_position;
	switch(current_index) {
		case end_dam:
			texture[top_dam][current_index] = "7";
			texture[bottom_dam][current_index] = "6";
			animation_position++;
			draw_texture();
			break;
		case start_dam:
			if (current_index - 1 > 0) {
				texture[top_dam][current_index] = "8";
				texture[bottom_dam][current_index] = "5";
			}
			else {
				texture[top_dam][current_index] = "u";
				texture[bottom_dam][current_index] = "b";
			}
			texture[top_dam][current_index + 1] = "u";
			texture[bottom_dam][current_index + 1] = "b";
			animation_position = 0;
			draw_texture();
			clearInterval(animation_timer);
			break;
		default:
			if(current_index + 1 != end_dam) {
				texture[top_dam][current_index + 1] = "u";
				texture[bottom_dam][current_index + 1] = "b";
			}
			texture[top_dam][current_index] = "a";
			texture[bottom_dam][current_index] = "d";
			animation_position++;
			draw_texture();
			break;
	}
	
	dam_open = true;
}

function close_dam_left() {
	var current_index = start_dam + animation_position;
	switch(current_index) {
		case start_dam:
			texture[top_dam][current_index] = "r";
			texture[bottom_dam][current_index] = "r";
			animation_position++;
			draw_texture();
			break;
		case end_dam:
			texture[top_dam][current_index] = "l";
			texture[bottom_dam][current_index] = "l";
			texture[top_dam][current_index - 1] = "9";
			texture[bottom_dam][current_index - 1] = "0";
			animation_position = 0;
			draw_texture();
			clearInterval(animation_timer);
			break;
		default:
			if (current_index - 1 != start_dam) {
				texture[top_dam][current_index - 1] = "9";
				texture[bottom_dam][current_index - 1] = "0";
			}
			texture[top_dam][current_index] = "a";
			texture[bottom_dam][current_index] = "d";
			animation_position++;
			draw_texture();
			break;
	}
	
	dam_open = false;
}

function open_dam_right() {
	var current_index = start_dam + animation_position;
	switch(current_index) {
		case start_dam:
			texture[top_dam][current_index] = "h";
			texture[bottom_dam][current_index] = "j";
			animation_position++;
			draw_texture();
			break;
		case start_dam + 1:
			texture[top_dam][current_index - 1] = "8";
			texture[bottom_dam][current_index - 1] = "5";
			texture[top_dam][current_index] = "k";
			texture[bottom_dam][current_index] = "m";
			animation_position++;
			draw_texture();
			break;
		case end_dam:
			if(current_index + 1 <= col_number) {
				texture[top_dam][current_index + 1] = "7";
				texture[bottom_dam][current_index + 1] = "6";
			}
			texture[top_dam][current_index] = "u";
			texture[bottom_dam][current_index] = "b";
			texture[top_dam][current_index - 1] = "u";
			texture[bottom_dam][current_index - 1] = "b";
			animation_position = 0;
			draw_texture();
			clearInterval(animation_timer);
			break;
		default:
			texture[top_dam][current_index - 1] = "u";
			texture[bottom_dam][current_index - 1] = "b";
			texture[top_dam][current_index] = "k";
			texture[bottom_dam][current_index] = "m";
			animation_position++;
			draw_texture();
			break;
	}
	
	dam_open = true;
}

function close_dam_right() {
	var current_index = end_dam - animation_position;
	switch(current_index) {
		case end_dam:
			if(current_index + 1 <= col_number) {
				texture[top_dam][current_index + 1] = "l";
				texture[bottom_dam][current_index + 1] = "l";
			}
			texture[top_dam][current_index] = "k";
			texture[bottom_dam][current_index] = "m";
			animation_position++;
			draw_texture();
			break;
		case start_dam:
			texture[top_dam][current_index] = "r";
			texture[bottom_dam][current_index] = "r";
			texture[top_dam][current_index + 1] = "9";
			texture[bottom_dam][current_index + 1] = "0";
			animation_position = 0;
			draw_texture();
			clearInterval(animation_timer);
			break;
		default:
			texture[top_dam][current_index] = "k";
			texture[bottom_dam][current_index] = "m";
			texture[top_dam][current_index + 1] = "9";
			texture[bottom_dam][current_index + 1] = "0";
			animation_position++;
			draw_texture();
			break;
	}

	dam_open = false;
}

function dam() {
	start_dam = 0;
	end_dam = 7;
	top_dam = 2;
	bottom_dam = 3;
	
	if (dam_open == true)
		animation_timer = setInterval(close_dam_left, 25);
	else
		animation_timer = setInterval(open_dam_left, 25);
}

function draw_texture() {
	var g = $('#g')[0];
	var l = $('#l')[0];
	var w = $('#w')[0];
	var r = $('#r')[0];
	var u = $('#u')[0];
	var b = $('#b')[0];
	var w1 = $('#1')[0];
	var w2 = $('#2')[0];
	var w3 = $('#3')[0];
	var w4 = $('#4')[0];
	var g1 = $('#5')[0];
	var g2 = $('#6')[0];
	var g3 = $('#7')[0];
	var g4 = $('#8')[0];
	var u2 = $('#u2')[0];
	var b2 = $('#b2')[0];
	var o1 = $('#o1')[0];
	var o2 = $('#o2')[0];
	var o3 = $('#o3')[0];
	var o4 = $('#o4')[0];
	var o5 = $('#o5')[0];
	var o6 = $('#o6')[0];
	var o7 = $('#o7')[0];
	var o8 = $('#o8')[0];
	var o9 = $('#o9')[0];
	var o0 = $('#o0')[0];

	var canvas = $('#river-ov')[0];
	canvas.width = tile_width * col_number;
	canvas.height = tile_height * row_number;

	var context = canvas.getContext("2d");
	
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
