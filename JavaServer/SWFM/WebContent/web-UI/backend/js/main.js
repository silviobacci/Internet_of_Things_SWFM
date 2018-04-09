var user;
var map_period = 10000;
var modal_period = 5000;

$(document).ready(function(){
	user = new User();
	
	user.get_user_data();
	
	var map = new Map(map_period);
});