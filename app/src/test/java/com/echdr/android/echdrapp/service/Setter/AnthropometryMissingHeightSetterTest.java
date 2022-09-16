package com.echdr.android.echdrapp.service.Setter;

import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnthropometryMissingHeightSetterTest{

    AnthropometryMissingHeightSetter anthropometryMissingHeightSetter;

    @Test
    public void onlyOneMissingField(){
        anthropometryMissingHeightSetter = new AnthropometryMissingHeightSetter();

        Map<Integer,Integer> map = new HashMap<>();
        map.put(0,10);
        map.put(1,20);
        map.put(2, 0);
        map.put(3, 40);

        anthropometryMissingHeightSetter.setDataElements(map);
        anthropometryMissingHeightSetter.setMissingElements();

        Map<Integer,Integer> actual_map = anthropometryMissingHeightSetter.getDataElements();

        System.out.println(actual_map);

        Map<Integer,Integer> expected_map = new HashMap<>();
        expected_map.put(0,10);
        expected_map.put(1,20);
        expected_map.put(2, 30);
        expected_map.put(3, 40);

        for(int i=0; i< actual_map.size(); i++){
            assertEquals(actual_map.get(i), expected_map.get(i));
        }

    }

    @Test
    public void multipleConsecutiveMissingField(){
        anthropometryMissingHeightSetter = new AnthropometryMissingHeightSetter();

        Map<Integer,Integer> map = new HashMap<>();
        map.put(0,10);
        map.put(1,20);
        map.put(2, 0);
        map.put(3, 0);
        map.put(4, 0);
        map.put(5, 60);
        map.put(6, 70);

        anthropometryMissingHeightSetter.setDataElements(map);
        anthropometryMissingHeightSetter.setMissingElements();

        Map<Integer,Integer> actual_map = anthropometryMissingHeightSetter.getDataElements();

        System.out.println(actual_map);

        Map<Integer,Integer> expected_map = new HashMap<>();
        expected_map.put(0,10);
        expected_map.put(1,20);
        expected_map.put(2, 30);
        expected_map.put(3, 40);
        expected_map.put(4,50);
        expected_map.put(5,60);
        expected_map.put(6, 70);

        for(int i=0; i< actual_map.size(); i++){
            assertEquals(actual_map.get(i), expected_map.get(i));
        }

    }

    @Test
    public void twoMissingField(){
        anthropometryMissingHeightSetter = new AnthropometryMissingHeightSetter();

        Map<Integer,Integer> map = new HashMap<>();
        map.put(0,10);
        map.put(1,20);
        map.put(2, 0);
        map.put(3, 0);
        map.put(4, 0);
        map.put(5, 60);
        map.put(6, 0);
        map.put(7, 0);
        map.put(8, 90);

        anthropometryMissingHeightSetter.setDataElements(map);
        anthropometryMissingHeightSetter.setMissingElements();

        Map<Integer,Integer> actual_map = anthropometryMissingHeightSetter.getDataElements();

        System.out.println(actual_map);

        Map<Integer,Integer> expected_map = new HashMap<>();
        expected_map.put(0,10);
        expected_map.put(1,20);
        expected_map.put(2, 30);
        expected_map.put(3, 40);
        expected_map.put(4,50);
        expected_map.put(5,60);
        expected_map.put(6, 70);
        expected_map.put(7, 80);
        expected_map.put(8, 90);

        for(int i=0; i< actual_map.size(); i++){
            assertEquals(actual_map.get(i), expected_map.get(i));
        }

    }

    @Test
    public void noEnd(){
        anthropometryMissingHeightSetter = new AnthropometryMissingHeightSetter();

        Map<Integer,Integer> map = new HashMap<>();
        map.put(0,10);
        map.put(1,20);
        map.put(2, 0);
        map.put(3, 0);
        map.put(4, 0);
        map.put(5, 60);
        map.put(6, 0);
        map.put(7, 0);
        map.put(8, 0);

        anthropometryMissingHeightSetter.setDataElements(map);
        anthropometryMissingHeightSetter.setMissingElements();

        Map<Integer,Integer> actual_map = anthropometryMissingHeightSetter.getDataElements();

        System.out.println(actual_map);

        Map<Integer,Integer> expected_map = new HashMap<>();
        expected_map.put(0,10);
        expected_map.put(1,20);
        expected_map.put(2, 30);
        expected_map.put(3, 40);
        expected_map.put(4,50);
        expected_map.put(5,60);
        expected_map.put(6, 0);
        expected_map.put(7, 0);
        expected_map.put(8, 0);

        for(int i=0; i< actual_map.size(); i++){
            assertEquals(actual_map.get(i), expected_map.get(i));
        }

    }

}