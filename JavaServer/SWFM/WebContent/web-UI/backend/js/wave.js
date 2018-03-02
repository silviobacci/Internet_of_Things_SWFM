var context_wave;
var brackground, wave;
var animation_speed;

function wave_constructor(canvas, container, as) {
	animation_speed = as;
	set_wave_dimension(canvas, container);
	brackground = $('#brackground')[0];
	wave = $('#wave')[0];
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
	if(++animation_wave_position == background.width)
		animation_wave_position = 0;
	var height = background.height - wave.height;
	var threshold_height = animation_wave_position;
	context_wave.drawImage(wave, animation_wave_position, height, wave.width, wave.height);
	context_wave.drawImage(wave, animation_wave_position - wave.width, height, wave.width, wave.height);
	
	context_wave.beginPath();
	context_wave.moveTo(0,threshold_height);
	context_wave.lineTo(background.width, threshold_height);
	context_wave.lineWidth = 5;
	context_wave.strokeStyle = '#ff0000';
	context_wave.stroke();
}

function draw_wave() {
	request_timer = setInterval(wave_animation, 10);
	
	context_wave.drawImage(background, 0, 0, background.width, background.height);
}