function HISTORY() {
	this.container_name = "chart-history";
	
	this.container = $("#" + this.container_name);
	
	this.canvas = $("#river-sec")[0];
	this.unable = $("#unable_rect")[0];
	this.click = $("#click")[0];
	
	this.container.css("width", this.canvas.width + 4);
	this.container.css("height", this.canvas.height + 4);
	
	this.chart_built = false;
}

HISTORY.prototype.create_placeholder = function () {
	$("#" + this.container_name +  " img:last-child").remove();
	this.container.append("<img src=" + this.unable.src + " width=" + this.canvas.width + "height=" + this.canvas.height + "/>");
}

HISTORY.prototype.create_click_to_open = function () {
	$("#" + this.container_name +  " img:last-child").remove();
	this.container.append("<img src=" + this.click.src + " width=" + this.canvas.width + "height=" + this.canvas.height + "/>");
}

HISTORY.prototype.add_data_point = function (data_points) {
	for(var i = 0; i < data_points.length; i++)
		if(this.chart.options.data[0].dataPoints[i] == undefined)
			this.chart.options.data[0].dataPoints.push(data_points[i]);
		else if (this.chart.options.data[0].dataPoints[i].y != data_points[i].y)
			this.chart.options.data[0].dataPoints[3].y = data_points[i].y;
}

HISTORY.prototype.build_chart = function (data_points) {
    this.chart = new CanvasJS.Chart(this.container_name, {
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
    this.chart.render();
}

HISTORY.prototype.success = function (reply) {
	if(reply.error == false && reply.message.length != 0) {
		var data_points = [];
		
		for(var i = 0; i < reply.message.length; i++)
			data_points[i] = reply.message[i];
		
		if(!this.chart_built)
			this.build_chart(data_points);
		else
			this.add_data_point(data_points);
		
		this.chart_built = true;
	}	
	else
		this.error(reply);
}

HISTORY.prototype.error = function (reply) {
	console.log(reply.message);
}

HISTORY.prototype.get_data = function (sensor) {
	var payload = "{\"id\" : \"" + sensor.id + "\"}";
	ajax_post_req(gethistorydata, payload, this, this.success, this.error);
}
