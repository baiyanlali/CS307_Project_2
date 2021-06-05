package cn.edu.sustech.cs307.dto;

import cn.edu.sustech.cs307.dto.grade.Grade;
import cn.edu.sustech.cs307.service.StudentService;
import reference.service.ReferenceStudentService;

import java.sql.Date;

public class StudentServiceTest {
    public static void main(String[] args) {
        StudentService ss=new ReferenceStudentService();
        Date d=new Date(2001, 1,30);
//        ss.addStudent(11911311,11,"Soviet","Fans",d);

//        var v1 = ss.getStudentMajor(11911311);
//        var v1=ss.enrollCourse(11911311,1);
//        var v2 = ss.getStudentMajor(11911311);

        ss.dropCourse(11911311,7);
        ss.dropCourse(11911311,8);
    }
}
