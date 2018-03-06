var map;
var aes = [];
var markers = [];

function map_constructor(container) {
	$('<script />', {type: "text/javascript", src : "https://maps.googleapis.com/maps/api/js?key=AIzaSyDQVpIU4EdpO_4ZI5mU2gTDKOsLRSeFUW8&callback=create_map"}).appendTo(container);
}

function create_map() {
	for(var i = 0; i < aes.length; i++) {
		var coordinates = {lat : aes[i].lat, lng : aes[i].lng};
		if(i == 0)
			map = new google.maps.Map($("#map")[0], {zoom: 6, center: coordinates});
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
		getSensorData(ae);
		$('#modal').modal('show');
	});
}

function getMarkerPositionSuccess(reply) {
	if(reply.error == false) {
		for(var i = 0; i < reply.message.length; i++) {
			aes[i] = reply.message[i];
		}
		map_constructor($("#map"));
	}	
	else
		getMarkerError(reply);
}

function getMarkerError(reply) {
	console.log(reply.message);
	create_map_placeholder($("#map"));
}

function getMarkerPosition() {
	ajax_get_req(getmarkerposition, getMarkerPositionSuccess, getMarkerError);
}