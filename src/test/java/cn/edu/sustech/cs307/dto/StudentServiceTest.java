package cn.edu.sustech.cs307.dto;

import cn.edu.sustech.cs307.dto.grade.Grade;
import cn.edu.sustech.cs307.service.StudentService;
import reference.service.ReferenceStudentService;

import java.sql.Date;
import java.time.LocalDate;

public class StudentServiceTest {
    public static void main(String[] args) {
        StudentService ss=new ReferenceStudentService();
//        Date d=new Date(2001, 1,30);
//        ss.addStudent(11911311,11,"Soviet","Fans",d);

//        var v1 = ss.getStudentMajor(11911311);
//        var v1=ss.enrollCourse(11911311,1);
//        var v2 = ss.getStudentMajor(11911311);
        var v2 = ss.searchCourse(11717372,145,null,null,null,null,null,null, StudentService.CourseType.ALL,false,false,true,true,30,0);
//        var v2=ss.getCourseTable(11712973,Date.valueOf(LocalDate.ofEpochDay(17934)));
//        System.out.println(LocalDate.ofEpochDay(17934));
        System.out.println(v2);
//        ss.dropCourse(11911311,7);
//        ss.dropCourse(11911311,8);
    }
}
