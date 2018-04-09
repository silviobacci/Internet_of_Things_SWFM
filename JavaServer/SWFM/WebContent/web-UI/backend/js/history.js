class History {
	constructor(a) {
		this.container_name = "chart-history";
		
		this.container = $("#" + this.container_name);
		
		this.canvas = $("#river-sec")[0];
		this.unable = $("#unable_rect")[0];
		this.click = $("#click")[0];
		
		this.container.css("width", this.canvas.width + 4);
		this.container.css("height", this.canvas.height + 4);
		
		this.chart_built = false;
		
		this.ae = a;
		
		this.data_points = [];
	}
	
	create_placeholder() {
		$("#" + this.container_name +  " img:last-child").remove();
		this.container.append("<img src=" + this.unable.src + " width=" + this.canvas.width + "height=" + this.canvas.height + "/>");
	}
	
	create_click_to_open() {
		$("#" + this.container_name +  " img:last-child").remove();
		this.container.append("<img src=" + this.click.src + " width=" + this.canvas.width + "height=" + this.canvas.height + "/>");
	}
	
	add_data_point(data_points) {
		for(var i = 0; i < data_points.length; i++)
			if(this.chart.options.data[0].dataPoints[i] == undefined)
				this.chart.options.data[0].dataPoints.push(data_points[i]);
			else if (this.chart.options.data[0].dataPoints[i].y != data_points[i].y)
				this.chart.options.data[0].dataPoints[3].y = data_points[i].y;
	}
	
	build_chart(data_points) {
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
	
	success(reply) {
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
	
	error(reply) {
		console.log(reply.message);
	}
	
	get_data(sensor) {
		var payload = "{\"id\" : \"" + sensor.id + "\"}";
		ajax_post_req(gethistorydata, payload, this, this.success, this.error);
	}
	
	onopen() {
		console.log("DAMS - Conncetion opened with the SSE");
	}
	
	onmessage(reply) {
		if(reply.error == false) {
			if(reply.message.length == 1)
				this.data_points[this.data_points.length] = reply.message[0];
			else	
				for(var i = 0; i < reply.message.length; i++)
					this.data_points[i] = reply.message[i];
			
			if(!this.chart_built)
				this.build_chart(this.data_points);
			else
				this.add_data_point(this.data_points);
			
			this.chart_built = true;
		}	
		else
			this.onerror(JSON.stringify(reply.message));
	}
	
	onerror(reply) {
		console.log("DAMS - Error: " + reply);
		this.create_placeholder();
	}
	
	createEventResource(sensor){
		if(sensor == undefined)
			return;
		
		var eventSource = new EventSource(getdamdata + '?reference_id=' + this.ae.reference_id + "&ae_id=" + this.ae.ae_id + "&ae_name=" + this.ae.ae_name +"&sensor_id=" + sensor.sensor_id);
		
		var self = this;
		
		eventSource.onopen = function(){self.onopen()};
		
		eventSource.onmessage = function(e){self.onmessage(JSON.parse(e.data));};
		
		eventSource.onerror = function(e){self.onerror(e);};
	}
}
