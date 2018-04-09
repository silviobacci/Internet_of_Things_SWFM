class Modal {
	constructor(p, ae) {
		this.selected_sensor;
		this.selected_dam;
		this.text_visible = [];
		this.dam_text_visible = [];
		
		this.ae = ae;
		this.period = p;
		
		this.rect = $("#rect")[0];
		
		this.background = new Background();
		
		var dim = this.background.canvas[0].width;
		var sf = this.background.scaling_factor;
		
		this.dams = new Dams(50, dim, sf, this.ae);
		this.motes = new Motes(this, dim, sf, this.ae);
		this.overlay = new Overlay(dim, sf);
		
		this.background.container2.css("width", dim + 4);
		this.background.container2.css("height", dim + 4);
		
		this.wave = new Wave();
		this.sensor_information = new SensorInformation();
		this.dam_information = new DamInformation();
		this.threshold = new Threshold(this.ae);
		this.state = new State(this.ae);
		this.history = new History(this.ae);
		this.alert = new Alert();
		
		this.create_scene();
	}
	
	create_overlay() {
		this.background.canvas.css("position", "absolute");
		this.dams.canvas.css("position", "absolute");
		this.motes.canvas.css("position", "absolute");
		this.overlay.canvas.css("position", "absolute");
		
		this.background.canvas.css("z-index", "0");
		this.dams.canvas.css("z-index", "1");
		this.motes.canvas.css("z-index", "2");
		this.overlay.canvas.css("z-index", "3");
	}
	
	create_placeholder() {
		this.background.create_placeholder();
		this.dams.create_placeholder();
		this.motes.create_placeholder();
		this.overlay.create_placeholder();
	}
	
	contains(r, x, y) {
		x = x / this.background.scaling_factor;
		y = y / this.background.scaling_factor;
		return (x >= r.x && x <= r.x + r.w && y >= r.y && y <= r.y + r.h)
	}
	
	mousemove_handler(ev) {
		var x = ev.pageX - this.overlay.canvas.offset().left;
		var y = ev.pageY - this.overlay.canvas.offset().top;
		
		for(var mote_index = 0; mote_index < this.motes.sensors.length; mote_index++)
			if (this.contains(this.motes.sensors[mote_index], x, y)) {
				this.overlay.canvas.css('cursor', 'pointer');
				if(!this.text_visible) {
					if(mote_index % 2 == 1)
						var image = {x : this.motes.sensors[mote_index].x - 104, y : this.motes.sensors[mote_index].y - 8, w : 6 * this.motes.tile_width, h : 2 * this.motes.tile_height};
					else
						var image = {x : this.motes.sensors[mote_index].x + 40, y : this.motes.sensors[mote_index].y - 8, w : 6 * this.motes.tile_width, h : 2 * this.motes.tile_height};
					
					this.overlay.context.drawImage(rect, image.x, image.y, image.w, image.h);
					this.overlay.context.fillStyle="#000000";
					this.overlay.context.fillText("click for more info", image.x + 10, image.y + 19);
				}
				
				this.text_visible = true;
				return;
			}
		
		for(var dam_index = 0; dam_index < this.dams.dams.length; dam_index++)
			if (user.is_admin && this.contains(this.dams.dams[dam_index], x, y)){
				this.overlay.canvas.css('cursor', 'pointer');
				
				if(!this.dam_text_visible[dam_index]) {
					if(dam_index % 2 == 1)
						var image = {x : this.dams.dams[dam_index].x - 96, y : this.dams.dams[dam_index].y, w : 6 * this.motes.tile_width, h : 2 * this.motes.tile_height};
					else
						var image = {x : this.dams.dams[dam_index].x + 16, y : this.dams.dams[dam_index].y, w : 6 * this.motes.tile_width, h : 2 * this.motes.tile_height};
					
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
		this.overlay.context.clearRect(0, 0, this.background.size, this.background.size);
	}
	
	click_handler(ev) {
		var x = ev.pageX - this.overlay.canvas.offset().left;
		var y = ev.pageY - this.overlay.canvas.offset().top;
		
		for(var mote_index = 0; mote_index < this.motes.sensors.length; mote_index++)
			if (this.contains(this.motes.sensors[mote_index], x, y)){
				this.selected_sensor = mote_index;
				this.on_sensor_selection();
				return;
			}
		
		for(var dam_index = 0; dam_index < this.dams.dams.length; dam_index++)
			if (user.is_admin && this.contains(this.dams.dams[dam_index], x, y)){
				this.selected_dam = dam_index;
				this.on_dam_selection();
				return;
			}
	}
	
	create_handlers(ev) {
		this.overlay.canvas.off("mousemove");
		this.overlay.canvas.off("click");
		
		var self = this;
		
		this.overlay.canvas.on("mousemove", function(ev) {self.mousemove_handler(ev)});
		this.overlay.canvas.on("click", function(ev) {self.click_handler(ev)});
	}
	
	on_dam_selection() {
		this.dam_information.draw_info(this.dams.dams[this.selected_dam]);
		this.state.draw_state(this.dams.dams[this.selected_dam]);
	}
	
	on_sensor_selection() {
		this.sensor_information.draw_info(this.motes.sensors[this.selected_sensor]);
		this.wave.draw_wave(this.motes.sensors[this.selected_sensor]);
		this.threshold.draw_threshold(this.motes.sensors[this.selected_sensor]);
		this.history.createEventResource(this.motes.sensors[this.selected_sensor]);
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
		this.threshold.create_click_to_open();
		this.state.create_click_to_open();
		
		this.create_handlers();
		
		this.create_overlay();
	}
}