var tile_width = 16;
var tile_height = 16;
var max_level = 32;
var tile_level_width = 1;
var tile_level_height = 4;

function MOTES(dim, sf) {
	this.canvas = $("#mote-canvas");
	this.context = this.canvas[0].getContext("2d");
	
	this.marker = $("#marker")[0];
	this.label = $("#label")[0];
	this.red = $("#red")[0];
	this.green = $("#green")[0];
	this.yellow = $("#yellow")[0];
	this.grey = $("#grey")[0];
	this.message = $("#rect")[0];
	
	this.sensors = [];
	
	this.canvas[0].width = dim;
	this.canvas[0].height = dim;
	
	if((this.scaling_factor = sf) != 1)
		this.context.scale(this.scaling_factor, this.scaling_factor);
}

MOTES.prototype.create_placeholder = function () {
	this.context.clearRect(0, 0, this.canvas[0].width, this.canvas[0].height);
}

MOTES.prototype.create_motes = function () {
	for(var mote_index = 0; mote_index < this.sensors.length; mote_index++) {
		draw_mote(mote_index);
		set_water_level(mote_index);
	}
}

MOTES.prototype.set_water_level = function (mote_index) {
	var th_mote = this.sensors[mote_index].th;
	var max_mote = this.sensors[mote_index].max;
	var min_mote = this.sensors[mote_index].min;
	var level_mote = this.sensors[mote_index].level;
	
	var level = Math.floor(max_level / (max_mote - min_mote) * (level_mote - min_mote));
	
	var th = Math.floor(max_level / (max_mote - min_mote) * (th_mote - min_mote));
	
	if(level >= th){
		for(var i = 0; i < level; i++)
			this.context.drawImage(this.red, this.sensors[mote_index].x + i, this.sensors[mote_index].y, tile_level_width, tile_level_height);
		
		for(var i = level; i < max_level; i++)
			this.context.drawImage(this.grey, this.sensors[mote_index].x + i, this.sensors[mote_index].y, tile_level_width, tile_level_height);
	}
	else if (level < th && level >=  Math.floor(th / 2)) {
		for(var i = 0; i < level; i++)
			this.context.drawImage(this.yellow, this.sensors[mote_index].x + i, this.sensors[mote_index].y, tile_level_width, tile_level_height);
		
		for(var i = level; i < max_level; i++)
			this.context.drawImage(this.grey, this.sensors[mote_index].x + i, this.sensors[mote_index].y, tile_level_width, tile_level_height);
	}
	else {
		for(var i = 0; i < level; i++)
			this.context.drawImage(this.green, this.sensors[mote_index].x + i, this.sensors[mote_index].y, tile_level_width, tile_level_height);
		
		for(var i = level; i < max_level; i++)
			this.context.drawImage(this.grey, this.sensors[mote_index].x + i, this.sensors[mote_index].y, tile_level_width, tile_level_height);
	}
}

MOTES.prototype.draw_mote = function (mote_index) {
	this.context.drawImage(this.label, this.sensors[mote_index].x - 8, this.sensors[mote_index].y - 6, 3 * tile_width, tile_height);
	this.context.drawImage(this.marker, this.sensors[mote_index].x + 8, this.sensors[mote_index].y + 10, tile_width, tile_height);
}

MOTES.prototype.success = function (reply) {
	if(reply.error == false) {
		if(this.sensors.length != 0 && this.sensors.length != reply.message.length) {
			this.error(reply);
			return;
		}
		
		for(var i = 0; i < reply.message.length; i++) {
			this.sensors[i] = reply.message[i];
			this.sensors[i].x = (this.sensors[i].lng - 1) * tile_width + 8;
			this.sensors[i].y = (this.sensors[i].lat - 1) * tile_height + 6;
			this.sensors[i].w = 3 * tile_width;
			this.sensors[i].h = 2 * tile_height;
			
			this.draw_mote(i);
			this.set_water_level(i);
		}
	}	
	else
		this.error(reply);
}

MOTES.prototype.error = function (reply) {
	console.log(reply);
	this.create_placeholder();
}

MOTES.prototype.get_data = function (id) {
	var payload = "{\"id\" : \"" + id + "\"}";
	ajax_post_req(getsensordata, payload, this, this.success, this.error);
}
