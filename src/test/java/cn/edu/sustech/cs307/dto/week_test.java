package cn.edu.sustech.cs307.dto;

import java.util.ArrayList;
import java.util.List;

public class week_test {
    public static void main(String[] args) {
        List<Short> weekList=new ArrayList<Short>();
        weekList.add((short) 2);
        weekList.add((short) 3);
        weekList.add((short) 5);
        weekList.add((short) 7);
        weekList.add((short) 9);
        weekList.add((short) 10);
        StringBuffer week=new StringBuffer();
        int index=0;
        for (int i = 1; i <= weekList.get(weekList.size()-1); i++) {
            if(i==weekList.get(index)){
                week.append(1);
                index++;
            }else{
                week.append(0);
            }
        }
        System.out.println(week.toString());

        List<Short> weekOfList=new ArrayList<>();
        for (short i = 0; i < week.toString().length(); i++) {
            if(week.toString().charAt(i)=='1'){
                weekOfList.add((short) (i+(short) 1));
            }
        }
        System.out.println(weekOfList);
    }

}
