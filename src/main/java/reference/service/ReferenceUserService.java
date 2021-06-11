package reference.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Instructor;
import cn.edu.sustech.cs307.dto.Semester;
import cn.edu.sustech.cs307.dto.Student;
import cn.edu.sustech.cs307.dto.User;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.service.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReferenceUserService implements UserService {
    @Override
    public void removeUser(int userId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {
            PreparedStatement p= connection.prepareStatement("select remove_user(?)");
            p.setInt(1,userId);
            p.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<User> getAllUsers() {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {

            PreparedStatement stmt = connection.prepareStatement("select id,first_name,last_name,classified_as from users");
            ResultSet rs = stmt.executeQuery();
            List<User> users=new ArrayList<>();
            if(rs.next()){
                User us;
                String first_name=rs.getString("first_name");
                String last_name=rs.getString("last_name");
                int classified=rs.getInt("classified_as");
                if(classified==0){
                    //student
                    us=new Student();
                }else if(classified==1){
                    us=new Instructor();
                }else{
                    //by default
                    us=new Student();
                }
                us.fullName=Util.getName(first_name,last_name);
                us.id=rs.getInt("id");
                users.add(us);
            }
//            if(!users.isEmpty()){
                return users;
//            }else{
//                throw new EntityNotFoundException();
//            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User getUser(int userId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {

            PreparedStatement stmt = connection.prepareStatement("select id,first_name,last_name,classified_as from users where id=?");
            stmt.setInt(1,userId);



            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                User us;
                String first_name=rs.getString("first_name");
                String last_name=rs.getString("last_name");
                int classified=rs.getInt("classified_as");
                if(classified==0){
                    //student
                    us=new Student();
                }else if(classified==1){
                    us=new Instructor();
                }else{
                    //by default
                    us=new Student();
                }
                us.fullName=Util.getName(first_name,last_name);
                us.id=rs.getInt("id");
                return us;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
