package cn.edu.sustech.cs307.dto;

import cn.edu.sustech.cs307.dto.prerequisite.AndPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.CoursePrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.OrPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.service.CourseService;
import cn.edu.sustech.cs307.service.MajorService;
import reference.service.ReferenceCourseService;
import reference.service.ReferenceMajorService;

import java.lang.reflect.Array;
import java.sql.Date;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CourseServiceTest {
    public static void main(String[] args) {
//        Prerequisite calculus = new OrPrerequisite(List.of(
//                new CoursePrerequisite("MA101A"),
//                new CoursePrerequisite("MA101B")
//        ));



//        Prerequisite algebra = new CoursePrerequisite("MA103A");
//        Prerequisite prerequisite = new AndPrerequisite(List.of(calculus, algebra));
//
//        CourseService cs=new ReferenceCourseService();
        MajorService ms = new ReferenceMajorService();
//        System.out.println(ms.addMajor("摩尔日报专业", 2));
//        System.out.println(ms.addMajor("摩尔警察专业", 2));

//        ms.removeMajor(12);

        ms.addMajorCompulsoryCourse(11, "YSQ101");
        ms.addMajorElectiveCourse(11, "MARS103");

        Semester s1=new Semester();
        s1.id=1;
        s1.name="yubhj";
        s1.begin=new Date(2001,1,1);
        s1.end=new Date(2001,9,1);

        Semester s2=new Semester();
        s2.id=2;
        s2.name="yfubhj";
        s2.begin=new Date(2001,10,1);
        s2.end=new Date(2002,1,1);

        Semester s3=new Semester();
        s3.id=3;
        s3.name="ygubhj";
        s3.begin=new Date(2002,2,1);
        s3.end=new Date(2002,9,1);



        CourseService cs=new ReferenceCourseService();
        cs.addCourse("PH101A", "小等物理上",4,64, Course.CourseGrading.HUNDRED_MARK_SCORE, null);
        Prerequisite prerequisite3 = new CoursePrerequisite("PH101A");
        cs.addCourse("PH101B", "小等物理下", 4, 64, Course.CourseGrading.HUNDRED_MARK_SCORE, prerequisite3);
        Prerequisite prerequisite4 = new CoursePrerequisite("PH101A");
        cs.addCourse("G173A", "地理A", 3, 32, Course.CourseGrading.HUNDRED_MARK_SCORE, prerequisite4);

        Prerequisite calculus = new OrPrerequisite(List.of(
                new CoursePrerequisite("PH101A"),
                new CoursePrerequisite("PH101B")
        ));
        Prerequisite algebra = new CoursePrerequisite("G173A");
        Prerequisite prerequisite1 = new AndPrerequisite(List.of(calculus, algebra));


        cs.addCourse("CS111", "计算机导论1", 3, 64, Course.CourseGrading.PASS_OR_FAIL, prerequisite1);

        Prerequisite prerequisite2 = new CoursePrerequisite("CS111");
        cs.addCourse("CS112", "计算机导论2", 3, 64, Course.CourseGrading.PASS_OR_FAIL, prerequisite2);

        Prerequisite c1 = new OrPrerequisite(List.of(
                new CoursePrerequisite("PH101A"),
                new CoursePrerequisite("PH101B")
        ));
        Prerequisite c2 = new CoursePrerequisite("CS112");
        Prerequisite c3 = new AndPrerequisite(List.of(c1,c2));

        cs.addCourse("CS113", "计算机导论3", 3, 64, Course.CourseGrading.PASS_OR_FAIL, c3);
//1
        int a1=cs.addCourseSection("PH101A",1,"英文一般",40);
        int a2=cs.addCourseSection("PH101A",1,"英文二般",40);
        int a3=cs.addCourseSection("PH101A",2,"英文三般",20);
//2
        int b1=cs.addCourseSection("PH101B",2,"英文一般",40);
        int b2=cs.addCourseSection("PH101B",2,"英文二般",40);
        int b3=cs.addCourseSection("PH101B",1,"英文三般",20);
//3
        int ck1=cs.addCourseSection("G173A",1,"中英文一般",80);
        int ck2=cs.addCourseSection("G173A",2,"中英文一般",80);
//4
        int d1=cs.addCourseSection("CS111",1,"中英文一般",100);
        int d2=cs.addCourseSection("CS111",1,"英文一般",50);
        int d3=cs.addCourseSection("CS112",2,"中英文一般",100);
        int d4=cs.addCourseSection("CS112",2,"英文一般",50);
        int d5=cs.addCourseSection("CS113",3,"中英文一般",100);
        int d6=cs.addCourseSection("CS113",3,"英文一般",50);

        //1
        cs.addCourseSectionClass(a1,1, DayOfWeek.MONDAY, Arrays.asList(new Short[]{1,3,5,6,7,8,9,10}) ,(short) 1,(short) 3,"半人马星");
        cs.addCourseSectionClass(a1,1, DayOfWeek.WEDNESDAY, Arrays.asList(new Short[]{1,3,5,6,7,8,9,10}) ,(short) 1,(short) 3,"半人马星");
        cs.addCourseSectionClass(a2,1, DayOfWeek.TUESDAY, Arrays.asList(new Short[]{1,3,5,6,7,8,9,10}) ,(short) 1,(short) 3,"半人马星");
        cs.addCourseSectionClass(a2,3, DayOfWeek.THURSDAY, Arrays.asList(new Short[]{1,3,5,6,7,8,9,10}) ,(short) 1,(short) 3,"半人马星");
        cs.addCourseSectionClass(a3,3, DayOfWeek.MONDAY, Arrays.asList(new Short[]{1,3,5,6,7,8,9,10}) ,(short) 4,(short) 6,"半人马星");
        cs.addCourseSectionClass(a3,3, DayOfWeek.FRIDAY, Arrays.asList(new Short[]{1,3,5,6,7,8,9,10}) ,(short) 1,(short) 3,"半人马星");
        //2
        cs.addCourseSectionClass(b1,2, DayOfWeek.MONDAY, Arrays.asList(new Short[]{1,2,3,4,5,6,7,8}) ,(short) 2,(short) 4,"p星");
        cs.addCourseSectionClass(b1,2, DayOfWeek.WEDNESDAY, Arrays.asList(new Short[]{1,3,5,6,7,8,9,10}) ,(short) 1,(short) 3,"半人马星");
        cs.addCourseSectionClass(b2,2, DayOfWeek.TUESDAY, Arrays.asList(new Short[]{1,3,5,6,7,8,9,10}) ,(short) 1,(short) 4,"p星");
        cs.addCourseSectionClass(b2,4, DayOfWeek.THURSDAY, Arrays.asList(new Short[]{1,3,5,6,7,8,9,10}) ,(short) 1,(short) 3,"半人马星");
        cs.addCourseSectionClass(b3,4, DayOfWeek.MONDAY, Arrays.asList(new Short[]{1,3,5,6,7,8,9,10}) ,(short) 3,(short) 5,"p星");
        cs.addCourseSectionClass(b3,4, DayOfWeek.FRIDAY, Arrays.asList(new Short[]{1,3,5,6,7,8,9,10}) ,(short) 6,(short) 7,"半人马星");
        //3
        cs.addCourseSectionClass(ck1,5, DayOfWeek.MONDAY, Arrays.asList(new Short[]{1,2,3,4,5,6,7,8}) ,(short) 2,(short) 4,"p星");
        cs.addCourseSectionClass(ck1,5, DayOfWeek.WEDNESDAY, Arrays.asList(new Short[]{1,2,3,4,5,6,7,8}) ,(short) 1,(short) 3,"半人马星");
        cs.addCourseSectionClass(ck2,5, DayOfWeek.TUESDAY, Arrays.asList(new Short[]{2,4,11}) ,(short) 1,(short) 4,"p星");
        cs.addCourseSectionClass(ck2,5, DayOfWeek.THURSDAY, Arrays.asList(new Short[]{2,4,11}) ,(short) 1,(short) 3,"半人马星");

    }


//        cs.removeCourse("MIAO101");
//        cs.addCourse("MIAO101","猫猫学导论",5,32, Course.CourseGrading.PASS_OR_FAIL,null);
//        int sec_id = cs.addCourseSection("MIAO101",1,"猫猫学期末学期~",20);
//        DayOfWeek dow=DayOfWeek.FRIDAY;
//        List<Short> weeklist=new ArrayList<>();
//
////        cs.addCourseSectionClass(sec_id,1);
//        Course ccc = cs.getCourseBySection(999);
//        System.out.println(ccc);
//        cs.addCourse("MA101A", "高等数学上",4,64, Course.CourseGrading.HUNDRED_MARK_SCORE, null);
//        cs.addCourse("MA101B", "低等数学上", 4, 64, Course.CourseGrading.HUNDRED_MARK_SCORE, null);
//        cs.addCourse("MA103A", "线性代数", 4, 64, Course.CourseGrading.HUNDRED_MARK_SCORE, null);
//        cs.addCourse("MA102A", "超等数学", 4, 64, Course.CourseGrading.HUNDRED_MARK_SCORE, prerequisite);
//        cs.removeCourse("MA102A");
//        cs.removeCourse("MA101A");
//        cs.removeCourse("MA101B");
//        cs.removeCourse("MA103A");


//        cs.addCourseSectionClass(sec_id,1);
//        Course ccc = cs.getCourseBySection(999);
//        System.out.println(ccc);



//=======
//        cs.removeCourse("MIAO101");
//        List<Course> cc =  cs.getAllCourses();
//        System.out.println(cc);

//        var csis=cs.getCourseSectionsInSemester("MARS101",2);
//        var csis=cs.getCourseBySection(4);
//        var csis=cs.getCourseSectionByClass(4);
//        var csis=cs.getCourseSectionClasses(4);
//        var csis=cs.getEnrolledStudentsInSemester("MARS101",1);
//        cs.removeCourse("MARS102");
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

    }

