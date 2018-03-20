function ALERT() {
	this.canvas = $("#river-sec")[0];
	
	this.container = $("#alert");
	this.to_hide = $("#alert-container");
	
	this.container.css("width", this.canvas.width + 4);
}

ALERT.prototype.create_click_to_open = function () {
	this.create_placeholder();
}

ALERT.prototype.create_placeholder = function () {
	this.to_hide.hide();
}

ALERT.prototype.draw_alert = function (ae) {
	this.to_hide.show();
	this.container.removeClass("alert-success");
	this.container.removeClass("alert-warning");
	this.container.removeClass("alert-danger");

	switch(ae.level) {
		case 1:
			this.container.addClass("alert-success");
			this.container.html("<strong>Quiet!</strong> " + ae.message);
			break;
		case 2:
			this.container.addClass("alert-warning");
			this.container.html("<strong>Warning!</strong> " + ae.message);
			break;
		default:
			this.container.addClass("alert-danger");
			this.container.html("<strong>Alarm!</strong> " + ae.message);
			break;
	}
}
