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

var row_number;
var col_number;

var dam_open = [false, false, false, false];
var animation_position_texture = [0, 0, 0, 0];
var animation_timer_texture = [0, 0, 0, 0];
var animation_speed_texture;

var sd = [0, 28, 0, 21];
var ed = [9, 32, 15, 32];
var td = [3, 3, 22, 30];
var bd = [4, 4, 23, 31];

var d0 = {x: (tile_width - 1) * ed[0], y: tile_height * td[0], w: tile_width * 2, h: tile_height * 3};
var d1 = {x: tile_width * sd[1], y: tile_height * td[1], w: tile_width * 2, h: tile_height * 3};
var d2 = {x: (tile_width - 1) * ed[2], y: tile_height * td[2], w: tile_width * 2, h: tile_height * 3};
var d3 = {x: tile_width * sd[3], y: tile_height * td[3], w: tile_width * 2, h: tile_height * 3};

var n_motes;
var mote = [];

var g, l, w, r, u, b, w1, w2, w3, w4, g1, g2, g3, g4, u2, b2, o1, o2, o3, o4, o5, o6, o7, o8, o9, o0;
var t1, t2, t3, m1, m2, m3, m4, m5, m6, m7, m8, f2, f5;
var h01, h02, h03, h04, h11, h12, h13, h14, h21, h22, h23, h24, h31, h32, h33, h34, h41, h42, h43, h44;
var fm1, fm2, marker, label, red, green, yellow, grey;

function texture_constructor(canvas, container, as) {
	animation_speed_texture = as;
	set_texture_dimension(canvas, container);
	create_tileset();
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

function set_texture_dimension(canvas, container) {
	context_texture = canvas[0].getContext("2d");
	
	var min_dim = container.innerWidth() < container.innerHeight() ? container.innerWidth() : container.innerHeight();
	
	if(min_dim < size) {
		canvas[0].width = min_dim;
		canvas[0].height = min_dim;
		var scaling_factor = min_dim/size;
		context_texture.scale(scaling_factor, scaling_factor);
	}
	else {
		canvas[0].width = tile_height * row_number;
		canvas[0].height = tile_width * row_number;
	}
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
}

function open_dam_left(index_dam) {
	var start_dam = sd[index_dam];
	var end_dam = ed[index_dam];
	var top_dam = td[index_dam];
	var bottom_dam = bd[index_dam];
	var current_index = end_dam - animation_position_texture[index_dam];
	
	switch(current_index) {
		case end_dam:
			context_texture.drawImage(o4, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o5, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			break;
		case end_dam - 1:
			context_texture.drawImage(g3, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(g2, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o1, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o3, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			break;
		case start_dam - 1:
			context_texture.drawImage(u, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			dam_open[index_dam] = !dam_open[index_dam];
			animation_position_texture[index_dam] = 0;
			clearInterval(animation_timer_texture[index_dam]);
			break;
		default:
			context_texture.drawImage(u, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o1, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o3, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			break;
	}
}

function close_dam_left(index_dam) {
	var start_dam = sd[index_dam];
	var end_dam = ed[index_dam];
	var top_dam = td[index_dam];
	var bottom_dam = bd[index_dam];
	var current_index = start_dam + animation_position_texture[index_dam];
	
	switch(current_index) {
		case start_dam:
			context_texture.drawImage(o1, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o3, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			break;
		case end_dam:
			context_texture.drawImage(u2, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b2, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(l, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(l, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			dam_open[index_dam] = !dam_open[index_dam];
			animation_position_texture[index_dam] = 0;
			clearInterval(animation_timer_texture[index_dam]);
			break;
		default:
			context_texture.drawImage(u2, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b2, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o1, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o3, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			break;
	}
}

function open_dam_right(index_dam) {
	var start_dam = sd[index_dam];
	var end_dam = ed[index_dam];
	var top_dam = td[index_dam];
	var bottom_dam = bd[index_dam];
	var current_index = start_dam + animation_position_texture[index_dam];
	
	switch(current_index) {
		case start_dam:
			context_texture.drawImage(o6, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o8, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			break;
		case start_dam + 1:
			context_texture.drawImage(g4, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(g1, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o9, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o0, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			break;
		case end_dam + 1:
			context_texture.drawImage(u, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			dam_open[index_dam] = !dam_open[index_dam];
			animation_position_texture[index_dam] = 0;
			clearInterval(animation_timer_texture[index_dam]);
			break;
		default:
			context_texture.drawImage(u, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o9, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o0, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			break;
	}
}

function close_dam_right(index_dam) {
	var start_dam = sd[index_dam];
	var end_dam = ed[index_dam];
	var top_dam = td[index_dam];
	var bottom_dam = bd[index_dam];
	var current_index = end_dam - animation_position_texture[index_dam];
	
	switch(current_index) {
		case end_dam:
			context_texture.drawImage(o9, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o0, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			break;
		case start_dam:
			context_texture.drawImage(u2, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b2, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(r, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(r, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			dam_open[index_dam] = !dam_open[index_dam];
			animation_position_texture[index_dam] = 0;
			clearInterval(animation_timer_texture[index_dam]);
			break;
		default:
			context_texture.drawImage(u2, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(b2, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o9, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			context_texture.drawImage(o0, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			animation_position_texture[index_dam]++;
			break;
	}
}

function dam(index_dam, right) {
	clearInterval(animation_timer_texture[index_dam]);
	
	if(right == true) {
		if (dam_open[index_dam] == true)
			animation_timer_texture[index_dam] = setInterval(close_dam_right, animation_speed_texture, index_dam);
		else
			animation_timer_texture[index_dam] = setInterval(open_dam_right, animation_speed_texture, index_dam);
	}
	else {
		if (dam_open[index_dam] == true)
			animation_timer_texture[index_dam] = setInterval(close_dam_left, animation_speed_texture, index_dam);
		else
			animation_timer_texture[index_dam] = setInterval(open_dam_left, animation_speed_texture, index_dam);
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

function add_mote(new_mote_row, new_mote_columns) {
	n_motes++;
	draw_mote(n_motes - 1, new_motes_row[n_motes - 1], new_motes_columns[n_motes - 1]);
}

function create_motes(new_motes_row, new_motes_columns) {
	n_motes = new_motes_row.length;
	for(var mote_index = 0; mote_index < n_motes; mote_index++)
		draw_mote(mote_index, new_motes_row[mote_index],  new_motes_columns[mote_index]);
}

function draw_mote(mote_index, row, column) {
	mote[mote_index] = {x : (column-1)*tile_width+8, y : (row-1)*tile_height+6, w : tile_width*3, h : tile_height};
	context_texture.drawImage(label, (column-1)*tile_width, (row-1)*tile_height, tile_width*3, tile_height);
	context_texture.drawImage(marker, column*tile_width, row*tile_height, tile_width, tile_height);
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

function create_texture_handlers() {
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