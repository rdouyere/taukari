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

var color = d3.scale.category10();

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

// load CSV
data = [];
d3.csv("/tmp/mining/meteo/norm.csv", function(error, d) {
	data = d;

	mapData();

	x.domain(d3.extent(data, function(d) { return d.x; })).nice();
	y.domain(d3.extent(data, function(d) { return d.y; })).nice();

	refresh();
});

function mapData() {
	var x_form = document.getElementById("form_x");
	var x_form_value = x_form.options[x_form.selectedIndex].value;
	var y_form = document.getElementById("form_y");
	var y_form_value = y_form.options[y_form.selectedIndex].value;
	
	data.forEach(function(d) {
		d.x = +d[x_form_value];
		d.y = +d[y_form_value];
	});
}

function refresh() {

	mapData();
	
	console.log(data);
	
	x.domain(d3.extent(data, function(d) { return d.x; })).nice();
	y.domain(d3.extent(data, function(d) { return d.y; })).nice();

	s = svg.selectAll(".dot").data(data, function(d) { return data.indexOf(d); });

	s.enter().append("circle")
		.attr("r", 1.5)
		.attr('class', 'dot')
		.attr("cx", function(d) { return x(d.x); })
		.attr("cy", function(d) { return y(d.y); })
		.style("fill", color(0));

	s.transition().duration(500)
		.attr("r", 3.5)
		.attr("cx", function(d) { return x(d.x); })
		.attr("cy", function(d) { return y(d.y); })
		.style("fill", color(1));

	s.exit().remove();

}