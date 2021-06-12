package reference.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.service.MajorService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReferenceMajorService implements MajorService {
    @Override
    public int addMajor(String name, int departmentId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select add_major(?,?)")) {
            stmt.setString(1,name);
            stmt.setInt(2,departmentId);

            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                int add_major=rs.getInt("add_major");
                connection.close();
                return add_major;
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void removeMajor(int majorId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select remove_major(?)")) {
            stmt.setInt(1,majorId);
            stmt.execute();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Major> getAllMajors() {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "select major_id, major_name, d.dept_name, d.dept_id\n" +
                     "from major\n" +
                     "join department d on d.dept_id = major.dept_id")) {

            ResultSet rs = stmt.executeQuery();

            ArrayList<Major> majors=new ArrayList<Major>();
            while (rs.next()) {
                Major tm = new Major();
                tm.id = rs.getInt("major_id");
                tm.name = rs.getString("major_name");
                Department td = new Department();
                td.id = rs.getInt("dept_id");
                td.name = rs.getString("dept_name");
                tm.department = td;

                majors.add(tm);
            }
            connection.close();
            if(majors.isEmpty()){
            throw new EntityNotFoundException();}
            return majors;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

        @Override
    public Major getMajor(int majorId) {
            try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
                 PreparedStatement stmt = connection.prepareStatement("select major_id, major_name, d.dept_name, d.dept_id\n" +
                         "       from major\n" +
                         "    join department d on d.dept_id = major.dept_id where major_id=?")) {

                stmt.setInt(1, majorId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Major tm = new Major();
                    tm.id = rs.getInt("major_id");
                    tm.name = rs.getString("major_name");
                    Department td = new Department();
                    td.id = rs.getInt("dept_id");
                    td.name = rs.getString("dept_name");
                    tm.department = td;

                    connection.close();
                    return tm;
                }else {
                    throw new EntityNotFoundException();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }

    }

    @Override
    public void addMajorCompulsoryCourse(int majorId, String courseId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select add_major_course(?,?,?)")) {

            stmt.setInt(1, majorId);
            stmt.setString(2, courseId);
            stmt.setInt(3, 1);
            stmt.execute();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addMajorElectiveCourse(int majorId, String courseId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select add_major_course(?,?,?)")) {

            stmt.setInt(1, majorId);
            stmt.setString(2, courseId);
            stmt.setInt(3, 0);
            stmt.execute();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
