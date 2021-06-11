package cn.edu.sustech.cs307.dto;

import reference.service.Util;

import java.util.HashMap;
import java.util.Map;

public class TEST {
    public static void main(String[] args) {
        HashMap<Integer,Integer> maps=new HashMap<>();
        maps.put(1,2);
        maps.put(99,10);
        System.out.println(maps);
        Map.Entry[] entries= maps.entrySet().toArray(Map.Entry[]::new);
        Map map=Map.ofEntries(entries);
        System.out.println(map.getClass());
        System.out.println(Map.copyOf(maps).getClass());
    }
}
