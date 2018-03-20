var scene_created = false;

function TEXTURE(p, ae) {
	this.selected_sensor;
	this.selected_dam;
	this.text_visible = [];
	this.dam_text_visible = [];
	
	this.ae = ae;
	this.period = p;
	
	this.rect = $("#rect")[0];
	
	this.background = new BACKGROUND();
	
	var dim = this.background.canvas[0].width;
	var sf = this.background.scaling_factor;
	
	this.dams = new DAMS(50, dim, sf);
	this.motes = new MOTES(dim, sf);
	this.overlay = new OVERLAY(dim, sf);
	
	this.background.container2.css("width", dim + 4);
	this.background.container2.css("height", dim + 4);
	
	this.information = new INFORMATION();
	this.wave = new WAVE();
	this.threshold = new THRESHOLD(this.ae);
	this.history = new HISTORY();
	this.alert = new ALERT();
	
	var self = this;
	
	this.req = requestAnimationFrame(function(timestamp){self.refresh(timestamp, true);});
}

TEXTURE.prototype.create_overlay = function () {
	this.background.canvas.css("position", "absolute");
	this.dams.canvas.css("position", "absolute");
	this.motes.canvas.css("position", "absolute");
	this.overlay.canvas.css("position", "absolute");
	
	this.background.canvas.css("z-index", "0");
	this.dams.canvas.css("z-index", "1");
	this.motes.canvas.css("z-index", "2");
	this.overlay.canvas.css("z-index", "3");
}

TEXTURE.prototype.create_placeholder = function () {
	this.background.create_placeholder();
	this.dams.create_placeholder();
	this.motes.create_placeholder();
	this.overlay.create_placeholder();
}

TEXTURE.prototype.contains = function (r, x, y) {
	x = x / this.background.scaling_factor;
	y = y / this.background.scaling_factor;
	return (x >= r.x && x <= r.x + r.w && y >= r.y && y <= r.y + r.h)
}

TEXTURE.prototype.mousemove_handler = function (ev) {
	var x = ev.pageX - this.overlay.canvas.offset().left;
	var y = ev.pageY - this.overlay.canvas.offset().top;
	
	for(var mote_index = 0; mote_index < this.motes.sensors.length; mote_index++)
		if (this.contains(this.motes.sensors[mote_index], x, y)) {
			this.overlay.canvas.css('cursor', 'pointer');
			if(!this.text_visible) {
				if(mote_index % 2 == 1)
					var image = {x : this.motes.sensors[mote_index].x - 104, y : this.motes.sensors[mote_index].y - 8, w : 6 * tile_width, h : 2*tile_height};
				else
					var image = {x : this.motes.sensors[mote_index].x + 40, y : this.motes.sensors[mote_index].y - 8, w : 6 * tile_width, h : 2*tile_height};
				
				this.overlay.context.drawImage(rect, image.x, image.y, image.w, image.h);
				this.overlay.context.fillStyle="#000000";
				this.overlay.context.fillText("click for more info", image.x + 10, image.y + 19);
			}
			
			this.text_visible = true;
			return;
		}
	
	for(var dam_index = 0; dam_index < this.dams.dams.length; dam_index++)
		if (is_admin && this.contains(this.dams.dams[dam_index], x, y)){
			this.overlay.canvas.css('cursor', 'pointer');
			
			if(!this.dam_text_visible[dam_index]) {
				if(dam_index % 2 == 1)
					var image = {x : this.dams.dams[dam_index].x - 96, y : this.dams.dams[dam_index].y, w : 6 * tile_width, h : 2 * tile_height};
				else
					var image = {x : this.dams.dams[dam_index].x + 16, y : this.dams.dams[dam_index].y, w : 6 * tile_width, h : 2*tile_height};
				
				this.overlay.context.drawImage(this.rect, image.x, image.y, image.w, image.h);
				this.overlay.context.fillStyle="#000000";
				
				if(!this.dams.dams[dam_index].state)
					var text = "click to open";
				else
					var text = "click to close";
				
				this.overlay.context.fillText(text, image.x + 21, image.y + 19);
			}
			this.dam_text_visible[dam_index] = true;
			return;
		}
	
	this.overlay.canvas.css('cursor', 'default');
	this.text_visible = false;
	this.dam_text_visible = [false, false, false, false];
	this.overlay.context.clearRect(0, 0, size, size);
}

TEXTURE.prototype.click_handler = function (ev) {
	var x = ev.pageX - this.overlay.canvas.offset().left;
	var y = ev.pageY - this.overlay.canvas.offset().top;
	
	for(var mote_index = 0; mote_index < this.motes.sensors.length; mote_index++)
		if (this.contains(this.motes.sensors[mote_index], x, y)){
			this.selected_sensor = mote_index;
			this.information.draw_info(this.motes.sensors[mote_index]);
			this.wave.draw_wave(this.motes.sensors[mote_index]);
			this.threshold.draw_threshold(this.motes.sensors[mote_index]);
			this.history.get_data(this.motes.sensors[mote_index]);
			return;
		}
	
	for(var dam_index = 0; dam_index < this.dams.dams.length; dam_index++)
		if (is_admin && this.contains(this.dams.dams[dam_index], x, y)){
			this.dams.set_data(this.ae, this.dams.dams[dam_index]);
			return;
		}
}

TEXTURE.prototype.create_handlers = function (ev) {
	this.overlay.canvas.off("mousemove");
	this.overlay.canvas.off("click");
	
	var self = this;
	
	this.overlay.canvas.on("mousemove", function(ev) {self.mousemove_handler(ev)});
	this.overlay.canvas.on("click", function(ev) {self.click_handler(ev)});
}


TEXTURE.prototype.update_scene = function () {
	this.information.draw_info(this.motes.sensors[this.selected_sensor]);
	this.wave.draw_wave(this.motes.sensors[this.selected_sensor]);
	this.threshold.draw_threshold(this.motes.sensors[this.selected_sensor]);
	this.history.get_data(this.motes.sensors[this.selected_sensor]);
}

TEXTURE.prototype.create_scene = function () {
	this.background.draw_background();
	
	this.information.create_click_to_open();
	this.wave.create_click_to_open();
	this.history.create_click_to_open();
	this.threshold.create_click_to_open();
	this.alert.create_click_to_open();
	
	this.create_handlers();
	
	this.create_overlay();
}

TEXTURE.prototype.refresh = function (timestamp, first_time) {
	if(first_time) {
		var id = this.ae.id.substring(this.ae.id.lastIndexOf("/") + 1, this.ae.id.length);
		$("#modal-label").html(this.ae.name + " - " + id);

		this.alert.draw_alert(this.ae);
		this.motes.get_data(this.ae.id);
		this.dams.get_data(this.ae.id);
		
		if(this.selected_sensor == undefined)
			this.create_scene();
		else
			this.update_scene();
		
		this.start_time = timestamp;
	}
	
	var self = this;
	
	if (timestamp - this.start_time < this.period)
		this.req = requestAnimationFrame(function(timestamp){self.refresh(timestamp, false);});
	else
		this.req = requestAnimationFrame(function(timestamp){self.refresh(timestamp, true);});
}
