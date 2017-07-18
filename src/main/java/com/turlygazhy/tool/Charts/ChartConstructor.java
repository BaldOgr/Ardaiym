package com.turlygazhy.tool.Charts;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Vanderkast on 07.07.2017.
 * <p>
 * this class uses to construct charts
 */
public class ChartConstructor {
    private ArrayList<Integer> xData = null;
    private ArrayList<Integer> yData = null;

    private String chartTitle = null;
    private String XTitle = null;
    private String YTitle = null;

    private XYChart chart = null;

    private String path = null;

    public ChartConstructor(int width, int height) {
        chart = new XYChart(width, height);
    }

    public ChartConstructor(String path, String XTitle, ArrayList<Integer> xData, String YTitle, ArrayList<Integer> yData, String chartTitle, int width, int height) {
        this.path = path;
        this.xData = xData;
        this.yData = yData;
        this.XTitle = XTitle;
        this.YTitle = YTitle;
        this.chartTitle = chartTitle;
        this.chart = new XYChart(width, height);
    }

    public String getChartConstructed() throws IOException{
        if (chart != null) {
            chart.setTitle(chartTitle);
            chart.setXAxisTitle(XTitle);
            chart.setYAxisTitle(YTitle);
            XYSeries series = chart.addSeries("y(x)", xData, yData);
            series.setMarker(SeriesMarkers.CIRCLE);

            path = path + "/" + chartTitle;
            BitmapEncoder.saveBitmap(chart, path, BitmapEncoder.BitmapFormat.JPG);
        }
        return path;
    }

    public String getXTitle() {
        return XTitle;
    }

    public void setXTitle(String XTitle) {
        this.XTitle = XTitle;
    }

    public String getYTitle() {
        return YTitle;
    }

    public void setYTitle(String YTitle) {
        this.YTitle = YTitle;
    }

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

    public ArrayList<Integer> getXData() {
        return xData;
    }

    public void setXData(ArrayList<Integer> xData) {
        this.xData = xData;
    }

    public ArrayList<Integer> getYData() {
        return yData;
    }

    public void setYData(ArrayList<Integer> yData) {
        this.yData = yData;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
