class Map {
	constructor(is_admin) {
		window.google_map_callback = this.google_map_callback.bind(this);
		
		var googleapi = "https://maps.googleapis.com/maps/api/js?key=AIzaSyDQVpIU4EdpO_4ZI5mU2gTDKOsLRSeFUW8&callback=google_map_callback";
		//var googleapi = "https://maps.googleapis.com/maps/api/js?key=AIzaSyDQVpIU4EdpO_4ZI5mU2gTDKOsLRSeFUW8";
		var script_type = "text/javascript";
		
		this.first_time = true;
		this.ae = [];
		this.markers = [];
		this.is_admin = is_admin;
		
		this.modal_iframe = $("#modal_frame");
		this.container_name = "map";
		this.container = $("#" + this.container_name);
		this.modal_container = $("#modal");
		
		this.unable = $("#unable_rect")[0];
		
		this.container.append("<script type=" + script_type + " src=" + googleapi + "/>");
		
		this.create_placeholder();
	}
	
	google_map_callback() {
		this.createEventResource();
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
	
	draw_map() {
		$("#" + this.container_name +  " img:last-child").remove();
		this.map = new google.maps.Map(this.container[0], {zoom: 6});
		this.first_time = false;
	}
	
	create_placeholder() {
		$("#" + this.container_name +  " img:last-child").remove();
		this.draw_image(this.container, this.unable);
	}
	
	add_marker(ae, coordinates) {
		if(this.first_time) {
			this.draw_map();
			this.map.setCenter(new google.maps.LatLng(coordinates.lat, coordinates.lng));
		}
		
		for(var i = 0; i < this.markers.length; i++)
			if(this.markers[i].lat == coordinates.lat && this.markers[i].lng == coordinates.lng)
				return;
		
		this.markers.push(coordinates);
		
		var marker = new google.maps.Marker({position: coordinates, map: this.map});
		
		var self = this;
		
		marker.addListener("click", function(ev) {self.marker_handler(ae)});
	}
	
	marker_handler(ae) {
		var reference_id = "reference_id=" + ae.reference_id;
		var ae_id = "&ae_id=" + ae.ae_id;
		var ae_name = "&ae_name=" + ae.ae_name;
		var lng = "&lng=" + ae.lng;
		var lat = "&lat=" + ae.lat;
		var message = "&message=" + ae.message;
		var level = "&level=" + ae.level;
		var is_admin = "&is_admin=" + this.is_admin;
		
		this.modal_iframe.attr("src", rel_iframe_path + "?" + reference_id + ae_id + ae_name + lng + lat + message + level + is_admin);
		this.modal_container.modal("show");
	}
	
	onmessage(reply) {
		if(reply.error == false && reply.message.length != 0 && reply.message != null) {
			for(var i = 0; i < reply.message.length; i++) {
				if(reply.message[i] != undefined && reply.message[i] != null) {
					var present = false;
					for(var j = 0; j < this.ae.length; j++) {
						if(this.ae[j] != undefined && this.ae[j] != null && this.ae[j].reference_id != undefined && this.ae[j].ae_id != undefined && this.ae[j].reference_id == reply.message[i].reference_id && this.ae[j].ae_id == reply.message[i].ae_id) {
							if(reply.message[i].reference_id != undefined) this.ae[j].reference_id = reply.message[i].reference_id;
							if(reply.message[i].ae_id != undefined) this.ae[j].ae_id = reply.message[i].ae_id;
							if(reply.message[i].ae_name != undefined) this.ae[j].ae_name = reply.message[i].ae_name;
							if(reply.message[i].lat != undefined) this.ae[j].lat = reply.message[i].lat;
							if(reply.message[i].lng != undefined) this.ae[j].lng = reply.message[i].lng;
							
							present = true;
						}
					}
					
					var index = i;
					
					if(!present) {
						index = this.ae.length;
						this.ae[index] = reply.message[i];
					}
					
					if(this.ae[index] != undefined && this.ae[index].reference_id != undefined && this.ae[index].ae_id != undefined && this.ae[index].ae_name != undefined && this.ae[index].lat != undefined && this.ae[index].lng != undefined) {
						this.add_marker(this.ae[index], {lat : this.ae[index].lat, lng : this.ae[index].lng});
					}
				}
			}
		}	
		else 
			this.create_placeholder();
	}
	
	createEventResource(){
		this.eventSource = new EventSource(getmarkerdata);
		
		var self = this;
		
		this.eventSource.onmessage = function(e){self.onmessage(JSON.parse(e.data));};

	}
}
