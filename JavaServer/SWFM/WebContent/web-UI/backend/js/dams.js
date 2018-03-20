var tile_width = 16;
var tile_height = 16;

var sd = [0, 28, 0, 21];
var ed = [9, 32, 15, 32];
var td = [3, 3, 22, 30];
var bd = [4, 4, 23, 31];

var d = [
		 {x: tile_width * ed[0], y: tile_height * td[0], w: tile_width * 2, h: tile_height * 3},
		 {x: tile_width * sd[1], y: tile_height * td[1], w: tile_width * 2, h: tile_height * 3},
		 {x: tile_width * ed[2], y: tile_height * td[2], w: tile_width * 2, h: tile_height * 3},
		 {x: tile_width * sd[3], y: tile_height * td[3], w: tile_width * 2, h: tile_height * 3}
];

function DAMS(p, dim, sf) {
	this.canvas = $("#dam-canvas");
	this.context = this.canvas[0].getContext("2d");
	
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
	
	this.period = p;
	
	this.dams = [];
	this.old_dams = [];
	this.start_time = [];
	this.position = [];
	
	this.canvas[0].width = dim;
	this.canvas[0].height = dim;
	
	if((this.scaling_factor = sf) != 1)
		this.context.scale(this.scaling_factor, this.scaling_factor);
}

DAMS.prototype.create_placeholder = function () {
	this.context.clearRect(0, 0, this.canvas[0].width, this.canvas[0].height);
}

DAMS.prototype.open_dam_left = function (timestamp, first_time, dam_index) {
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
			this.context.drawImage(this.wave_corner_top_left, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_corner_bottom_left, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.position[dam_index]++;
			requestAnimationFrame(function(timestamp){self.open_dam_left(timestamp, true, dam_index);});
			break;
		case end_dam - 1:
			this.context.drawImage(this.water_corner_top_left, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.water_corner_bottom_left, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_top_to_left, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_bottom_to_left, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.position[dam_index]++;
			requestAnimationFrame(function(timestamp){self.open_dam_left(timestamp, true, dam_index);});
			break;
		case start_dam - 1:
			this.context.drawImage(this.water_top, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.water_bottom, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.position[dam_index] = 0;
			break;
		default:
			this.context.drawImage(this.water_top, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.water_bottom, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_top_to_left, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_bottom_to_left, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.position[dam_index]++;
			requestAnimationFrame(function(timestamp){self.open_dam_left(timestamp, true, dam_index);});
			break;
	}
}

DAMS.prototype.close_dam_left = function (timestamp, first_time, dam_index) {
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
			this.context.drawImage(this.water_left, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.water_left, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_top_to_right, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_bottom_to_right, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.position[dam_index]++;
			requestAnimationFrame(function(timestamp){self.close_dam_left(timestamp, true, dam_index);});
			break;
		case start_dam:
			this.context.drawImage(this.ground_top, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.ground_bottom, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.position[dam_index] = 0;
			break;
		default:
			this.context.drawImage(this.ground_top, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.ground_bottom, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_top_to_right, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_bottom_to_right, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.position[dam_index]++;
			requestAnimationFrame(function(timestamp){self.close_dam_left(timestamp, true, dam_index);});
			break;
	}
}

DAMS.prototype.open_dam_right = function (timestamp, first_time, dam_index) {
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
			this.context.drawImage(this.wave_corner_top_right, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_corner_bottom_right, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.position[dam_index]++;
			requestAnimationFrame(function(timestamp){self.open_dam_right(timestamp, true, dam_index);});
			break;
		case start_dam + 1:
			this.context.drawImage(this.water_corner_top_right, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.water_corner_bottom_right, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_top_to_right, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_bottom_to_right, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.position[dam_index]++;
			requestAnimationFrame(function(timestamp){self.open_dam_right(timestamp, true, dam_index);});
			break;
		case end_dam + 1:
			this.context.drawImage(this.water_top, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.water_bottom, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.position[dam_index] = 0;
			break;
		default:
			this.context.drawImage(this.water_top, (current_index - 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.water_bottom, (current_index - 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_top_to_right, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_bottom_to_right, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.position[dam_index]++;
			requestAnimationFrame(function(timestamp){self.open_dam_right(timestamp, true, dam_index);});
			break;
	}
}

DAMS.prototype.close_dam_right = function (timestamp, first_time, dam_index) {
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
			this.context.drawImage(this.water_right, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.water_right, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_top_to_left, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_bottom_to_left, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.position[dam_index]++;
			requestAnimationFrame(function(timestamp){self.close_dam_right(timestamp, true, dam_index);});
			break;
		case end_dam:
			this.context.drawImage(this.ground_top, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.ground_bottom, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.position[dam_index] = 0;
			break;
		default:
			this.context.drawImage(this.ground_top, current_index * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.ground_bottom, current_index * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_top_to_left, (current_index + 1) * tile_width, top_dam * tile_height, tile_width, tile_height);
			this.context.drawImage(this.wave_bottom_to_left, (current_index + 1) * tile_width, bottom_dam * tile_height, tile_width, tile_height);
			this.position[dam_index]++;
			requestAnimationFrame(function(timestamp){self.close_dam_right(timestamp, true, dam_index);});
			break;
	}
}

DAMS.prototype.draw_dam = function (dam_index) {
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

DAMS.prototype.get_success = function (reply) {
	if(reply.error == false) {
		if(d.length != reply.message.length) {
			this.get_error(reply);
			return;
		}
		
		for(var dam_index = 0; dam_index < d.length; dam_index++) {
			this.start_time[dam_index] = 0;
			this.position[dam_index] = 0;
			
			this.old_dams[dam_index] = this.dams[dam_index];
			this.dams[dam_index] = reply.message[dam_index];
			
			this.dams[dam_index].x = d[dam_index].x;
			this.dams[dam_index].y = d[dam_index].y;
			this.dams[dam_index].w = d[dam_index].w;
			this.dams[dam_index].h = d[dam_index].h;
			this.dams[dam_index].sd = sd[dam_index];
			this.dams[dam_index].ed = ed[dam_index];
			this.dams[dam_index].td = td[dam_index];
			this.dams[dam_index].bd = bd[dam_index];
			
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

DAMS.prototype.get_error = function (replay) {
	this.create_placeholder();
}

DAMS.prototype.get_data = function (id) {
	var payload = "{\"id\" : \"" + id + "\"}";
	ajax_post_req(getdamdata, payload, this, this.get_success, this.get_error);
}

DAMS.prototype.set_success = function (reply) {
	if(reply.error == true)
		this.set_error(reply);
}

DAMS.prototype.set_error = function (reply) {
	console.log(reply);
}

DAMS.prototype.set_data = function (ae, dam) {
	var payload = "{\"ae\" : \"" + ae.id + "\", \"id\" : \"" + dam.id + "\", \"data\" : " + !dam.state + "}";
	ajax_post_req(setdamdata, payload, this, this.success, this.error);
}