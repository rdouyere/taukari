// constants
var margin = {
	top : 40,
	right : 40,
	bottom : 40,
	left : 40
};
var width = 500 - margin.left - margin.right;
var height = 500 - margin.top - margin.bottom;

// D3 init (scales, axis, graph)
var x = d3.scale.linear().range([ 0, width ]);
var y = d3.scale.linear().range([ height, 0 ]);

//var color = d3.scale.category10();

var xAxis = d3.svg.axis().scale(x).orient("bottom");
var yAxis = d3.svg.axis().scale(y).orient("left");

var svg = d3.select("#_graph").append("svg")
	.attr("width", width + margin.left + margin.right)
	.attr("height", height + margin.top + margin.bottom)
	.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

svg.append("g")
	.attr("class", "x axis")
	.attr("transform", "translate(0," + height + ")")
	.call(xAxis)

svg.append("g")
	.attr("class", "y axis")
	.call(yAxis);


var source = extractUrlParams()['source'];

// load CSV
data = [];
d3.csv(source, function(error, d) {
	data = d;
	addForm();
	prepareColors();
	refresh();
});

function prepareColors() {
	var names = d3.set(function(d) { return d.name; });
	console.log(names);
	if (names.values().length <= 10) {
		color = d3.scale.category10().domain(d3.set(function(d) { return d.name; }));
	} else {
		color = d3.scale.category20().domain(d3.set(function(d) { return d.name; }));
	}
}

// Adding a form with the two select containing the columns
function addForm() {
	var form = d3.select("#form")
		.append("form")
		.attr("class", "form-inline");

	addSelect(form, data[0], "form_x");
	addSelect(form, data[0], "form_y");
}

// Adding a select to the given form given the specified name and options
function addSelect(form, options, name) {
	var select = form.append("select")
		.attr("id", name)
		.attr("onchange", "refresh()");
	
	select.selectAll("option")
		.data(d3.keys(options))
		.enter()
		.append("option")
		.text(function(d) { return d; });

	select.selectAll("option")
		.data(d3.keys(options))
		.attr("value", function(d) { return d; });
}

// Extracts the URL parameters
function extractUrlParams() {
	var t = location.search.substring(1).split('&');
	var f = [];
	for ( var i = 0; i < t.length; i++) {
		var x = t[i].split('=');
		f[x[0]] = x[1];
	}
	return f;
}

// Extracts the selected option from an HTML select
function extractSelected(selectName) {
	var x_form = document.getElementById(selectName);
	return x_form.options[x_form.selectedIndex].value;
}

// Uses the x and y select to map data on the requested columns
function mapData() {
	var x_form_value = extractSelected("form_x");
	var y_form_value = extractSelected("form_y");
	data.forEach(function(d) {
		d.x = +d[x_form_value];
		d.y = +d[y_form_value];
	});
}

function refresh() {
	mapData();
	
	x.domain(d3.extent(data, function(d) { return d.x; }));
	y.domain(d3.extent(data, function(d) { return d.y; }));
	
	s = svg.selectAll(".dot").data(data, function(d) { return data.indexOf(d); });

	s.enter().append("circle")
		.attr("r", 3.5)
		.attr('class', 'dot')
		.attr("cx", function(d) { return x(d.x + ((Math.random() - 0.5) * 4)); })
		.attr("cy", function(d) { return y(d.y + ((Math.random() - 0.5) * 2)); })
		.style("fill", function(d) { return color(d.name); });

	s.transition().duration(800)
		.attr("r", 3.5)
		.attr("cx", function(d) { return x(d.x); })
		.attr("cy", function(d) { return y(d.y); });

	s.exit().remove();
}