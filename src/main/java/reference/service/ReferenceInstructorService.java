package reference.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.CourseSection;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.service.InstructorService;

import javax.print.attribute.standard.MediaSize;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReferenceInstructorService implements InstructorService {
    @Override
    public void addInstructor(int userId, String firstName, String lastName) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {

            PreparedStatement stmt = connection.prepareStatement("insert into users(id, first_name, last_name, classified_as) VALUES (?,?,?,1)");
            stmt.setInt(1,userId);
            stmt.setString(2,firstName);
            stmt.setString(3,lastName);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<CourseSection> getInstructedCourseSections(int instructorId, int semesterId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {

            PreparedStatement stmt = connection.prepareStatement(
                    "select sec_id, sec_name, tot_capacity, left_capacity\n" +
                    "from\n" +
                    "     section\n" +
                    "where sec_id=\n" +
                    "            (select sec_id\n" +
                    "            from class\n" +
                    "            join teaching_info ti on class.class_id = ti.class_id\n" +
                    "            where instructor_id=?) and semester_id=?;");
            stmt.setInt(1,instructorId);
            stmt.setInt(2,semesterId);
            ResultSet rs = stmt.executeQuery();
            List<CourseSection> con=new ArrayList<>();
            while(rs.next()){
                int sec_id=rs.getInt("sec_id");
                String sec_name=rs.getString("sec_name");
                int tot_capacity=rs.getInt("tot_capacity");
                int left_capacity=rs.getInt("left_capacity");
                CourseSection cs=new CourseSection();
                cs.leftCapacity=left_capacity;
                cs.name= sec_name;
                cs.id=sec_id;
                cs.totalCapacity=tot_capacity;
                con.add(cs);
            }
            if(!con.isEmpty()){
                return con;
            }else{
                throw new EntityNotFoundException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
