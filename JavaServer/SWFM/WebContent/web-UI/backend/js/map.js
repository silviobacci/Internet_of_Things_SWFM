function MAP(p) {
	this.container = $("#map");
	this.modal = $("#modal");
	this.img = $("#map img:last-child");
	
	this.unable = $("#unable_rect")[0];
	
	this.ae = [];
	this.period = p;
}

MAP.prototype.draw_map = function () {
	this.map = new google.maps.Map(this.container[0], {zoom: 6});
	this.req = requestAnimationFrame(function(timestamp){this.refresh(timestamp, true);});
}

MAP.prototype.create_placeholder = function () {
	this.img.remove();
	this.container.append("<img src=" + this.unable.src + " width=" + this.map[0].width + "height=" + this.map[0].height + "/>");
}

MAP.prototype.add_marker = function (ae, coordinates) {
	var marker = new google.maps.Marker({position: coordinates, map: this.map});
	
	this.map.setCenter(new google.maps.LatLng(coordinates.lat, coordinates.lng));
	
	var self = this;
	
	marker.addListener("click", function(ev) {self.marker_handler(ae)});
}

MAP.prototype.marker_handler = function (ae) {
	this.modal.modal("show");
	
	setTimeout(this.modal_handler, 500, ae);
}

MAP.prototype.modal_handler = function (ae) {
	var texture = new TEXTURE(5000, ae);
}

MAP.prototype.success = function (reply) {
	if(reply.error == false && reply.message.length != 0) {
		for(var i = 0; i < reply.message.length; i++)
			if(this.ae[i] != reply.message[i]) {
				this.ae[i] = reply.message[i];
				this.add_marker(this.ae[i], {lat : this.ae[i].lat, lng : this.ae[i].lng});
			}
	}	
	else
		this.error(reply);
}

MAP.prototype.error = function (reply) {
	this.create_placeholder();
}

MAP.prototype.get_marker_data = function () {
	ajax_get_req(getmarkerdata, this, this.success, this.error);
}

MAP.prototype.refresh = function (timestamp, first_time) {
	if(first_time) {
		this.start_time = timestamp;
		this.get_marker_data();
	}
	
	var self = this;
	
	if (timestamp - this.start_time < this.period)
		this.req = requestAnimationFrame(function(timestamp){self.refresh(timestamp, false);});
	else
		this.req = requestAnimationFrame(function(timestamp){self.refresh(timestamp, true);});
}
