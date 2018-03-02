var context_wave;

var animation_speed_wave;
var animation_position_wave = 0;
var animation_timer_wave;

var brackground, wave;

function wave_constructor(canvas, container, as) {
	brackground = $('#brackground')[0];
	wave = $('#wave')[0];
	animation_speed_wave = as;
	set_wave_dimension(canvas, container);
}

function set_wave_dimension(canvas, container) {
	context_wave = canvas[0].getContext("2d");
	
	var min_dim = background.width < container.innerWidth() ? background.width : container.innerWidth();
	
	canvas[0].width = min_dim;
	canvas[0].height = background.height;
	
	if(min_dim == container.innerWidth()) {
		var scaling_factor = min_dim/background.width;
		canvas[0].height = background.height * scaling_factor;
		context_wave.scale(scaling_factor, scaling_factor);
	}
}

function wave_animation() {
	context_wave.drawImage(background, 0, 0, background.width, background.height);
	if(++animation_position_wave == background.width)
		animation_position_wave = 0;
	var height = background.height - wave.height;
	var threshold_height = animation_position_wave;
	context_wave.drawImage(wave, animation_position_wave, height, wave.width, wave.height);
	context_wave.drawImage(wave, animation_position_wave - wave.width, height, wave.width, wave.height);
	
	context_wave.beginPath();
	context_wave.moveTo(0,threshold_height);
	context_wave.lineTo(background.width, threshold_height);
	context_wave.lineWidth = 5;
	context_wave.strokeStyle = '#ff0000';
	context_wave.stroke();
}

function draw_wave() {
	context_wave.drawImage(background, 0, 0, background.width, background.height);
	animation_timer_wave = setInterval(wave_animation, animation_speed_wave);
}