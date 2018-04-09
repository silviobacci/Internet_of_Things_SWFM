class DamInformation {
	constructor() {
		this.canvas = $("#river-ov")[0];
		this.container = $("#dam-info");
		
		this.to_hide = $("#dam-info-container");
		
		this.container.css("width", this.canvas.width + 4);
	}
	
	create_placeholder() {
		this.to_hide.hide();
	}
	
	create_click_to_open() {
		this.container.html("<strong>Selected mote:</strong> No dam selected.");
	}
	
	draw_info(dam) {
		if(dam == undefined)
			return;

		var dam_id = dam.dam_id.substring(dam.dam_id.lastIndexOf("/") + 1, dam.dam_id.length)
		this.container.html("<strong>Selected dam: </strong> " + dam_id);
	}
}
