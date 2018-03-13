var map;

function create_map() {
	var default_coordinates = {lat: 43.843176, lng: 10.734928};
	map = new google.maps.Map(document.getElementById('map'), {zoom: 6, center: default_coordinates});
	
	add_marker(default_coordinates);
}

function add_marker(coordinates) {
	var marker = new google.maps.Marker({position: coordinates, map: map});
	
	map.setCenter(new google.maps.LatLng(coordinates.lat, coordinates.lng) );
	
	marker.addListener('click', function() {$('#modal').modal('show');});
}