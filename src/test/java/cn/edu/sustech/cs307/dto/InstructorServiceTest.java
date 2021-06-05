package cn.edu.sustech.cs307.dto;

import cn.edu.sustech.cs307.service.InstructorService;
import reference.service.ReferenceInstructorService;

public class InstructorServiceTest {
    public static void main(String[] args) {
        InstructorService is=new ReferenceInstructorService();
        is.addInstructor(30000002,"矮","冬瓜");
        var v1 = is.getInstructedCourseSections(30000000,1);
        var v2 = is.getInstructedCourseSections(30000002,1);
    }
}
