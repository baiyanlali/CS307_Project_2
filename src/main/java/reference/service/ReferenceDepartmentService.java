package reference.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.DepartmentService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReferenceDepartmentService implements DepartmentService {
    @Override
    public int addDepartment(String name) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {
            PreparedStatement stmt = connection.prepareStatement("select add_department(?) as dept_id");
            stmt.setString(1,name);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                int dept_id=rs.getInt("dept_id");
                if(dept_id==-1){
                    throw new IntegrityViolationException();
                }
                connection.close();
                return dept_id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;

    }

    @Override
    public void removeDepartment(int departmentId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {
            PreparedStatement stmt = connection.prepareStatement(
                    "delete from major_course\n" +
                    "where major_id=\n" +
                    "(select m.major_id\n" +
                    "from major_course\n" +
                    "join major m on m.major_id = major_course.major_id\n" +
                    "where dept_id=?);\n"
                    );

            stmt.setInt(1,departmentId);
            stmt.execute();
            stmt = connection.prepareStatement(
                    "delete from major where dept_id=?;\n" );
            stmt.setInt(1,departmentId);
            stmt.execute();

            stmt = connection.prepareStatement(
                    "delete from department where dept_id=?;");
            stmt.setInt(1,departmentId);
            stmt.execute();

            stmt=connection.prepareStatement("commit; ");
            stmt.execute();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Department> getAllDepartments() {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {
            PreparedStatement stmt = connection.prepareStatement("select dept_id, dept_name from department");
            ResultSet rs = stmt.executeQuery();
            List<Department> departments=new ArrayList<>();
            while (rs.next()){
                Department d=new Department();
                d.id=rs.getInt("dept_id");
                d.name=rs.getString("dept_name");
                departments.add(d);
//                return rs.getInt("dept_id");
            }
            connection.close();
//            if(departments.size()!=0)
            return departments;
//            else throw new EntityNotFoundException();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
//        return null;
    }

    @Override
    public Department getDepartment(int departmentId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {
            PreparedStatement stmt = connection.prepareStatement("select dept_id, dept_name from department where dept_id=?");
            stmt.setInt(1,departmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                Department d=new Department();
                d.id=rs.getInt("dept_id");
                d.name=rs.getString("dept_name");
                connection.close();
                return d;
//                return rs.getInt("dept_id");
            }else{
                throw new EntityNotFoundException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
