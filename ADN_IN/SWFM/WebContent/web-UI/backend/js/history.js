class History {
	constructor(a) {
		this.container_name = "chart-history";
		
		this.container = $("#" + this.container_name);
		
		this.canvas = $("#river-sec")[0];
		this.unable = $("#unable_rect")[0];
		this.click = $("#click")[0];
		
		this.container.css("width", this.canvas.width + 4);
		this.container.css("height", this.canvas.height + 4);
		
		this.ae = a;
		
		this.data_points = [];
		this.dates = [];
		this.counter = 0;
	}
	
	draw_image(container, image) {
		var is_inside = container[0].clientWidth >= image.width &&  container[0].clientHeight >= image.height ? true : false;
		
		if(!is_inside) {
			var check_width = (container[0].clientWidth < image.width && container[0].clientHeight >= image.height) ? true : false;
			var check_height = (container[0].clientWidth >= image.width && container[0].clientHeight < image.height) ? true : false;
			
			if(!check_width && !check_height) {
				var container_ratio = container[0].clientWidth / container[0].clientHeight;
				var image_ratio = image.width / image.height;
				
				check_width = image_ratio >= container_ratio ? true : false;
				check_height = !check_width;
			}
		}
			
		var padding_top = 0;
		var padding_bottom = 0;
		var padding_left = 0;
		var padding_right = 0;
		var width = image.width;
		var height = image.height;
		
		if(is_inside) {
			padding_top = (container[0].clientHeight - height) / 2;
			padding_left = (container[0].clientWidth - width) / 2;
		}
		else if(check_width) {
			var scaling_factor = container[0].clientWidth / image.width;
			width = container[0].clientWidth;
			height = image.height * scaling_factor;
			padding_top = (container[0].clientHeight - height) / 2;
			padding_bottom = (container[0].clientHeight - height) / 2;
		} 
		else if(check_height){
			var scaling_factor = container[0].clientHeight / image.height;
			width =  image.width * scaling_factor;
			height = container[0].clientHeight;
			padding_left = (container[0].clientWidth - width) / 2;
			padding_right = (container[0].clientWidth - width) / 2;
		}
		
		container.css("padding-top", padding_top);
		container.css("padding-bottom", padding_bottom);
		container.css("padding-left", padding_left);
		container.css("padding-right", padding_right);
		
		container.append("<img src=" + image.src + " width=" + width + " height=" + height + "/>");
	}
	
	create_placeholder() {
		$("#" + this.container_name +  " img:last-child").remove();
		this.draw_image(this.container, this.unable);
	}
	
	create_click_to_open() {
		$("#" + this.container_name +  " img:last-child").remove();
		this.draw_image(this.container, this.click);
	}
	
	add_data_point(data_points) {
		for(var i = 0; i < data_points.length; i++)
			this.chart.options.data[0].dataPoints.push(data_points[i]);
		
		while(this.chart.options.data[0].dataPoints.length > 5)
			this.chart.options.data[0].dataPoints.shift();
		
		this.chart.render();
	}
	
	build_chart() {
		var self = this;
	    this.chart = new CanvasJS.Chart(this.container_name, {
	        animationEnabled: true,
	        axisX: {
	        		valueFormatString: "#",
	            gridThickness: 0,
	            interval: 1, 
	            labelFormatter: function (e) {return CanvasJS.formatDate(self.dates[e.value], "DD-MM-YY");}
	        },
	        axisY: {
	            gridThickness: 0
	        },
	        toolTip: {
	        		contentFormatter	: function (e) {return CanvasJS.formatDate(self.dates[e.entries[0].dataPoint.x], "HH:mm:ss") + " : " + e.entries[0].dataPoint.y;}
	        },
	        data: [{
	            legendMarkerType: "square",
	            type: "area",
	            color: "#87D0DE",
	            dataPoints: this.data_points
	        }]
	    });
	    this.chart.render();
	}
	
	onmessage(reply) {
		if(reply.error == false) {
			if(this.data_points.length == 0)
				var start_counter = 0;
			else
				var start_counter = this.data_points[this.data_points.length - 1].x + 1;
			
			var new_data_points = [];
			var new_dates = [];
			
			for(var i = 0; i < reply.message.length; i++) {
				if(reply.message[i] != undefined && reply.message[i] != null) {
					this.data_points.push({x: this.counter, y: reply.message[i].y})
					new_data_points.push({x: this.counter, y: reply.message[i].y});
					this.dates.push(reply.message[i].x);
					new_dates.push(reply.message[i].x);
					this.counter++;
				}
			}
			
			while(this.data_points.length > 5) {
				this.data_points.shift();
				this.dates.shift();
			}
			
			if(!this.chart_built)
				this.build_chart();
			else
				this.add_data_point(new_data_points, new_dates);
			
			this.chart_built = true;
		}	
		else
			this.create_placeholder();
	}
	
	createEventResource(sensor){
		if(sensor == undefined)
			return;
		
		this.chart_built = false;
		
		this.eventSource = new EventSource(gethistorydata + '?reference_id=' + this.ae.reference_id + "&ae_id=" + this.ae.ae_id + "&ae_name=" + this.ae.ae_name +"&sensor_id=" + sensor.sensor_id);
		
		var self = this;
		
		this.eventSource.onmessage = function(e){self.onmessage(JSON.parse(e.data));};
		
		this.eventSource.onopen = function(e){if(self.chart != undefined) self.chart.options.data[0].dataPoints = []; self.data_points = []; self.dates = [];};
	}
}
