window.onload = function(e) { 
	var reference_id = getParamValue("reference_id");
	var ae_id = getParamValue("ae_id");
	var ae_name = getParamValue("ae_name");
	var lat = parseFloat(getParamValue("lat"));
	var lng = parseFloat(getParamValue("lng"));
	var message = decodeURI(getParamValue("message"));
	var level = parseInt(getParamValue("level"));
	var is_admin = getParamValue("is_admin") == "true";
	
	var ae = {
		reference_id : reference_id,
		ae_id : ae_id,
		ae_name : ae_name,
		lat : lat,
		lng : lng,
		message : message,
		level : level
	};
	
	var modal = new ModalCreater(ae, is_admin);
}

function getParamValue(paramName) {
    url = window.location.search.substring(1);
    var qArray = url.split('&');
    for (var i = 0; i < qArray.length; i++) {
        var pArr = qArray[i].split('=');
        if (pArr[0] == paramName) 
            return pArr[1];
    }
}