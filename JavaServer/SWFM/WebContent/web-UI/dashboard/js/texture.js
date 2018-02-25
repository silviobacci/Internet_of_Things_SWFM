// ----------------------------
// MAIN.JS
// It contains js function
// to prepare home dashboard page
// ------------------------------

class texture {
	var size = 528;
	
	var tile_width = 16;
	var tile_height = 16;
	
	var tile_level_width = 1;
	var tile_level_height = 4;
	var min_level = 8;
	var av_level = 24;
	var max_level = 32;
	
	var animation_position = [0, 0, 0, 0];
	var animation_timer = [0, 0, 0, 0];
	var animation_speed;
	
	var row_number;
	var col_number;
	
	constructor(canvas, container, as) {
		animation_speed = as;
		init(canvas);
	}
	
	function init(canvas, container) {
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
		
		context = canvas[0].getContext("2d");
		
		var min_dim = container.innerWidth() < container.innerHeight() ? container.innerWidth() : container.innerHeight();
		
		if(min_dim < size) {
			canvas[0].width = min_dim;
			canvas[0].height = min_dim;
			var scaling_factor = min_dim/size;
			context.scale(scaling_factor, scaling_factor);
		}
		else {
			canvas[0].width = tile_height * row_number;
			canvas[0].height = tile_width * row_number;
		}
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
				context.drawImage(o0, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
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
		clearInterval(animation_timer[index_dam]);
		
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
	
	function set_water_level(mote_index, level) {
		if(level <= min_level){
			for(var i = 0; i < level; i++)
				context.drawImage(red, mote[mote_index].x + i, mote[mote_index].y, tile_level_width, tile_level_height);
			for(var i = level; i < max_level; i++)
				context.drawImage(grey, mote[mote_index].x + i, mote[mote_index].y, tile_level_width, tile_level_height);
		}
		else if (level > min_level && level <= av_level) {
			for(var i = 0; i < level; i++)
				context.drawImage(yellow, mote[mote_index].x + i, mote[mote_index].y, tile_level_width, tile_level_height);
			for(var i = level; i < max_level; i++)
				context.drawImage(grey, mote[mote_index].x + i, mote[mote_index].y, tile_level_width, tile_level_height);
		}
		else {
			for(var i = 0; i < level; i++)
				context.drawImage(green, mote[mote_index].x + i, mote[mote_index].y, tile_level_width, tile_level_height);
			for(var i = level; i < max_level; i++)
				context.drawImage(grey, mote[mote_index].x + i, mote[mote_index].y, tile_level_width, tile_level_height);
		}
	}
}


var context, context_wave;
var n_motes = 6;
var scaling_factor;

var mote = [];

create_texture();

var dam_open = [false, false, false, false];

var animation_position = [0, 0, 0, 0];
var animation_timer = [0, 0, 0, 0];
var animation_speed = 50;
var animation_wave_position = 0;

var sd = [0, 28, 0, 21];
var ed = [9, 32, 15, 32];
var td = [3, 3, 22, 30];
var bd = [4, 4, 23, 31];

var d0, d1, d2, d3;

var g, l, w, r, u, b, w1, w2, w3, w4, g1, g2, g3, g4, u2, b2, o1, o2, o3, o4, o5, o6, o7, o8, o9, o0;
var t1, t2, t3, m1, m2, m3, m4, m5, m6, m7, m8, f2, f5;
var h01, h02, h03, h04, h11, h12, h13, h14, h21, h22, h23, h24, h31, h32, h33, h34, h41, h42, h43, h44;
var fm1, fm2, marker, label, red, green, yellow, grey;
var brackground, wave;

var chart;

function

function draw_texture() {
	//request_timer = setInterval(request_to_server, 1000);
	
	
	
	var mote_index = 0;
	
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
				case "[":
					context.drawImage(h01, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "]":
					context.drawImage(h02, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "+":
					context.drawImage(h03, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "*":
					context.drawImage(h04, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "\\":
					context.drawImage(h11, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "|":
					context.drawImage(h12, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "\"":
					context.drawImage(h13, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "£":
					context.drawImage(h14, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "%":
					context.drawImage(h21, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "&":
					context.drawImage(h22, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "/":
					context.drawImage(h23, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "(":
					context.drawImage(h24, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case ")":
					context.drawImage(h31, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "=":
					context.drawImage(h32, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "?":
					context.drawImage(h33, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "'":
					context.drawImage(h34, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "^":
					context.drawImage(h41, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "ì":
					context.drawImage(h42, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "è":
					context.drawImage(h43, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "é":
					context.drawImage(h44, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "ò":
					context.drawImage(fm1, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "@":
					context.drawImage(fm2, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				case "ç":
					mote[mote_index] = {x : (j-1)*tile_width+8, y : (i-1)*tile_height+6, w : tile_width*3, h : tile_height};
					mote_index++;
					context.drawImage(label, (j-1)*tile_width, (i-1)*tile_height, tile_width*3, tile_height);
					context.drawImage(marker, j*tile_width, i*tile_height, tile_width, tile_height);
					break;
				default:
					context.drawImage(g, j*tile_width, i*tile_height, tile_width, tile_height);
			}
		}
	}
	
	d0 = {x: (tile_width - 1) * ed[0], y: tile_height * td[0], w: tile_width * 2, h: tile_height * 3};
	d1 = {x: tile_width * sd[1], y: tile_height * td[1], w: tile_width * 2, h: tile_height * 3};
	d2 = {x: (tile_width - 1) * ed[2], y: tile_height * td[2], w: tile_width * 2, h: tile_height * 3};
	d3 = {x: tile_width * sd[3], y: tile_height * td[3], w: tile_width * 2, h: tile_height * 3};
	
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
}}
