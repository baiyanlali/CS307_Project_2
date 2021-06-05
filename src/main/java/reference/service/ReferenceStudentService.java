package reference.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.grade.Grade;
import cn.edu.sustech.cs307.dto.grade.HundredMarkGrade;
import cn.edu.sustech.cs307.dto.grade.PassOrFailGrade;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.service.StudentService;

import javax.annotation.Nullable;
import java.sql.*;
import java.sql.Date;
import java.time.DayOfWeek;
import java.util.*;
import java.util.ArrayList;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ReferenceStudentService implements StudentService {
    @Override
    public void addStudent(int userId, int majorId, String firstName, String lastName, Date enrolledDate) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select addstudent(?,?,?,?,?) ")) {
            stmt.setInt(1, userId);
            stmt.setInt(2, majorId);
            stmt.setString(3, firstName);
            stmt.setString(4,lastName);
            stmt.setDate(5,enrolledDate);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<CourseSearchEntry> searchCourse(
            int studentId,                          int semesterId,
            @Nullable String searchCid,             @Nullable String searchName,        @Nullable String searchInstructor,
            @Nullable DayOfWeek searchDayOfWeek,    @Nullable Short searchClassTime,    @Nullable List<String> searchClassLocations,
            CourseType searchCourseType,            boolean ignoreFull,                 boolean ignoreConflict,
            boolean ignorePassed,                   boolean ignoreMissingPrerequisites, int pageSize,
            int pageIndex
    ) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select searchCourse(?,?,?,?) ")) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, semesterId);
            ResultSet rs = stmt.executeQuery();

            List<CourseSearchEntry> con=new ArrayList<>();

            CourseSearchEntry cse=null;
            Course c;
            CourseSection cs;
            List<CourseSectionClass> courseSectionClasses=null;

            int sec_id=-1;
            while (rs.next()){
                    //start new value

                    cse=new CourseSearchEntry();

                    //Create Course
                    String  c_id            =   rs.getString(   "course_id");
                    String  c_name          =   rs.getString(   "course_name");
                    int     credit          =   rs.getInt(      "credit");
                    int     course_hour     =   rs.getInt(      "course_hour");
                    int     grade_type      =   rs.getInt(      "grade_type");
                    c=Util.getCourse(
                            c_id,
                            c_name,
                            credit,
                            course_hour,
                            grade_type
                    );
                    cse.course=c;

                    //Create Course Section

                    sec_id                  =       rs.getInt(      "sec_id");
                    String  name            =       rs.getString(   "sec_name");
                    int     tot_capacity    =       rs.getInt(      "tot_capacity");
                    int     left_capacity   =       rs.getInt(      "left_capacity");
                    cs=Util.getCourseSection(sec_id,name,tot_capacity,left_capacity);
                    cse.section=cs;

                    //Create a new List
                    courseSectionClasses=new ArrayList<>();

                    List<String> conflictedCourses=new ArrayList<>();

                    cse.sectionClasses=courseSectionClasses;

                    conflictedCourses.addAll(Arrays.asList((String[]) rs.getArray("conflicted_courses").getArray()));




//                int     class_id        =       rs.getInt(      "class_id");
//                short   begin           =       rs.getShort(    "begin");
//                short   end             =       rs.getShort(    "end");
//                String  week_list       =       rs.getString(   "week_list");
//                int     day_of_week     =       rs.getInt(      "day_of_week");
//                int     user_id         =       rs.getInt(      "user_id");
//                String  first_name      =       rs.getString(   "first_name");
//                String  last_name       =       rs.getString(   "last_name");
//                String  location        =       rs.getString(   "location");
//                CourseSectionClass csc=Util.getCourseSectionClass(
//                        class_id,
//                        begin,
//                        end,
//                        week_list,
//                        day_of_week,
//                        user_id,
//                        first_name,
//                        last_name,
//                        location
//                );

//                courseSectionClasses.add(csc);
            }
            //no need to throw exception
            return con;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public EnrollResult enrollCourse(int studentId, int sectionId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select COURSE_FOUND(?) ")) {
            boolean judge=false;
            EnrollResult ans;
            stmt.setInt(1, sectionId);
            stmt.execute();
            ResultSet rs = stmt.executeQuery();
            judge=rs.next();
             if(!judge){
                 ans=EnrollResult.COURSE_NOT_FOUND;
                 return ans;
             }
            PreparedStatement stmt2=connection.prepareStatement("select ALREADY_ENROLLED(?,?) as judge");
            stmt2.setInt(1, sectionId);
            stmt2.setInt(2, studentId);
            stmt2.execute();
            rs = stmt2.executeQuery();
            if(rs.next()) {
                judge = rs.getBoolean("judge");
                if (judge) {
                    ans = EnrollResult.ALREADY_ENROLLED;
                    return ans;
                }
            }
            PreparedStatement stmt3=connection.prepareStatement("select ALREADY_PASSED(?,?) as judge");
            stmt3.setInt(1, sectionId);
            stmt3.setInt(2, studentId);
            stmt3.execute();
            rs = stmt3.executeQuery();
            if(rs.next()) {
                judge = rs.getBoolean("judge");
                if (judge) {
                    ans = EnrollResult.ALREADY_PASSED;
                    return ans;
                }
            }
            PreparedStatement stmt4=connection.prepareStatement("select COURSE_CONFLICT_FOUND(?,?) as judge");
            stmt4.setInt(1, sectionId);
            stmt4.setInt(2, studentId);
            stmt4.execute();
            rs = stmt4.executeQuery();
            if(rs.next()){
                judge=rs.getBoolean("judge");
                if(judge){
                    ans=EnrollResult.COURSE_CONFLICT_FOUND;
                    return ans;
                }
            }

            PreparedStatement stmt5=connection.prepareStatement("select getCoursebySection(?) as trans");
            stmt5.setInt(1, sectionId);
            stmt5.execute();
            rs = stmt5.executeQuery();
            if(rs.next()){
                String courseid=rs.getString("trans");
                judge=passedPrerequisitesForCourse(studentId,courseid);
                if(!judge){
                    ans=EnrollResult.PREREQUISITES_NOT_FULFILLED;
                    return ans;
                }
            }
//            boolean judge1=passedPrerequisitesForCourse(studentId,)

            PreparedStatement stmt1=connection.prepareStatement("select COURSE_IS_FULL(?) as judge");
            stmt1.setInt(1, sectionId);
            stmt1.execute();
            rs = stmt1.executeQuery();
            if(rs.next()) {
                judge=rs.getBoolean("judge");
                if (judge) {
                    ans = EnrollResult.COURSE_IS_FULL;
                    return ans;
                }
            }
            PreparedStatement stmt6=connection.prepareStatement("select enrollCourse(?,?) ");
            stmt6.setInt(1, studentId);
            stmt6.setInt(2, sectionId);
            stmt6.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void dropCourse(int studentId, int sectionId) throws IllegalStateException {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select drop_course(?, ?) as success")) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                boolean success=rs.getBoolean("success");
                if(!success){
                    throw new IllegalStateException();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addEnrolledCourseWithGrade(int studentId, int sectionId, @Nullable Grade grade) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select addEnrolledCourseWithGrade(?, ?, ?)")) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            if(grade!=null){
                String g = grade.when(new Grade.Cases<String>() {
                    @Override
                    public String match(PassOrFailGrade self) {
                        if(self==PassOrFailGrade.PASS)
                            return "p";
                        else
                            return "f";
                    }

                    @Override
                    public String match(HundredMarkGrade self) {
                        return String.valueOf(self.mark);
                    }
                });
                stmt.setString(3,g);
            }
            else
                stmt.setString(3,null);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setEnrolledCourseGrade(int studentId, int sectionId, Grade grade) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("call setEnrolledCourseWithGrade(?, ?, ?)")) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            String g = grade.when(new Grade.Cases<String>() {
                    @Override
                    public String match(PassOrFailGrade self) {
                        if(self==PassOrFailGrade.PASS)
                            return "p";
                        else
                            return "f";
                    }

                    @Override
                    public String match(HundredMarkGrade self) {
                        return String.valueOf(self.mark);
                    }
                });
            stmt.setString(3,g);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<Course, Grade> getEnrolledCoursesAndGrades(int studentId, @Nullable Integer semesterId) {
        Map<Course,Grade> a=new HashMap<>();
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("call getEnrolledCoursesAndGrades(?, ?)")) {
            ResultSet rs = stmt.executeQuery();
            stmt.setInt(1, studentId);
//            if(semesterId !=null) {
//                String g = grade.when(new Grade.Cases<String>() {
//                    @Override
//                    public String match(PassOrFailGrade self) {
//                        if (self == PassOrFailGrade.PASS)
//                            return "p";
//                        else
//                            return "f";
//                    }
//
//                    @Override
//                    public String match(HundredMarkGrade self) {
//                        return String.valueOf(self.mark);
//                    }
//                });
//                stmt.execute();
//
//            }else{
//                stmt.setString(2, null);
//                stmt.execute();
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CourseTable getCourseTable(int studentId, Date date) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("call getCourseTable(?, ?)")) {
            stmt.setInt(1, studentId);
            stmt.setDate(2, date);
            ResultSet rs = stmt.executeQuery();
            List<CourseTable.CourseTableEntry>[] entries=new List[7];
            for (int i = 0; i < 7; i++) {
                entries[i]=new ArrayList<>();
            }
            CourseTable ct=new CourseTable();
            Map<DayOfWeek,List<CourseTable.CourseTableEntry>> mappp=new HashMap<>();
            while (rs.next()) {
                String coursename = rs.getString("course_name");
                Instructor ins=new Instructor();
                String instructorName=rs.getString("Instructor");
                int instructorId=rs.getInt("InstructorId");
                ins.fullName=instructorName;
                ins.id=instructorId;
                int classbegin=rs.getInt("class_begin");
                int classend=rs.getInt("class_end");
                String location=rs.getString("loc");
                int day=rs.getInt("day_of_week");
                CourseTable.CourseTableEntry cte=new CourseTable.CourseTableEntry();
                cte.courseFullName=coursename;
                cte.instructor=ins;
                cte.classBegin=(short)classbegin;
                cte.classEnd=(short)classend;
                cte.location=location;
                entries[day].add(cte);
            }
            for(int i=0;i<7;i++){
                DayOfWeek dow=DayOfWeek.of(i);
                mappp.put(dow,entries[i]);
            }
            ct.table=mappp;
            return ct;
        }catch (SQLException e) {
            e.printStackTrace();
        }
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

        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {
            PreparedStatement stmt = connection.prepareStatement("select m.dept_id, dept_name,m.major_id, major_name\n" +
                    "            from department\n" +
                    "            join major m on department.dept_id = m.dept_id\n" +
                    "            join student_info si on m.major_id = si.major_id\n" +
                    "            where sid=?;");

            stmt.setInt(1,studentId);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                Department d=new Department();
                Major m=new Major();
                m.name=rs.getString("major_name");
                m.id=rs.getInt("major_id");
                d.name=rs.getString("dept_name");
                d.id=rs.getInt("dept_id");
                m.department=d;
                return m;
            }else{
                throw new EntityNotFoundException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }
}
