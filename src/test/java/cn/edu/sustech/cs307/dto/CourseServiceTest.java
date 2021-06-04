package cn.edu.sustech.cs307.dto;

import cn.edu.sustech.cs307.service.CourseService;
import reference.service.ReferenceCourseService;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseServiceTest {
    public static void main(String[] args) {

        CourseService cs=new ReferenceCourseService();
//        cs.removeCourse("MIAO101");
        List<Course> cc =  cs.getAllCourses();
        System.out.println(cc);
//        cs.addCourse("MIAO101","猫猫学导论",5,32, Course.CourseGrading.PASS_OR_FAIL,null);
//        int sec_id = cs.addCourseSection("MIAO101",1,"猫猫学期末学期~",20);
//        DayOfWeek dow=DayOfWeek.FRIDAY;
//        List<Short> weeklist=new ArrayList<>();
//        weeklist.add((short) 1);
//        weeklist.add((short) 3);
//        weeklist.add((short) 9);
//        weeklist.add((short) 10);
//        cs.addCourseSectionClass(sec_id,1,dow,weeklist,(short) 1,(short) 3,"半人马星");
//
//        Course ccc = cs.getCourseBySection(999);
//        System.out.println(ccc);
    }
}
