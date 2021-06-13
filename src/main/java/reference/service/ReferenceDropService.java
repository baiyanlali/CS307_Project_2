package reference.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.service.DropService;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ReferenceDropService implements DropService {
    @Override
    public void dropTables() {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {
            Statement stmt=connection.createStatement();
            stmt.execute("truncate table teaching_info cascade;\n" +
                    "truncate table class cascade;\n" +
                    "truncate table location cascade;\n" +
                    "truncate table major_course cascade;\n" +
                    "truncate table pre_courses cascade;\n" +
                    "truncate table student_info cascade;\n" +
                    "truncate table major cascade;\n" +
                    "truncate table department cascade;\n" +
                    "truncate table learning_info cascade;\n" +
                    "truncate table section cascade;\n" +
                    "truncate table course cascade;\n" +
                    "truncate table semester cascade;\n" +
                    "truncate table users cascade;");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
