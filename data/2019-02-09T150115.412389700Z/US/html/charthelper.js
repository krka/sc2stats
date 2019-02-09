allcharts = {};

function loadChart(name, title, xtitle, ytitle, series) {
  var layout = {
    title: title,
    xaxis: {
      title: xtitle,
      titlefont: {
        family: 'Courier New, monospace',
        size: 18,
        color: '#7f7f7f'
      }
    },
    yaxis: {
      title: ytitle,
      titlefont: {
        family: 'Courier New, monospace',
        size: 18,
        color: '#7f7f7f'
      }
    },
    paper_bgcolor: "black",
    plot_bgcolor: "black"
  };
  var element = document.createElement("div");
  allcharts[name] = {element: element, series: series, layout: layout};
}

function attachChart(element, name) {
  var chart = allcharts[name];
  if (typeof element == 'string') {
    document.getElementById(element).appendChild(chart.element);
  } else {
    element.appendChild(chart.element);
  }
  Plotly.newPlot(chart.element, chart.series, chart.layout);
}
