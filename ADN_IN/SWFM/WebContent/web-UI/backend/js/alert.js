class Alert {
	constructor(ae) {
		this.canvas = $("#river-sec")[0];
		
		this.ae = ae;
		
		this.container = $("#alert");
		this.to_hide = $("#alert-container");
		
		this.container.css("width", this.canvas.width + 4);
		
		this.createEventResource();
	}
	
	create_click_to_open() {
		this.create_placeholder();
	}
	
	create_placeholder() {
		this.to_hide.hide();
	}
	
	draw_alert() {
		this.to_hide.show();
		this.container.removeClass("alert-success");
		this.container.removeClass("alert-warning");
		this.container.removeClass("alert-danger");
		this.container.removeClass("alert-info");
	
		switch(this.alert.level) {
			case 1:
				this.container.addClass("alert-success");
				this.container.html("<strong>Quiet!</strong> " + this.alert.message);
				break;
			case 2:
				this.container.addClass("alert-warning");
				this.container.html("<strong>Warning!</strong> " + this.alert.message);
				break;
			case 3:
				this.container.addClass("alert-danger");
				this.container.html("<strong>Alarm!</strong> " + this.alert.message);
				break;
			default:
				this.container.addClass("alert-info");
				this.container.html("<strong>Info!</strong> No message received by the system.");
				break;
		}
	}
	
	onmessage(reply) {
		if(reply.error == false) {
			for(var i = 0; i < reply.message.length; i++)	
				if(this.alert == undefined || this.alert.message != reply.message[i].message)
					this.alert = reply.message[i];
					
			this.draw_alert();
		}	
		else
			this.create_placeholder();
	}
	
	createEventResource(){
		this.eventSource = new EventSource(getalertdata + '?reference_id=' + this.ae.reference_id + "&ae_id=" + this.ae.ae_id + "&ae_name=" + this.ae.ae_name);
		
		var self = this;
		
		this.eventSource.onmessage = function(e){self.onmessage(JSON.parse(e.data));};
	}
}