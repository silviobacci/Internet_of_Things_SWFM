class Motes {
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
		
		this.canvas[0].width = dim;
		this.canvas[0].height = dim;
		
		if((this.scaling_factor = sf) != 1)
			this.context.scale(this.scaling_factor, this.scaling_factor);
		
		this.createEventResource();
	}
	
	create_placeholder() {
		this.context.clearRect(0, 0, this.canvas[0].width, this.canvas[0].height);
	}
	
	create_motes() {
		for(var mote_index = 0; mote_index < this.sensors.length; mote_index++) {
			draw_mote(mote_index);
			set_water_level(mote_index);
		}
	}
	
	set_water_level(mote_index) {
		var th_mote = this.sensors[mote_index].th;
		var max_mote = this.sensors[mote_index].max;
		var min_mote = this.sensors[mote_index].min;
		var level_mote = this.sensors[mote_index].level;
		
		var level = Math.floor(this.max_level / (max_mote - min_mote) * (level_mote - min_mote));
		
		var th = Math.floor(this.max_level / (max_mote - min_mote) * (th_mote - min_mote));
		
		if(level >= th){
			for(var i = 0; i < level; i++)
				this.context.drawImage(this.red, this.sensors[mote_index].x + i, this.sensors[mote_index].y, this.tile_level_width, this.tile_level_height);
			
			for(var i = level; i < this.max_level; i++)
				this.context.drawImage(this.grey, this.sensors[mote_index].x + i, this.sensors[mote_index].y, this.tile_level_width, this.tile_level_height);
		}
		else if (level < th && level >=  Math.floor(th / 2)) {
			for(var i = 0; i < level; i++)
				this.context.drawImage(this.yellow, this.sensors[mote_index].x + i, this.sensors[mote_index].y, this.tile_level_width, this.tile_level_height);
			
			for(var i = level; i < this.max_level; i++)
				this.context.drawImage(this.grey, this.sensors[mote_index].x + i, this.sensors[mote_index].y, this.tile_level_width, this.tile_level_height);
		}
		else {
			for(var i = 0; i < level; i++)
				this.context.drawImage(this.green, this.sensors[mote_index].x + i, this.sensors[mote_index].y, this.tile_level_width, this.tile_level_height);
			
			for(var i = level; i < this.max_level; i++)
				this.context.drawImage(this.grey, this.sensors[mote_index].x + i, this.sensors[mote_index].y, this.tile_level_width, this.tile_level_height);
		}
	}
	
	draw_mote(mote_index) {
		this.context.drawImage(this.label, this.sensors[mote_index].x - 8, this.sensors[mote_index].y - 6, 3 * this.tile_width, this.tile_height);
		this.context.drawImage(this.marker, this.sensors[mote_index].x + 8, this.sensors[mote_index].y + 10, this.tile_width, this.tile_height);
	}
	
	onopen() {
		console.log("SENSORS - Conncetion opened with the SSE");
	}
	
	onmessage(reply) {
		if(reply.error == false) {
			for(var i = 0; i < reply.message.length; i++) {
				var present = false;
				for(var j = 0; i < this.sensors.length; j++) {
					if(this.sensors[j] != undefined && this.sensors[j].sensor_id != undefined && this.sensors[j].sensor_id == reply.message[i].sensor_id) {
						if(reply.message[i].sensor_id != undefined) this.sensors[j].sensor_id = reply.message[i].sensor_id;
						if(reply.message[i].lat != undefined) this.sensors[j].lat = reply.message[i].lat;
						if(reply.message[i].lng != undefined) this.sensors[j].lng = reply.message[i].lng;
						if(reply.message[i].level != undefined) this.sensors[j].level = reply.message[i].level;
						if(reply.message[i].max != undefined) this.sensors[j].max = reply.message[i].max;
						if(reply.message[i].min != undefined) this.sensors[j].min = reply.message[i].min;
						if(reply.message[i].th != undefined) this.sensors[j].th = reply.message[i].th;
		
						present = true;
					}
				}
				
				var index = i;
				
				if(!present) {
					this.sensors[this.sensors.length] = reply.message[i];
					index = i;
				}
				
				if(this.sensors[i].sensor_id != undefined && this.sensors[i].lat != undefined && this.sensors[i].lng != undefined && this.sensors[i].level != undefined && this.sensors[i].max != undefined && this.sensors[i].min != undefined && this.sensors[i].th != undefined) {
					this.sensors[index].x = (this.sensors[index].lng - 1) * this.tile_width + 8;
					this.sensors[index].y = (this.sensors[index].lat - 1) * this.tile_height + 6;
					this.sensors[index].w = 3 * this.tile_width;
					this.sensors[index].h = 2 * this.tile_height;
					
					this.draw_mote(index);
					this.set_water_level(index);
					
					this.modal.on_sensor_selection();
				}
			}
		}	
		else
			this.onerror(JSON.stringify(reply.message));
	}
	
	onerror(reply) {
		console.log("SENSORS - Error: " + reply);
		this.create_placeholder();
	}
	
	createEventResource(){
		var eventSource = new EventSource(getsensordata + '?reference_id=' + this.ae.reference_id + "&ae_id=" + this.ae.ae_id + "&ae_name=" + this.ae.ae_name);
		
		var self = this;
		
		eventSource.onopen = function(){self.onopen()};
		
		eventSource.onmessage = function(e){self.onmessage(JSON.parse(e.data));};
		
		eventSource.onerror = function(e){self.onerror(e);};
	}
}