var map;

function map_constructor(container) {
	$('<script />', {type: "text/javascript", src : "https://maps.googleapis.com/maps/api/js?key=AIzaSyDQVpIU4EdpO_4ZI5mU2gTDKOsLRSeFUW8&callback=create_map"}).appendTo(container);
}

function create_map() {
	var default_coordinates = {lat: 43.843176, lng: 10.734928};
	map = new google.maps.Map($("#map")[0], {zoom: 6, center: default_coordinates});
	
	add_marker(default_coordinates);
}

function create_map_placeholder(map) {
	$('<img />', {src: $("#unable_quad")[0].src, width: map[0].width, height : map[0].height}).appendTo(map);
}

function add_marker(coordinates) {
	var marker = new google.maps.Marker({position: coordinates, map: map});
	
	map.setCenter(new google.maps.LatLng(coordinates.lat, coordinates.lng) );
	
	marker.addListener('click', function() {$('#modal').modal('show');});
}

function getMarkerSuccess(reply) {
	console.log(reply);
}

function getMarkerError(reply) {
	console.log(reply);
}

function getMarkers() {
	ajax_get_req(getmarkerposition, getMarkerSuccess, getMarkerError);
}