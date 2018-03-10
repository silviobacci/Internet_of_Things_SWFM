var map;
var aes = [];
var markers = [];

function map_constructor(container) {
	$('<script />', {type: "text/javascript", src : "https://maps.googleapis.com/maps/api/js?key=AIzaSyDQVpIU4EdpO_4ZI5mU2gTDKOsLRSeFUW8&callback=create_map"}).appendTo(container);
}

function create_map() {
	map = new google.maps.Map($("#map")[0], {zoom: 6});
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
		var id = ae.id.substring(ae.id.lastIndexOf("/") + 1, ae.id.length);
		$('#modal-label').html(ae.name + " - " + id);
		draw_alert(ae);
		$('#modal').modal('show');
		getSensorData(ae);
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