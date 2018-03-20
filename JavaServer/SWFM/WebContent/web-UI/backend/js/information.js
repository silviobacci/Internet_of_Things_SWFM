function INFORMATION() {
	this.canvas = $("#river-sec")[0];
	
	this.container = $("#info");
	this.to_hide = $("#info-container");
	
	this.container.css("width", this.canvas.width + 4);
}

INFORMATION.prototype.create_placeholder = function () {
	this.to_hide.hide();
}

INFORMATION.prototype.create_click_to_open = function () {
	this.container.html("<strong>Selected mote:</strong> No mote selected.");
}

INFORMATION.prototype.draw_info = function (sensor) {
	var mote_id = sensor.id.substring(sensor.id.lastIndexOf("/") + 1, sensor.id.length)
	this.container.html("<strong>Selected mote: </strong> " + mote_id);
}
