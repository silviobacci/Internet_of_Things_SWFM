function threshold_constructor(canvas, container) {
	set_threshold_dimension(canvas, container);
}

function set_threshold_dimension(canvas, container) {
	container.css("width", canvas[0].width + 4);
	container.css("height", canvas[0].height + 4);
}

function create_threshold_placeholder(canvas, container) {
	$('#threshold-container').hide();
	$("#threshold img:last-child").remove();
	container.append("<img src=" + $("#unable_rect")[0].src + " width=" + canvas[0].width + "height=" + canvas[0].height + "/>");
}

function create_threshold_click_to_open(canvas, container) {
	$('#threshold-container').hide();
	$("#threshold img:last-child").remove();
	container.append("<img src=" + $("#click")[0].src + " width=" + canvas[0].width + "height=" + canvas[0].height + "/>");
}

function create_slider_handler() {
	$('#slider').on("input", function() {$('#new_th').html("NEW THRESHOLD " + this.value + " cm");});
}

function draw_threshold() {
	$('#slider')[0].min = selected_sensor.min;
	$('#slider')[0].max = selected_sensor.max;
	$('#slider')[0].step = 1;
	$('#slider')[0].value = selected_sensor.th;

	$("#threshold img:last-child").remove();
	$('#threshold-container').show();
	$('#new_th').html("NEW THRESHOLD " + $('#slider')[0].value + " cm");
	$('#current-threshold').html("CURRENT THRESHOLD " + selected_sensor.th + " cm");
	create_slider_handler();
}

function submit_threshold_success(reply) {
	if(reply.error == false) {
		$('#result-container').show();
		$('#result-th').removeClass('alert-success');
		$('#result-th').removeClass('alert-warning');
		$('#result-th').removeClass('alert-danger');
		$('#result-th').addClass('alert-success');
		$('#result-th').html("<strong>Error:</strong> " + reply.message);
	}	
	else
		submit_threshold_error(reply);
}

function submit_threshold_error(reply) {
	$('#result-container').show();
	$('#result-th').removeClass('alert-success');
	$('#result-th').removeClass('alert-warning');
	$('#result-th').removeClass('alert-danger');
	$('#result-th').addClass('alert-danger');
	$('#result-th').html("<strong>Error:</strong> " + reply.message);
}

function submit_threshold() {
	var data = "{\"MIN\" : " + $('#slider')[0].min + ", \"MAX\" : " + $('#slider')[0].max + ", \"TH\" : " + $('#slider')[0].value + "}";
	var payload = "{\"ae\" : \"" + ae.id + "\", \"id\" : \"" + selected_sensor.id + "\", \"data\" : " + data + "}";
	ajax_post_req(setsensordata, payload, submit_threshold_success, submit_threshold_error);
}