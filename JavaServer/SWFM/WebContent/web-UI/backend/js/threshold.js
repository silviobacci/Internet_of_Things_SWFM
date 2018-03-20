function THRESHOLD(ae) {
	this.ae = ae;
	
	this.container_name = "threshold";
	
	this.container = $("#" + this.container_name);
	this.to_hide = $("#threshold-container");
	
	this.canvas = $("#river-sec")[0];
	this.unable = $("#unable_rect")[0];
	this.click = $("#click")[0];
	
	this.slider = $("#slider");
	this.new_threshold = $("#new-th");
	this.current_threshold = $("#current-threshold");
	this.button = $("#button-change-th");
	
	this.result_container = $("#result-container");
	this.result_threshold = $("#result-th");
	
	this.container.css("width", this.canvas.width + 4);
	this.container.css("height", this.canvas.height + 4);
}

THRESHOLD.prototype.create_placeholder = function () {
	this.to_hide.hide();
	$("#" + this.container_name + " img:last-child").remove();
	this.container.append("<img src=" + this.unable.src + " width=" + this.canvas.width + "height=" + this.canvas.height + "/>");
}

THRESHOLD.prototype.create_click_to_open = function () {
	this.to_hide.hide();
	$("#" + this.container_name +  " img:last-child").remove();
	this.container.append("<img src=" + this.click.src + " width=" + this.canvas.width + "height=" + this.canvas.height + "/>");
}

THRESHOLD.prototype.slider_hanlder = function () {
	this.new_threshold.html("NEW THRESHOLD " + this.slider[0].value + " cm");
}

THRESHOLD.prototype.draw_threshold = function (sensor) {
	this.slider[0].min = sensor.min;
	this.slider[0].max = sensor.max;
	this.slider[0].step = 1;
	
	if(this.slider[0].value == 0)
		this.slider[0].value = sensor.th;

	$("#" + this.container_name + " img:last-child").remove();
	this.to_hide.show();
	
	this.new_threshold.html("NEW THRESHOLD " + this.slider[0].value + " cm");
	this.current_threshold.html("CURRENT THRESHOLD " + sensor.th + " cm");
	
	this.slider.on("input", this.slider_hanlder());
	
	this.button.on("click", this.set_data(sensor.id));
}

THRESHOLD.prototype.success = function (reply) {
	if(reply.error == false) {
		this.result_container.show();
		
		this.result_threshold.removeClass('alert-success');
		this.result_threshold.removeClass('alert-warning');
		this.result_threshold.removeClass('alert-danger');
		
		this.result_threshold.addClass('alert-danger');
		this.result_threshold.html("<strong>Success:</strong> " + reply.message);
	}	
	else
		this.error(reply);
}

THRESHOLD.prototype.error = function (reply) {
	this.result_container.show();
	
	this.result_threshold.removeClass('alert-success');
	this.result_threshold.removeClass('alert-warning');
	this.result_threshold.removeClass('alert-danger');
	
	this.result_threshold.addClass('alert-danger');
	this.result_threshold.html("<strong>Error:</strong> " + reply.message);
}

THRESHOLD.prototype.set_data = function (id) {
	var data = "{\"MIN\" : " + this.slider[0].min + ", \"MAX\" : " + this.slider[0].max + ", \"TH\" : " + this.slider[0].value + "}";
	var payload = "{\"ae\" : \"" + this.ae + "\", \"id\" : \"" + id + "\", \"data\" : " + data + "}";
	ajax_post_req(setsensordata, payload, this, this.success, this.error);
}
