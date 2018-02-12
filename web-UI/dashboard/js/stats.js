// -------------------------
// CRASH.JS
// It contains all js code to
// fill up crash page
// -----------------------

const CAL_FIRST_DAY = 0;
const CAL_LAST_DAY = 41;

get_user_data();

$(document).ready(function(){
    datepicker_build();
    datepickerview_event_register();
    datepickerchange_event_register();
    datepickerupdate_event_trigger();
    datepickerchange_event_trigger();
    select_event_register();
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
// DATE PICKER BUILDER FUNCTION
// -----------------------------

// build the datepicker
function datepicker_build() {
    $('#datetimepicker').datetimepicker({
        format: 'DD/MM/YYYY',
        inline: true
    });
}

// -----------------------------
// DATE PICKER AJAX FUNCTION
// -----------------------------

// AJAX-REQ
// register event function
function datepickerview_event_register() {
    $('#datetimepicker').on('dp.update', function(e){
        if(e.change == 'M' || e.change == null) {
            var initdata = $('.datepicker-days tbody td').eq(CAL_FIRST_DAY).attr("data-day");
            var lastdata = $('.datepicker-days tbody td').eq(CAL_LAST_DAY).attr("data-day");
            var serialized = "init="+initdata+"&last="+lastdata;
            ajax_req(php_stat, serialized, datepicker_fill, datepicker_fill_error);
        }
    });
}

// trigger datepicker update event
function datepickerupdate_event_trigger() {
    $('#datetimepicker').trigger('dp.update');
}

// AJAX-REP
// fill with notify icon after user view change
function datepicker_fill(reply) {
    if(reply.error == true)
        alert(reply.message);
    else
        for(var i = 0; i < $('.datepicker-days tbody td').length; i++)
            if(reply.statsdates.includes($('.datepicker-days tbody td').eq(i).attr("data-day")))   
                $( ".datepicker-days tbody td" ).eq(i).addClass( "notify");
}

// AJAX-ERR
// alert with an error in case of server error
function datepicker_fill_error(reply) {
    alert("Unable to fill calendar - Server unrechable!");
}

// --------------------------
// SELECT LIST AJAX FUNCTION
// --------------------------

// AJAX-REQ
// register event function
function datepickerchange_event_register() {
    $('#datetimepicker').on('dp.change', function(e){
        var date;

        if(e != null)
            date = moment(e.date).format('L');
        else
            date = moment();

        var serialized = "date="+date;
        ajax_req(php_stat, serialized, select_fill, select_fill_error);
        datepickerupdate_event_trigger();
    });
}

// trigger datepicker change event
function datepickerchange_event_trigger() {
    $('#datetimepicker').trigger('dp.change');
}

// AJAX-REP
// crash table fill
function select_fill(reply) {
    if(reply.error == true) {
        select_fill_empty();
        return;
    }
    
    select_reset();

    for(var i = 0; i < reply.statlist.length; i++) {
        $('#trip-list').append(`<option value="`+reply.statlist[i].id+`">`+reply.statlist[i].time+`</option>`);
    }

    select_event_trigger();
}

// AJAX-ERR
// alert with an error in case of server error
function select_fill_error() {
    select_fill_empty();
    alert("Unable to fill trip list - Server unrechable!");
}

// --------------------------
// COUNTER AJAX FUNCTION
// --------------------------

// AJAX-REQ
// register event function
function select_event_register() {
    $('#trip-list').change(function(e){
        console.log("ABC");
        var id = $('#trip-list').find(":selected").val();

        if(id == null)
            return;

        var serialized = "trip_id="+id;
        ajax_req(php_stat, serialized, counter_fill, counter_fill_error);
    });
}

// trigger select change event
function select_event_trigger() {
    $('#trip-list').trigger('change');
}

// AJAX-REP
// counter fill
function counter_fill(reply) {
    if(reply.error == true) {
        counter_fill_empty();
        return;
    }
    
    counter_reset();

    counter_animate('#acc-num', reply.statdata.numberacc);
    counter_animate('#acc-wrs', reply.statdata.worstacc);
    counter_animate('#bra-num', reply.statdata.numberbra);
    counter_animate('#bra-wrs', reply.statdata.worstbra);
    counter_animate('#trn-num', reply.statdata.numbercur);
    counter_animate('#trn-wrs', reply.statdata.worstcur);
}

// AJAX-ERR
// alert with an error in case of server error
function counter_fill_error() {
    counter_fill_empty();
    alert("Unable to fill counters - Server unrechable!");
}

// --------------------------
// SELECT UTILITY FUNCTION
// --------------------------

// reset select content
function select_reset() {
    $('#trip-list').html("");
}

// fill the table with empty option list
function select_fill_empty() {
    $('#trip-list').html("<option>-</option>");
}

// --------------------------
// COUNTER UTILITY FUNCTION
// --------------------------

// reset counter content
function counter_reset() {
    for(var i = 0; i < $('.counter-card h3').length; i++) {
        $('.counter-card h3').eq(i).html("");
        $('.counter-card h3').eq(i).attr('style', '');
    }     
}

// fill counters with empty value
function counter_fill_empty() {
    for(var i = 0; i < $('.counter-card h3').length; i++)
        $('.counter-card h3').eq(i).html("-");
}

function counter_animate(selector, num) {
    $(selector).html("0");
    $(selector).animateNumber(
        {
          number: num,
          color: counter_get_color(num),
        },
        2000
      );
}

function counter_get_color(num) {
    if(num < 30)
        return $.Color("#1BC98E");
    else if (num >= 30 && num < 70)
        return $.Color("rgb(201, 175, 27)");
    else
        return $.Color("#E64759");
}

// -------------------------------------
// GENERAL UTILITY
// -------------------------------------

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
