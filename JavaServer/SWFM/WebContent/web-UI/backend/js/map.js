class Map {
	constructor(p) {
		window.google_map_callback = this.google_map_callback.bind(this);
		
		var googleapi = "https://maps.googleapis.com/maps/api/js?key=AIzaSyDQVpIU4EdpO_4ZI5mU2gTDKOsLRSeFUW8&callback=google_map_callback";
		//var googleapi = "https://maps.googleapis.com/maps/api/js?key=AIzaSyDQVpIU4EdpO_4ZI5mU2gTDKOsLRSeFUW8";
		var script_type = "text/javascript";
		
		this.first_time = true;
		this.ae = [];
		this.period = p;
		
		this.container_name = "map";
		this.container = $("#" + this.container_name);
		this.modal = $("#modal");
		
		this.unable = $("#unable_rect")[0];
		
		this.container.append("<script type=" + script_type + " src=" + googleapi + "/>");
		
		this.create_placeholder();
	}
	
	google_map_callback() {
		this.createEventResource();
	}
	
	draw_map() {
		$("#" + this.container_name +  " img:last-child").remove();
		this.map = new google.maps.Map(this.container[0], {zoom: 6});
		this.first_time = false;
	}
	
	create_placeholder() {
		$("#" + this.container_name +  " img:last-child").remove();
		this.container.append("<img src=" + this.unable.src + " width=" + this.container[0].width + "height=" + this.container[0].height + "/>");
	}
	
	add_marker(ae, coordinates) {
		if(this.first_time)
			this.draw_map();
		
		var marker = new google.maps.Marker({position: coordinates, map: this.map});
		
		this.map.setCenter(new google.maps.LatLng(coordinates.lat, coordinates.lng));
		
		var self = this;
		
		marker.addListener("click", function(ev) {self.marker_handler(ae)});
	}
	
	marker_handler(ae) {
		this.modal.modal("show");
		
		setTimeout(this.modal_handler, 500, ae);
	}
	
	modal_handler(ae) {
		var modal = new Modal(modal_period, ae);
	}
	
	onopen() {
		console.log("MAP - Conncetion opened with the SSE");
	}
	
	onmessage(reply) {
		if(reply.error == false && reply.message.length != 0 && reply.message != null) {
			for(var i = 0; i < reply.message.length; i++) {
				var present = false;
				for(var j = 0; i < this.ae.length; j++) {
					if(this.ae[j] != undefined && this.ae[j].reference_id != undefined && this.ae[j].ae_id != undefined && this.ae[j].reference_id == reply.message[i].reference_id && this.ae[j].ae_id == reply.message[i].ae_id) {
						if(reply.message[i].reference_id != undefined) this.ae[j].reference_id = reply.message[i].reference_id;
						if(reply.message[i].ae_id != undefined) this.ae[j].ae_id = reply.message[i].ae_id;
						if(reply.message[i].ae_name != undefined) this.ae[j].ae_name = reply.message[i].ae_name;
						if(reply.message[i].lat != undefined) this.ae[j].lat = reply.message[i].lat;
						if(reply.message[i].lng != undefined) this.ae[j].lng = reply.message[i].lng;
						if(reply.message[i].level != undefined) this.ae[j].level = reply.message[i].level;
						if(reply.message[i].message != undefined) this.ae[j].message = reply.message[i].message;
						
						present = true;
					}
				}
				
				var index = i;
				
				if(!present) {
					index = this.ae.length;
					this.ae[index] = reply.message[i];
				}
				
				if(this.ae[index].reference_id != undefined && this.ae[index].ae_id != undefined && this.ae[index].ae_name != undefined && this.ae[index].lat != undefined && this.ae[index].lng != undefined && this.ae[index].level != undefined && this.ae[index].message != undefined)
					this.add_marker(this.ae[index], {lat : this.ae[index].lat, lng : this.ae[index].lng});
			}
		}	
		else
			this.onerror(JSON.stringify(reply.message));
	}
	
	onerror(reply) {
		console.log("MAP - Error: " + reply);
		this.create_placeholder();
	}
	
	createEventResource(){
		var eventSource = new EventSource(getmarkerdata);
		
		var self = this;
		
		eventSource.onopen = function(){self.onopen()};
		
		eventSource.onmessage = function(e){self.onmessage(JSON.parse(e.data));};
		
		eventSource.onerror = function(e){self.onerror(e);};
	}
}
