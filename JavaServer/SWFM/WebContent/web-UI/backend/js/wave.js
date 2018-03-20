function WAVE(p) {
	this.container = $("#canvas-river-sec");
	this.current_level = $("#current-level");
	
	this.canvas = $("#river-sec")[0];
	this.background = $("#background_wave")[0];
	this.wave = $("#wave")[0];
	this.unable = $("#unable_rect")[0];
	this.click = $("#click")[0];
	
	this.period = p;
	this.position = 0;
	
	this.context = this.canvas.getContext("2d");
	
	if(this.background.width < this.container.innerWidth()) {
		this.scaling_factor = 1;
		
		this.canvas.width =  this.background.width;
		this.canvas.height = this.background.height;
	}
	else {
		this.scaling_factor = this.container.innerWidth() / this.background.width;
		
		this.canvas.width = this.container.innerWidth();
		this.canvas.height = this.background.height * this.scaling_factor;
		
		this.context.scale(this.scaling_factor, this.scaling_factor);
	}
	
	this.container.css("width", this.canvas.width + 4);
	this.container.css("height", this.canvas.height + 4);
}

WAVE.prototype.create_placeholder = function () {
	this.context.drawImage(this.unable, 0, 0, this.background.width, this.background.height);
}

WAVE.prototype.create_click_to_open = function () {
	this.context.drawImage(this.click, 0, 0, this.background.width, this.background.height);
}

WAVE.prototype.wave_animation = function (timestamp, first_time) {
	if(first_time)
		this.start_time = timestamp;
	
	var self = this;
	
	if (timestamp - this.start_time < this.period) {
		this.req = requestAnimationFrame(function(timestamp){self.wave_animation(timestamp, false);});
		return;
	}
	
	this.context.drawImage(this.background, 0, 0, this.background.width, this.background.height);
	
	if(++this.position == this.wave.width)
		this.position = 0;
	
	this.context.drawImage(this.wave, this.position, this.background.height - this.level, this.wave.width, this.wave.height);
	this.context.drawImage(this.wave, this.position - this.wave.width,  this.background.height - this.level, this.wave.width, this.wave.height);
	
	this.draw_threshold();
	
	this.req = requestAnimationFrame(function(timestamp){self.wave_animation(timestamp, true);});
}

WAVE.prototype.create_level = function (sensor) {
	var max_level = this.background.height;
	
	var max_mote = sensor.max;
	var min_mote = sensor.min;
	var level_mote = sensor.level;
	
	this.th_mote = sensor.th;
	
	this.level = Math.floor(max_level / (max_mote - min_mote) * (level_mote - min_mote));
	
	this.th = Math.floor(max_level + (-max_level / (max_mote - min_mote) * (this.th_mote - min_mote)));
}

WAVE.prototype.draw_threshold = function () {
	var max_level = this.background.height;
	
	var text = "THRESHOLD " + this.th_mote + " cm";
	
	this.context.fillStyle = "#ff0000";
	this.context.font = "20px Arial";
	this.context.textAlign = "center";
	
	if(this.th > max_level * 1/3)
		this.context.fillText(text, this.background.width/2, this.th - 10);
	else
		this.context.fillText(text, this.background.width/2, this.th + 20);
	
	this.context.beginPath();
	this.context.moveTo(0, this.th);
	this.context.lineTo(this.background.width, this.th);
	this.context.lineWidth = 2;
	this.context.strokeStyle = '#ff0000';
	this.context.stroke();
}


WAVE.prototype.draw_wave = function (sensor) {
	if(this.req != null && this.req != undefined)
		cancelAnimationFrame(this.req);
	
	this.current_level.html("CURRENT LEVEL " + sensor.level + " cm");
	
	this.create_level(sensor);
	
	var self = this;
	
	this.req = requestAnimationFrame(function(timestamp){self.wave_animation(timestamp, true);});
}
