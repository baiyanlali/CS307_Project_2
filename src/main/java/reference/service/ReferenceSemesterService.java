package reference.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Semester;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.SemesterService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReferenceSemesterService implements SemesterService {

    @Override
    public int addSemester(String name, Date begin, Date end) {

        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {

            PreparedStatement stmt = connection.prepareStatement("select add_semster(?,?,?) as sem_id");
            stmt.setString(1,name);
            stmt.setDate(2,begin);
            stmt.setDate(3,end);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    int sem_id=rs.getInt("sem_id");
                    connection.close();
                    return sem_id;
                }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new IntegrityViolationException();
        }
        throw new IntegrityViolationException();
    }

    @Override
    public void removeSemester(int semesterId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {

            PreparedStatement stmt = connection.prepareStatement("select remove_semester(?)");
            stmt.setInt(1,semesterId);
            stmt.execute();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Semester> getAllSemesters() {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {

            PreparedStatement stmt = connection.prepareStatement("select sem_id, sem_name, sem_begin, sem_end  from semester");

            ResultSet rs = stmt.executeQuery();
            List<Semester> con=new ArrayList<>();

            while (rs.next()){
                Semester sm=new Semester();
                sm.id=rs.getInt("sem_id");
                sm.name=rs.getString("sem_name");
                sm.begin=rs.getDate("sem_begin");
                sm.end=rs.getDate("sem_end");
                con.add(sm);
            }
            connection.close();
//            if(!con.isEmpty()){
                return con;
//            }else{
//                throw new EntityNotFoundException();
//            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Semester getSemester(int semesterId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {

            PreparedStatement stmt = connection.prepareStatement("select sem_id, sem_name, sem_begin, sem_end  from semester where sem_id=?");
            stmt.setInt(1,semesterId);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                Semester sm=new Semester();
                sm.id=rs.getInt("sem_id");
                sm.name=rs.getString("sem_name");
                sm.begin=rs.getDate("sem_begin");
                sm.end=rs.getDate("sem_end");
                connection.close();
                return sm;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
