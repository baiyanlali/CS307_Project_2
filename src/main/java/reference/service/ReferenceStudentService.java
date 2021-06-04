package reference.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Course;
import cn.edu.sustech.cs307.dto.CourseSearchEntry;
import cn.edu.sustech.cs307.dto.CourseTable;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.dto.grade.Grade;
import cn.edu.sustech.cs307.service.StudentService;

import javax.annotation.Nullable;
import java.sql.*;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ReferenceStudentService implements StudentService {
    @Override
    public void addStudent(int userId, int majorId, String firstName, String lastName, Date enrolledDate) {

    }

    @Override
    public List<CourseSearchEntry> searchCourse(int studentId, int semesterId, @Nullable String searchCid, @Nullable String searchName, @Nullable String searchInstructor, @Nullable DayOfWeek searchDayOfWeek, @Nullable Short searchClassTime, @Nullable List<String> searchClassLocations, CourseType searchCourseType, boolean ignoreFull, boolean ignoreConflict, boolean ignorePassed, boolean ignoreMissingPrerequisites, int pageSize, int pageIndex) {
        return null;
    }

    @Override
    public EnrollResult enrollCourse(int studentId, int sectionId) {
        return null;
    }

    @Override
    public void dropCourse(int studentId, int sectionId) throws IllegalStateException {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("call drop_course(?, ?)")) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addEnrolledCourseWithGrade(int studentId, int sectionId, @Nullable Grade grade) {

    }

    @Override
    public void setEnrolledCourseGrade(int studentId, int sectionId, Grade grade) {

    }

    @Override
    public Map<Course, Grade> getEnrolledCoursesAndGrades(int studentId, @Nullable Integer semesterId) {
        return null;
    }

    @Override
    public CourseTable getCourseTable(int studentId, Date date) {
        return null;
    }

    @Override
    public boolean passedPrerequisitesForCourse(int studentId, String courseId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();){
             PreparedStatement stmt = connection.prepareStatement("select pre_list(?, ?)");
             PreparedStatement patterQuery=connection.prepareStatement("select pre_pattern from course where course_id=?");

             //get list
            stmt.setInt(1, studentId);
            stmt.setString(2, courseId);
            ArrayList<Integer> learn_list = new ArrayList<Integer>();
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                learn_list.add(rs.getInt("pre_list"));
            }

            //get pattern
            patterQuery.setString(1,courseId);
            ResultSet rs2 = patterQuery.executeQuery();
            String pattern=rs2.getString("pre_pattern");


            //TODO: test this
            String evaluate=String.format(pattern, learn_list.toArray());

            ScriptEngineManager manager=new ScriptEngineManager();
            ScriptEngine engine=manager.getEngineByName("js");
            int result=(Integer) engine.eval(evaluate);
            if(result==1){
                return true;
            }else {
                return false;
            }

        } catch (SQLException | ScriptException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Major getStudentMajor(int studentId) {
        return null;
    }
}
