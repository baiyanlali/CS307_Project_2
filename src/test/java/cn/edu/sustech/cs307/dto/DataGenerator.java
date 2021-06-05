package cn.edu.sustech.cs307.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {
    static final String[] charaters1={"汉字","文学","计算机","摩尔庄园","赛尔号","咖啡","电影","篮球","读音","虫子"};
    static final String[] charaters_id1={"c","w","j","m","s","k","d","l","b","z"};
    static final String[] charaters2={"看","烹饪","打","学习","纠正","解剖","速冻"};
    public static void main(String[] args) {
        var v1 = generateCourse(5);
    }
    static Random r=new Random();
    public static List<String> generateCourse(int count){
        List<String> con=new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String name=range(charaters2).concat(range(charaters1));
            String id=range(charaters_id1).concat(String.valueOf(r.nextInt(999)));
            int credit=r.nextInt(20);
            int course_hour=r.nextInt(100);
            int grade_type=r.nextInt(100);
            String c1=String.format("insert into course(course_id, course_name, credit, course_hour, pre_pattern, grade_type) VALUES " +
                                        "(\'%s\',\'%s\',%d,%d,,%d)",name,id,credit,course_hour,grade_type);
            con.add(c1);
        }
        return con;
    }


    public static <T> T range(T[] ts){
        return ts[r.nextInt(ts.length)];
    }
}
