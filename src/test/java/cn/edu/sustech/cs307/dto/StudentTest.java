package cn.edu.sustech.cs307.dto;

import cn.edu.sustech.cs307.service.StudentService;
import reference.service.ReferenceStudentService;

import java.sql.Date;

public class StudentTest {
    public static void main(String[] args) {
        StudentService ss=new ReferenceStudentService();
        Date d=new Date(2001,1,1);
        ss.getCourseTable(11911309,d);

    }
}
