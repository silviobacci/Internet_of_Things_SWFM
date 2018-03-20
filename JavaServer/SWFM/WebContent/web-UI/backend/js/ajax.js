// Send an ajax post req
function ajax_post_req(dest, info, ctx, succ, err) {
    $.ajax({
		type: "POST",
		context : ctx,
		url: dest,
		data: info,
		dataType: "json",
		success: succ,
		error: err
    });
}

//Send an ajax get req
function ajax_get_req(dest, ctx, succ, err) {
    $.ajax({
		type: "GET",
		context : ctx,
		url: dest,
		success: succ,
		error: err
    });
}