function threshold_constructor(canvas, container) {
	set_threshold_dimension(canvas, container);
}

function set_threshold_dimension(canvas, container) {
	container.css("width", canvas[0].width + 4);
	container.css("height", canvas[0].height + 4);
}

function create_threshold_placeholder(canvas, container) {
	$('#threshold-container').hide();
	container.append("<img src=" + $("#unable_rect")[0].src + " width=" + canvas[0].width + "height=" + canvas[0].height + "/>");
}

function create_threshold_click_to_open(canvas, container) {
	$('#threshold-container').hide();
	container.append("<img src=" + $("#click")[0].src + " width=" + canvas[0].width + "height=" + canvas[0].height + "/>");
}

function create_slider_handler(mote_index) {
	$('#slider').on("input", function() {$('#new_th').html("NEW THRESHOLD " + this.value + " cm");});
}

function draw_threshold(mote_index) {
	$('#slider').prop({
        'min': sensors[mote_index].min,
        'max': sensors[mote_index].max
    });
	
	$('#slider').val(sensors[mote_index].th);
	
	$("#threshold img:last-child").remove();
	$('#threshold-container').show();
	$('#new_th').html("NEW THRESHOLD " + sensors[mote_index].th + " cm");
	$('#current-threshold').html("CURRENT THRESHOLD " + sensors[mote_index].th + " cm");
	create_slider_handler(mote_index);
}