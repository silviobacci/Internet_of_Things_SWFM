class State {
	constructor(ae) {
		this.ae = ae;
		
		this.container_name = "state";
		
		this.container = $("#" + this.container_name);
		this.to_hide = $("#state-container");
		
		this.canvas = $("#river-ov")[0];
		this.unable = $("#unable_rect")[0];
		this.click = $("#click")[0];
		
		this.button = $("#button-change-dam");
		
		this.result_container = $("#result-container-dam");
		this.result_dam = $("#result-dam");
		
		this.title = $("#current-state");
		
		this.container.css("width", this.canvas.width + 4);
		this.container.css("height", this.canvas.height / 2 + 4);
	}
	
	create_placeholder() {
		this.to_hide.hide();
		$("#" + this.container_name + " img:last-child").remove();
		this.container.append("<img src=" + this.unable.src + " width=" + this.canvas.width + " height=" + this.canvas.height / 2 + "/>");
	}
	
	create_click_to_open() {
		this.to_hide.hide();
		$("#" + this.container_name +  " img:last-child").remove();
		this.container.append("<img src=" + this.click.src + " width=" + this.canvas.width + " height=" + this.canvas.height / 2 + "/>");
	}
	
	draw_state(dam) {
		if(dam == undefined)
			return;
		
		$("#" + this.container_name + " img:last-child").remove();
		this.to_hide.show();
		
		if(dam.state == true)
			this.title.html("CURRENT STATE: OPEN");
		else
			this.title.html("CURRENT STATE: CLOSED");
		
		var self = this;
		
		this.button.off("click");
		this.button.on("click", function(ev) {self.set_data(dam)});
	}
	
	success(reply) {
		if(reply.error == false) {
			this.result_container.show();
			
			this.result_dam.removeClass('alert-success');
			this.result_dam.removeClass('alert-warning');
			this.result_dam.removeClass('alert-danger');
			
			this.result_dam.addClass('alert-success');
			this.result_dam.html("<strong>Success:</strong> " + reply.message);
		}	
		else
			this.error(reply);
	}
	
	error(reply) {
		this.result_container.show();
		
		this.result_dam.removeClass('alert-success');
		this.result_dam.removeClass('alert-warning');
		this.result_dam.removeClass('alert-danger');
		
		this.result_dam.addClass('alert-danger');
		this.result_dam.html("<strong>Error:</strong> " + reply.message);
	}
	
	set_data(dam) {
		var payload = "{\"reference_id: \" : \"" + this.ae.reference_id + "\", \"ae_id: \" : \"" + this.ae.ae_id + "\", \"ae_name: \" : \"" + this.ae.ae_name + "\", \"dam_id\" : \"" + dam.id + "\", \"data\" : " + !dam.state + "}";
		ajax_post_req(setdamdata, payload, this, this.success, this.error);
	}
}
