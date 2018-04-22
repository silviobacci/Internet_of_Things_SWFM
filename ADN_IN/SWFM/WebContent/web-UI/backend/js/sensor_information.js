class SensorInformation {
	constructor() {
		this.canvas = $("#river-sec")[0];
		this.container = $("#info");
		this.threshold_container = $(".is-not-working-sensor");
		
		this.to_hide = $("#info-container");
		
		this.container.css("width", this.canvas.width + 4);
	}
	
	create_placeholder() {
		this.to_hide.hide();
	}
	
	create_click_to_open() {
		this.container.html("<strong>Selected sensor:</strong> No mote selected.");
	}
	
	draw_info(sensor, is_admin) {
		if(sensor == undefined)
			return;
		
		this.container.removeClass("alert-info");
		this.container.removeClass("alert-success");
		this.container.removeClass("alert-danger");
		
		var date = new Date(sensor.creation_time);
		
		var text = "<strong>Selected sensor:</strong> " + sensor.sensor_name + "<br> <strong>Last update: </strong>" + date.toLocaleString();
		
		if(sensor.is_working) {
			this.container.addClass("alert-success");
			this.container.html(text);
			if(is_admin) this.threshold_container.show();
		}
		else {
			this.container.addClass("alert-danger");
			this.container.html( text + "<br><strong>ATTENTION: The selected sensor is not working anymore.</strong>");
			if(is_admin) this.threshold_container.hide();
		}
	}
}
