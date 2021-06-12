package reference.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.prerequisite.AndPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.CoursePrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.OrPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.CourseService;

import javax.annotation.Nullable;
import java.sql.*;
import java.time.DayOfWeek;
import java.util.*;
import java.util.regex.*;

public class ReferenceCourseService implements CourseService {
    @Override
    public void addCourse(String courseId, String courseName, int credit, int classHour, Course.CourseGrading grading, @Nullable Prerequisite prerequisite) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();) {

            if(courseId==null||courseName==null||credit<=0||classHour<=0){
                throw new IntegrityViolationException();
            }
            PreparedStatement stmt = connection.prepareStatement("call add_course(?,?,?,?,?,?)");
            PreparedStatement pre_courses_list=connection.prepareStatement("insert into pre_courses(course_id, pre_course_id) VALUES (?,?)");
            stmt.setString(1,courseId);
            stmt.setString(2,courseName);
            stmt.setInt(3,credit);
            stmt.setInt(4,classHour);
            stmt.setInt(5,grading.equals(Course.CourseGrading.PASS_OR_FAIL)?0:1);
            if(prerequisite != null) {
                String expression = prerequisite.when(new Prerequisite.Cases<>() {
                    @Override
                    public String match(AndPrerequisite self) {
                        String[] children = self.terms.stream()
                                .map(term -> term.when(this))
                                .toArray(String[]::new);
                        return '(' + String.join(" && ", children) + ')';
                    }

                    @Override
                    public String match(OrPrerequisite self) {
                        String[] children = self.terms.stream()
                                .map(term -> term.when(this))
                                .toArray(String[]::new);
                        return '(' + String.join(" || ", children) + ')';
                    }

                    @Override
                    public String match(CoursePrerequisite self) {
                        return self.courseID;
                    }
                });


                String clean = expression.replaceAll("\\|\\||\\&\\&|\\(|\\)", "").trim();
                String[] nameList = clean.split("\\s+");

                String pattern = prerequisite.when(new Prerequisite.Cases<>() {
                    @Override
                    public String match(AndPrerequisite self) {
                        String[] children = self.terms.stream()
                                .map(term -> term.when(this))
                                .toArray(String[]::new);
                        return '(' + String.join(" & ", children) + ')';
                    }

                    @Override
                    public String match(OrPrerequisite self) {
                        String[] children = self.terms.stream()
                                .map(term -> term.when(this))
                                .toArray(String[]::new);
                        return '(' + String.join(" | ", children) + ')';
                    }

                    @Override
                    public String match(CoursePrerequisite self) {
                        return "%s";
                    }
                });

                stmt.setString(6, pattern);
                stmt.execute();

                for (int i = 0; i < nameList.length; i++) {
                    pre_courses_list.setString(1, courseId);
                    pre_courses_list.setString(2, nameList[i].trim());        //remove white space
                    pre_courses_list.execute();
                }
            }else {
                stmt.setNull(6, Types.NULL);
                stmt.execute();
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new IntegrityViolationException();
        }
    }

    //Use function
    @Override
    public int addCourseSection(String courseId, int semesterId, String sectionName, int totalCapacity) {
        if(courseId.equals("")||semesterId<=0||sectionName==null||totalCapacity<=0){
            throw new IntegrityViolationException();
        }
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select add_section(?,?,?,?) as sem_id")) {
            stmt.setString(1,courseId);
            stmt.setInt(2,semesterId);
            stmt.setString(3,sectionName);
            stmt.setInt(4,totalCapacity);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                int sem_id=rs.getInt("sem_id");
                connection.close();
                return sem_id;
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IntegrityViolationException();
        }
        return -1;
    }

    public Comparator<Short> cmp=new Comparator<Short>() {
        @Override
        public int compare(Short o1, Short o2) {
            return o1-o2;
        }
    };

    @Override
    public int addCourseSectionClass(int sectionId, int instructorId, DayOfWeek dayOfWeek, Set<Short> weekList, short classStart, short classEnd, String location) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {
            PreparedStatement stmt = connection.prepareStatement("select add_class(?,?,?,?,?,?,?)");


            // parts to get week count
            PreparedStatement myWeeks=connection.prepareStatement("select howmany_weeks(?)");
            myWeeks.setInt(1,sectionId);
            ResultSet weekRs=myWeeks.executeQuery();
            int howManyWeeks=0;
            if(weekRs.next()){
                howManyWeeks=weekRs.getInt("howmany_weeks");
            }

            stmt.setInt(1,sectionId);
            stmt.setInt(2,instructorId);
            stmt.setInt(3,dayOfWeek.getValue());
            StringBuffer week=new StringBuffer();
            ArrayList<Short> weekLists=new ArrayList<>(weekList);
//            weekLists.addAll(Arrays.asList((Short[]) weekList.toArray()));
            weekLists.sort(cmp);
            howManyWeeks=32; //TODO
            int index=0;
            for (int i = 1; i <= Math.max(howManyWeeks,weekLists.get(weekLists.size()-1)); i++) {
                if(index<weekLists.size() && i==weekLists.get(index)){  //protect the margin case
                    week.append(1);
                    index++;
                }else{
                    week.append(0);
                }
            }
            stmt.setString(4,week.toString());
            stmt.setInt(5,classStart);
            stmt.setInt(6,classEnd);

            stmt.setString(7,location);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                int add_class=rs.getInt("add_class");
                connection.close();
                return add_class;
            }else
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IntegrityViolationException();
        }
        return 1;
    }

    @Override
    public void removeCourse(String courseId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection())
        {
            PreparedStatement p1=connection.prepareStatement("select sec_id from section where course_id=?");
            p1.setString(1,courseId);
            ResultSet rs= p1.executeQuery();
            while (rs.next()){
                int secId = rs.getInt("sec_id");
                removeCourseSection(secId);
            }

            Statement stat= connection.createStatement();
            stat.execute(String.format("delete from pre_courses where course_id = \'%s\'",courseId));
            stat.execute(String.format("delete from major_course where course_id = \'%s\'",courseId));


            PreparedStatement stmt = connection.prepareStatement("delete from course where course_id=?");
            stmt.setString(1, courseId);
            stmt.execute();

            stmt=connection.prepareStatement("commit; ");
            stmt.execute();
            connection.close();
            } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void removeCourseSection(int sectionId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection())
        {
            PreparedStatement p1=connection.prepareStatement("select class_id from class where sec_id=?");
            p1.setInt(1,sectionId);
            ResultSet rs= p1.executeQuery();
            while (rs.next()){
                int classID = rs.getInt("class_id");
                removeCourseSectionClass(classID);
            }

            Statement stat= connection.createStatement();
            stat.execute(String.format("delete from learning_info where sec_id = \'%s\'",sectionId));

            PreparedStatement stmt=connection.prepareStatement("delete from section where sec_id=?");
            stmt.setInt(1,sectionId);
            stmt.execute();


            stmt=connection.prepareStatement("commit; ");
            stmt.execute();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeCourseSectionClass(int classId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection())
        {
            PreparedStatement stmt = connection.prepareStatement("delete from teaching_info where class_id=?");
            stmt.setInt(1,classId);
            stmt.execute();



            stmt=connection.prepareStatement("delete from class where class_id=?");
            stmt.setInt(1,classId);
            stmt.execute();

            stmt=connection.prepareStatement("commit; ");
            stmt.execute();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Course> getAllCourses() {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select * from course")) {
            ResultSet rs = stmt.executeQuery();
            List<Course> con=new ArrayList<>();
            while (rs.next()){
                String course_id=   rs.getString("course_id");
                String course_name= rs.getString("course_name");
                int credit=         rs.getInt("credit");
                int course_hour=    rs.getInt("course_hour");
                //0 is Pass or Fail, 1 is Hundred
                int grade_type =    rs.getInt("grade_type");
//                Course c1=new Course();
                Course c1=Util.getCourse(course_id,course_name,credit,course_hour,grade_type);
//                c1.credit=credit;
//                c1.classHour=course_hour;
//                c1.id=course_id;
//                c1.name=course_name;
//                switch (grade_type){
//                    case 0:c1.grading= Course.CourseGrading.PASS_OR_FAIL;break;
//                    case 1:c1.grading= Course.CourseGrading.HUNDRED_MARK_SCORE;break;
//                }

                con.add(c1);
            }
            connection.close();
            return con;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<CourseSection> getCourseSectionsInSemester(String courseId, int semesterId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {
            PreparedStatement stmt = connection.prepareStatement("select sec_id,sec_name,tot_capacity,left_capacity from section where course_id=? and semester_id=?");
            stmt.setString(1,courseId);
            stmt.setInt(2,semesterId);
            ResultSet rs = stmt.executeQuery();
            List<CourseSection> con=new ArrayList<>();
            while (rs.next()){
                try{
                    int sec_id=rs.getInt("sec_id");
                    String sec_name=rs.getString("sec_name");
                    int tot_capacity=rs.getInt("tot_capacity");
                    int left_capacity=rs.getInt("left_capacity");
                    CourseSection c1=Util.getCourseSection(sec_id,sec_name,tot_capacity,left_capacity);
                    con.add(c1);

                }catch (SQLException e){
                    throw new EntityNotFoundException();
                }
            }
            connection.close();
            if(!con.isEmpty())
                return con;
            else {
                connection.close();
                throw new EntityNotFoundException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Course getCourseBySection(int sectionId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {
            PreparedStatement stmt = connection.prepareStatement("select course_id from section where sec_id=?");
            stmt.setInt(1,sectionId);
                ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                //TODO: TOOO SLOWWWWW!!!
                String course_id = rs.getString("course_id");
                stmt=connection.prepareStatement("select * from course where course_id=?");
                stmt.setString(1,course_id);
                rs = stmt.executeQuery();
                if(rs.next()) {
                    String course_name = rs.getString("course_name");
                    int credit = rs.getInt("credit");
                    int course_hour = rs.getInt("course_hour");
                    //0 is Pass or Fail, 1 is Hundred
                    int grade_type = rs.getInt("grade_type");
//                    Course c1 = new Course();
                    Course c1 = Util.getCourse(course_id,course_name,credit,course_hour,grade_type);
                    connection.close();
                    return c1;
                }
                else{
                    connection.close();
                    throw new EntityNotFoundException();
                }

            }else{
                throw new EntityNotFoundException();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<CourseSectionClass> getCourseSectionClasses(int sectionId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {
            PreparedStatement stmt = connection.prepareStatement(
                    "select *\n" +
                        "from class\n" +
                        "left join teaching_info ti on class.class_id = ti.class_id\n" +
                        "join location l on l.location_id = class.loc_id\n" +
                        "left join users u on u.id = ti.instructor_id\n" +
                        "where sec_id=?;");
            stmt.setInt(1,sectionId);
            ResultSet rs = stmt.executeQuery();
            List<CourseSectionClass> con=new ArrayList<>();
            while (rs.next()){
                try{

                    int class_id=rs.getInt("class_id");
                    short class_end= (short) rs.getInt("class_end");
                    short class_begin= (short) rs.getInt("class_begin");
                    String week_list=rs.getString("week_list");
                    int day_of_week=rs.getInt("day_of_week");
                    int user_id=rs.getInt("id");
                    String first_name=rs.getString("first_name");
                    String last_name=rs.getString("last_name");
                    String location=rs.getString("loc");
                    CourseSectionClass c1=Util.getCourseSectionClass(class_id,class_begin,class_end,week_list,day_of_week,user_id,first_name,last_name,location);


                    con.add(c1);
                }catch (SQLException e){
                    throw new EntityNotFoundException();
                }
            }
            if(!con.isEmpty()){
                connection.close();
                return con;
            }
            else {
                connection.close();
                throw new EntityNotFoundException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CourseSection getCourseSectionByClass(int classId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()) {
            PreparedStatement stmt = connection.prepareStatement(
                    "select section.sec_id,sec_name,tot_capacity,left_capacity\n" +
                    "from section\n" +
                    "join\n" +
                    "(select sec_id\n" +
                    "from class\n" +
                    "where class_id=?)p\n" +
                    "on p.sec_id=section.sec_id");

            stmt.setInt(1,classId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                try{
                    int sec_id=rs.getInt("sec_id");
                    String sec_name=rs.getString("sec_name");
                    int tot_capacity=rs.getInt("tot_capacity");
                    int left_capacity=rs.getInt("left_capacity");
                    CourseSection c1=Util.getCourseSection(sec_id,sec_name,tot_capacity,left_capacity);
                    connection.close();
                    return c1;

                }catch (SQLException e){
                    throw new EntityNotFoundException();
                }

            }else{
                connection.close();
                throw new EntityNotFoundException();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Student> getEnrolledStudentsInSemester(String courseId, int semesterId) {
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection()){
            PreparedStatement stmt = connection.prepareStatement(
                        "select  case m.major_id when null then false else true end as has_major  ,id,first_name,last_name,enroll_date,major_name,m.major_id,d.dept_id,dept_name\n" +
                                "from section as s\n" +
                                "left join learning_info li on s.sec_id = li.sec_id\n" +
                                "left join users u on u.id = li.sid\n" +
                                "left join student_info si on u.id = si.sid\n" +
                                "left join major m on si.major_id = m.major_id\n" +
                                "left join department d on m.dept_id = d.dept_id\n" +
                                "where course_id=? and semester_id=?");
            stmt.setString(1,courseId);
            stmt.setInt(2,semesterId);
            ResultSet rs=stmt.executeQuery();

            List<Student> con=new ArrayList<>();
            while (rs.next()){
                Student s=new Student();
                boolean has_major=rs.getBoolean("has_major");
                s.id=rs.getInt("id");
                String first_name=rs.getString("first_name");
                String last_name=rs.getString("last_name");
                s.fullName = Util.getName(first_name,last_name);


                s.enrolledDate=rs.getDate("enroll_date");

                if(has_major){
                    Department d=new Department();
                    d.id=rs.getInt("dept_id");
                    d.name=rs.getString("dept_name");
                    Major m=new Major();
                    m.department=d;
                    m.name=rs.getString("major_name");
                    m.id=rs.getInt("major_id");
                    s.major=m;
                }else{
                    s.major=null;
                }
                con.add(s);
            }
            connection.close();
            if(!con.isEmpty()) {
                return con;
            }
            else {
                throw new EntityNotFoundException();
            }
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
