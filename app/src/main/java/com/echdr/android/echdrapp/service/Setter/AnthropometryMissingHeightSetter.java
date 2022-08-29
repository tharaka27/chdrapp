package com.echdr.android.echdrapp.service.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AnthropometryMissingHeightSetter {
    private  Map<Integer,Integer> dataElements;

    public Map<Integer, Integer> getDataElements(){
        return dataElements;
    }

    public void setDataElements(Map<Integer, Integer> dataElements){
        this.dataElements = dataElements;
    }

    public void setMissingElements(){
        List<Integer> nonZeroElements = countNonZero();

        if(nonZeroElements.size() <= 1){
            throw new IllegalArgumentException("interpolate: illegal count!");
        }else{
            System.out.print("nonZeroElements ");
            System.out.println(nonZeroElements.size());
        }

        Map<Integer, Integer> pairs = make_pairs(nonZeroElements);

        for (Map.Entry<Integer, Integer> set : pairs.entrySet()) {
            int[] values = interpolate(dataElements.get(set.getKey()),
                    dataElements.get(set.getValue()), set.getValue()-set.getKey());
            positionValues(set.getKey(), set.getValue(), values );
        }
    }

    private Map<Integer, Integer> make_pairs(List<Integer> occurrences){
        Map<Integer,Integer> pairs = new HashMap<>();
        for(int i=0; i<occurrences.size()-1;i++){
            if(occurrences.get(i+1) - occurrences.get(i) >=2 ){
                pairs.put(occurrences.get(i), occurrences.get(i+1));
            }
        }
        return pairs;
    }

    private List<Integer> countNonZero() {
        List<Integer> occurrences = new ArrayList<>();
        for(int i=0; i < dataElements.size(); i++){
            if(dataElements.get(i) != 0){
                occurrences.add(i);
            }
        }
        return occurrences;
    }

    private void positionValues(int start, int end, int[] values) {
        for(int i=0; i< end-start+1; i++){
            dataElements.put(start + i, values[i]);
        }
    }

    private int[]  interpolate(int start, int end, int count) {
        if (count < 2) {
            throw new IllegalArgumentException("interpolate: illegal count!");
        }
        int[] array = new int[count+1];
        for (int i = 0; i <= count; ++ i) {
            array[i] = start + i * (end - start) / count;
        }
        return array;
    }

}
