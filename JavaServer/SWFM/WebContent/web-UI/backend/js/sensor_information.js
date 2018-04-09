class SensorInformation {
	constructor() {
		this.canvas = $("#river-sec")[0];
		this.container = $("#info");
		
		this.to_hide = $("#info-container");
		
		this.container.css("width", this.canvas.width + 4);
	}
	
	create_placeholder() {
		this.to_hide.hide();
	}
	
	create_click_to_open() {
		this.container.html("<strong>Selected mote:</strong> No mote selected.");
	}
	
	draw_info(sensor) {
		if(sensor == undefined)
			return;

		var mote_id = sensor.sensor_id.substring(sensor.sensor_id.lastIndexOf("/") + 1, sensor.sensor_id.length)
		this.container.html("<strong>Selected mote: </strong> " + mote_id);
	}
}
