var map;
var aes = [];
var markers = [];
var start_time_refresh_map;
var refresh_map_period = 10000;
var refresh_req;

function map_constructor(container) {
	$('<script />', {type: "text/javascript", src : "https://maps.googleapis.com/maps/api/js?key=AIzaSyDQVpIU4EdpO_4ZI5mU2gTDKOsLRSeFUW8&callback=create_map"}).appendTo(container);
}

function create_map() {
	map = new google.maps.Map($("#map")[0], {zoom: 6});
	refresh_req = requestAnimationFrame(function(timestamp){refresh_map(timestamp, true);});
}

function create_markers() {
	for(var i = 0; i < aes.length; i++) {
		var coordinates = {lat : aes[i].lat, lng : aes[i].lng};
		add_marker(aes[i], coordinates);
	}
}

function create_map_placeholder(map) {
	$('<img />', {src: $("#unable_quad")[0].src, width: map[0].width, height : map[0].height}).appendTo(map);
}

function add_marker(ae, coordinates) {
	var marker = new google.maps.Marker({position: coordinates, map: map});
	
	map.setCenter(new google.maps.LatLng(coordinates.lat, coordinates.lng));
	
	marker.addListener('click', function() {
		$('#modal').modal('show');
		refresh_sensor_req = requestAnimationFrame(function(timestamp){refresh_sensors(timestamp, ae, true);});
	});
}

function getMarkerDataSuccess(reply) {
	if(reply.error == false && reply.message.length != 0) {
		aes = [];
		for(var i = 0; i < reply.message.length; i++)
			aes[i] = reply.message[i];
		create_markers();
	}	
	else
		getMarkerError(reply);
}

function getMarkerError(reply) {
	create_map_placeholder($("#map"));
}

function getMarkerData() {
	ajax_get_req(getmarkerdata, getMarkerDataSuccess, getMarkerError);
}

function refresh_map(timestamp, first_time) {		
	if(first_time) {
		start_time_refresh_map = timestamp;
		getMarkerData();
	}
	
	if (timestamp - start_time_refresh_map < refresh_map_period)
		refresh_req = requestAnimationFrame(function(timestamp){refresh_map(timestamp, false);});
	else
		refresh_req = requestAnimationFrame(function(timestamp){refresh_map(timestamp, true);});
}