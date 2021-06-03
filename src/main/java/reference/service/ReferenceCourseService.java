package reference.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Course;
import cn.edu.sustech.cs307.dto.CourseSection;
import cn.edu.sustech.cs307.dto.CourseSectionClass;
import cn.edu.sustech.cs307.dto.Student;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.service.CourseService;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.List;

public class ReferenceCourseService implements CourseService {
    @Override
    public void addCourse(String courseId, String courseName, int credit, int classHour, Course.CourseGrading grading, @Nullable Prerequisite prerequisite) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("insert course(course_id,course_name,credit,course_hour)value (?,?,?,?)")) {
            stmt.setString(1,courseId);
            stmt.setString(2,courseName);
            stmt.setInt(3,credit);
            stmt.setInt(4,classHour);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Use function
    @Override
    public int addCourseSection(String courseId, int semesterId, String sectionName, int totalCapacity) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select add_course_section(?,?,?,?)")) {
            stmt.setString(1,courseId);
            stmt.setInt(2,semesterId);
            stmt.setString(3,sectionName);
            stmt.setInt(4,totalCapacity);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("sem_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    @Override
    public int addCourseSectionClass(int sectionId, int instructorId, DayOfWeek dayOfWeek, List<Short> weekList, short classStart, short classEnd, String location) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select add_course_section_class(?,?,?,?,?)")) {
            stmt.setInt(1,sectionId);
            stmt.setInt(2,dayOfWeek.getValue());
            StringBuffer week=new StringBuffer();
            int index=0;
            for (int i = 1; i <= weekList.get(weekList.size()-1); i++) {
                if(i==weekList.get(index)){
                    week.append(1);
                    index++;
                }else{
                    week.append(0);
                }
            }
            stmt.setString(3,week.toString());
            stmt.setInt(4,classStart);
            stmt.setInt(5,classEnd);

            stmt.setString(6,location);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("class_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    @Override
    public void removeCourse(String courseId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection())
        {
            PreparedStatement p1=connection.prepareStatement("select sec_id from section where course_id=?");
            ResultSet rs= p1.executeQuery();
            while (rs.next()){
                int secId = rs.getInt("sec_id");
                removeCourseSection(secId);
            }

            p1.execute(("delete from pre_courses where course_id = ".concat(courseId)));
            p1.execute(("delete from major_course where course_id = ".concat(courseId)));
            p1.execute(("delete from learning_info where course_id = ".concat(courseId)));


            PreparedStatement stmt = connection.prepareStatement("delete from course where course_id=?");
            stmt.setString(1, courseId);
            stmt.execute();

            stmt.execute("commit");
            } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void removeCourseSection(int sectionId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection())
        {
            PreparedStatement p1=connection.prepareStatement("select class_id from class where sec_id=?");
            ResultSet rs= p1.executeQuery();
            while (rs.next()){
                int classID = rs.getInt("class_id");
                removeCourseSectionClass(classID);
            }
            PreparedStatement stmt=connection.prepareStatement("delete from section where sec_id=?");
            stmt.setInt(1,sectionId);
            stmt.execute();
            stmt.execute("commit");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeCourseSectionClass(int classId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection())
        {
            PreparedStatement stmt = connection.prepareStatement("delete from location where class_id=?");
            stmt.setInt(1,classId);
            stmt.execute();
            stmt=connection.prepareStatement("delete from teaching_info where class_id=?");
            stmt.setInt(1,classId);
            stmt.execute();
            stmt=connection.prepareStatement("delete from class where class_id=?");
            stmt.setInt(1,classId);
            stmt.execute();
            stmt.execute("commit");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Course> getAllCourses() {

        return null;
    }

    @Override
    public List<CourseSection> getCourseSectionsInSemester(String courseId, int semesterId) {
        return null;
    }

    @Override
    public Course getCourseBySection(int sectionId) {
        return null;
    }

    @Override
    public List<CourseSectionClass> getCourseSectionClasses(int sectionId) {
        return null;
    }

    @Override
    public CourseSection getCourseSectionByClass(int classId) {
        return null;
    }

    @Override
    public List<Student> getEnrolledStudentsInSemester(String courseId, int semesterId) {
        return null;
    }
}
