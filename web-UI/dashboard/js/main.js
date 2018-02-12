// ----------------------------
// MAIN.JS
// It contains js function
// to prepare home dashboard page
// ------------------------------

// ----------------------------
// GLOBAL GAUGES VARIABLE
// ----------------------------

var gauge_tot;
var gauge_acc;
var gauge_bra;
var gauge_ste;

// ----------------------------
// PAGE CODE
// ----------------------------

get_user_data();

$(document).ready(function(){
    get_lasttrip_data();
    gauge_build();
});

// ----------------------------
// GET USER FUNCTIONS
// ----------------------------

// AJAX-REQ
// Change navbar link if already logged in
function get_user_data() {
    ajax_req(
        php_redir, 
        "",     
        get_succ, 
        get_err
    );
}

// AJAX-REP
// Action done in case of success
function get_succ(reply) {
    if (reply.error == false)
        prepare_page(reply.message);
    else
        window.location.replace(rel_fron_path);
}

// AJAX-ERR
// Action done in case of failure
function get_err(reply) {
	alert("Server unreachable.");
    window.location.replace(rel_fron_path);
}

// Prepare page with custom user data
function prepare_page(userdata) {
    $('.nav-user-a').attr("href", php_logout);
    $('.nav-user-a').attr("title", userdata.username + " - Logout");
    $('.nav-avatar').attr("src", img_svr_path + userdata.avatar);
    $('.card-avatar').attr("src", img_svr_path + userdata.avatar);
    $('.cover-img').css('background-image', 'url(' + img_svr_path + userdata.cover + ')');
    $('.card-name').html(userdata.name + " " + userdata.surname);
	if(userdata.admin == "1")
		$('.card-text').html("ADMINISTRATOR");
	else
		$('.card-text').html("NORMAL USER");
		
    $('a.fa.fa-times-circle-o').attr("href", rel_fron_path);
    toggle_tooltip();
}

// -------------------------
// GET LAST TRIP FUNCTION
// -------------------------

// AJAX-REQ
// Get last trip data with AJAX req
function get_lasttrip_data() {
    ajax_req(
        php_home,
        "",     
        get_lasttrip_succ, 
        get_lasttrip_err
    );
}

// AJAX-REP
// Set gauge data and counter
function get_lasttrip_succ(reply) {
    if (reply.error == false) {
        trip_data = reply.lasttripdata;
        gauge_set(trip_data.pointstotal, 
                    trip_data.pointsacceleration,
                    trip_data.pointsbraking,
                    trip_data.pointssteering);
        counter_set(trip_data.starttime, trip_data.secondslength);
    }
        
}

// AJAX-ERR
// Alert with error text in case of failure
function get_lasttrip_err() {
    alert("Server unreachable.");
}

// Prepare counter with last trip data
function counter_set(starttime, secondslength) {
    hour_minute = starttime.substr(11, 5);
    $('#counter-length').html(secondslength+'<small class="unit green">s</small>');
    $('#counter-starttime').html(hour_minute);
}

// -------------------------------------
// GAUGE OPTIONS AND BUILD
// -------------------------------------

var opts = {
    angle: -0.1,    // The span of the gauge arc
    lineWidth: 0.2, // The line thickness
    radiusScale: 1, // Relative radius
    pointer: {
      length: 0.5, // Relative to gauge radius
      strokeWidth: 0.035, // The thickness
      color: '#000000' // Fill color
    },
    minValue: 0,
    limitMax: false,     // If false, max value increases automatically if value > maxValue
    limitMin: true,     // If true, the min value of the gauge will be fixed
    colorStart: '#6FADCF',   // Colors
    colorStop: '#8FC0DA',    // just experiment with them
    strokeColor: '#E0E0E0',  // to see which ones work best for you
    generateGradient: true,
    highDpiSupport: true,     // High resolution support
    percentColors: [[0.0, "#ff0000" ], [0.50, "#f9c802"], [1.0, "#a9d70b"]]
  };

// Build gauge and set initial data
function gauge_build() {
    gauge_tot = new Gauge(document.getElementById("gauge-total")).setOptions(opts);
    gauge_acc = new Gauge(document.getElementById("gauge-acc")).setOptions(opts);
    gauge_bra = new Gauge(document.getElementById("gauge-bra")).setOptions(opts);
    gauge_ste = new Gauge(document.getElementById("gauge-ste")).setOptions(opts);
    gauge_tot.setTextField(document.getElementById("textfield-tot"));
    gauge_acc.setTextField(document.getElementById("textfield-acc"));
    gauge_bra.setTextField(document.getElementById("textfield-bra"));
    gauge_ste.setTextField(document.getElementById("textfield-ste"));
    gauge_tot.maxValue = 100;
    gauge_acc.maxValue = 100;
    gauge_bra.maxValue = 100;
    gauge_ste.maxValue = 100;
    gauge_tot.set(0);
    gauge_bra.set(0);
    gauge_acc.set(0);
    gauge_ste.set(0);
}

// Set gauge to last trip data
function gauge_set(tot, acc, bra, ste) {
    gauge_tot.set(tot);
    gauge_bra.set(acc);
    gauge_acc.set(bra);
    gauge_ste.set(ste);
}

// -------------------------------------
// UTILITY
// -------------------------------------

// Send an ajax req
function ajax_req(dest, info, succ, err) {
    $.ajax({
        type: "POST",
        url: dest,
        xhrFields: { withCredentials: true },
        data: info,
        dataType: "json",
        success: succ,
        error: err
    });
}

// Toggle on boostrap tooltip
function toggle_tooltip() {
    $('[data-toggle="tooltip"]').tooltip(); 
}
