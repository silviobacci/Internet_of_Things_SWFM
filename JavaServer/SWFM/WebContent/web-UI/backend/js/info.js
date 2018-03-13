function info_constructor(canvas, container) {
	set_info_dimension(canvas, container);
}

function set_info_dimension(canvas, container) {
	container.css("width", canvas[0].width + 4);
}

function create_info_placeholder() {
	$('#info-container').hide();
}

function create_info_click_to_open() {
	$('#info').html("<strong>Selected mote: </strong> No mote selected.");
}

function draw_info() {
	var id = sensors[selected_sensor].id.substring(sensors[selected_sensor].id.lastIndexOf("/") + 1, sensors[selected_sensor].id.length)
	$('#info').html("<strong>Selected mote: </strong> " + id);
}
