package cn.edu.sustech.cs307.dto;

import cn.edu.sustech.cs307.service.SemesterService;
import reference.service.ReferenceSemesterService;

import java.sql.Date;

public class SemesterServiceTest {
    public static void main(String[] args) {
        SemesterService ss=new ReferenceSemesterService();
        Date begin=new Date(2001,1,1);
        Date end=new Date(2001,1,30);
        int i = ss.addSemester("伏地魔专属学期",begin,end);
        var v1 = ss.getSemester(i);
        var v2=ss.getAllSemesters();
        ss.removeSemester(i);
    }
}
