function alert_constructor(canvas, container) {
	set_alert_dimension(canvas, container);
}

function set_alert_dimension(canvas, container) {
	container.css("width", canvas[0].width + 4);
}

function create_alert_placeholder() {
	$('#alert-container').hide();
}

function draw_alert(ae) {
	$('#alert-container').show();
	$('#alert').removeClass('alert-success');
	$('#alert').removeClass('alert-warning');
	$('#alert').removeClass('alert-danger');

	switch(ae.level) {
		case 1:
			$('#alert').addClass('alert-success');
			$('#alert').html("<strong>Quiet!</strong> " + ae.message);
			break;
		case 2:
			$('#alert').addClass('alert-warning');
			$('#alert').html("<strong>Warning!</strong> " + ae.message);
			break;
		default:
			$('#alert').addClass('alert-danger');
		$('#alert').html("<strong>Alarm!</strong> " + ae.message);
			break;
	}
}