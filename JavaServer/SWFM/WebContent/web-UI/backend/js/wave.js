var context_wave;

var animation_position_wave;
var animation_period_wave;

var background_wave, wave;

var start_time_wave;

var req;

function wave_constructor(canvas, container, container2, ap) {
	background_wave = $('#background_wave')[0];
	wave = $('#wave')[0];
	animation_period_wave = ap;
	animation_position_wave = 0;
	set_wave_dimension(canvas, container, container2);
}

function set_wave_dimension(canvas, container, container2) {
	context_wave = canvas[0].getContext("2d");
	
	var min_dim = background_wave.width < container.innerWidth() ? background_wave.width : container.innerWidth();
	
	canvas[0].width = min_dim;
	canvas[0].height = background_wave.height;
	
	if(min_dim == container.innerWidth()) {
		var scaling_factor = min_dim/background_wave.width;
		canvas[0].height = background_wave.height * scaling_factor;
		context_wave.scale(scaling_factor, scaling_factor);
	}
	
	container2.css("width", canvas[0].width + 4);
	container2.css("height", canvas[0].height + 4);
}

function create_wave_placeholder(canvas) {
	context_wave.drawImage($("#unable_rect")[0], 0, 0, canvas[0].width, canvas[0].height);
}

function create_wave_click_to_open(canvas) {
	context_wave.drawImage($("#click")[0], 0, 0, canvas[0].width, canvas[0].height);
}

function wave_animation(timestamp, mote_index, level, first_time) {		
	if(first_time)
		start_time_wave = timestamp;
	
	first_time = false;
	
	if (timestamp - start_time_wave < animation_period_wave) {
		req = requestAnimationFrame(function(timestamp){wave_animation(timestamp, mote_index, level, first_time);});
		return;
	}
	
	context_wave.drawImage(background_wave, 0, 0, background_wave.width, background_wave.height);
	if(++animation_position_wave == background_wave.width)
		animation_position_wave = 0;
	context_wave.drawImage(wave, animation_position_wave, background_wave.height - level, wave.width, wave.height);
	context_wave.drawImage(wave, animation_position_wave - wave.width,  background_wave.height - level, wave.width, wave.height);
	
	level = create_threshold_level(mote_index);
	
	first_time = true;
	req = requestAnimationFrame(function(timestamp){wave_animation(timestamp, mote_index, level, first_time);});
}

function create_threshold_level(mote_index) {
	var max_level = background_wave.height;
	
	var max_mote = sensors[mote_index].max;
	var min_mote = sensors[mote_index].min;
	var level_mote = sensors[mote_index].level;
	
	var level = Math.floor(max_level / (max_mote - min_mote) * (level_mote - min_mote));
	
	var th = Math.floor(max_level / (max_mote - min_mote) * (sensors[mote_index].th - min_mote));
	
	var text = "THRESHOLD " + sensors[mote_index].th + " cm";
	
	context_wave.fillStyle="#ff0000";
	context_wave.font="20px Arial";
	context_wave.textAlign = "center"; 
	context_wave.fillText(text, background_wave.width/2, th - 10);
	
	context_wave.beginPath();
	context_wave.moveTo(0, th);
	context_wave.lineTo(background_wave.width, th);
	context_wave.lineWidth = 2;
	context_wave.strokeStyle = '#ff0000';
	context_wave.stroke();
	
	return level;
}

function draw_wave(mote_index) {
	if(req != null && req != undefined)
		cancelAnimationFrame(req);
	$('#current-level').html("CURRENT LEVEL " + sensors[mote_index].level + " cm");
	var first_time = true;
	var level = create_threshold_level(mote_index);
	req = requestAnimationFrame(function(timestamp){wave_animation(timestamp, mote_index, level, first_time);});
}