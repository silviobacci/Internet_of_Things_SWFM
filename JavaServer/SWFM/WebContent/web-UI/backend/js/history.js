var chart_id = "chart-history";
var context_history;

function history_constructor(canvas, container) {
	container.css("width", canvas[0].width + 4);
	container.css("height", canvas[0].height + 4);
}

function create_history_placeholder(canvas, container) {
	$('<img />', {src: $("#unable_rect")[0].src, width: canvas[0].width, height : canvas[0].height}).appendTo(container);
}

function create_history_click_to_open(canvas, container) {
	$('<img />', {src: $("#click")[0].src, width: canvas[0].width, height : canvas[0].height}).appendTo(container);
}

// build a chart
function build_chart(data_points) {
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