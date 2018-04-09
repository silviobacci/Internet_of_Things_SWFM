class Threshold {
	constructor(ae) {
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
	
	create_placeholder() {
		this.to_hide.hide();
		$("#" + this.container_name + " img:last-child").remove();
		this.container.append("<img src=" + this.unable.src + " width=" + this.canvas.width + "height=" + this.canvas.height + "/>");
	}
	
	create_click_to_open() {
		this.to_hide.hide();
		$("#" + this.container_name +  " img:last-child").remove();
		this.container.append("<img src=" + this.click.src + " width=" + this.canvas.width + "height=" + this.canvas.height + "/>");
	}
	
	slider_hanlder() {
		this.new_threshold.html("NEW THRESHOLD " + this.slider[0].value + " cm");
	}
	
	draw_threshold(sensor) {
		if(sensor == undefined)
			return;
		
		this.slider[0].min = sensor.min;
		this.slider[0].max = sensor.max;
		this.slider[0].step = 1;
		
		if(this.slider[0].value == 0)
			this.slider[0].value = sensor.th;
	
		$("#" + this.container_name + " img:last-child").remove();
		this.to_hide.show();
		
		this.new_threshold.html("NEW THRESHOLD " + this.slider[0].value + " cm");
		this.current_threshold.html("CURRENT THRESHOLD " + sensor.th + " cm");
		
		var self = this;
		
		this.slider.off("input");
		this.button.off("click");
		
		this.slider.on("input", function(ev) {self.slider_hanlder()});
		this.button.on("click", function(ev) {self.set_data(sensor)});
	}
	
	success(reply) {
		if(reply.error == false) {
			this.result_container.show();
			
			this.result_threshold.removeClass('alert-success');
			this.result_threshold.removeClass('alert-warning');
			this.result_threshold.removeClass('alert-danger');
			
			this.result_threshold.addClass('alert-success');
			this.result_threshold.html("<strong>Success:</strong> " + reply.message);
		}	
		else
			this.error(reply);
	}
	
	error(reply) {
		this.result_container.show();
		
		this.result_threshold.removeClass('alert-success');
		this.result_threshold.removeClass('alert-warning');
		this.result_threshold.removeClass('alert-danger');
		
		this.result_threshold.addClass('alert-danger');
		this.result_threshold.html("<strong>Error:</strong> " + reply.message);
	}
	
	set_data(sensor) {
		var data = "{\"MIN\" : " + this.slider[0].min + ", \"MAX\" : " + this.slider[0].max + ", \"TH\" : " + this.slider[0].value + "}";
		var payload = "{\"reference_id: \" : \"" + this.ae.reference_id + "\", \"ae_id: \" : \"" + this.ae.ae_id + "\", \"ae_name: \" : \"" + this.ae.ae_name + "\", \"sensor_id\" : \"" + sensor.id + "\", \"data\" : " + data + "}";
		ajax_post_req(setsensordata, payload, this, this.success, this.error);
	}
}
