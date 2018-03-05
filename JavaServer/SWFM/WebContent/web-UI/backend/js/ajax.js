// Send an ajax post req
function ajax_post_req(dest, info, succ, err) {
    $.ajax({
		type: "POST",
		url: dest,
		data: info,
		dataType: "json",
		success: succ,
		error: err
    });
}

//Send an ajax get req
function ajax_get_req(dest, succ, err) {
    $.ajax({
		type: "GET",
		url: dest,
		success: succ,
		error: err
    });
}