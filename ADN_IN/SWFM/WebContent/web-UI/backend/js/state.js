class State {
	constructor(ae) {
		this.ae = ae;
		
		this.container_name = "state";
		
		this.container = $("#" + this.container_name);
		this.to_hide = $("#state-container");
		
		this.canvas = $("#river-sec")[0];
		this.unable = $("#unable_rect")[0];
		this.click = $("#click")[0];
		
		this.button = $("#button-change-dam");
		
		this.result_container = $("#result-container-dam");
		this.result_dam = $("#result-dam");
		
		this.title = $("#current-state");
		
		this.container.css("width", this.canvas.width + 4);
		this.container.css("height", this.canvas.height / 2 + 4);
		
		this.result_container.hide();
	}
	
	draw_image(container, image) {
		var is_inside = container[0].clientWidth >= image.width &&  container[0].clientHeight >= image.height ? true : false;
		
		if(!is_inside) {
			var check_width = (container[0].clientWidth < image.width && container[0].clientHeight >= image.height) ? true : false;
			var check_height = (container[0].clientWidth >= image.width && container[0].clientHeight < image.height) ? true : false;
			
			if(!check_width && !check_height) {
				var container_ratio = container[0].clientWidth / container[0].clientHeight;
				var image_ratio = image.width / image.height;
				
				check_width = image_ratio >= container_ratio ? true : false;
				check_height = !check_width;
			}
		}
			
		var padding_top = 0;
		var padding_bottom = 0;
		var padding_left = 0;
		var padding_right = 0;
		var width = image.width;
		var height = image.height;
		
		if(is_inside) {
			padding_top = (container[0].clientHeight - height) / 2;
			padding_left = (container[0].clientWidth - width) / 2;
		}
		else if(check_width) {
			var scaling_factor = container[0].clientWidth / image.width;
			width = container[0].clientWidth;
			height = image.height * scaling_factor;
			padding_top = (container[0].clientHeight - height) / 2;
			padding_bottom = (container[0].clientHeight - height) / 2;
		} 
		else if(check_height){
			var scaling_factor = container[0].clientHeight / image.height;
			width =  image.width * scaling_factor;
			height = container[0].clientHeight;
			padding_left = (container[0].clientWidth - width) / 2;
			padding_right = (container[0].clientWidth - width) / 2;
		}
		
		container.css("padding-top", padding_top);
		container.css("padding-bottom", padding_bottom);
		container.css("padding-left", padding_left);
		container.css("padding-right", padding_right);
		
		container.append("<img src=" + image.src + " width=" + width + " height=" + height + "/>");
	}
	
	create_placeholder() {
		this.to_hide.hide();
		$("#" + this.container_name + " img:last-child").remove();
		this.draw_image(this.container, this.unable);
	}
	
	create_click_to_open() {
		this.to_hide.hide();
		$("#" + this.container_name +  " img:last-child").remove();
		this.draw_image(this.container, this.click);
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
		
		var d = dam;
		
		this.button.off("click");
		this.button.on("click", function(ev) {self.set_data(d)});
	}
	
	success(reply) {
		if(reply.error == false) {
			if(this.timeout != undefined)
				clearTimeout(this.timeout);
			
			this.result_container.show();
			
			this.result_dam.removeClass('alert-success');
			this.result_dam.removeClass('alert-warning');
			this.result_dam.removeClass('alert-danger');
			
			this.result_dam.addClass('alert-success');
			this.result_dam.html("<strong>Success:</strong> " + reply.message);
			
			var delay = 10000;
			var self = this;
			
			this.timeout = setTimeout(function() {self.result_container.hide()}, delay);
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
		var payload = "{\"reference_id\" : \"" + this.ae.reference_id + "\", \"ae_id\" : \"" + this.ae.ae_id + "\", \"ae_name\" : \"" + this.ae.ae_name + "\", \"dam_id\" : \"" + dam.dam_id + "\", \"data\" : " + !dam.state + "}";
		ajax_post_req(setdamdata, payload, this, this.success, this.error);
	}
}
