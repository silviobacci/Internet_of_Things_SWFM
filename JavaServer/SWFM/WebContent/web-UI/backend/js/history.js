var chart_built = false;
var context_history;
var chart;

function history_constructor(canvas, container) {
	container.css("width", canvas[0].width + 4);
	container.css("height", canvas[0].height + 4);
}

function create_history_placeholder(canvas, container) {
	$("#history img:last-child").remove();
	container.append("<img src=" + $("#unable_rect")[0].src + " width=" + canvas[0].width + "height=" + canvas[0].height + "/>");
}

function create_history_click_to_open(canvas, container) {
	$("#history img:last-child").remove();
	container.append("<img src=" + $("#click")[0].src + " width=" + canvas[0].width + "height=" + canvas[0].height + "/>");
}

function add_data_point(data_points) {
	for(var i = 0; i < data_points.length; i++) {
		if(chart.options.data[0].dataPoints[i] == undefined)
			chart.options.data[0].dataPoints.push(data_points[i]);
		else if (chart.options.data[0].dataPoints[i].y != data_points[i].y)
			chart.options.data[0].dataPoints[3].y = data_points[i].y;
	}
}

function build_chart(data_points) {
    chart = new CanvasJS.Chart("chart-history", {
        animationEnabled: true,
        axisX: {
            gridThickness: 0,
            interval:3, 
            intervalType: "second", 
        },
        axisY: {
            gridThickness: 0
        },
        legend: {
            fontFamily: "Roboto",
            verticalAlign: "top",
            horizontalAlign: "right",
            dockInsidePlotArea: true
        },
        data: [{
            legendMarkerType: "square",
            type: "area",
            xValueType: "dateTime",
            color: "#87D0DE",
            dataPoints: data_points
        }]
    });
    chart.render();
}

function getHistoryDataSuccess(reply) {
	if(reply.error == false && reply.message.length != 0) {
		var data_points = [];
		
		for(var i = 0; i < reply.message.length; i++) {
			data_points[i] = reply.message[i];
		}
		console.log(data_points);

		if(data_points.lenght == 1)
			data_points[1] = data_points[0];
		
		if(!chart_built) {
			build_chart(data_points);
			chart_built = true;
		}
		else
			add_data_point(data_points);
	}	
	else
		getHistoryError(reply);
}

function getHistoryError(reply) {
	console.log(reply.message);
}

function getHistoryData(bc) {
	chart_built = bc;
	var payload = "{\"id\" : \"" + sensors[selected_sensor].id + "\"}";
	ajax_post_req(gethistorydata, payload ,getHistoryDataSuccess, getHistoryError);
}