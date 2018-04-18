class DamInformation {
	constructor() {
		this.canvas = $("#river-ov")[0];
		this.container = $("#dam-info");
		this.state_container = $(".is-not-working-dam");
		
		this.to_hide = $("#dam-info-container");
		
		this.container.css("width", this.canvas.width + 4);
	}
	
	create_placeholder() {
		this.to_hide.hide();
	}
	
	create_click_to_open() {
		this.container.html("<strong>Selected dam:</strong> No dam selected.");
	}
	
	draw_info(dam, is_admin) {
		if(dam == undefined)
			return;
		
		this.container.removeClass("alert-info");
		this.container.removeClass("alert-success");
		this.container.removeClass("alert-danger");

		var date = new Date(dam.creation_time);
		var state = dam.state ? "open" : "closed";
		
		var text = "<strong>Selected dam: </strong> " + dam.dam_name + "<br>" +
		"<strong>Last update: </strong>" + date.toLocaleString() + "<br>" +
		"<strong>Last state: </strong>" + state;
		
		if(dam.is_working) {
			this.container.addClass("alert-success");
			this.container.html(text);
			if(is_admin) this.state_container.show();
		}
		else {
			this.container.addClass("alert-danger");
			this.container.html( text + "<br><strong>ATTENTION: The selected dam is not working anymore.</strong>");
			if(is_admin) this.state_container.hide();
		}
	}
}
