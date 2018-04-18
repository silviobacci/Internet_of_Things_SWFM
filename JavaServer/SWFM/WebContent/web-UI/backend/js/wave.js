class Wave {
	constructor() {
		this.container_name = "canvas-river-sec";
		this.container1 = $("#canvas-left-container");
		this.container2 = $("#canvas-river-sec");
		
		this.current_level = $("#current-level");
		
		this.to_hide = $("#river-sec");
		this.canvas = $("#river-sec")[0];
		this.background = $("#background_wave")[0];
		this.wave = $("#wave")[0];
		this.unable = $("#unable_rect")[0];
		this.click = $("#click")[0];
		
		this.position = 0;
		
		this.context = this.canvas.getContext("2d");
		
		var width = this.container1.innerWidth() < this.background.width ? this.container1.innerWidth() : this.background.width;
				
		if(width < this.background.width) {
			this.scaling_factor = width / this.background.width;
			
			this.canvas.width = width;
			this.canvas.height = this.background.height * this.scaling_factor;
			
			this.context.scale(this.scaling_factor, this.scaling_factor);
		}
		else {
			this.scaling_factor = 1;
			
			this.canvas.width =  this.background.width;
			this.canvas.height = this.background.height;
		}
		
		this.container2.css("width", this.canvas.width + 4);
		this.container2.css("height", this.canvas.height + 4);
	}
	
	draw_image(container, image) {
		var is_inside = container[0].clientWidth >= image.width &&  container[0].clientHeight >= image.height ? true : false;
		
		if(!is_inside) {
			var check_width = (container[0].clientWidth < image.width && container[0].clientHeight >= image.height) ? true : false;
			var check_height = (container[0].clientWidth >= image.width && container[0].clientHeight < image.height) ? true : false;
			
			if(!check_width && !check_height) {
				var container_ratio = container[0].clientWidth / container[0].clientHeight;
				var image_ratio = image.width / image.height;
				
				check_width = image_ratio >= container_ratio ? true : false;
				check_height = !check_width;
			}
		}
			
		var padding_top = 0;
		var padding_bottom = 0;
		var padding_left = 0;
		var padding_right = 0;
		var width = image.width;
		var height = image.height;
		
		if(is_inside) {
			padding_top = (container[0].clientHeight - height) / 2;
			padding_left = (container[0].clientWidth - width) / 2;
		}
		else if(check_width) {
			var scaling_factor = container[0].clientWidth / image.width;
			width = container[0].clientWidth;
			height = image.height * scaling_factor;
			padding_top = (container[0].clientHeight - height) / 2;
			padding_bottom = (container[0].clientHeight - height) / 2;
		} 
		else if(check_height){
			var scaling_factor = container[0].clientHeight / image.height;
			width =  image.width * scaling_factor;
			height = container[0].clientHeight;
			padding_left = (container[0].clientWidth - width) / 2;
			padding_right = (container[0].clientWidth - width) / 2;
		}
		
		container.css("padding-top", padding_top);
		container.css("padding-bottom", padding_bottom);
		container.css("padding-left", padding_left);
		container.css("padding-right", padding_right);
		
		container.append("<img src=" + image.src + " width=" + width + " height=" + height + "/>");
		//this.context.drawImage(image, padding_left, padding_top, width, height);
	}	
	
	create_placeholder() {
		//this.context.drawImage(this.unable, 0, 0, this.background.width, this.background.height);
		this.to_hide.hide();
		$("#" + this.container_name + " img:last-child").remove();
		this.draw_image(this.container2, this.unable);
	}
	
	create_click_to_open() {
		//this.context.drawImage(this.click, 0, 0, this.background.width, this.background.height);
		this.to_hide.hide();
		$("#" + this.container_name + " img:last-child").remove();
		this.draw_image(this.container2, this.click);
	}
	
	wave_animation(timestamp, first_time) {
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
	
	create_level(sensor) {
		var max_level = this.background.height;
		
		var max_mote = sensor.max;
		var min_mote = sensor.min;
		var level_mote = sensor.level;
		
		this.th_mote = sensor.th;
		
		this.level = Math.floor(max_level / (max_mote - min_mote) * (level_mote - min_mote));
		
		this.th = Math.floor(max_level + (-max_level / (max_mote - min_mote) * (this.th_mote - min_mote)));
	}
	
	draw_threshold() {
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
	
	
	draw_wave(sensor) {
		if(sensor == undefined)
			return;
		
		if(this.req != null && this.req != undefined)
			cancelAnimationFrame(this.req);
		
		$("#" + this.container_name + " img:last-child").remove();
		this.to_hide.show();
		
		this.current_level.html("CURRENT LEVEL " + sensor.level + " cm");
		
		this.create_level(sensor);
		
		var self = this;
		
		this.req = requestAnimationFrame(function(timestamp){self.wave_animation(timestamp, true);});
	}
}