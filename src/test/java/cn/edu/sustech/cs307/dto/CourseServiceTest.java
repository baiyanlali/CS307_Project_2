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
<<<<<<< HEAD
        cs.removeCourse("MIAO101");
        cs.addCourse("MIAO101","猫猫学导论",5,32, Course.CourseGrading.PASS_OR_FAIL,null);
        int sec_id = cs.addCourseSection("MIAO101",1,"猫猫学期末学期~",20);
        DayOfWeek dow=DayOfWeek.FRIDAY;
        List<Short> weeklist=new ArrayList<>();
        
//        cs.addCourseSectionClass(sec_id,1);
        Course ccc = cs.getCourseBySection(999);
        System.out.println(ccc);
=======
//        cs.removeCourse("MIAO101");
//        List<Course> cc =  cs.getAllCourses();
//        System.out.println(cc);

//        var csis=cs.getCourseSectionsInSemester("MARS101",2);
//        var csis=cs.getCourseBySection(4);
//        var csis=cs.getCourseSectionByClass(4);
//        var csis=cs.getCourseSectionClasses(4);
//        var csis=cs.getEnrolledStudentsInSemester("MARS101",1);
        cs.removeCourse("MARS102");
//        System.out.println(csis);
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
>>>>>>> c16a8af558ce1ab46bf5f10075e4e152000e1983
    }
}
