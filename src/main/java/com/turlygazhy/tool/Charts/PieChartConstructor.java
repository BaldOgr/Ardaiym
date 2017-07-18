package com.turlygazhy.tool.Charts;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Vanderkast on 07.07.2017.
 */
public class PieChartConstructor {
    private PieChart pieChart;

    private String path;
    private String title;

    private ArrayList<String> seriesNames;
    private ArrayList<Integer> values;

    public PieChartConstructor(int width, int height) {
        this.pieChart = new PieChart(width, height);
    }

    public PieChartConstructor(String path, String title, ArrayList<String> seriesNames, ArrayList<Integer> values, int width, int height) {
        this.path = path;
        this.title = title;
        this.seriesNames = seriesNames;
        this.values = values;
        pieChart = new PieChart(width, height);
    }

    public String getPieChartConstructed(){
        pieChart.setTitle(title);
        for(int i = 0; i < seriesNames.size(); i++){
            pieChart.addSeries(seriesNames.get(i), values.get(i));
        }
        path = path + "/" + title;
        try {
            BitmapEncoder.saveBitmap(pieChart, path, BitmapEncoder.BitmapFormat.JPG);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public ArrayList<String> getSeriesNames() {
        return seriesNames;
    }

    public void setSeriesNames(ArrayList<String> seriesNames) {
        this.seriesNames = seriesNames;
    }

    public ArrayList<Integer> getValues() {
        return values;
    }

    public void setValues(ArrayList<Integer> values) {
        this.values = values;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
