class Sensors {
	constructor(m, dim, sf, ae) {
		this.tile_width = 16;
		this.tile_height = 16;
		this.max_level = 32;
		this.tile_level_width = 1;
		this.tile_level_height = 4;
		
		this.canvas = $("#mote-canvas");
		this.context = this.canvas[0].getContext("2d");
		
		this.marker = $("#marker")[0];
		this.label = $("#label")[0];
		this.red = $("#red")[0];
		this.green = $("#green")[0];
		this.yellow = $("#yellow")[0];
		this.grey = $("#grey")[0];
		this.message = $("#rect")[0];
		
		this.modal = m;
		this.ae = ae;
		this.sensors = [];
		this.written = [];
		
		this.canvas[0].width = dim;
		this.canvas[0].height = dim;
		
		if((this.scaling_factor = sf) != 1)
			this.context.scale(this.scaling_factor, this.scaling_factor);
		
		this.createEventResource();
	}
	
	create_placeholder() {
		this.context.clearRect(0, 0, this.canvas[0].width, this.canvas[0].height);
	}
	
	set_water_level(sensor_index) {
		var th_mote = this.sensors[sensor_index].th;
		var max_mote = this.sensors[sensor_index].max;
		var min_mote = this.sensors[sensor_index].min;
		var level_mote = this.sensors[sensor_index].level;
		
		var level = Math.floor(this.max_level / (max_mote - min_mote) * (level_mote - min_mote));
		
		var th = Math.floor(this.max_level / (max_mote - min_mote) * (th_mote - min_mote));
		
		if(level >= th){
			for(var i = 0; i < level; i++)
				this.context.drawImage(this.red, this.sensors[sensor_index].x + i, this.sensors[sensor_index].y, this.tile_level_width, this.tile_level_height);
			
			for(var i = level; i < this.max_level; i++)
				this.context.drawImage(this.grey, this.sensors[sensor_index].x + i, this.sensors[sensor_index].y, this.tile_level_width, this.tile_level_height);
		}
		else if (level < th && level >=  Math.floor(th / 2)) {
			for(var i = 0; i < level; i++)
				this.context.drawImage(this.yellow, this.sensors[sensor_index].x + i, this.sensors[sensor_index].y, this.tile_level_width, this.tile_level_height);
			
			for(var i = level; i < this.max_level; i++)
				this.context.drawImage(this.grey, this.sensors[sensor_index].x + i, this.sensors[sensor_index].y, this.tile_level_width, this.tile_level_height);
		}
		else {
			for(var i = 0; i < level; i++)
				this.context.drawImage(this.green, this.sensors[sensor_index].x + i, this.sensors[sensor_index].y, this.tile_level_width, this.tile_level_height);
			
			for(var i = level; i < this.max_level; i++)
				this.context.drawImage(this.grey, this.sensors[sensor_index].x + i, this.sensors[sensor_index].y, this.tile_level_width, this.tile_level_height);
		}
	}
	
	draw_sensor(sensor_index) {
		this.context.drawImage(this.label, this.sensors[sensor_index].x - 8, this.sensors[sensor_index].y - 6, 3 * this.tile_width, this.tile_height);
		this.context.drawImage(this.marker, this.sensors[sensor_index].x + 8, this.sensors[sensor_index].y + 10, this.tile_width, this.tile_height);
	}
	
	map_cooja_coordinates(index) {
		var cooja_canvas_start = 0;
		var cooja_canvas_end = 100.0;
		
		var browser_canvas_start = 0;
		var browser_canvas_end = 528.0;
			
		var slope = (browser_canvas_end - browser_canvas_start) / (cooja_canvas_end - cooja_canvas_start);

		this.sensors[index].x = (browser_canvas_start + slope * (this.sensors[index].lat - cooja_canvas_start)) + 8;
		this.sensors[index].y = (browser_canvas_start + slope * (this.sensors[index].lng - cooja_canvas_start)) + 6;
		
		this.sensors[index].w = 3 * this.tile_width;
		this.sensors[index].h = 2 * this.tile_height;
	}
	
	draw_sensors(sensor_index) {
		this.context.clearRect(0, 0, this.canvas[0].width, this.canvas[0].height);
		for(var index = 0; index < this.sensors.length; index++)
			if(this.sensors[index] != undefined && this.sensors[index].sensor_id != undefined && this.sensors[index].sensor_name != undefined && this.sensors[index].lat != undefined && this.sensors[index].lng != undefined && this.sensors[index].level != undefined && this.sensors[index].max != undefined && this.sensors[index].min != undefined && this.sensors[index].th != undefined && this.sensors[index].creation_time != undefined && this.sensors[index].is_working != undefined) {
				if(this.written[index] == undefined) 
					this.written[index] = true;
					
				if(this.written[index]) {
					this.written[index] = false;
					console.log(this.sensors[index].sensor_name);
				}
				
				this.map_cooja_coordinates(index);
				
				this.draw_sensor(index);
				this.set_water_level(index);
				
				this.modal.update_sensor();
				
				this.modal.selection.context.clearRect(0, 0, this.canvas[0].width, this.canvas[0].height);
				this.modal.draw_sensor_selected_label();
				this.modal.draw_dam_selected_label();
			}
	}
	
	onmessage(reply) {
		if(reply.error == false) {
			for(var i = 0; i < reply.message.length; i++) {
				if(reply.message[i] != undefined && reply.message[i] != null) {
					var present = false;
					for(var j = 0; j < this.sensors.length; j++) {
						if(this.sensors[j] != undefined && this.sensors[j] != null && this.sensors[j].sensor_id != undefined && this.sensors[j].sensor_id == reply.message[i].sensor_id) {
							if(reply.message[i].sensor_id != undefined) this.sensors[j].sensor_id = reply.message[i].sensor_id;
							if(reply.message[i].sensor_name != undefined) this.sensors[j].sensor_name = reply.message[i].sensor_name;
							if(reply.message[i].lat != undefined) this.sensors[j].lat = reply.message[i].lat;
							if(reply.message[i].lng != undefined) this.sensors[j].lng = reply.message[i].lng;
							if(reply.message[i].level != undefined) this.sensors[j].level = reply.message[i].level;
							if(reply.message[i].max != undefined) this.sensors[j].max = reply.message[i].max;
							if(reply.message[i].min != undefined) this.sensors[j].min = reply.message[i].min;
							if(reply.message[i].th != undefined) this.sensors[j].th = reply.message[i].th;
							if(reply.message[i].creation_time != undefined) this.sensors[j].creation_time = reply.message[i].creation_time;
							if(reply.message[i].is_working != undefined) this.sensors[j].is_working = reply.message[i].is_working;
			
							present = true;
						}
					}
					
					if(!present) 
						this.sensors.push(reply.message[i]);
				}
			}
			
			this.draw_sensors();
		}	
		else
			this.create_placeholder();
	}
	
	createEventResource(){
		this.eventSource = new EventSource(getsensordata + '?reference_id=' + this.ae.reference_id + "&ae_id=" + this.ae.ae_id + "&ae_name=" + this.ae.ae_name);
		
		var self = this;
		
		this.eventSource.onmessage = function(e){self.onmessage(JSON.parse(e.data));};
	}
}