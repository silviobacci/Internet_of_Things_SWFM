class Dams {
	constructor(p, dim, sf, ae) {
		this.canvas = $("#dam-canvas");
		this.context = this.canvas[0].getContext("2d");
		
		this.create_tileset();
		
		this.create_constants();
		
		this.ae = ae;
		this.period = p;
		
		this.dams = [];
		this.old_dams = [];
		this.start_time = [];
		this.position = [];
		
		this.canvas[0].width = dim;
		this.canvas[0].height = dim;
		
		if((this.scaling_factor = sf) != 1)
			this.context.scale(this.scaling_factor, this.scaling_factor);
		
		this.createEventResource();
	}
	
	create_constants() {
		this.tile_width = 16;
		this.tile_height = 16;

		this.sd = [0, 28, 0, 21];
		this.ed = [9, 32, 15, 32];
		this.td = [3, 3, 22, 30];
		this.bd = [4, 4, 23, 31];

		this.d = [
				 {x: this.tile_width * this.ed[0], y: this.tile_height * this.td[0], w: this.tile_width * 2, h: this.tile_height * 3},
				 {x: this.tile_width * this.sd[1], y: this.tile_height * this.td[1], w: this.tile_width * 2, h: this.tile_height * 3},
				 {x: this.tile_width * this.ed[2], y: this.tile_height * this.td[2], w: this.tile_width * 2, h: this.tile_height * 3},
				 {x: this.tile_width * this.sd[3], y: this.tile_height * this.td[3], w: this.tile_width * 2, h: this.tile_height * 3}
		];
	}
	
	create_tileset() {
		this.water_left = $("#water_left")[0];
		this.water_right = $("#water_right")[0];
		this.water_top = $("#water_top")[0];
		this.water_bottom = $("#water_bottom")[0];
		this.water_corner_bottom_right = $("#water_corner_bottom_right")[0];
		this.water_corner_bottom_left = $("#water_corner_bottom_left")[0];
		this.water_corner_top_left = $("#water_corner_top_left")[0];
		this.water_corner_top_right = $("#water_corner_top_right")[0];
		this.ground_top = $("#ground_top")[0];
		this.ground_bottom = $("#ground_bottom")[0];
		this.wave_bottom_to_right = $("#wave_bottom_to_right")[0];
		this.wave_bottom_to_left = $("#wave_bottom_to_left")[0];
		this.wave_top_to_right = $("#wave_top_to_right")[0];
		this.wave_top_to_left = $("#wave_top_to_left")[0];
		this.wave_to_left = $("#wave_to_left")[0];
		this.wave_to_right = $("#wave_to_right")[0];
		this.wave_corner_bottom_right = $("#wave_corner_bottom_right")[0];
		this.wave_corner_bottom_left = $("#wave_corner_bottom_left")[0];
		this.wave_corner_top_right = $("#wave_corner_top_right")[0];
		this.wave_corner_top_left = $("#wave_corner_top_left")[0];
	}
	
	create_placeholder() {
		this.context.clearRect(0, 0, this.canvas[0].width, this.canvas[0].height);
	}
	
	open_dam_left(timestamp, first_time, dam_index) {
		if(first_time)
			this.start_time[dam_index] = timestamp;
		
		var self = this;
		
		if (timestamp - this.start_time[dam_index] < this.period) {
			requestAnimationFrame(function(timestamp){self.open_dam_left(timestamp, false, dam_index);});
			return;
		}
		
		var start_dam = this.dams[dam_index].sd;
		var end_dam = this.dams[dam_index].ed;
		var top_dam = this.dams[dam_index].td;
		var bottom_dam = this.dams[dam_index].bd;
		var current_index = end_dam - this.position[dam_index];
		
		switch(current_index) {
			case end_dam:
				this.context.drawImage(this.wave_corner_top_left, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_corner_bottom_left, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.position[dam_index]++;
				requestAnimationFrame(function(timestamp){self.open_dam_left(timestamp, true, dam_index);});
				break;
			case end_dam - 1:
				this.context.drawImage(this.water_corner_top_left, (current_index + 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_corner_bottom_left, (current_index + 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_left, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_left, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.position[dam_index]++;
				requestAnimationFrame(function(timestamp){self.open_dam_left(timestamp, true, dam_index);});
				break;
			case start_dam - 1:
				this.context.drawImage(this.water_top, (current_index + 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_bottom, (current_index + 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.position[dam_index] = 0;
				break;
			default:
				this.context.drawImage(this.water_top, (current_index + 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_bottom, (current_index + 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_left, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_left, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.position[dam_index]++;
				requestAnimationFrame(function(timestamp){self.open_dam_left(timestamp, true, dam_index);});
				break;
		}
	}
	
	close_dam_left(timestamp, first_time, dam_index) {
		if(first_time)
			this.start_time[dam_index] = timestamp;
		
		var self = this;
			
		if (timestamp - this.start_time[dam_index] < this.period) {
			requestAnimationFrame(function(timestamp){self.close_dam_left(timestamp, false, dam_index);});
			return;
		}
		
		var start_dam = this.dams[dam_index].sd;
		var end_dam = this.dams[dam_index].ed;
		var top_dam = this.dams[dam_index].td;
		var bottom_dam = this.dams[dam_index].bd;
		var current_index = end_dam - this.position[dam_index];
		
		switch(current_index) {
			case end_dam:
				this.context.drawImage(this.water_left, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_left, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_right, (current_index - 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_right, (current_index - 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.position[dam_index]++;
				requestAnimationFrame(function(timestamp){self.close_dam_left(timestamp, true, dam_index);});
				break;
			case start_dam:
				this.context.drawImage(this.ground_top, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.ground_bottom, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.position[dam_index] = 0;
				break;
			default:
				this.context.drawImage(this.ground_top, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.ground_bottom, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_right, (current_index - 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_right, (current_index - 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.position[dam_index]++;
				requestAnimationFrame(function(timestamp){self.close_dam_left(timestamp, true, dam_index);});
				break;
		}
	}
	
	open_dam_right(timestamp, first_time, dam_index) {
		if(first_time)
			this.start_time[dam_index] = timestamp;
		
		var self = this;
		
		if (timestamp - this.start_time[dam_index] < this.period) {
			requestAnimationFrame(function(timestamp){self.open_dam_right(timestamp, false, dam_index);});
			return;
		}
		
		var start_dam = this.dams[dam_index].sd;
		var end_dam = this.dams[dam_index].ed;
		var top_dam = this.dams[dam_index].td;
		var bottom_dam = this.dams[dam_index].bd;
		var current_index = start_dam + this.position[dam_index];
		
		switch(current_index) {
			case start_dam:
				this.context.drawImage(this.wave_corner_top_right, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_corner_bottom_right, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.position[dam_index]++;
				requestAnimationFrame(function(timestamp){self.open_dam_right(timestamp, true, dam_index);});
				break;
			case start_dam + 1:
				this.context.drawImage(this.water_corner_top_right, (current_index - 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_corner_bottom_right, (current_index - 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_right, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_right, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.position[dam_index]++;
				requestAnimationFrame(function(timestamp){self.open_dam_right(timestamp, true, dam_index);});
				break;
			case end_dam + 1:
				this.context.drawImage(this.water_top, (current_index - 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_bottom, (current_index - 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.position[dam_index] = 0;
				break;
			default:
				this.context.drawImage(this.water_top, (current_index - 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_bottom, (current_index - 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_right, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_right, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.position[dam_index]++;
				requestAnimationFrame(function(timestamp){self.open_dam_right(timestamp, true, dam_index);});
				break;
		}
	}
	
	close_dam_right(timestamp, first_time, dam_index) {
		if(first_time)
			this.start_time[dam_index] = timestamp;
		
		var self = this;
		
		if (timestamp - this.start_time[dam_index] < this.period) {
			requestAnimationFrame(function(timestamp){self.close_dam_right(timestamp, false, dam_index);});
			return;
		}
		
		var start_dam = this.dams[dam_index].sd;
		var end_dam = this.dams[dam_index].ed;
		var top_dam = this.dams[dam_index].td;
		var bottom_dam = this.dams[dam_index].bd;
		var current_index = start_dam + this.position[dam_index];
		
		switch(current_index) {
			case start_dam:
				this.context.drawImage(this.water_right, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_right, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_left, (current_index + 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_left, (current_index + 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.position[dam_index]++;
				requestAnimationFrame(function(timestamp){self.close_dam_right(timestamp, true, dam_index);});
				break;
			case end_dam:
				this.context.drawImage(this.ground_top, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.ground_bottom, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.position[dam_index] = 0;
				break;
			default:
				this.context.drawImage(this.ground_top, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.ground_bottom, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_left, (current_index + 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_left, (current_index + 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.position[dam_index]++;
				requestAnimationFrame(function(timestamp){self.close_dam_right(timestamp, true, dam_index);});
				break;
		}
	}
	
	draw_dam(dam_index) {
		if(this.position[dam_index] != 0)
			return;
		
		var self = this;
		
		if(dam_index % 2 == 1) {
			if (this.dams[dam_index].state == false)
				requestAnimationFrame(function(timestamp){self.close_dam_right(timestamp, true, dam_index);});
			else
				requestAnimationFrame(function(timestamp){self.open_dam_right(timestamp, true, dam_index);});
		}
		else {
			if (this.dams[dam_index].state == false)
				requestAnimationFrame(function(timestamp){self.close_dam_left(timestamp, true, dam_index);})
			else
				requestAnimationFrame(function(timestamp){self.open_dam_left(timestamp, true, dam_index);});
		}
	}
	
	get_success(reply) {
		if(reply.error == false) {
			if(this.d.length != reply.message.length) {
				this.get_error(reply);
				return;
			}
			
			for(var dam_index = 0; dam_index < this.d.length; dam_index++) {
				this.start_time[dam_index] = 0;
				this.position[dam_index] = 0;
				
				this.old_dams[dam_index] = this.dams[dam_index];
				this.dams[dam_index] = reply.message[dam_index];
				
				this.dams[dam_index].x = this.d[dam_index].x;
				this.dams[dam_index].y = this.d[dam_index].y;
				this.dams[dam_index].w = this.d[dam_index].w;
				this.dams[dam_index].h = this.d[dam_index].h;
				this.dams[dam_index].sd = this.sd[dam_index];
				this.dams[dam_index].ed = this.ed[dam_index];
				this.dams[dam_index].td = this.td[dam_index];
				this.dams[dam_index].bd = this.bd[dam_index];
				
				if(this.old_dams[dam_index] == undefined) {
					if (this.dams[dam_index].state == true)
						this.draw_dam(dam_index);
				}
				else if (this.old_dams[dam_index].state != this.dams[dam_index].state)
					this.draw_dam(dam_index);
			}
		}	
		else
			this.get_error(reply);
	}
	
	onopen() {
		console.log("DAMS - Conncetion opened with the SSE");
	}
	
	onmessage(reply) {
		if(reply.error == false) {
			for(var i = 0; i < reply.message.length; i++) {
				var present = false;
				for(var j = 0; i < this.dams.length; j++) {
					if(this.dams[j] != undefined && this.dams[j].dam_id != undefined && this.dams[j].dam_id == reply.message[i].dam_id) {
						if(reply.message[i].dam_id != undefined) this.dams[j].dam_id = reply.message[i].dam_id;
						if(reply.message[i].lat != undefined) this.dams[j].lat = reply.message[i].lat;
						if(reply.message[i].lng != undefined) this.dams[j].lng = reply.message[i].lng;
						if(reply.message[i].state != undefined) this.dams[j].state = reply.message[i].state;
		
						present = true;
					}
				}
				
				var index = i;
				
				if(!present) {
					this.dams[this.dams.length] = reply.message[i];
					index = i;
				}
				else
					this.old_dams[index] = this.dams[index];
				
				if(this.dams[index].lat != undefined && this.dams[index].lng != undefined && this.dams[index].state != undefined) {
					this.start_time[index] = 0;
					this.position[index] = 0;
					
					this.dams[index].x = this.d[index].x;
					this.dams[index].y = this.d[index].y;
					this.dams[index].w = this.d[index].w;
					this.dams[index].h = this.d[index].h;
					this.dams[index].sd = this.sd[index];
					this.dams[index].ed = this.ed[index];
					this.dams[index].td = this.td[index];
					this.dams[index].bd = this.bd[index];
					
					if(this.old_dams[index] == undefined) {
						if (this.dams[index].state == true)
							this.draw_dam(index);
					}
					else if (this.old_dams[index].state != this.dams[index].state)
						this.draw_dam(index);
				}
			}
		}	
		else
			this.onerror(JSON.stringify(reply.message));
	}
	
	onerror(reply) {
		console.log("DAMS - Error: " + reply);
		this.create_placeholder();
	}
	
	createEventResource(){
		var eventSource = new EventSource(getdamdata + '?reference_id=' + this.ae.reference_id + "&ae_id=" + this.ae.ae_id + "&ae_name=" + this.ae.ae_name);
		
		var self = this;
		
		eventSource.onopen = function(){self.onopen()};
		
		eventSource.onmessage = function(e){self.onmessage(JSON.parse(e.data));};
		
		eventSource.onerror = function(e){self.onerror(e);};
	}
}