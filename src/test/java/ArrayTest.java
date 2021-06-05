import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.service.StudentService;
import reference.service.Util;

import java.sql.*;

public class ArrayTest {
    public static void main(String[] args) {
//        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()){
//            PreparedStatement stmt = connection.prepareStatement("select array_agg(format('%s,%s,%s,%s,%s,%s,%s,%s,%s', c.class_id, class_begin, class_end, week_list, day_of_week,\n" +
//                    "               instructor_id, first_name, last_name, loc)) as class_info\n" +
//                    "                    from class c\n" +
//                    "                   join location l on l.location_id = c.loc_id\n" +
//                    "                   join teaching_info ti on c.class_id = ti.class_id\n" +
//                    "                    join users u on u.id = ti.instructor_id\n" +
//                    "                    join section s on c.sec_id = s.sec_id\n" +
//                    "                    where c.sec_id=7");
//            ResultSet rs = stmt.executeQuery();
//            while(rs.next()){
//               Array a = rs.getArray("class_info");
//               var lists = (String[])a.getArray();
//                for (int i = 0; i < lists.length; i++) {
//                    var v2 = Util.getCourseSectionClass(lists[i]);
//                    System.out.println(v2);
//                }
//                System.out.println(a);
//            }
//
//
//        }catch(SQLException e){
//
//        }
//        var v1 = Util.getCourseSectionClass("11911309,2,3,10101010,4,11911309,ba,ga,yijiao");
//        String searchName="数据库压榨花生油[压榨1班]";
//        String courseName=searchName.substring(0,searchName.indexOf('['));
//        String sectionName=searchName.substring(searchName.indexOf('[')+1,searchName.lastIndexOf(']'));
        System.out.println(StudentService.CourseType.CROSS_MAJOR.ordinal());
    }
}
