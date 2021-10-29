package com.echdr.android.echdrapp.ui.event_form;

import android.graphics.Color;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.HashMap;
import java.util.Map;




public class dataValuesWHO {

    private static dataValuesWHO instance;
    Map<Integer, double[]> heightForAgeGirls;
    Map<Integer, double[]> heightForAgeBoys;
    Map<Integer, double[]> weightForAgeGirls;
    Map<Integer, double[]> weightForAgeBoys;

    private dataValuesWHO()
    {
        heightForAgeGirls = new HashMap<Integer, double[]>();
        heightForAgeBoys = new HashMap<Integer, double[]>();
        weightForAgeGirls = new HashMap<Integer, double[]>();
        weightForAgeBoys = new HashMap<Integer, double[]>();
    }

    public static dataValuesWHO getInstance()
    {
        if(instance == null)
        {
            instance  = new dataValuesWHO();
        }
        return instance;
    }

    public void initializeheightForAgeGirls() {
        heightForAgeGirls.put(0, new double[] {43.6,45.4,47.3,49.1,51,52.9,54.7});
        heightForAgeGirls.put(1, new double[] {47.8,49.8,51.7,53.7,55.6,57.6,59.5});
        heightForAgeGirls.put(2, new double[] {51,53,55,57.1,59.1,61.1,63.2});
        heightForAgeGirls.put(3, new double[] {53.5,55.6,57.7,59.8,61.9,64,66.1});
        heightForAgeGirls.put(4, new double[] {55.6,57.8,59.9,62.1,64.3,66.4,68.6});
        heightForAgeGirls.put(5, new double[] {57.4,59.6,61.8,64,66.2,68.5,70.7});
        heightForAgeGirls.put(6, new double[] {58.9,61.2,63.5,65.7,68,70.3,72.5});
        heightForAgeGirls.put(7, new double[] {60.3,62.7,65,67.3,69.6,71.9,74.2});
        heightForAgeGirls.put(8, new double[] {61.7,64,66.4,68.7,71.1,73.5,75.8});
        heightForAgeGirls.put(9, new double[] {62.9,65.3,67.7,70.1,72.6,75,77.4});
        heightForAgeGirls.put(10, new double[] {64.1,66.5,69,71.5,73.9,76.4,78.9});
        heightForAgeGirls.put(11, new double[] {65.2,67.7,70.3,72.8,75.3,77.8,80.3});
        heightForAgeGirls.put(12, new double[] {66.3,68.9,71.4,74,76.6,79.2,81.7});
        heightForAgeGirls.put(13, new double[] {67.3,70,72.6,75.2,77.8,80.5,83.1});
        heightForAgeGirls.put(14, new double[] {68.3,71,73.7,76.4,79.1,81.7,84.4});
        heightForAgeGirls.put(15, new double[] {69.3,72,74.8,77.5,80.2,83,85.7});
        heightForAgeGirls.put(16, new double[] {70.2,73,75.8,78.6,81.4,84.2,87});
        heightForAgeGirls.put(17, new double[] {71.1,74,76.8,79.7,82.5,85.4,88.2});
        heightForAgeGirls.put(18, new double[] {72,74.9,77.8,80.7,83.6,86.5,89.4});
        heightForAgeGirls.put(19, new double[] {72.8,75.8,78.8,81.7,84.7,87.6,90.6});
        heightForAgeGirls.put(20, new double[] {73.7,76.7,79.7,82.7,85.7,88.7,91.7});
        heightForAgeGirls.put(21, new double[] {74.5,77.5,80.6,83.7,86.7,89.8,92.9});
        heightForAgeGirls.put(22, new double[] {75.2,78.4,81.5,84.6,87.7,90.8,94});
        heightForAgeGirls.put(23, new double[] {76,79.2,82.3,85.5,88.7,91.9,95});
        heightForAgeGirls.put(24, new double[] {76.7,80,83.2,86.4,89.6,92.9,96.1});
        heightForAgeGirls.put(25, new double[] {76.8,80,83.3,86.6,89.9,93.1,96.4});
        heightForAgeGirls.put(26, new double[] {77.5,80.8,84.1,87.4,90.8,94.1,97.4});
        heightForAgeGirls.put(27, new double[] {78.1,81.5,84.9,88.3,91.7,95,98.4});
        heightForAgeGirls.put(28, new double[] {78.8,82.2,85.7,89.1,92.5,96,99.4});
        heightForAgeGirls.put(29, new double[] {79.5,82.9,86.4,89.9,93.4,96.9,100.3});
        heightForAgeGirls.put(30, new double[] {80.1,83.6,87.1,90.7,94.2,97.7,101.3});
        heightForAgeGirls.put(31, new double[] {80.7,84.3,87.9,91.4,95,98.6,102.2});
        heightForAgeGirls.put(32, new double[] {81.3,84.9,88.6,92.2,95.8,99.4,103.1});
        heightForAgeGirls.put(33, new double[] {81.9,85.6,89.3,92.9,96.6,100.3,103.9});
        heightForAgeGirls.put(34, new double[] {82.5,86.2,89.9,93.6,97.4,101.1,104.8});
        heightForAgeGirls.put(35, new double[] {83.1,86.8,90.6,94.4,98.1,101.9,105.6});
        heightForAgeGirls.put(36, new double[] {83.6,87.4,91.2,95.1,98.9,102.7,106.5});
        heightForAgeGirls.put(37, new double[] {84.2,88,91.9,95.7,99.6,103.4,107.3});
        heightForAgeGirls.put(38, new double[] {84.7,88.6,92.5,96.4,100.3,104.2,108.1});
        heightForAgeGirls.put(39, new double[] {85.3,89.2,93.1,97.1,101,105,108.9});
        heightForAgeGirls.put(40, new double[] {85.8,89.8,93.8,97.7,101.7,105.7,109.7});
        heightForAgeGirls.put(41, new double[] {86.3,90.4,94.4,98.4,102.4,106.4,110.5});
        heightForAgeGirls.put(42, new double[] {86.8,90.9,95,99,103.1,107.2,111.2});
        heightForAgeGirls.put(43, new double[] {87.4,91.5,95.6,99.7,103.8,107.9,112});
        heightForAgeGirls.put(44, new double[] {87.9,92,96.2,100.3,104.5,108.6,112.7});
        heightForAgeGirls.put(45, new double[] {88.4,92.5,96.7,100.9,105.1,109.3,113.5});
        heightForAgeGirls.put(46, new double[] {88.9,93.1,97.3,101.5,105.8,110,114.2});
        heightForAgeGirls.put(47, new double[] {89.3,93.6,97.9,102.1,106.4,110.7,114.9});
        heightForAgeGirls.put(48, new double[] {89.8,94.1,98.4,102.7,107,111.3,115.7});
        heightForAgeGirls.put(49, new double[] {90.3,94.6,99,103.3,107.7,112,116.4});
        heightForAgeGirls.put(50, new double[] {90.7,95.1,99.5,103.9,108.3,112.7,117.1});
        heightForAgeGirls.put(51, new double[] {91.2,95.6,100.1,104.5,108.9,113.3,117.7});
        heightForAgeGirls.put(52, new double[] {91.7,96.1,100.6,105,109.5,114,118.4});
        heightForAgeGirls.put(53, new double[] {92.1,96.6,101.1,105.6,110.1,114.6,119.1});
        heightForAgeGirls.put(54, new double[] {92.6,97.1,101.6,106.2,110.7,115.2,119.8});
        heightForAgeGirls.put(55, new double[] {93,97.6,102.2,106.7,111.3,115.9,120.4});
        heightForAgeGirls.put(56, new double[] {93.4,98.1,102.7,107.3,111.9,116.5,121.1});
        heightForAgeGirls.put(57, new double[] {93.9,98.5,103.2,107.8,112.5,117.1,121.8});
        heightForAgeGirls.put(58, new double[] {94.3,99,103.7,108.4,113,117.7,122.4});
        heightForAgeGirls.put(59, new double[] {94.7,99.5,104.2,108.9,113.6,118.3,123.1});
        heightForAgeGirls.put(60, new double[] {95.2,99.9,104.7,109.4,114.2,118.9,123.7});
    }

    public LineGraphSeries<DataPoint> heightForAgeGirlsValues(int pos, int maxMonth) {
        LineGraphSeries<DataPoint> heightforage_series = new LineGraphSeries<DataPoint>();
        for(int i=0; i< maxMonth; i++)
        {
            heightforage_series.appendData(
                    new DataPoint(i, heightForAgeGirls.get(i)[pos] ), true, 60);
        }

        heightforage_series.setColor(getColor(pos));
        heightforage_series.setThickness(1);
        heightforage_series.setBackgroundColor(getColor(pos));
        heightforage_series.setDrawBackground(true);
        return heightforage_series;
    }

    public void initializeheightForAgeBoys() {
        heightForAgeBoys.put(0, new double[] {44.2,46.1,48,49.9,51.8,53.7,55.6});
        heightForAgeBoys.put(1, new double[] {48.9,50.8,52.8,54.7,56.7,58.6,60.6});
        heightForAgeBoys.put(2, new double[] {52.4,54.4,56.4,58.4,60.4,62.4,64.4});
        heightForAgeBoys.put(3, new double[] {55.3,57.3,59.4,61.4,63.5,65.5,67.6});
        heightForAgeBoys.put(4, new double[] {57.6,59.7,61.8,63.9,66,68,70.1});
        heightForAgeBoys.put(5, new double[] {59.6,61.7,63.8,65.9,68,70.1,72.2});
        heightForAgeBoys.put(6, new double[] {61.2,63.3,65.5,67.6,69.8,71.9,74});
        heightForAgeBoys.put(7, new double[] {62.7,64.8,67,69.2,71.3,73.5,75.7});
        heightForAgeBoys.put(8, new double[] {64,66.2,68.4,70.6,72.8,75,77.2});
        heightForAgeBoys.put(9, new double[] {65.2,67.5,69.7,72,74.2,76.5,78.7});
        heightForAgeBoys.put(10, new double[] {66.4,68.7,71,73.3,75.6,77.9,80.1});
        heightForAgeBoys.put(11, new double[] {67.6,69.9,72.2,74.5,76.9,79.2,81.5});
        heightForAgeBoys.put(12, new double[] {68.6,71,73.4,75.7,78.1,80.5,82.9});
        heightForAgeBoys.put(13, new double[] {69.6,72.1,74.5,76.9,79.3,81.8,84.2});
        heightForAgeBoys.put(14, new double[] {70.6,73.1,75.6,78,80.5,83,85.5});
        heightForAgeBoys.put(15, new double[] {71.6,74.1,76.6,79.1,81.7,84.2,86.7});
        heightForAgeBoys.put(16, new double[] {72.5,75,77.6,80.2,82.8,85.4,88});
        heightForAgeBoys.put(17, new double[] {73.3,76,78.6,81.2,83.9,86.5,89.2});
        heightForAgeBoys.put(18, new double[] {74.2,76.9,79.6,82.3,85,87.7,90.4});
        heightForAgeBoys.put(19, new double[] {75,77.7,80.5,83.2,86,88.8,91.5});
        heightForAgeBoys.put(20, new double[] {75.8,78.6,81.4,84.2,87,89.8,92.6});
        heightForAgeBoys.put(21, new double[] {76.5,79.4,82.3,85.1,88,90.9,93.8});
        heightForAgeBoys.put(22, new double[] {77.2,80.2,83.1,86,89,91.9,94.9});
        heightForAgeBoys.put(23, new double[] {78,81,83.9,86.9,89.9,92.9,95.9});
        heightForAgeBoys.put(24, new double[] {78.7,81.7,84.8,87.8,90.9,93.9,97});
        //heightForAgeBoys.put(24, new double[] {78,81,84.1,87.1,90.2,93.2,96.3});
        heightForAgeBoys.put(25, new double[] {78.6,81.7,84.9,88,91.1,94.2,97.3});
        heightForAgeBoys.put(26, new double[] {79.3,82.5,85.6,88.8,92,95.2,98.3});
        heightForAgeBoys.put(27, new double[] {79.9,83.1,86.4,89.6,92.9,96.1,99.3});
        heightForAgeBoys.put(28, new double[] {80.5,83.8,87.1,90.4,93.7,97,100.3});
        heightForAgeBoys.put(29, new double[] {81.1,84.5,87.8,91.2,94.5,97.9,101.2});
        heightForAgeBoys.put(30, new double[] {81.7,85.1,88.5,91.9,95.3,98.7,102.1});
        heightForAgeBoys.put(31, new double[] {82.3,85.7,89.2,92.7,96.1,99.6,103});
        heightForAgeBoys.put(32, new double[] {82.8,86.4,89.9,93.4,96.9,100.4,103.9});
        heightForAgeBoys.put(33, new double[] {83.4,86.9,90.5,94.1,97.6,101.2,104.8});
        heightForAgeBoys.put(34, new double[] {83.9,87.5,91.1,94.8,98.4,102,105.6});
        heightForAgeBoys.put(35, new double[] {84.4,88.1,91.8,95.4,99.1,102.7,106.4});
        heightForAgeBoys.put(36, new double[] {85,88.7,92.4,96.1,99.8,103.5,107.2});
        heightForAgeBoys.put(37, new double[] {85.5,89.2,93,96.7,100.5,104.2,108});
        heightForAgeBoys.put(38, new double[] {86,89.8,93.6,97.4,101.2,105,108.8});
        heightForAgeBoys.put(39, new double[] {86.5,90.3,94.2,98,101.8,105.7,109.5});
        heightForAgeBoys.put(40, new double[] {87,90.9,94.7,98.6,102.5,106.4,110.3});
        heightForAgeBoys.put(41, new double[] {87.5,91.4,95.3,99.2,103.2,107.1,111});
        heightForAgeBoys.put(42, new double[] {88,91.9,95.9,99.9,103.8,107.8,111.7});
        heightForAgeBoys.put(43, new double[] {88.4,92.4,96.4,100.4,104.5,108.5,112.5});
        heightForAgeBoys.put(44, new double[] {88.9,93,97,101,105.1,109.1,113.2});
        heightForAgeBoys.put(45, new double[] {89.4,93.5,97.5,101.6,105.7,109.8,113.9});
        heightForAgeBoys.put(46, new double[] {89.8,94,98.1,102.2,106.3,110.4,114.6});
        heightForAgeBoys.put(47, new double[] {90.3,94.4,98.6,102.8,106.9,111.1,115.2});
        heightForAgeBoys.put(48, new double[] {90.7,94.9,99.1,103.3,107.5,111.7,115.9});
        heightForAgeBoys.put(49, new double[] {91.2,95.4,99.7,103.9,108.1,112.4,116.6});
        heightForAgeBoys.put(50, new double[] {91.6,95.9,100.2,104.4,108.7,113,117.3});
        heightForAgeBoys.put(51, new double[] {92.1,96.4,100.7,105,109.3,113.6,117.9});
        heightForAgeBoys.put(52, new double[] {92.5,96.9,101.2,105.6,109.9,114.2,118.6});
        heightForAgeBoys.put(53, new double[] {93,97.4,101.7,106.1,110.5,114.9,119.2});
        heightForAgeBoys.put(54, new double[] {93.4,97.8,102.3,106.7,111.1,115.5,119.9});
        heightForAgeBoys.put(55, new double[] {93.9,98.3,102.8,107.2,111.7,116.1,120.6});
        heightForAgeBoys.put(56, new double[] {94.3,98.8,103.3,107.8,112.3,116.7,121.2});
        heightForAgeBoys.put(57, new double[] {94.7,99.3,103.8,108.3,112.8,117.4,121.9});
        heightForAgeBoys.put(58, new double[] {95.2,99.7,104.3,108.9,113.4,118,122.6});
        heightForAgeBoys.put(59, new double[] {95.6,100.2,104.8,109.4,114,118.6,123.2});
        heightForAgeBoys.put(60, new double[] {96.1,100.7,105.3,110,114.6,119.2,123.9});
    }

    public LineGraphSeries<DataPoint> heightForAgeBoysValues(int pos, int maxMonth) {
        LineGraphSeries<DataPoint> heightforage_series = new LineGraphSeries<DataPoint>();
        for(int i=0; i< maxMonth; i++)
        {
            heightforage_series.appendData(
                    new DataPoint(i, heightForAgeBoys.get(i)[pos] ), true, 60);
        }

        heightforage_series.setColor(getColor(pos));
        heightforage_series.setThickness(1);
        heightforage_series.setBackgroundColor(getColor(pos));
        heightforage_series.setDrawBackground(true);
        return heightforage_series;
    }

    public void initializeweightForAgeGirls() {
        weightForAgeGirls.put(0, new double[] {2,2.4,2.8,3.2,3.7,4.2,4.8});
        weightForAgeGirls.put(1, new double[] {2.7,3.2,3.6,4.2,4.8,5.5,6.2});
        weightForAgeGirls.put(2, new double[] {3.4,3.9,4.5,5.1,5.8,6.6,7.5});
        weightForAgeGirls.put(3, new double[] {4,4.5,5.2,5.8,6.6,7.5,8.5});
        weightForAgeGirls.put(4, new double[] {4.4,5,5.7,6.4,7.3,8.2,9.3});
        weightForAgeGirls.put(5, new double[] {4.8,5.4,6.1,6.9,7.8,8.8,10});
        weightForAgeGirls.put(6, new double[] {5.1,5.7,6.5,7.3,8.2,9.3,10.6});
        weightForAgeGirls.put(7, new double[] {5.3,6,6.8,7.6,8.6,9.8,11.1});
        weightForAgeGirls.put(8, new double[] {5.6,6.3,7,7.9,9,10.2,11.6});
        weightForAgeGirls.put(9, new double[] {5.8,6.5,7.3,8.2,9.3,10.5,12});
        weightForAgeGirls.put(10, new double[] {5.9,6.7,7.5,8.5,9.6,10.9,12.4});
        weightForAgeGirls.put(11, new double[] {6.1,6.9,7.7,8.7,9.9,11.2,12.8});
        weightForAgeGirls.put(12, new double[] {6.3,7,7.9,8.9,10.1,11.5,13.1});
        weightForAgeGirls.put(13, new double[] {6.4,7.2,8.1,9.2,10.4,11.8,13.5});
        weightForAgeGirls.put(14, new double[] {6.6,7.4,8.3,9.4,10.6,12.1,13.8});
        weightForAgeGirls.put(15, new double[] {6.7,7.6,8.5,9.6,10.9,12.4,14.1});
        weightForAgeGirls.put(16, new double[] {6.9,7.7,8.7,9.8,11.1,12.6,14.5});
        weightForAgeGirls.put(17, new double[] {7,7.9,8.9,10,11.4,12.9,14.8});
        weightForAgeGirls.put(18, new double[] {7.2,8.1,9.1,10.2,11.6,13.2,15.1});
        weightForAgeGirls.put(19, new double[] {7.3,8.2,9.2,10.4,11.8,13.5,15.4});
        weightForAgeGirls.put(20, new double[] {7.5,8.4,9.4,10.6,12.1,13.7,15.7});
        weightForAgeGirls.put(21, new double[] {7.6,8.6,9.6,10.9,12.3,14,16});
        weightForAgeGirls.put(22, new double[] {7.8,8.7,9.8,11.1,12.5,14.3,16.4});
        weightForAgeGirls.put(23, new double[] {7.9,8.9,10,11.3,12.8,14.6,16.7});
        weightForAgeGirls.put(24, new double[] {8.1,9,10.2,11.5,13,14.8,17});
        weightForAgeGirls.put(25, new double[] {8.2,9.2,10.3,11.7,13.3,15.1,17.3});
        weightForAgeGirls.put(26, new double[] {8.4,9.4,10.5,11.9,13.5,15.4,17.7});
        weightForAgeGirls.put(27, new double[] {8.5,9.5,10.7,12.1,13.7,15.7,18});
        weightForAgeGirls.put(28, new double[] {8.6,9.7,10.9,12.3,14,16,18.3});
        weightForAgeGirls.put(29, new double[] {8.8,9.8,11.1,12.5,14.2,16.2,18.7});
        weightForAgeGirls.put(30, new double[] {8.9,10,11.2,12.7,14.4,16.5,19});
        weightForAgeGirls.put(31, new double[] {9,10.1,11.4,12.9,14.7,16.8,19.3});
        weightForAgeGirls.put(32, new double[] {9.1,10.3,11.6,13.1,14.9,17.1,19.6});
        weightForAgeGirls.put(33, new double[] {9.3,10.4,11.7,13.3,15.1,17.3,20});
        weightForAgeGirls.put(34, new double[] {9.4,10.5,11.9,13.5,15.4,17.6,20.3});
        weightForAgeGirls.put(35, new double[] {9.5,10.7,12,13.7,15.6,17.9,20.6});
        weightForAgeGirls.put(36, new double[] {9.6,10.8,12.2,13.9,15.8,18.1,20.9});
        weightForAgeGirls.put(37, new double[] {9.7,10.9,12.4,14,16,18.4,21.3});
        weightForAgeGirls.put(38, new double[] {9.8,11.1,12.5,14.2,16.3,18.7,21.6});
        weightForAgeGirls.put(39, new double[] {9.9,11.2,12.7,14.4,16.5,19,22});
        weightForAgeGirls.put(40, new double[] {10.1,11.3,12.8,14.6,16.7,19.2,22.3});
        weightForAgeGirls.put(41, new double[] {10.2,11.5,13,14.8,16.9,19.5,22.7});
        weightForAgeGirls.put(42, new double[] {10.3,11.6,13.1,15,17.2,19.8,23});
        weightForAgeGirls.put(43, new double[] {10.4,11.7,13.3,15.2,17.4,20.1,23.4});
        weightForAgeGirls.put(44, new double[] {10.5,11.8,13.4,15.3,17.6,20.4,23.7});
        weightForAgeGirls.put(45, new double[] {10.6,12,13.6,15.5,17.8,20.7,24.1});
        weightForAgeGirls.put(46, new double[] {10.7,12.1,13.7,15.7,18.1,20.9,24.5});
        weightForAgeGirls.put(47, new double[] {10.8,12.2,13.9,15.9,18.3,21.2,24.8});
        weightForAgeGirls.put(48, new double[] {10.9,12.3,14,16.1,18.5,21.5,25.2});
        weightForAgeGirls.put(49, new double[] {11,12.4,14.2,16.3,18.8,21.8,25.5});
        weightForAgeGirls.put(50, new double[] {11.1,12.6,14.3,16.4,19,22.1,25.9});
        weightForAgeGirls.put(51, new double[] {11.2,12.7,14.5,16.6,19.2,22.4,26.3});
        weightForAgeGirls.put(52, new double[] {11.3,12.8,14.6,16.8,19.4,22.6,26.6});
        weightForAgeGirls.put(53, new double[] {11.4,12.9,14.8,17,19.7,22.9,27});
        weightForAgeGirls.put(54, new double[] {11.5,13,14.9,17.2,19.9,23.2,27.4});
        weightForAgeGirls.put(55, new double[] {11.6,13.2,15.1,17.3,20.1,23.5,27.7});
        weightForAgeGirls.put(56, new double[] {11.7,13.3,15.2,17.5,20.3,23.8,28.1});
        weightForAgeGirls.put(57, new double[] {11.8,13.4,15.3,17.7,20.6,24.1,28.5});
        weightForAgeGirls.put(58, new double[] {11.9,13.5,15.5,17.9,20.8,24.4,28.8});
        weightForAgeGirls.put(59, new double[] {12,13.6,15.6,18,21,24.6,29.2});
        weightForAgeGirls.put(60, new double[] {12.1,13.7,15.8,18.2,21.2,24.9,29.5});
    }

    public LineGraphSeries<DataPoint> weightForAgeGirlsValues(int pos, int maxMonth) {
        LineGraphSeries<DataPoint> weightforage_series = new LineGraphSeries<DataPoint>();
        for(int i=0; i< maxMonth; i++)
        {
            weightforage_series.appendData(
                    new DataPoint(i, weightForAgeGirls.get(i)[pos] ), true, 60);
        }

        weightforage_series.setColor(getColor(pos));
        weightforage_series.setThickness(1);
        weightforage_series.setBackgroundColor(getColor(pos));
        weightforage_series.setDrawBackground(true);
        return weightforage_series;
    }

    public void initializeweightForAgeBoys() {
        weightForAgeBoys.put(0, new double[] {2.1,2.5,2.9,3.3,3.9,4.4,5});
        weightForAgeBoys.put(1, new double[] {2.9,3.4,3.9,4.5,5.1,5.8,6.6});
        weightForAgeBoys.put(2, new double[] {3.8,4.3,4.9,5.6,6.3,7.1,8});
        weightForAgeBoys.put(3, new double[] {4.4,5,5.7,6.4,7.2,8,9});
        weightForAgeBoys.put(4, new double[] {4.9,5.6,6.2,7,7.8,8.7,9.7});
        weightForAgeBoys.put(5, new double[] {5.3,6,6.7,7.5,8.4,9.3,10.4});
        weightForAgeBoys.put(6, new double[] {5.7,6.4,7.1,7.9,8.8,9.8,10.9});
        weightForAgeBoys.put(7, new double[] {5.9,6.7,7.4,8.3,9.2,10.3,11.4});
        weightForAgeBoys.put(8, new double[] {6.2,6.9,7.7,8.6,9.6,10.7,11.9});
        weightForAgeBoys.put(9, new double[] {6.4,7.1,8,8.9,9.9,11,12.3});
        weightForAgeBoys.put(10, new double[] {6.6,7.4,8.2,9.2,10.2,11.4,12.7});
        weightForAgeBoys.put(11, new double[] {6.8,7.6,8.4,9.4,10.5,11.7,13});
        weightForAgeBoys.put(12, new double[] {6.9,7.7,8.6,9.6,10.8,12,13.3});
        weightForAgeBoys.put(13, new double[] {7.1,7.9,8.8,9.9,11,12.3,13.7});
        weightForAgeBoys.put(14, new double[] {7.2,8.1,9,10.1,11.3,12.6,14});
        weightForAgeBoys.put(15, new double[] {7.4,8.3,9.2,10.3,11.5,12.8,14.3});
        weightForAgeBoys.put(16, new double[] {7.5,8.4,9.4,10.5,11.7,13.1,14.6});
        weightForAgeBoys.put(17, new double[] {7.7,8.6,9.6,10.7,12,13.4,14.9});
        weightForAgeBoys.put(18, new double[] {7.8,8.8,9.8,10.9,12.2,13.7,15.3});
        weightForAgeBoys.put(19, new double[] {8,8.9,10,11.1,12.5,13.9,15.6});
        weightForAgeBoys.put(20, new double[] {8.1,9.1,10.1,11.3,12.7,14.2,15.9});
        weightForAgeBoys.put(21, new double[] {8.2,9.2,10.3,11.5,12.9,14.5,16.2});
        weightForAgeBoys.put(22, new double[] {8.4,9.4,10.5,11.8,13.2,14.7,16.5});
        weightForAgeBoys.put(23, new double[] {8.5,9.5,10.7,12,13.4,15,16.8});
        weightForAgeBoys.put(24, new double[] {8.6,9.7,10.8,12.2,13.6,15.3,17.1});
        weightForAgeBoys.put(25, new double[] {8.8,9.8,11,12.4,13.9,15.5,17.5});
        weightForAgeBoys.put(26, new double[] {8.9,10,11.2,12.5,14.1,15.8,17.8});
        weightForAgeBoys.put(27, new double[] {9,10.1,11.3,12.7,14.3,16.1,18.1});
        weightForAgeBoys.put(28, new double[] {9.1,10.2,11.5,12.9,14.5,16.3,18.4});
        weightForAgeBoys.put(29, new double[] {9.2,10.4,11.7,13.1,14.8,16.6,18.7});
        weightForAgeBoys.put(30, new double[] {9.4,10.5,11.8,13.3,15,16.9,19});
        weightForAgeBoys.put(31, new double[] {9.5,10.7,12,13.5,15.2,17.1,19.3});
        weightForAgeBoys.put(32, new double[] {9.6,10.8,12.1,13.7,15.4,17.4,19.6});
        weightForAgeBoys.put(33, new double[] {9.7,10.9,12.3,13.8,15.6,17.6,19.9});
        weightForAgeBoys.put(34, new double[] {9.8,11,12.4,14,15.8,17.8,20.2});
        weightForAgeBoys.put(35, new double[] {9.9,11.2,12.6,14.2,16,18.1,20.4});
        weightForAgeBoys.put(36, new double[] {10,11.3,12.7,14.3,16.2,18.3,20.7});
        weightForAgeBoys.put(37, new double[] {10.1,11.4,12.9,14.5,16.4,18.6,21});
        weightForAgeBoys.put(38, new double[] {10.2,11.5,13,14.7,16.6,18.8,21.3});
        weightForAgeBoys.put(39, new double[] {10.3,11.6,13.1,14.8,16.8,19,21.6});
        weightForAgeBoys.put(40, new double[] {10.4,11.8,13.3,15,17,19.3,21.9});
        weightForAgeBoys.put(41, new double[] {10.5,11.9,13.4,15.2,17.2,19.5,22.1});
        weightForAgeBoys.put(42, new double[] {10.6,12,13.6,15.3,17.4,19.7,22.4});
        weightForAgeBoys.put(43, new double[] {10.7,12.1,13.7,15.5,17.6,20,22.7});
        weightForAgeBoys.put(44, new double[] {10.8,12.2,13.8,15.7,17.8,20.2,23});
        weightForAgeBoys.put(45, new double[] {10.9,12.4,14,15.8,18,20.5,23.3});
        weightForAgeBoys.put(46, new double[] {11,12.5,14.1,16,18.2,20.7,23.6});
        weightForAgeBoys.put(47, new double[] {11.1,12.6,14.3,16.2,18.4,20.9,23.9});
        weightForAgeBoys.put(48, new double[] {11.2,12.7,14.4,16.3,18.6,21.2,24.2});
        weightForAgeBoys.put(49, new double[] {11.3,12.8,14.5,16.5,18.8,21.4,24.5});
        weightForAgeBoys.put(50, new double[] {11.4,12.9,14.7,16.7,19,21.7,24.8});
        weightForAgeBoys.put(51, new double[] {11.5,13.1,14.8,16.8,19.2,21.9,25.1});
        weightForAgeBoys.put(52, new double[] {11.6,13.2,15,17,19.4,22.2,25.4});
        weightForAgeBoys.put(53, new double[] {11.7,13.3,15.1,17.2,19.6,22.4,25.7});
        weightForAgeBoys.put(54, new double[] {11.8,13.4,15.2,17.3,19.8,22.7,26});
        weightForAgeBoys.put(55, new double[] {11.9,13.5,15.4,17.5,20,22.9,26.3});
        weightForAgeBoys.put(56, new double[] {12,13.6,15.5,17.7,20.2,23.2,26.6});
        weightForAgeBoys.put(57, new double[] {12.1,13.7,15.6,17.8,20.4,23.4,26.9});
        weightForAgeBoys.put(58, new double[] {12.2,13.8,15.8,18,20.6,23.7,27.2});
        weightForAgeBoys.put(59, new double[] {12.3,14,15.9,18.2,20.8,23.9,27.6});
        weightForAgeBoys.put(60, new double[] {12.4,14.1,16,18.3,21,24.2,27.9});
    }

    public LineGraphSeries<DataPoint> weightForAgeBoys(int pos, int maxMonth) {
        LineGraphSeries<DataPoint> weightforage_series = new LineGraphSeries<DataPoint>();
        for(int i=0; i< maxMonth; i++)
        {
            weightforage_series.appendData(
                    new DataPoint(i, weightForAgeBoys.get(i)[pos] ), true, 60);
        }

        weightforage_series.setColor(getColor(pos));
        weightforage_series.setThickness(1);
        weightforage_series.setBackgroundColor(getColor(pos));
        weightforage_series.setDrawBackground(true);
        return weightforage_series;
    }

    public Map<Integer, double[]> getHeightForAgeGirls() {
        return heightForAgeGirls;
    }

    public Map<Integer, double[]> getHeightForAgeBoys() {
        return heightForAgeBoys;
    }

    public Map<Integer, double[]> getWeightForAgeGirls() {
        return weightForAgeGirls;
    }

    public Map<Integer, double[]> getWeightForAgeBoys() {
        return weightForAgeBoys;
    }

    private int getColor(int pos)
    {
        int color = Color.RED;
        switch(pos)
        {
            case 0 :
            case 6 :
                color = Color.RED;
                break;
            case 1 :
            case 5 :
                color = Color.rgb(255, 165, 0);
                break;
            case 2 :
            case 4 :
                color = Color.YELLOW;
                break;
            case 3 :
                color = Color.GREEN;
                break;
        }
        return color;
    }

}