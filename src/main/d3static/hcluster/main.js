// constants
var margin = {
	top : 40,
	right : 40,
	bottom : 40,
	left : 40
};
var width = 700 - margin.left - margin.right;
var height = 700 - margin.top - margin.bottom;

// D3 init (scales, axis, graph)
var x = d3.scale.linear().range([ 0, width ]);
var y = d3.scale.linear().range([ height, 0 ]);

//var color = d3.scale.category10();

var xAxisScale = d3.svg.axis().scale(x).orient("bottom");
var yAxisScale = d3.svg.axis().scale(y).orient("left");

var svg = d3.select("#_graph").append("svg")
	.attr("width", width + margin.left + margin.right)
	.attr("height", height + margin.top + margin.bottom)
	.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

var xAxis = svg.append("g")
	.attr("class", "x axis")
	.attr("transform", "translate(0," + height + ")")
	.call(xAxisScale);

var yAxis = svg.append("g")
	.attr("class", "y axis")
	.call(yAxisScale);

var source = extractUrlParams()['source'];

// load CSV
data = [];
d3.csv(source, function(error, d) {
	data = d;
	data.sort(function(a, b) {return b.cl_level - a.cl_level;})
	addForm();
	prepareColors();
	refresh();
});


function prepareColors() {
	color = d3.scale.category10().domain(d3.set(function(d) { return d.name; }));
}

// Adding a form with the two select containing the columns
function addForm() {
	var form = d3.select("#form")
		.append("form");
	
	var columns = d3.keys(data[0]);
	// X-Axis
	addSelect(form, columns, "form_x");
	// Y-Axis
	addSelect(form, columns, "form_y");
	// Level
	addSelect(form, d3.set(data.map( function(d) { return +d.cl_level; })).values().sort(function(a, b) {return a - b;}), "form_count");
	// Loop switch
	form.append('label')
    	.text("loop")
    .append("input")
    	.attr("type", "checkbox")
    	.attr("id", "form_loop")
    	.attr("onClick", "loop()");
}

// Adding a select to the given form given the specified name and options
function addSelect(form, options, name) {
	var select = form.append("select")
		.attr("id", name)
		.attr("onchange", "refresh()");
	
	select.selectAll("option")
		.data(options)
		.enter()
		.append("option")
		.text(function(d) { return d; });

	select.selectAll("option")
		.data(options)
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
	var _form = document.getElementById(selectName);
	return _form.options[_form.selectedIndex].value;
}

// Sets the selected option from an HTML select
function setSelected(selectName, value) {
	var _form = document.getElementById(selectName);
	_form.selectedIndex = value;
}

//Extracts the state from an HTML checkbox
function extractCheck(selectName) {
	var x_form = document.getElementById(selectName);
	return x_form.checked;
}

// Uses the x and y select to map data on the requested columns
function mapData() {
	var x_form_value = extractSelected("form_x");
	var y_form_value = extractSelected("form_y");
	data.forEach(function(d) {
		d.x = +d[x_form_value];
		d.y = +d[y_form_value];
		d.timezone = +d.timezone;
		d.cl_nbLeaves = +d.cl_nbLeaves;
		d.cl_radius = +d.cl_radius;
		d.cl_level = +d.cl_level;
		d.cl_isLeaf = +d.cl_isLeaf;
	});
}

function refresh() {
	mapData();
	
	x.domain(d3.extent(data, function(d) { return d.x; }));
	xAxis.call(xAxisScale);
	y.domain(d3.extent(data, function(d) { return d.y; }));
	yAxis.call(yAxisScale);
	
	s = svg.selectAll(".dot").data(data, function(d) { return data.indexOf(d); });

	var count = parseFloat(extractSelected("form_count"),10);
	
	// Setting the size of dots depending on the level
	s.filter(function(d) { return !shouldShow(d, count) })
		.attr("r", 0);
	s.filter(function(d) { return shouldShow(d, count) })
		.attr("r",function(d) { return Math.sqrt(d.cl_nbLeaves / 3.1415) * 2 });
	
	s.enter().append("circle")
		.attr('class', 'dot')
		.attr("cx", function(d) { return x(d.x + ((Math.random() - 0.5) * 4)); })
		.attr("cy", function(d) { return y(d.y + ((Math.random() - 0.5) * 2)); })
		.style("fill", function(d) { return color(Math.round(d.timezone)); });

	s.transition().duration(800)
		.attr("cx", function(d) { return x(d.x); })
		.attr("cy", function(d) { return y(d.y); });

	s.exit().remove();
}

// Will we show this point? Yes if it is a node with level equals to selected or a leaf with level less of equals to selected
function shouldShow (d, count) {
	var ret = false;
	if ((d.cl_level == count && d.cl_isLeaf == 0.0) || (d.cl_level <= count && d.cl_isLeaf == 1.0)) {
		ret = true;
	}  
	return ret;
}

// Let's loop and call moveAndRefresh()
function loop() {
	(function myLoop (i) {          
		   setTimeout(function () {   
			  var l = moveAndRefresh();         
			  if (l) {
				  if (--i) myLoop(i);     
			  }
		   }, 100)
		})(50);     
}

// Change the level and refresh the view.
function moveAndRefresh() {
	var loopSwitch = extractCheck("form_loop");
	
	if (loopSwitch) {
		var count = parseFloat(extractSelected("form_count"),10);
		count--;
		if (count < 0) {
			count = 20; // Gasp, should not be hardcoded
		}
		setSelected("form_count", count);
		refresh();
	}
	
	return loopSwitch;
}
