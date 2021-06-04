package cn.edu.sustech.cs307.dto;

import cn.edu.sustech.cs307.service.DepartmentService;
import reference.service.ReferenceDepartmentService;

public class DepartmentServiceTest {
    public static void main(String[] args) {
        DepartmentService ds=new ReferenceDepartmentService();
//        int id = ds.addDepartment("睡眠研究所");
//        var v1=ds.getAllDepartments();
        var v2=ds.getDepartment(1);
        ds.removeDepartment(1);
    }
}
