// -------------------------
// EVALUATION.JS
// It contains all js code to
// fill up evaluation page
// -----------------------

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
    scroll_page_toggle();
    gauge_build();
    get_evaluation_data();
});

// -------------------------------------
// REDIRECT USER FUNCTION
// -------------------------------------

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
function get_err() {
    alert("Server unreachable.");
    window.location.replace(rel_fron_path);
}

// Build page with user data
function prepare_page(userdata) {
    $('.nav-user-a').attr("href", php_logout);
    $('.nav-user-a').attr("title", userdata.username + " - Logout");
    $('.nav-avatar').attr("src", img_svr_path + userdata.avatar);
    $('a.fa.fa-times-circle-o').attr("href", rel_fron_path);
    toggle_tooltip();
}

// -----------------------------
// GET EVALUATION DATA FUNCTIONS
// -----------------------------

// AJAX-REQ
// Get evaluation data from server
function get_evaluation_data() {
    ajax_req(
        php_eval, 
        "",     
        get_evaluation_data_succ, 
        get_evaluation_data_err
    );
}

// AJAX-REP
// In case of success prepare chart and gauge
function get_evaluation_data_succ(reply) {
    if (reply.error == false) {
        eval_data = reply.evaluationdata;
        gauge_set(eval_data[eval_data.length-1].pointstotal, 
                eval_data[eval_data.length-1].pointsacceleration,
                eval_data[eval_data.length-1].pointsbraking,
                eval_data[eval_data.length-1].pointssteering);
        charts_draw(eval_data);
    }
        
}

// AJAX-ERR
// In case of error alert with text
function get_evaluation_data_err() {
    alert("Server unreachable.");
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
    gauge_tot = new Gauge(document.getElementById("gauge-tot")).setOptions(opts);
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
// CHART OPTIONS AND BUILD
// -------------------------------------

function charts_draw(eval_data) {
    chart_tot_draw(eval_data);
    chart_acc_draw(eval_data);
    chart_bra_draw(eval_data);
    chart_ste_draw(eval_data);
}

// build chart tot
function chart_tot_draw(eval_data) {
    var data_points = [];
    for(var i = 0; i < eval_data.length; i++) {
        data_points[i] = { x : Number(eval_data[i].number), 
                          y : Number(eval_data[i].pointstotal) };
    }

    build_chart("chart-tot", data_points);
}

// build chart acc
function chart_acc_draw(eval_data) {
    var data_points = [];
    for(var i = 0; i < eval_data.length; i++) {
        data_points[i] = { x : Number(eval_data[i].number), 
                          y : Number(eval_data[i].pointsacceleration) };
    }

    build_chart("chart-acc", data_points);
}

// build chart bra
function chart_bra_draw(eval_data) {
    var data_points = [];
    for(var i = 0; i < eval_data.length; i++) {
        data_points[i] = { x : Number(eval_data[i].number), 
                          y : Number(eval_data[i].pointsbraking) };
    }

    build_chart("chart-bra", data_points);
}

// build chart ste
function chart_ste_draw(eval_data) {
    var data_points = [];
    for(var i = 0; i < eval_data.length; i++) {
        data_points[i] = { x : Number(eval_data[i].number), 
                          y : Number(eval_data[i].pointssteering) };
    }

    build_chart("chart-ste", data_points);
}

// -------------------------------------
// GENERAL UTILITY
// -------------------------------------

// build a chart
function build_chart(chart_id, data_points) {
    var chart = new CanvasJS.Chart(chart_id, {
        animationEnabled: true,
        axisX: {
            valueFormatString: "#",
            titleFontFamily: "Roboto",
            interval: 1,
            minimum: data_points[0].x,
            maximum: data_points[data_points.length-1].x
        },
        axisY: {
            gridThickness: 0,
            tickLength: 0,
            margin: 0,
            lineThickness: 0,
            valueFormatString: " "
        },
        legend: {
            fontFamily: "Roboto",
            verticalAlign: "top",
            horizontalAlign: "right",
            dockInsidePlotArea: true
        },
        data: [{
            name: "Overall",
            legendMarkerType: "square",
            type: "area",
            color: "rgba(40,175,101,0.6)",
            markerSize: 0,
            dataPoints: data_points
        }]
    });
    chart.render();
}

// make an ajax req
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

// toggle on boostrap tooltip
function toggle_tooltip() {
    $('[data-toggle="tooltip"]').tooltip(); 
}

// toggle on full height scroll on big devices
function scroll_page_toggle() {
    if(window.matchMedia("(min-width: 768px)").matches)
        $('#fullpage').fullpage();
    else
        $('.row-heading:not(:first)').hide();
}