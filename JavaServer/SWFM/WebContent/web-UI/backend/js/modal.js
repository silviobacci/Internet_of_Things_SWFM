class Modal {
	constructor(ae, user) {
		this.selected_sensor;
		this.selected_dam;
		this.text_visible = [];
		this.dam_text_visible = [];
		
		this.ae = ae;
		this.is_admin = user;
		
		this.rect = $("#rect")[0];
		
		this.background = new Background();
		
		var dim = this.background.canvas[0].width;
		var sf = this.background.scaling_factor;
		
		var animation_period = 100;
		this.dams = new Dams(animation_period, this, dim, sf, this.ae);
		this.sensors = new Sensors(this, dim, sf, this.ae);
		this.overlay = new Overlay(dim, sf);
		this.selection = new Selection(dim, sf);
		
		this.background.container2.css("width", dim + 4);
		this.background.container2.css("height", dim + 4);
		this.background.container1.css("width", this.background.container1[0].width);
		this.background.container1.css("height", this.background.container1[0].height);
		
		this.wave = new Wave();
		this.sensor_information = new SensorInformation();
		this.dam_information = new DamInformation();
		if(this.is_admin) this.threshold = new Threshold(this.ae);
		this.history = new History(this.ae);
		if(this.is_admin) this.state = new State(this.ae);
		this.alert = new Alert();
		
		this.create_scene();
	}
	
	create_overlay() {
		this.background.canvas.css("position", "absolute");
		this.dams.canvas.css("position", "absolute");
		this.sensors.canvas.css("position", "absolute");
		this.overlay.canvas.css("position", "absolute");
		this.selection.canvas.css("position", "absolute");
		
		this.background.canvas.css("z-index", "0");
		this.dams.canvas.css("z-index", "1");
		this.sensors.canvas.css("z-index", "2");
		this.overlay.canvas.css("z-index", "3");
		this.selection.canvas.css("z-index", "4");
	}
	
	create_placeholder() {
		this.background.create_placeholder();
		this.dams.create_placeholder();
		this.sensors.create_placeholder();
		this.selection.create_placeholder();
		this.overlay.create_placeholder();
	}
	
	contains(r, x, y) {
		x = x / this.background.scaling_factor;
		y = y / this.background.scaling_factor;
		return (x >= r.x && x <= r.x + r.w && y >= r.y && y <= r.y + r.h)
	}
	
	mousemove_handler(ev) {
		var x = ev.pageX - this.selection.canvas.offset().left;
		var y = ev.pageY - this.selection.canvas.offset().top;
		
		for(var sensor_index = 0; sensor_index < this.sensors.sensors.length; sensor_index++)
			if (this.contains(this.sensors.sensors[sensor_index], x, y)) {
				this.selection.canvas.css('cursor', 'pointer');
				if(!this.text_visible) {
					if(this.sensors.sensors[sensor_index].x > this.background.canvas[0].width / 2)
						var image = {x : this.sensors.sensors[sensor_index].x - 104, y : this.sensors.sensors[sensor_index].y - 8, w : 6 * this.sensors.tile_width, h : 2 * this.sensors.tile_height};
					else
						var image = {x : this.sensors.sensors[sensor_index].x + 40, y : this.sensors.sensors[sensor_index].y - 8, w : 6 * this.sensors.tile_width, h : 2 * this.sensors.tile_height};
					
					this.overlay.context.drawImage(this.rect, image.x, image.y, image.w, image.h);
					this.overlay.context.fillStyle="#000000";
					this.overlay.context.fillText("click for more info", image.x + 10, image.y + 19);
				}
				
				this.text_visible = true;
				return;
			}
		
		for(var dam_index = 0; dam_index < this.dams.dams.length; dam_index++)
			if (this.contains(this.dams.dams[dam_index], x, y)){
				this.selection.canvas.css('cursor', 'pointer');
				if(!this.dam_text_visible[dam_index]) {
					if(this.dams.dams[dam_index].x > this.background.canvas[0].width / 2)
						var image = {x : this.dams.dams[dam_index].x - 96, y : this.dams.dams[dam_index].y, w : 6 * this.sensors.tile_width, h : 2 * this.sensors.tile_height};
					else
						var image = {x : this.dams.dams[dam_index].x + 16, y : this.dams.dams[dam_index].y, w : 6 * this.sensors.tile_width, h : 2 * this.sensors.tile_height};
					
					this.overlay.context.drawImage(this.rect, image.x, image.y, image.w, image.h);
					this.overlay.context.fillStyle="#000000";
					this.overlay.context.fillText("click for more info", image.x + 10, image.y + 19);
				}
				
				this.dam_text_visible[dam_index] = true;
				return;
			}
		
		this.selection.canvas.css('cursor', 'default');
		this.text_visible = false;
		this.dam_text_visible = [false, false, false, false];
		this.overlay.context.clearRect(0, 0, this.background.size, this.background.size);
	}
	
	click_handler(ev) {
		var x = ev.pageX - this.selection.canvas.offset().left;
		var y = ev.pageY - this.selection.canvas.offset().top;
		
		for(var sensor_index = 0; sensor_index < this.sensors.sensors.length; sensor_index++)
			if (this.contains(this.sensors.sensors[sensor_index], x, y)){
				this.selected_sensor = sensor_index;
				this.on_sensor_selection();
				return;
			}
		
		for(var dam_index = 0; dam_index < this.dams.dams.length; dam_index++)
			if (this.contains(this.dams.dams[dam_index], x, y)){
				this.selected_dam = dam_index;
				this.on_dam_selection();
				return;
			}
	}
	
	create_handlers(ev) {
		this.overlay.canvas.off("mousemove");
		this.overlay.canvas.off("click");
		
		var self = this;
		
		this.selection.canvas.on("mousemove", function(ev) {self.mousemove_handler(ev)});
		this.selection.canvas.on("click", function(ev) {self.click_handler(ev)});
	}
	
	update_dam() {
		this.dam_information.draw_info(this.dams.dams[this.selected_dam], this.is_admin);
		if(this.is_admin) this.state.draw_state(this.dams.dams[this.selected_dam]);
	}
	
	draw_dam_selected_label() {
		if(this.selected_dam == undefined)
			return;
		
		if(this.dams.dams[this.selected_dam].x > this.background.canvas[0].width / 2)
			var image = {x : this.dams.dams[this.selected_dam].x - 96, y : this.dams.dams[this.selected_dam].y, w : 6 * this.sensors.tile_width, h : 2 * this.sensors.tile_height};
		else
			var image = {x : this.dams.dams[this.selected_dam].x + 16, y : this.dams.dams[this.selected_dam].y, w : 6 * this.sensors.tile_width, h : 2 * this.sensors.tile_height};
			
		this.selection.context.drawImage(this.rect, image.x, image.y, image.w, image.h);
		this.selection.context.fillStyle="#000000";
		
		this.selection.context.fillText("selected dam", image.x + 21, image.y + 19);
	}
	
	on_dam_selection() {
		if(this.is_admin) this.state.result_container.hide();
		this.update_dam();
		
		this.selection.context.clearRect(0, 0, this.background.size, this.background.size);
		this.draw_sensor_selected_label();
		this.draw_dam_selected_label();
	}
	
	update_sensor() {
		this.sensor_information.draw_info(this.sensors.sensors[this.selected_sensor], this.is_admin);
		this.wave.draw_wave(this.sensors.sensors[this.selected_sensor]);
		if(this.is_admin) this.threshold.draw_threshold(this.sensors.sensors[this.selected_sensor]);
	}
	
	draw_sensor_selected_label() {
		if(this.selected_sensor == undefined)
			return;
		
		if(this.sensors.sensors[this.selected_sensor].x > this.background.canvas[0].width / 2)
			var image = {x : this.sensors.sensors[this.selected_sensor].x - 104, y : this.sensors.sensors[this.selected_sensor].y - 8, w : 6 * this.sensors.tile_width, h : 2 * this.sensors.tile_height};
		else
			var image = {x : this.sensors.sensors[this.selected_sensor].x + 40, y : this.sensors.sensors[this.selected_sensor].y - 8, w : 6 * this.sensors.tile_width, h : 2 * this.sensors.tile_height};
			
		this.selection.context.drawImage(this.rect, image.x, image.y, image.w, image.h);
		this.selection.context.fillStyle="#000000";
		
		this.selection.context.fillText("selected sensor", image.x + 15, image.y + 19);
	}
	
	on_sensor_selection() {
		if(this.is_admin) this.threshold.result_container.hide();
		
		if(this.history != undefined && this.history.eventSource != undefined) this.history.eventSource.close();
		this.history = new History(this.ae);
		this.history.createEventResource(this.sensors.sensors[this.selected_sensor]);
		this.update_sensor();
		
		this.selection.context.clearRect(0, 0, this.background.size, this.background.size);
		this.draw_sensor_selected_label();
		this.draw_dam_selected_label();
	}
	
	create_scene() {
		var reference_id = this.ae.reference_id.substring(this.ae.reference_id.lastIndexOf("/") + 1, this.ae.reference_id.length);
		var ae_id = this.ae.ae_id.substring(this.ae.ae_id.lastIndexOf("/") + 1, this.ae.ae_id.length);
		var ae_name = this.ae.ae_name.substring(this.ae.ae_name.lastIndexOf("/") + 1, this.ae.ae_name.length);
		$("#modal-label").html(reference_id + " - " + ae_name + " (" + ae_id + ")");
		
		this.background.draw_background();
		
		this.alert.draw_alert(this.ae);
		this.sensor_information.create_click_to_open();
		this.dam_information.create_click_to_open();
		this.wave.create_click_to_open();
		this.history.create_click_to_open();
		if(this.is_admin) this.threshold.create_click_to_open();
		if(this.is_admin) this.state.create_click_to_open();
		
		this.create_handlers();
		
		this.create_overlay();
	}
}