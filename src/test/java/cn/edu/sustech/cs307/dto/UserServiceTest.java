package cn.edu.sustech.cs307.dto;

import cn.edu.sustech.cs307.service.StudentService;
import cn.edu.sustech.cs307.service.UserService;
import reference.service.ReferenceStudentService;
import reference.service.ReferenceUserService;

public class UserServiceTest {
    public static void main(String[] args) {
        UserService us=new ReferenceUserService();
        var v1 =  us.getAllUsers();
        var v2 =  us.getUser(30000000);
        us.removeUser(30000000);
    }
}
