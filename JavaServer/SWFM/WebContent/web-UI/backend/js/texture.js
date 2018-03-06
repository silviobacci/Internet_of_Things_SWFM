// ----------------------------
// MAIN.JS
// It contains js function
// to prepare home dashboard page
// ------------------------------

var size = 528;

var tile_width = 16;
var tile_height = 16;

var tile_level_width = 1;
var tile_level_height = 4;
var min_level = 8;
var av_level = 24;
var max_level = 32;

var texture = [];
var context_texture;
var context_overlay;
var context_mote_canvas;
var scaling_factor;

var row_number;
var col_number;

var n_dams = 4;
var dam_open = [false, false, false, false];
var animation_position_texture = [0, 0, 0, 0];
var animation_timer_texture = [0, 0, 0, 0];
var animation_period_texture;
var start_time = [0, 0, 0, 0];

var sd = [0, 28, 0, 21];
var ed = [9, 32, 15, 32];
var td = [3, 3, 22, 30];
var bd = [4, 4, 23, 31];

var d = [
			{x: (tile_width - 1) * ed[0], y: tile_height * td[0], w: tile_width * 2, h: tile_height * 3},
			{x: tile_width * sd[1], y: tile_height * td[1], w: tile_width * 2, h: tile_height * 3},
			{x: (tile_width - 1) * ed[2], y: tile_height * td[2], w: tile_width * 2, h: tile_height * 3},
			{x: tile_width * sd[3], y: tile_height * td[3], w: tile_width * 2, h: tile_height * 3}
];

var n_motes;
var mote = [];

var g, l, w, r, u, b, w1, w2, w3, w4, g1, g2, g3, g4, u2, b2, o1, o2, o3, o4, o5, o6, o7, o8, o9, o0;
var t1, t2, t3, m1, m2, m3, m4, m5, m6, m7, m8, f2, f5;
var h01, h02, h03, h04, h11, h12, h13, h14, h21, h22, h23, h24, h31, h32, h33, h34, h41, h42, h43, h44;
var fm1, fm2, marker, label, red, green, yellow, grey, rect;

var text_visible = false;

function texture_constructor(canvas, container, container2, as) {
	animation_period_texture = as;
	set_texture_dimension(canvas, container, container2);
	create_tileset();
	var refresh_canvas = true;
}

function create_texture(texture_file) {
	$.get(texture_svr_path + texture_file, function(data) {
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

function set_texture_dimension(canvas, container, container2) {
	context_texture = canvas[0].getContext("2d");
	
	var min_dim = container.innerWidth() < container.innerHeight() ? container.innerWidth() : container.innerHeight();

	if(min_dim < size) {
		canvas[0].width = min_dim;
		canvas[0].height = min_dim;
		scaling_factor = min_dim/size;
		context_texture.scale(scaling_factor, scaling_factor);
	}
	else {
		canvas[0].width = tile_height * row_number;
		canvas[0].height = tile_width * row_number;
	}
	
	container2.css("width", canvas[0].width + 4);
	container2.css("height", canvas[0].height + 4);
}

function create_overlay(canvas, mote_canvas, overlay) {
	context_overlay = overlay[0].getContext("2d");
	context_overlay.scale(scaling_factor, scaling_factor);
	
	context_mote_canvas = mote_canvas[0].getContext("2d");
	context_mote_canvas.scale(scaling_factor, scaling_factor);
	
	overlay[0].width = canvas[0].width;
	overlay[0].height = canvas[0].height;
	
	mote_canvas[0].width = canvas[0].width;
	mote_canvas[0].height = canvas[0].height;
	
	canvas.css("position", "absolute");
	mote_canvas.css("position", "absolute");
	overlay.css("position", "absolute");
	
	canvas.css("z-index", "0");
	mote_canvas.css("z-index", "1");
	overlay.css("z-index", "2");
}

function create_tileset() {
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
	h01 = $('#h01')[0];
	h02 = $('#h02')[0];
	h03 = $('#h03')[0];
	h04 = $('#h04')[0];
	h11 = $('#h11')[0];
	h12 = $('#h12')[0];
	h13 = $('#h13')[0];
	h14 = $('#h14')[0];
	h21 = $('#h21')[0];
	h22 = $('#h22')[0];
	h23 = $('#h23')[0];
	h24 = $('#h24')[0];
	h31 = $('#h31')[0];
	h32 = $('#h32')[0];
	h33 = $('#h33')[0];
	h34 = $('#h34')[0];
	h41 = $('#h41')[0];
	h42 = $('#h42')[0];
	h43 = $('#h43')[0];
	h44 = $('#h44')[0];
	fm1 = $('#fm1')[0];
	fm2 = $('#fm2')[0];
	marker = $('#marker')[0];
	label = $('#label')[0];
	red = $('#red')[0];
	green = $('#green')[0];
	yellow = $('#yellow')[0];
	grey = $('#grey')[0];
	rect = $('#rect')[0];
}

function create_texture_placeholder(canvas) {
	context_texture.drawImage($("#unable_quad")[0], 0, 0, canvas[0].width, canvas[0].height);
}

function open_dam_left(timestamp, index_dam, first_time) {
	if(first_time)
		start_time[index_dam] = timestamp;
	
	first_time = false;
	
	if (timestamp - start_time[index_dam] < animation_period_texture) {
		requestAnimationFrame(function(timestamp){open_dam_left(timestamp, index_dam, first_time);});
		return;
	}
	
	var start_dam = sd[index_dam];
	var end_dam = ed[index_dam];
	var top_dam = td[index_dam];
	var bottom_dam = bd[index_dam];
	var current_index = end_dam - animation_position_texture[index_dam];
	
	first_time = true;
	
	switch(current_index) {
		case end_dam:
			context_texture.drawImage(o4, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o5, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			requestAnimationFrame(function(timestamp){open_dam_left(timestamp, index_dam, first_time);});
			break;
		case end_dam - 1:
			context_texture.drawImage(g3, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(g2, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o1, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o3, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			requestAnimationFrame(function(timestamp){open_dam_left(timestamp, index_dam, first_time);});
			break;
		case start_dam - 1:
			context_texture.drawImage(u, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			dam_open[index_dam] = !dam_open[index_dam];
			animation_position_texture[index_dam] = 0;
			if(text_visible) {
				if(index_dam == 1 || index_dam == 3)
					var image = {x : d[index_dam].x - 6 * tile_width, y : d[index_dam].y, w : 6 * tile_width, h : 2*tile_height}; 
				else
					var image = {x : d[index_dam].x + tile_width, y : d[index_dam].y, w : 6 * tile_width, h : 2*tile_height}; 
				context_overlay.drawImage(rect, image.x, image.y, image.w, image.h);
				context_overlay.fillStyle="#000000";
				if(!dam_open[index_dam])
					var text = "click to open";
				else
					var text = "click to close";
				context_overlay.fillText(text, image.x + 21, image.y + 19);
			}
			break;
		default:
			context_texture.drawImage(u, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o1, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o3, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			requestAnimationFrame(function(timestamp){open_dam_left(timestamp, index_dam, first_time);});
			break;
	}
}

function close_dam_left(timestamp, index_dam, first_time) {
	if(first_time)
		start_time[index_dam] = timestamp;
	
	first_time = false;
		
	if (timestamp - start_time[index_dam] < animation_period_texture) {
		requestAnimationFrame(function(timestamp){close_dam_left(timestamp, index_dam, first_time);});
		return;
	}
	
	var start_dam = sd[index_dam];
	var end_dam = ed[index_dam];
	var top_dam = td[index_dam];
	var bottom_dam = bd[index_dam];
	var current_index = end_dam - animation_position_texture[index_dam];
	
	first_time = true;
	
	switch(current_index) {
		case end_dam:
			context_texture.drawImage(l, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(l, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o9, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o0, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			requestAnimationFrame(function(timestamp){close_dam_left(timestamp, index_dam, first_time);});
			break;
		case start_dam:
			context_texture.drawImage(u2, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b2, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			dam_open[index_dam] = !dam_open[index_dam];
			animation_position_texture[index_dam] = 0;
			if(text_visible) {
				if(index_dam == 1 || index_dam == 3)
					var image = {x : d[index_dam].x - 6 * tile_width, y : d[index_dam].y, w : 6 * tile_width, h : 2*tile_height}; 
				else
					var image = {x : d[index_dam].x + tile_width, y : d[index_dam].y, w : 6 * tile_width, h : 2*tile_height}; 
				context_overlay.drawImage(rect, image.x, image.y, image.w, image.h);
				context_overlay.fillStyle="#000000";
				if(!dam_open[index_dam])
					var text = "click to open";
				else
					var text = "click to close";
				context_overlay.fillText(text, image.x + 21, image.y + 19);
			}
			break;
		default:
			context_texture.drawImage(u2, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b2, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o9, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o0, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			requestAnimationFrame(function(timestamp){close_dam_left(timestamp, index_dam, first_time);});
			break;
	}
}

function open_dam_right(timestamp, index_dam, first_time) {
	if(first_time)
		start_time[index_dam] = timestamp;
	
	first_time = false;
		
	if (timestamp - start_time[index_dam] < animation_period_texture) {
		requestAnimationFrame(function(timestamp){open_dam_right(timestamp, index_dam, first_time);});
		return;
	}
	
	var start_dam = sd[index_dam];
	var end_dam = ed[index_dam];
	var top_dam = td[index_dam];
	var bottom_dam = bd[index_dam];
	var current_index = start_dam + animation_position_texture[index_dam];
	
	first_time = true;
	
	switch(current_index) {
		case start_dam:
			context_texture.drawImage(o6, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o8, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			requestAnimationFrame(function(timestamp){open_dam_right(timestamp, index_dam, first_time);});
			break;
		case start_dam + 1:
			context_texture.drawImage(g4, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(g1, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o9, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o0, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			requestAnimationFrame(function(timestamp){open_dam_right(timestamp, index_dam, first_time);});
			break;
		case end_dam + 1:
			context_texture.drawImage(u, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			dam_open[index_dam] = !dam_open[index_dam];
			animation_position_texture[index_dam] = 0;
			if(text_visible) {
				if(index_dam == 1 || index_dam == 3)
					var image = {x : d[index_dam].x - 6 * tile_width, y : d[index_dam].y, w : 6 * tile_width, h : 2*tile_height}; 
				else
					var image = {x : d[index_dam].x + tile_width, y : d[index_dam].y, w : 6 * tile_width, h : 2*tile_height}; 
				context_overlay.drawImage(rect, image.x, image.y, image.w, image.h);
				context_overlay.fillStyle="#000000";
				if(!dam_open[index_dam])
					var text = "click to open";
				else
					var text = "click to close";
				context_overlay.fillText(text, image.x + 21, image.y + 19);
			}
			break;
		default:
			context_texture.drawImage(u, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o9, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o0, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			requestAnimationFrame(function(timestamp){open_dam_right(timestamp, index_dam, first_time);});
			break;
	}
}

function close_dam_right(timestamp, index_dam, first_time) {
	if(first_time)
		start_time[index_dam] = timestamp;
	
	first_time = false;
	
	if (timestamp - start_time[index_dam] < animation_period_texture) {
		requestAnimationFrame(function(timestamp){close_dam_right(timestamp, index_dam, first_time);});
		return;
	}
	
	var start_dam = sd[index_dam];
	var end_dam = ed[index_dam];
	var top_dam = td[index_dam];
	var bottom_dam = bd[index_dam];
	var current_index = start_dam + animation_position_texture[index_dam];
	
	first_time = true;
	
	switch(current_index) {
		case start_dam:
			context_texture.drawImage(r, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(r, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o1, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o3, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			requestAnimationFrame(function(timestamp){close_dam_right(timestamp, index_dam, first_time);});
			break;
		case end_dam:
			context_texture.drawImage(u2, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b2, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			dam_open[index_dam] = !dam_open[index_dam];
			animation_position_texture[index_dam] = 0;
			if(text_visible) {
				if(index_dam == 1 || index_dam == 3)
					var image = {x : d[index_dam].x - 6 * tile_width, y : d[index_dam].y, w : 6 * tile_width, h : 2*tile_height}; 
				else
					var image = {x : d[index_dam].x + tile_width, y : d[index_dam].y, w : 6 * tile_width, h : 2*tile_height}; 
				context_overlay.drawImage(rect, image.x, image.y, image.w, image.h);
				context_overlay.fillStyle="#000000";
				if(!dam_open[index_dam])
					var text = "click to open";
				else
					var text = "click to close";
				context_overlay.fillText(text, image.x + 21, image.y + 19);
			}
			break;
		default:
			context_texture.drawImage(u2, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b2, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o1, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o3, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			requestAnimationFrame(function(timestamp){close_dam_right(timestamp, index_dam, first_time);});
			break;
	}
}

function dam(index_dam, right) {
	var first_time = true;
	if(animation_position_texture[index_dam] != 0)
		return;
	
	if(right == true) {
		if (dam_open[index_dam] == true)
			animation_timer_texture[index_dam] = requestAnimationFrame(function(timestamp){close_dam_right(timestamp, index_dam, first_time);});
		else
			animation_timer_texture[index_dam] = requestAnimationFrame(function(timestamp){open_dam_right(timestamp, index_dam, first_time);});
	}
	else {
		if (dam_open[index_dam] == true)
			animation_timer_texture[index_dam] = requestAnimationFrame(function(timestamp){close_dam_left(timestamp, index_dam, first_time);});
		else
			animation_timer_texture[index_dam] = requestAnimationFrame(function(timestamp){open_dam_left(timestamp, index_dam, first_time);});
	}
}

function set_water_level(mote_index, level) {
	if(level <= min_level){
		for(var i = 0; i < level; i++)
			context_texture.drawImage(red, mote[mote_index].x + i, mote[mote_index].y, tile_level_width, tile_level_height);
		for(var i = level; i < max_level; i++)
			context_texture.drawImage(grey, mote[mote_index].x + i, mote[mote_index].y, tile_level_width, tile_level_height);
	}
	else if (level > min_level && level <= av_level) {
		for(var i = 0; i < level; i++)
			context_texture.drawImage(yellow, mote[mote_index].x + i, mote[mote_index].y, tile_level_width, tile_level_height);
		for(var i = level; i < max_level; i++)
			context_texture.drawImage(grey, mote[mote_index].x + i, mote[mote_index].y, tile_level_width, tile_level_height);
	}
	else {
		for(var i = 0; i < level; i++)
			context_texture.drawImage(green, mote[mote_index].x + i, mote[mote_index].y, tile_level_width, tile_level_height);
		for(var i = level; i < max_level; i++)
			context_texture.drawImage(grey, mote[mote_index].x + i, mote[mote_index].y, tile_level_width, tile_level_height);
	}
}

function create_motes() {
	n_motes = sensors.length;
	for(var mote_index = 0; mote_index < n_motes; mote_index++)
		draw_mote(mote_index, sensors[mote_index].lat,  sensors[mote_index].lng);
}

function draw_mote(mote_index, row, column) {
	mote[mote_index] = {x : (column-1)*tile_width+8, y : (row-1)*tile_height+6, w : tile_width*3, h : tile_height};
	context_mote_canvas.drawImage(label, (column-1)*tile_width, (row-1)*tile_height, tile_width*3, tile_height);
	context_mote_canvas.drawImage(marker, column*tile_width, row*tile_height, tile_width, tile_height);
}

function draw_texture() {
	for(var i = 0; i < row_number; i++) {
		for(var j = 0; j < col_number; j++) {
			switch(texture[i][j]) {
				case "r":
					context_texture.drawImage(r, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "w":
					context_texture.drawImage(w, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "l":
					context_texture.drawImage(l, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "u":
					context_texture.drawImage(u, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "b":
					context_texture.drawImage(b, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "1":
					context_texture.drawImage(w1, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "2":
					context_texture.drawImage(w2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "3":
					context_texture.drawImage(w3, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "4":
					context_texture.drawImage(w4, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "5":
					context_texture.drawImage(g1, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "6":
					context_texture.drawImage(g2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "7":
					context_texture.drawImage(g3, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "8":
					context_texture.drawImage(g4, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "9":
					context_texture.drawImage(u2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "0":
					context_texture.drawImage(b2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "a":
					context_texture.drawImage(o1, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "c":
					context_texture.drawImage(o2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "d":
					context_texture.drawImage(o3, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "e":
					context_texture.drawImage(o4, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "f":
					context_texture.drawImage(o5, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "h":
					context_texture.drawImage(o6, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "i":
					context_texture.drawImage(o7, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "j":
					context_texture.drawImage(o8, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "k":
					context_texture.drawImage(o9, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "m":
					context_texture.drawImage(o0, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "n":
					context_texture.drawImage(t1, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "o":
					context_texture.drawImage(t2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "p":
					context_texture.drawImage(t3, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "q":
					context_texture.drawImage(m1, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "s":
					context_texture.drawImage(m2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "t":
					context_texture.drawImage(m3, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "v":
					context_texture.drawImage(m4, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "x":
					context_texture.drawImage(m5, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "y":
					context_texture.drawImage(m6, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "z":
					context_texture.drawImage(m7, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "$":
					context_texture.drawImage(f2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "!":
					context_texture.drawImage(f5, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "[":
					context_texture.drawImage(h01, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "]":
					context_texture.drawImage(h02, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "+":
					context_texture.drawImage(h03, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "*":
					context_texture.drawImage(h04, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "\\":
					context_texture.drawImage(h11, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "|":
					context_texture.drawImage(h12, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "\"":
					context_texture.drawImage(h13, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "£":
					context_texture.drawImage(h14, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "%":
					context_texture.drawImage(h21, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "&":
					context_texture.drawImage(h22, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "/":
					context_texture.drawImage(h23, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "(":
					context_texture.drawImage(h24, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case ")":
					context_texture.drawImage(h31, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "=":
					context_texture.drawImage(h32, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "?":
					context_texture.drawImage(h33, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "'":
					context_texture.drawImage(h34, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "^":
					context_texture.drawImage(h41, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "ì":
					context_texture.drawImage(h42, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "è":
					context_texture.drawImage(h43, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "é":
					context_texture.drawImage(h44, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "ò":
					context_texture.drawImage(fm1, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "@":
					context_texture.drawImage(fm2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				default:
					context_texture.drawImage(g, j*tile_width, i*tile_height, tile_width, tile_height);
			}
		}
	}
}

function contains(rect, x, y) {
	return (x >= rect.x && x <= rect.x + rect.w && y >= rect.y && y <= rect.y + rect.h)
}

function create_texture_handlers(is_admin) {
	$('#overlay').mousemove(function(ev) {
		var x = ev.pageX - $(this).offset().left;
		var y = ev.pageY - $(this).offset().top;
		
		for(var mote_index = 0; mote_index < mote.length; mote_index++)
			if (contains(mote[mote_index], x, y)){
				$(this).css('cursor', 'pointer');
				if(!text_visible) {
					if(mote_index == 1 || mote_index == 3)
						var image = {x : mote[1].x - 7 * tile_width, y : mote[1].y - 8, w : 6 * tile_width, h : 2*tile_height}; 
					else
						var image = {x : mote[mote_index].x + 2 * tile_width + 5, y : mote[mote_index].y - 8, w : 6 * tile_width, h : 2*tile_height}; 
					
					context_overlay.drawImage(rect, image.x, image.y, image.w, image.h);
					context_overlay.fillStyle="#000000";
					var text = "click for more info";
					context_overlay.fillText(text, image.x + 10, image.y + 19);
				}
				text_visible = true;
			}
		
		for(var dam_index = 0; dam_index < mote.length; dam_index++)
			if (is_admin && contains(d[dam_index], x, y)){
				$(this).css('cursor', 'pointer');
				if(!text_visible) {
					if(dam_index == 0 || dam_index == 2)
						var image = {x : d[dam_index].x + tile_width, y : d[dam_index].y, w : 6 * tile_width, h : 2*tile_height};
					else
						var image = {x : d[dam_index].x - 6 * tile_width, y : d[dam_index].y, w : 6 * tile_width, h : 2*tile_height}; 
					context_overlay.drawImage(rect, image.x, image.y, image.w, image.h);
					context_overlay.fillStyle="#000000";
					if(!dam_open[dam_index])
						var text = "click to open";
					else
						var text = "click to close";
					context_overlay.fillText(text, image.x + 21, image.y + 19);
				}
				text_visible = true;
			}
	});
	
	$('#overlay').click(function(ev) {
		var x = ev.pageX - $(this).offset().left;
		var y = ev.pageY - $(this).offset().top;

		for(var mote_index = 0; mote_index < mote.length; mote_index++)
			if (contains(mote[mote_index], x, y))
				set_water_level(2, 15);
		
		for(var dam_index = 0; dam_index < mote.length; dam_index++)
			if (contains(d[dam_index], x, y)){
				if(dam_index == 0 || dam_index == 2)
					dam(dam_index, false);
				else
					dam(dam_index, true);
			}
	});
}

var sensors = [];

function createSensorStructure(reply) {
	sensors = [];
	for(var i = 0; i < reply.message.length; i++) {
		sensors[i] = {id : 0, lat : 0, lng : 0, level : 0, min : 0, max : 0, th : 0};
	}
}

function getSensorDataSuccess(reply) {
	if(reply.error == false) {
		if(sensors.length == 0)
			createSensorStructure(reply);
		
		if(sensors.length != reply.message.length) {
			getSensorDataError(reply);
			return;
		}
		
		for(var i = 0; i < reply.message.length; i++)
			sensors[i] = reply.message[i];
		
		draw_texture();
		create_texture_handlers(is_admin);
		create_overlay($("#river-ov"), $("#mote-canvas") ,$("#overlay"));
		create_motes();
	}	
	else
		getSensorDataError(reply);
}

function getSensorDataError(reply) {
	console.log(reply.message);
	create_texture_placeholder($("#river-ov"));
}

function getSensorData(ae) {
	var payload = "{\"id\" : \"" + ae.id + "\"}";
	ajax_post_req(getsensordata, payload, getSensorDataSuccess, getSensorDataError);
}