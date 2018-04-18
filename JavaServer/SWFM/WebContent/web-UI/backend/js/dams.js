class Dams {
	constructor(p, m, dim, sf, ae) {
		this.canvas = $("#dam-canvas");
		this.context = this.canvas[0].getContext("2d");
		
		this.create_tileset();
		
		this.create_constants();
		
		this.ae = ae;
		this.period = p;
		this.modal = m;
		
		this.dams = [];
		this.old_dams = [];
		
		this.canvas[0].width = dim;
		this.canvas[0].height = dim;
		
		this.cooja_canvas_dim = 33;
		
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
			this.dams[dam_index].start_time = timestamp;
		
		var self = this;
		
		if (timestamp - this.dams[dam_index].start_time < this.period) {
			requestAnimationFrame(function(timestamp){self.open_dam_left(timestamp, false, dam_index);});
			return;
		}
		
		var start_dam = this.dams[dam_index].sd;
		var end_dam = this.dams[dam_index].ed;
		var top_dam = this.dams[dam_index].td;
		var bottom_dam = this.dams[dam_index].bd;
		var current_index = end_dam - this.dams[dam_index].position;
		
		switch(current_index) {
			case end_dam:
				this.context.drawImage(this.wave_corner_top_left, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_corner_bottom_left, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.dams[dam_index].position++;
				requestAnimationFrame(function(timestamp){self.open_dam_left(timestamp, true, dam_index);});
				break;
			case end_dam - 1:
				this.context.drawImage(this.water_corner_top_left, (current_index + 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_corner_bottom_left, (current_index + 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_left, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_left, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.dams[dam_index].position++;
				requestAnimationFrame(function(timestamp){self.open_dam_left(timestamp, true, dam_index);});
				break;
			case start_dam - 1:
				this.context.drawImage(this.water_top, (current_index + 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_bottom, (current_index + 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.dams[dam_index].position = 0;
				break;
			default:
				this.context.drawImage(this.water_top, (current_index + 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_bottom, (current_index + 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_left, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_left, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.dams[dam_index].position++;
				requestAnimationFrame(function(timestamp){self.open_dam_left(timestamp, true, dam_index);});
				break;
		}
	}
	
	close_dam_left(timestamp, first_time, dam_index) {
		if(first_time)
			this.dams[dam_index].start_time = timestamp;
		
		var self = this;
			
		if (timestamp - this.dams[dam_index].start_time < this.period) {
			requestAnimationFrame(function(timestamp){self.close_dam_left(timestamp, false, dam_index);});
			return;
		}
		
		var start_dam = this.dams[dam_index].sd;
		var end_dam = this.dams[dam_index].ed;
		var top_dam = this.dams[dam_index].td;
		var bottom_dam = this.dams[dam_index].bd;
		var current_index = end_dam - this.dams[dam_index].position;
		
		switch(current_index) {
			case end_dam:
				this.context.drawImage(this.water_left, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_left, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_right, (current_index - 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_right, (current_index - 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.dams[dam_index].position++;
				requestAnimationFrame(function(timestamp){self.close_dam_left(timestamp, true, dam_index);});
				break;
			case start_dam:
				this.context.drawImage(this.ground_top, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.ground_bottom, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.dams[dam_index].position = 0;
				break;
			default:
				this.context.drawImage(this.ground_top, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.ground_bottom, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_right, (current_index - 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_right, (current_index - 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.dams[dam_index].position++;
				requestAnimationFrame(function(timestamp){self.close_dam_left(timestamp, true, dam_index);});
				break;
		}
	}
	
	open_dam_right(timestamp, first_time, dam_index) {
		if(first_time)
			this.dams[dam_index].start_time = timestamp;
		
		var self = this;
		
		if (timestamp - this.dams[dam_index].start_time < this.period) {
			requestAnimationFrame(function(timestamp){self.open_dam_right(timestamp, false, dam_index);});
			return;
		}
		
		var start_dam = this.dams[dam_index].sd;
		var end_dam = this.dams[dam_index].ed;
		var top_dam = this.dams[dam_index].td;
		var bottom_dam = this.dams[dam_index].bd;
		var current_index = start_dam + this.dams[dam_index].position;
		
		switch(current_index) {
			case start_dam:
				this.context.drawImage(this.wave_corner_top_right, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_corner_bottom_right, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.dams[dam_index].position++;
				requestAnimationFrame(function(timestamp){self.open_dam_right(timestamp, true, dam_index);});
				break;
			case start_dam + 1:
				this.context.drawImage(this.water_corner_top_right, (current_index - 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_corner_bottom_right, (current_index - 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_right, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_right, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.dams[dam_index].position++;
				requestAnimationFrame(function(timestamp){self.open_dam_right(timestamp, true, dam_index);});
				break;
			case end_dam + 1:
				this.context.drawImage(this.water_top, (current_index - 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_bottom, (current_index - 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.dams[dam_index].position = 0;
				break;
			default:
				this.context.drawImage(this.water_top, (current_index - 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_bottom, (current_index - 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_right, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_right, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.dams[dam_index].position++;
				requestAnimationFrame(function(timestamp){self.open_dam_right(timestamp, true, dam_index);});
				break;
		}
	}
	
	close_dam_right(timestamp, first_time, dam_index) {
		if(first_time)
			this.dams[dam_index].start_time = timestamp;
		
		var self = this;
		
		if (timestamp - this.dams[dam_index].start_time < this.period) {
			requestAnimationFrame(function(timestamp){self.close_dam_right(timestamp, false, dam_index);});
			return;
		}
		
		var start_dam = this.dams[dam_index].sd;
		var end_dam = this.dams[dam_index].ed;
		var top_dam = this.dams[dam_index].td;
		var bottom_dam = this.dams[dam_index].bd;
		var current_index = start_dam + this.dams[dam_index].position;
		
		switch(current_index) {
			case start_dam:
				this.context.drawImage(this.water_right, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.water_right, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_left, (current_index + 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_left, (current_index + 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.dams[dam_index].position++;
				requestAnimationFrame(function(timestamp){self.close_dam_right(timestamp, true, dam_index);});
				break;
			case end_dam:
				this.context.drawImage(this.ground_top, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.ground_bottom, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.dams[dam_index].position = 0;
				break;
			default:
				this.context.drawImage(this.ground_top, current_index * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.ground_bottom, current_index * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_top_to_left, (current_index + 1) * this.tile_width, top_dam * this.tile_height, this.tile_width, this.tile_height);
				this.context.drawImage(this.wave_bottom_to_left, (current_index + 1) * this.tile_width, bottom_dam * this.tile_height, this.tile_width, this.tile_height);
				this.dams[dam_index].position++;
				requestAnimationFrame(function(timestamp){self.close_dam_right(timestamp, true, dam_index);});
				break;
		}
	}
	
	draw_dam(dam_index) {
		if(this.dams[dam_index].position != 0)
			return;
		
		var self = this;
		
		if(this.dams[dam_index].x > this.canvas[0].width / 2) {
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
	
	set_dam_position(dam_index) {
		var index;
		
		if(this.dams[dam_index].lng < this.cooja_canvas_dim / 2 && this.dams[dam_index].lat < this.cooja_canvas_dim / 2)
			index = 0;
		else if(this.dams[dam_index].lng < this.cooja_canvas_dim / 2 && this.dams[dam_index].lat >= this.cooja_canvas_dim / 2)
			index = 1;
		else if(this.dams[dam_index].lng >= this.cooja_canvas_dim / 2 && this.dams[dam_index].lat < this.cooja_canvas_dim / 2)
			index = 2;
		else if(this.dams[dam_index].lng >= this.cooja_canvas_dim / 2 && this.dams[dam_index].lat >= this.cooja_canvas_dim / 2)
			index = 3;
		
		this.dams[dam_index].start_time = 0;
		this.dams[dam_index].position = 0;
		
		this.dams[dam_index].x = this.d[index].x;
		this.dams[dam_index].y = this.d[index].y;
		this.dams[dam_index].w = this.d[index].w;
		this.dams[dam_index].h = this.d[index].h;
		this.dams[dam_index].sd = this.sd[index];
		this.dams[dam_index].ed = this.ed[index];
		this.dams[dam_index].td = this.td[index];
		this.dams[dam_index].bd = this.bd[index];
	}
	
	onmessage(reply) {
		if(reply.error == false) {
			console.log(reply.message);
			for(var i = 0; i < this.dams.length; i++) {
				if(this.old_dams[i] == undefined) {
					if(this.dams[i] != undefined && this.dams[i].dam_id != undefined && this.dams[i].dam_name != undefined && this.dams[i].lat != undefined && this.dams[i].lng != undefined && this.dams[i].state != undefined && this.dams[i].is_working != undefined && this.dams[i].creation_time != undefined)
						this.old_dams[i] = {
							dam_id : this.dams[i].dam_id,
							dam_name : this.dams[i].dam_name,
							lat : this.dams[i].lat,
							lng : this.dams[i].lng,
							state : this.dams[i].state,
							creation_time : this.dams[i].creation_time,
							is_working : this.dams[i].is_working
						}
				}
				else {
					if(this.dams[i].dam_id != undefined) this.old_dams[i].dam_id = this.dams[i].dam_id;
					if(this.dams[i].dam_name != undefined) this.old_dams[i].dam_name = this.dams[i].dam_name;
					if(this.dams[i].lat != undefined) this.old_dams[i].lat = this.dams[i].lat;
					if(this.dams[i].lng != undefined) this.old_dams[i].lng = this.dams[i].lng;
					if(this.dams[i].state != undefined) this.old_dams[i].state = this.dams[i].state;
					if(this.dams[i].creation_time != undefined) this.old_dams[i].creation_time = this.dams[i].creation_time;
					if(this.dams[i].is_working != undefined) this.old_dams[i].is_working = this.dams[i].is_working;
				}
			}
			
			for(var i = 0; i < reply.message.length; i++) {
				if(reply.message[i] != undefined && reply.message[i] != null) {
					var present = false;
					
					for(var j = 0; j < this.dams.length; j++) {
						if(this.dams[j] != undefined && this.dams[j].dam_id != undefined && this.dams[j].dam_id == reply.message[i].dam_id) {
							if(reply.message[i].dam_id != undefined) this.dams[j].dam_id = reply.message[i].dam_id;
							if(reply.message[i].dam_name != undefined) this.dams[j].dam_name = reply.message[i].dam_name;
							if(reply.message[i].lat != undefined) this.dams[j].lat = reply.message[i].lat;
							if(reply.message[i].lng != undefined) this.dams[j].lng = reply.message[i].lng;
							if(reply.message[i].state != undefined) this.dams[j].state = reply.message[i].state;
							if(reply.message[i].creation_time != undefined) this.dams[j].creation_time = reply.message[i].creation_time;
							if(reply.message[i].is_working != undefined) this.dams[j].is_working = reply.message[i].is_working;
			
							present = true;
							
							var index = j;
						}
					}
					
					if(!present) {
						this.dams.push(reply.message[i]);
						index = this.dams.length - 1;
					}
					
					if(this.dams[index] != undefined && this.dams[index].dam_id != undefined && this.dams[index].dam_name != undefined && this.dams[index].lat != undefined && this.dams[index].lng != undefined && this.dams[index].state != undefined && this.dams[index].is_working != undefined && this.dams[index].creation_time != undefined) {
						this.set_dam_position(index);
						
						if(this.old_dams[index] == undefined) {
							if (this.dams[index].state == true)
								this.draw_dam(index);
						}
						else if (this.old_dams[index].state != this.dams[index].state)
							this.draw_dam(index);
						
						this.modal.update_dam();
					}
				}
			}
		}	
		else
			this.create_placeholder();
	}
	
	createEventResource(){
		this.eventSource = new EventSource(getdamdata + '?reference_id=' + this.ae.reference_id + "&ae_id=" + this.ae.ae_id + "&ae_name=" + this.ae.ae_name);
		
		var self = this;
		
		this.eventSource.onmessage = function(e){self.onmessage(JSON.parse(e.data));};
	}
}