package cn.edu.sustech.cs307.dto;

import cn.edu.sustech.cs307.dto.prerequisite.AndPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.CoursePrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.OrPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.service.CourseService;
import cn.edu.sustech.cs307.service.MajorService;
import reference.service.ReferenceCourseService;
import reference.service.ReferenceMajorService;

import java.time.DayOfWeek;
import java.util.ArrayList;
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
        CourseService cs=new ReferenceCourseService();
        cs.addCourse("PH101A", "小等物理上",4,64, Course.CourseGrading.HUNDRED_MARK_SCORE, null);
        cs.addCourse("PH101B", "小等物理下", 4, 64, Course.CourseGrading.HUNDRED_MARK_SCORE, null);
        cs.addCourse("G173A", "地理A", 3, 32, Course.CourseGrading.HUNDRED_MARK_SCORE, null);
        cs.addCourse("CS111", "计算机导论1", 3, 64, Course.CourseGrading.PASS_OR_FAIL, null);
        cs.addCourse("CS112", "计算机导论2", 3, 64, Course.CourseGrading.PASS_OR_FAIL, null);
        cs.addCourse("CS113", "计算机导论3", 3, 64, Course.CourseGrading.PASS_OR_FAIL, null);
//1
        int a1=cs.addCourseSection("PH101A",1,"英文一般",40);
        int a2=cs.addCourseSection("PH101A",1,"英文二般",40);
        int a3=cs.addCourseSection("PH101A",2,"英文三般",20);
//2
        int b1=cs.addCourseSection("PH101B",2,"英文一般",40);
        int b2=cs.addCourseSection("PH101B",2,"英文二般",40);
        int b3=cs.addCourseSection("PH101B",1,"英文三般",20);
//3
        int c1=cs.addCourseSection("G173A",1,"中英文一般",80);
        int c2=cs.addCourseSection("G173A",2,"中英文一般",80);
//4
        int d1=cs.addCourseSection("CS111",1,"中英文一般",100);
        int d2=cs.addCourseSection("CS111",1,"英文一般",50);
        int d3=cs.addCourseSection("CS112",1,"中英文一般",100);
        int d4=cs.addCourseSection("CS112",1,"英文一般",50);
        int d5=cs.addCourseSection("CS113",1,"中英文一般",100);
        int d6=cs.addCourseSection("CS113",1,"英文一般",50);

        //1
//        cs.addCourseSectionClass(a1,1,dow,weeklist,(short) 1,(short) 3,"半人马星")



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

