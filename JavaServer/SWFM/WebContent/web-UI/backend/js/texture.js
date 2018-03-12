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

var context_texture;
var context_overlay;
var context_mote_canvas;
var context_dam_canvas;
var scaling_factor = 1;

var row_number;
var col_number;

var animation_position_texture = [];
var animation_period_texture;
var start_time = [];

var sd = [0, 28, 0, 21];
var ed = [9, 32, 15, 32];
var td = [3, 3, 22, 30];
var bd = [4, 4, 23, 31];

var d = [{x: tile_width * ed[0], y: tile_height * td[0], w: tile_width * 2, h: tile_height * 3},
		{x: tile_width * sd[1], y: tile_height * td[1], w: tile_width * 2, h: tile_height * 3},
		{x: tile_width * ed[2], y: tile_height * td[2], w: tile_width * 2, h: tile_height * 3},
		{x: tile_width * sd[3], y: tile_height * td[3], w: tile_width * 2, h: tile_height * 3}];

var sensors = [];
var dams = [];
var old_dams = [];

var water_left;
var water_right;
var water_top;
var water_bottom;
var water_corner_bottom_right;
var water_corner_bottom_left;
var water_corner_top_left;
var water_corner_top_right;
var ground_top;
var ground_bottom;
var wave_bottom_to_right;
var wave_bottom_to_left;
var wave_top_to_right;
var wave_top_to_left;
var wave_to_left;
var wave_to_right;
var wave_corner_bottom_right;
var wave_corner_bottom_left;
var wave_corner_top_right;
var wave_corner_top_left;
var background_texture;
var marker;
var label;
var red;
var green;
var yellow;
var grey;
var rect;

var dam_text_visible = [false, false, false, false];
var text_visible;

var selected_sensor;
var selected_dam;
var ae;

var scene_created = false;

function texture_constructor(canvas1, canvas2, canvas3, canvas4, container1, container2, ap) {
	animation_period_texture = ap;
	set_texture_dimension(canvas1, canvas2, canvas3, canvas4, container1, container2);
	create_tileset();
}

function set_texture_dimension(canvas1, canvas2, canvas3, canvas4, container1, container2) {
	context_texture = canvas1[0].getContext("2d");
	context_dam_canvas = canvas2[0].getContext("2d");
	context_mote_canvas = canvas3[0].getContext("2d");
	context_overlay = canvas4[0].getContext("2d");
	
	var min_dim = container1.innerWidth() < container1.innerHeight() ? container1.innerWidth() : container1.innerHeight();
	
	if(min_dim < size) {
		canvas1[0].width = min_dim;
		canvas1[0].height = min_dim;
		scaling_factor = min_dim/size;
		context_texture.scale(scaling_factor, scaling_factor);
	}
	else {
		canvas1[0].width = size;
		canvas1[0].height = size;
	}
	
	canvas2[0].width = canvas1[0].width;
	canvas2[0].height = canvas1[0].height;
	
	context_dam_canvas.scale(scaling_factor, scaling_factor);
	
	canvas3[0].width = canvas1[0].width;
	canvas3[0].height = canvas1[0].height;
	
	context_mote_canvas.scale(scaling_factor, scaling_factor);
	
	canvas4[0].width = canvas1[0].width;
	canvas4[0].height = canvas1[0].height;
	
	context_overlay.scale(scaling_factor, scaling_factor);
	
	container2.css("width", canvas1[0].width + 4);
	container2.css("height", canvas1[0].height + 4);
}

function create_tileset() {
	water_left = $('#water_left')[0];
	water_right = $('#water_right')[0];
	water_top = $('#water_top')[0];
	water_bottom = $('#water_bottom')[0];
	water_corner_bottom_right = $('#water_corner_bottom_right')[0];
	water_corner_bottom_left = $('#water_corner_bottom_left')[0];
	water_corner_top_left = $('#water_corner_top_left')[0];
	water_corner_top_right = $('#water_corner_top_right')[0];
	ground_top = $('#ground_top')[0];
	ground_bottom = $('#ground_bottom')[0];
	wave_bottom_to_right = $('#wave_bottom_to_right')[0];
	wave_bottom_to_left = $('#wave_bottom_to_left')[0];
	wave_top_to_right = $('#wave_top_to_right')[0];
	wave_top_to_left = $('#wave_top_to_left')[0];
	wave_to_left = $('#wave_to_left')[0];
	wave_to_right = $('#wave_to_right')[0];
	wave_corner_bottom_right = $('#wave_corner_bottom_right')[0];
	wave_corner_bottom_left = $('#wave_corner_bottom_left')[0];
	wave_corner_top_right = $('#wave_corner_top_right')[0];
	wave_corner_top_left = $('#wave_corner_top_left')[0];
	background_texture = $('#background_texture')[0];
	marker = $('#marker')[0];
	label = $('#label')[0];
	red = $('#red')[0];
	green = $('#green')[0];
	yellow = $('#yellow')[0];
	grey = $('#grey')[0];
	rect = $('#rect')[0];
}

function create_overlay(canvas1, canvas2, canvas3, canvas4) {
	canvas1.css("position", "absolute");
	canvas2.css("position", "absolute");
	canvas3.css("position", "absolute");
	canvas4.css("position", "absolute");
	
	canvas1.css("z-index", "0");
	canvas2.css("z-index", "1");
	canvas3.css("z-index", "2");
	canvas4.css("z-index", "3");
}

function create_texture_placeholder(canvas) {
	context_texture.clearRect(0, 0, canvas[0].width, canvas[0].height);
	context_dam_canvas.clearRect(0, 0, canvas[0].width, canvas[0].height);
	context_mote_canvas.clearRect(0, 0, canvas[0].width, canvas[0].height);
	context_overlay.clearRect(0, 0, canvas[0].width, canvas[0].height);
	context_texture.drawImage($("#unable_quad")[0], 0, 0, canvas[0].width, canvas[0].height);
}

function open_dam_left(timestamp, dam_index, first_time) {
	if(first_time)
		start_time[dam_index] = timestamp;
	
	first_time = false;
	
	if (timestamp - start_time[dam_index] < animation_period_texture) {
		requestAnimationFrame(function(timestamp){open_dam_left(timestamp, dam_index, first_time);});
		return;
	}
	
	var start_dam = dams[dam_index].sd;
	var end_dam = dams[dam_index].ed;
	var top_dam = dams[dam_index].td;
	var bottom_dam = dams[dam_index].bd;
	var current_index = end_dam - animation_position_texture[dam_index];
	
	first_time = true;
	
	switch(current_index) {
		case end_dam:
			context_dam_canvas.drawImage(wave_corner_top_left, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_corner_bottom_left, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[dam_index]++;
			requestAnimationFrame(function(timestamp){open_dam_left(timestamp, dam_index, first_time);});
			break;
		case end_dam - 1:
			context_dam_canvas.drawImage(water_corner_top_left, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(water_corner_bottom_left, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_top_to_left, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_bottom_to_left, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[dam_index]++;
			requestAnimationFrame(function(timestamp){open_dam_left(timestamp, dam_index, first_time);});
			break;
		case start_dam - 1:
			context_dam_canvas.drawImage(water_top, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(water_bottom, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[dam_index] = 0;
			if(dam_text_visible[dam_index]) {
				if(dam_index == 1 || dam_index == 3)
					var image = {x : dams[dam_index].x - 6 * tile_width, y : dams[dam_index].y, w : 6 * tile_width, h : 2*tile_height}; 
				else
					var image = {x : dams[dam_index].x + tile_width, y : dams[dam_index].y, w : 6 * tile_width, h : 2*tile_height}; 
				context_overlay.drawImage(rect, image.x, image.y, image.w, image.h);
				context_overlay.fillStyle="#000000";
				if(!dams[dam_index].state)
					var text = "click to open";
				else
					var text = "click to close";
				context_overlay.fillText(text, image.x + 21, image.y + 19);
			}
			break;
		default:
			context_dam_canvas.drawImage(water_top, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(water_bottom, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_top_to_left, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_bottom_to_left, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[dam_index]++;
			requestAnimationFrame(function(timestamp){open_dam_left(timestamp, dam_index, first_time);});
			break;
	}
}

function close_dam_left(timestamp, dam_index, first_time) {
	if(first_time)
		start_time[dam_index] = timestamp;
	
	first_time = false;
		
	if (timestamp - start_time[dam_index] < animation_period_texture) {
		requestAnimationFrame(function(timestamp){close_dam_left(timestamp, dam_index, first_time);});
		return;
	}
	
	var start_dam = dams[dam_index].sd;
	var end_dam = dams[dam_index].ed;
	var top_dam = dams[dam_index].td;
	var bottom_dam = dams[dam_index].bd;
	var current_index = end_dam - animation_position_texture[dam_index];
	
	first_time = true;
	
	switch(current_index) {
		case end_dam:
			context_dam_canvas.drawImage(water_left, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(water_left, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_top_to_right, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_bottom_to_right, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[dam_index]++;
			requestAnimationFrame(function(timestamp){close_dam_left(timestamp, dam_index, first_time);});
			break;
		case start_dam:
			context_dam_canvas.drawImage(ground_top, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(ground_bottom, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[dam_index] = 0;
			if(dam_text_visible[dam_index]) {
				if(dam_index == 1 || dam_index == 3)
					var image = {x : dams[dam_index].x - 6 * tile_width, y : dams[dam_index].y, w : 6 * tile_width, h : 2*tile_height}; 
				else
					var image = {x : dams[dam_index].x + tile_width, y : dams[dam_index].y, w : 6 * tile_width, h : 2*tile_height}; 
				context_overlay.drawImage(rect, image.x, image.y, image.w, image.h);
				context_overlay.fillStyle="#000000";
				if(!dams[dam_index].state)
					var text = "click to open";
				else
					var text = "click to close";
				context_overlay.fillText(text, image.x + 21, image.y + 19);
			}
			break;
		default:
			context_dam_canvas.drawImage(ground_top, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(ground_bottom, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_top_to_right, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_bottom_to_right, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[dam_index]++;
			requestAnimationFrame(function(timestamp){close_dam_left(timestamp, dam_index, first_time);});
			break;
	}
}

function open_dam_right(timestamp, dam_index, first_time) {
	if(first_time)
		start_time[dam_index] = timestamp;
	
	first_time = false;
		
	if (timestamp - start_time[dam_index] < animation_period_texture) {
		requestAnimationFrame(function(timestamp){open_dam_right(timestamp, dam_index, first_time);});
		return;
	}
	
	var start_dam = dams[dam_index].sd;
	var end_dam = dams[dam_index].ed;
	var top_dam = dams[dam_index].td;
	var bottom_dam = dams[dam_index].bd;
	var current_index = start_dam + animation_position_texture[dam_index];
	
	first_time = true;
	
	switch(current_index) {
		case start_dam:
			context_dam_canvas.drawImage(wave_corner_top_right, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_corner_bottom_right, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[dam_index]++;
			requestAnimationFrame(function(timestamp){open_dam_right(timestamp, dam_index, first_time);});
			break;
		case start_dam + 1:
			context_dam_canvas.drawImage(water_corner_top_right, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(water_corner_bottom_right, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_top_to_right, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_bottom_to_right, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[dam_index]++;
			requestAnimationFrame(function(timestamp){open_dam_right(timestamp, dam_index, first_time);});
			break;
		case end_dam + 1:
			context_dam_canvas.drawImage(water_top, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(water_bottom, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[dam_index] = 0;
			if(dam_text_visible[dam_index]) {
				if(dam_index == 1 || dam_index == 3)
					var image = {x : dams[dam_index].x - 6 * tile_width, y : dams[dam_index].y, w : 6 * tile_width, h : 2*tile_height}; 
				else
					var image = {x : dams[dam_index].x + tile_width, y : dams[dam_index].y, w : 6 * tile_width, h : 2*tile_height}; 
				context_overlay.drawImage(rect, image.x, image.y, image.w, image.h);
				context_overlay.fillStyle="#000000";
				if(!dams[dam_index].state)
					var text = "click to open";
				else
					var text = "click to close";
				context_overlay.fillText(text, image.x + 21, image.y + 19);
			}
			break;
		default:
			context_dam_canvas.drawImage(water_top, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(water_bottom, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_top_to_right, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_bottom_to_right, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[dam_index]++;
			requestAnimationFrame(function(timestamp){open_dam_right(timestamp, dam_index, first_time);});
			break;
	}
}

function close_dam_right(timestamp, dam_index, first_time) {
	if(first_time)
		start_time[dam_index] = timestamp;
	
	first_time = false;
	
	if (timestamp - start_time[dam_index] < animation_period_texture) {
		requestAnimationFrame(function(timestamp){close_dam_right(timestamp, dam_index, first_time);});
		return;
	}
	
	var start_dam = dams[dam_index].sd;
	var end_dam = dams[dam_index].ed;
	var top_dam = dams[dam_index].td;
	var bottom_dam = dams[dam_index].bd;
	var current_index = start_dam + animation_position_texture[dam_index];
	
	first_time = true;
	
	switch(current_index) {
		case start_dam:
			context_dam_canvas.drawImage(water_right, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(water_right, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_top_to_left, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_bottom_to_left, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[dam_index]++;
			requestAnimationFrame(function(timestamp){close_dam_right(timestamp, dam_index, first_time);});
			break;
		case end_dam:
			context_dam_canvas.drawImage(ground_top, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(ground_bottom, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[dam_index] = 0;
			if(dam_text_visible[dam_index]) {
				if(dam_index == 1 || dam_index == 3)
					var image = {x : dams[dam_index].x - 6 * tile_width, y : dams[dam_index].y, w : 6 * tile_width, h : 2*tile_height}; 
				else
					var image = {x : dams[dam_index].x + tile_width, y : dams[dam_index].y, w : 6 * tile_width, h : 2*tile_height}; 
				context_overlay.drawImage(rect, image.x, image.y, image.w, image.h);
				context_overlay.fillStyle="#000000";
				if(!dams[dam_index].state)
					var text = "click to open";
				else
					var text = "click to close";
				context_overlay.fillText(text, image.x + 21, image.y + 19);
			}
			break;
		default:
			context_dam_canvas.drawImage(ground_top, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(ground_bottom, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_top_to_left, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_dam_canvas.drawImage(wave_bottom_to_left, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[dam_index]++;
			requestAnimationFrame(function(timestamp){close_dam_right(timestamp, dam_index, first_time);});
			break;
	}
}

function dam(dam_index, right) {
	if(animation_position_texture[dam_index] != 0)
		return;
	
	if(right == true) {
		if (dams[dam_index].state == false)
			requestAnimationFrame(function(timestamp){close_dam_right(timestamp, dam_index, true);});
		else
			requestAnimationFrame(function(timestamp){open_dam_right(timestamp, dam_index, true);});
	}
	else {
		if (dams[dam_index].state == false)
			requestAnimationFrame(function(timestamp){close_dam_left(timestamp, dam_index, true);});
		else
			requestAnimationFrame(function(timestamp){open_dam_left(timestamp, dam_index, true);});
	}
}

function set_water_level(mote_index, level) {
	var max_level = 32;
	
	var max_mote = sensors[mote_index].max;
	var min_mote = sensors[mote_index].min;
	var level_mote = sensors[mote_index].level;
	
	var level = Math.floor(max_level / (max_mote - min_mote) * (level_mote - min_mote));
	
	var th = Math.floor(max_level / (max_mote - min_mote) * (sensors[mote_index].th - min_mote));
	
	if(level >= th){
		for(var i = 0; i < level; i++)
			context_mote_canvas.drawImage(red, sensors[mote_index].x + i, sensors[mote_index].y, tile_level_width, tile_level_height);
		for(var i = level; i < max_level; i++)
			context_mote_canvas.drawImage(grey, sensors[mote_index].x + i, sensors[mote_index].y, tile_level_width, tile_level_height);
	}
	else if (level < th && level >=  Math.floor(th/2)) {
		for(var i = 0; i < level; i++)
			context_mote_canvas.drawImage(yellow, sensors[mote_index].x + i, sensors[mote_index].y, tile_level_width, tile_level_height);
		for(var i = level; i < max_level; i++)
			context_mote_canvas.drawImage(grey, sensors[mote_index].x + i, sensors[mote_index].y, tile_level_width, tile_level_height);
	}
	else {
		for(var i = 0; i < level; i++)
			context_mote_canvas.drawImage(green, sensors[mote_index].x + i, sensors[mote_index].y, tile_level_width, tile_level_height);
		for(var i = level; i < max_level; i++)
			context_mote_canvas.drawImage(grey, sensors[mote_index].x + i, sensors[mote_index].y, tile_level_width, tile_level_height);
	}
}

function create_motes() {
	for(var mote_index = 0; mote_index < sensors.length; mote_index++)
		draw_mote(mote_index, sensors[mote_index].lat,  sensors[mote_index].lng);
	
	for(var mote_index = 0; mote_index < sensors.length; mote_index++)
		set_water_level(mote_index);	
}

function draw_mote(mote_index) {
	context_mote_canvas.drawImage(label, sensors[mote_index].x - 8, sensors[mote_index].y - 6, 3 * tile_width, tile_height);
	context_mote_canvas.drawImage(marker, sensors[mote_index].x + tile_width - 8, sensors[mote_index].y + tile_height - 6, tile_width, tile_height);
}

function draw_texture_background(canvas) {
	context_texture.drawImage(background_texture, 0, 0, background_texture.width, background_texture.height);
}

function contains(r, x, y) {
	x = x / scaling_factor;
	y = y / scaling_factor;
	return (x >= r.x && x <= r.x + r.w && y >= r.y && y <= r.y + r.h)
}

function create_texture_handlers(is_admin) {
	$('#overlay').mousemove(function(ev) {
		var x = ev.pageX - $(this).offset().left;
		var y = ev.pageY - $(this).offset().top;
		
		for(var mote_index = 0; mote_index < sensors.length; mote_index++)
			if (contains(sensors[mote_index], x, y)){
				$(this).css('cursor', 'pointer');
				if(!text_visible) {
					if(mote_index == 1 || mote_index == 3)
						var image = {x : sensors[mote_index].x - 104, y : sensors[mote_index].y - 8, w : 6 * tile_width, h : 2*tile_height}; 
					else
						var image = {x : sensors[mote_index].x + 40, y : sensors[mote_index].y - 8, w : 6 * tile_width, h : 2*tile_height}; 
					
					context_overlay.drawImage(rect, image.x, image.y, image.w, image.h);
					context_overlay.fillStyle="#000000";
					var text = "click for more info";
					context_overlay.fillText(text, image.x + 10, image.y + 19);
				}
				text_visible = true;
				return;
			}
		
		for(var dam_index = 0; dam_index < dams.length; dam_index++)
			if (is_admin && contains(dams[dam_index], x, y)){
				$(this).css('cursor', 'pointer');
				if(!dam_text_visible[dam_index]) {
					if(dam_index == 0 || dam_index == 2)
						var image = {x : dams[dam_index].x + 16, y : dams[dam_index].y, w : 6 * tile_width, h : 2*tile_height};
					else
						var image = {x : dams[dam_index].x - 96, y : dams[dam_index].y, w : 6 * tile_width, h : 2 * tile_height}; 
					
					context_overlay.drawImage(rect, image.x, image.y, image.w, image.h);
					context_overlay.fillStyle="#000000";
					if(!dams[dam_index].state)
						var text = "click to open";
					else
						var text = "click to close";
					context_overlay.fillText(text, image.x + 21, image.y + 19);
				}
				dam_text_visible[dam_index] = true;
				return;
			}
		
		$(this).css('cursor', 'default');
		text_visible = false;
		dam_text_visible = [false, false, false, false];
		context_overlay.clearRect(0, 0, size, size);
	});
	
	$('#overlay').click(function(ev) {
		var x = ev.pageX - $(this).offset().left;
		var y = ev.pageY - $(this).offset().top;

		for(var mote_index = 0; mote_index < sensors.length; mote_index++)
			if (contains(sensors[mote_index], x, y)){
				selected_sensor = mote_index;
				draw_info();
				draw_wave();
				draw_threshold();
				getHistoryData(false);
				return;
			}
		
		for(var dam_index = 0; dam_index < dams.length; dam_index++)
			if (contains(dams[dam_index], x, y)){
				selected_dam = dam_index;
				set_dam_state();
				return;
			}
	});
}

function updateScene() {
	create_motes();
	
	if(selected_sensor != undefined) {
		draw_info();
		draw_wave();
		draw_threshold();
		getHistoryData(chart_built);
	}
	
	for(var dam_index = 0; dam_index < dams.length; dam_index++) {
		if (old_dams[dam_index].state != dams[dam_index].state) {
			if(dam_index == 0 || dam_index == 2)
				dam(dam_index, false);
			else
				dam(dam_index, true);
		}
	}
}

function construct_objects() {
	texture_constructor($("#river-ov"), $("#dam-canvas"), $("#mote-canvas"), $("#overlay"), $("#canvas-left-container"), $("#canvas-river-ov"), 50);
	wave_constructor($("#river-sec"), $("#canvas-right-container"), $("#canvas-river-sec"), 1);
	history_constructor($("#river-sec"), $("#chart-history"));
	threshold_constructor($("#river-sec"), $("#threshold"));
	alert_constructor($("#river-sec"), $("#alert"));
	info_constructor($("#river-sec"), $("#info"));
}

function createScene() {
	construct_objects();
	
	draw_texture_background($("#river-ov"));
	create_motes();
	create_texture_handlers(is_admin);
	
	create_wave_click_to_open($("#river-sec"));
	create_threshold_click_to_open($("#river-sec"), $("#threshold"));
	create_history_click_to_open($("#river-sec"), $("#chart-history"));
	create_info_click_to_open();
	
	for(var dam_index = 0; dam_index < dams.length; dam_index++)
		if (dams[dam_index].state == true)
			if(dam_index == 0 || dam_index == 2)
				dam(dam_index, false);
			else
				dam(dam_index, true);
	
	create_overlay($("#river-ov"), $("#dam-canvas"), $("#mote-canvas"), $("#overlay"));
}

function getDamDataSuccess(reply) {
	if(reply.error == false) {
		if(d.length != reply.message.length) {
			getDataError(reply);
			return;
		}
		
		for(var dam_index = 0; dam_index < d.length; dam_index++) {
			start_time[dam_index] = 0;
			animation_position_texture[dam_index] = 0;
			
			old_dams[dam_index] = dams[dam_index];
			dams[dam_index] = reply.message[dam_index];
			
			dams[dam_index].x = d[dam_index].x;
			dams[dam_index].y = d[dam_index].y;
			dams[dam_index].w = d[dam_index].w;
			dams[dam_index].h = d[dam_index].h;
			dams[dam_index].sd = sd[dam_index];
			dams[dam_index].ed = ed[dam_index];
			dams[dam_index].td = td[dam_index];
			dams[dam_index].bd = bd[dam_index];
		}
		
		if(!scene_created) {
			createScene();
			scene_created = true;
		}
		else
			updateScene();
	}	
	else
		getDataError(reply);
}

function getSensorDataSuccess(reply) {
	if(reply.error == false) {
		if(sensors.length != 0 && sensors.length != reply.message.length) {
			getDataError(reply);
			return;
		}
		
		for(var i = 0; i < reply.message.length; i++) {
			sensors[i] = reply.message[i];
			sensors[i].x = (sensors[i].lng - 1) * tile_width + 8;
			sensors[i].y = (sensors[i].lat - 1) * tile_height + 6;
			sensors[i].w = 3 * tile_width;
			sensors[i].h = 2 * tile_height;
		}
		
		getDamData();
	}	
	else
		getDataError(reply);
}

function getDataError(reply) {
	create_texture_placeholder($("#river-ov"));
	create_wave_placeholder($("#river-sec"));
	create_history_placeholder($("#river-sec"), $("#chart-history"));
	create_threshold_placeholder($("#river-sec"), $("#threshold"));
	create_alert_placeholder($("#river-sec"), $("#alert"));
	create_info_placeholder();
}

function getSensorData(a) {
	ae = a;
	var payload = "{\"id\" : \"" + ae.id + "\"}";
	ajax_post_req(getsensordata, payload, getSensorDataSuccess, getDataError);
}

function getDamData() {
	var payload = "{\"id\" : \"" + ae.id + "\"}";
	ajax_post_req(getdamdata, payload, getDamDataSuccess, getDataError);
}

function set_dam_state_success(reply) {
	if(reply.error == true)
		set_dam_state_error(reply);
}

function set_dam_state_error(reply) {
	console.log(reply.message);
}

function set_dam_state() {
	var payload = "{\"ae\" : \"" + ae.id + "\", \"id\" : \"" + dams[selected_dam].id + "\", \"data\" : " + !dams[selected_dam].state + "}";
	ajax_post_req(setdamdata, payload, set_dam_state_success, set_dam_state_error);
}

var start_time_refresh_sensor;
var refresh_sensor_period = 5000;
var refresh_sensor_req;

function refresh_sensors(timestamp, ae, first_time) {		
	if(first_time) {
		var id = ae.id.substring(ae.id.lastIndexOf("/") + 1, ae.id.length);
		$('#modal-label').html(ae.name + " - " + id);
		draw_alert(ae);
		getSensorData(ae);
		start_time_refresh_sensor = timestamp;
	}
	
	if (timestamp - start_time_refresh_sensor < refresh_sensor_period)
		refresh_sensor_req = requestAnimationFrame(function(timestamp){refresh_sensors(timestamp, ae, false);});
	else
		refresh_sensor_req = requestAnimationFrame(function(timestamp){refresh_sensors(timestamp, ae, true);});
}