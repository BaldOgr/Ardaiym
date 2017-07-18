package Charts

This package has consructors of charts.

1) ChartConstructor - constructor of simple graphs
	Methods:
		+ public ChartConstructor(String path, String XTitle, ArrayList<Integer> xData, String YTitle, ArrayList<Integer> yData, String chartTitle, int width, int height)
			sets values
			
		+ public String getChartConstructed() throws IOException
			returns full path to file of chart;
				for creating file use fileInputStream

2) PieChartConstructor - constructor for pie charts
	metgods
		+ public PieChartConstructor(String path, String title, ArrayList<String> seriesNames, ArrayList<Integer> values, int width, int height)
			sets values
		
		+ public String getPieChartConstructed()
			returns full path to file of pie chart;
				for creating file use fileInputStream