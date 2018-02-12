// -------------------------
// CRASH.JS
// It contains all js code to
// fill up crash page
// -----------------------

const MAX_CRASH_PER_PAGE = 4;
const CAL_FIRST_DAY = 0;
const CAL_LAST_DAY = 41;

get_user_data();

$(document).ready(function(){
    datepicker_build();
    datepickerview_event_register();
    datepickerchange_event_register();
    datepickerupdate_event_trigger();
    datepickerchange_event_trigger();
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
            ajax_req(php_crash, serialized, datepicker_fill, datepicker_fill_error);
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
    if(reply.error != true)
        for(var i = 0; i < $('.datepicker-days tbody td').length; i++)
            if(reply.crashdates.includes($('.datepicker-days tbody td').eq(i).attr("data-day")))   
                $( ".datepicker-days tbody td" ).eq(i).addClass( "notify");
}

// AJAX-ERR
// alert with an error in case of server error
function datepicker_fill_error(reply) {
    alert("Unable to fill calendar - Server unrechable!");
}

// --------------------------
// CRASH TABLE AJAX FUNCTION
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
        ajax_req(php_crash, serialized, table_fill, table_fill_error);
        datepickerupdate_event_trigger();
    });
}

// trigger datepicker change event
function datepickerchange_event_trigger() {
    $('#datetimepicker').trigger('dp.change');
}

// AJAX-REP
// crash table fill
function table_fill(reply) {
    if(reply.error == true) {
        table_fill_empty();
        return;
    }
    
    pagination_reset();
    pagination_create(reply.crashinfos.length);
    table_reset();

    for(var i = 0; i < reply.crashinfos.length; i++) {
        $('.crash-table tbody').append(`
        <tr style="display: none;">
            <th>`+reply.crashinfos[i].number+`</th>
            <td>`+reply.crashinfos[i].crashtime+`</td>
            <td>`+reply.crashinfos[i].intensity+`</td>
            <td>`+Boolean(reply.crashinfos[i].stationary)+`</td>
        </tr>
        `);
    }

    show_rows(0);
}

// AJAX-ERR
// alert with an error in case of server error
function table_fill_error() {
    table_fill_empty();
    alert("Unable to fill table - Server unrechable!");
}

// --------------------------
// TABLE UTILITY FUNCTION
// --------------------------

// reset table content
function table_reset() {
    $('.crash-table tbody').html("");
}

// fill the table with empty row
function table_fill_empty() {
    pagination_reset();
    $('.crash-table tbody').html(`
        <tr>
            <th>-</th>
            <td>-</td>
            <td>-</td>
            <td>-</td>
        </tr>`);
}

// show only limited number of rows
function show_rows(init_number) {
    for(var i = 0; i < $('.crash-table tbody tr').length; i++) {
        if(i >= init_number && i < MAX_CRASH_PER_PAGE+init_number)
            $('.crash-table tbody tr').eq(i).css("display", "table-row");
        else
            $('.crash-table tbody tr').eq(i).css("display", "none");
    }
}

// ---------------------------
// PAGINATION UTILITY FUNCTION
// ----------------------------

// reset pagination to default one
function pagination_reset() {
    $('.pagination').html(`<li class="page-item active">
                                <a class="page-link" href="javascript:pagechange(1)">1</a>
                        </li>`);
}

// create button for pagination
function pagination_create(number) {
    if(number > MAX_CRASH_PER_PAGE) {
        for(var i = 0; i < (number % MAX_CRASH_PER_PAGE); i++) {
            $('.pagination').append(`
                <li class="page-item">
                    <a class="page-link" href="javascript:pagechange(`+(i+2)+`)">`+(i+2)+`</a>
                </li>
            `);
        }
    }
}

// display the selected page
function pagechange(pagenum) {
    $('.pagination li').removeClass("active");
    $('.pagination li').eq(pagenum-1).addClass("active");
    show_rows((pagenum-1) * MAX_CRASH_PER_PAGE);
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
