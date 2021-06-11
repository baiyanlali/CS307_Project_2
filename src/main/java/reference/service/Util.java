package reference.service;

import cn.edu.sustech.cs307.dto.Course;
import cn.edu.sustech.cs307.dto.CourseSection;
import cn.edu.sustech.cs307.dto.CourseSectionClass;
import cn.edu.sustech.cs307.dto.Instructor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Util {
    public static String getName(String first_name,String last_name){
        Pattern pattern=Pattern.compile("[a-zA-Z]*");
        boolean english_name = pattern.matcher(first_name).matches();
        if(english_name){
            return first_name.concat(" ").concat(last_name);
        }else{
            return first_name.concat(last_name);
        }
    }

    public static Course getCourse(String id,String name,int credit,int course_hour,int grade_type){
        Course c=new Course();
        c.id=id;
        c.name=name;
        c.credit=credit;
        c.classHour=course_hour;
        switch (grade_type){
            case 0:c.grading= Course.CourseGrading.PASS_OR_FAIL;break;
            case 1:c.grading= Course.CourseGrading.HUNDRED_MARK_SCORE;break;
        }
        return c;
    }

    public static CourseSection getCourseSection(int id,String name,int tot_capacity,int left_capacity){
        CourseSection cs=new CourseSection();
        cs.id=id;
        cs.name=name;
        cs.totalCapacity=tot_capacity;
        cs.leftCapacity=left_capacity;
        return cs;
    }

    public static CourseSectionClass getCourseSectionClass(int class_id,short begin,short end,String week_list,int day_of_week,int user_id,String first_name,String last_name,String location){
        CourseSectionClass csc=new CourseSectionClass();
        csc.id=class_id;
        csc.classBegin=begin;
        csc.classEnd=end;

        List<Short> weekOfList=new ArrayList<>();

        for (short i = 0; i < week_list.length(); i++) {
            if(week_list.charAt(i)=='1'){
                weekOfList.add((short) (i + 1));
            }
        }
        csc.weekList= (Set<Short>) weekOfList;
        csc.dayOfWeek= DayOfWeek.of(day_of_week);

        if(first_name!=null){
            Instructor ins=new Instructor();
            ins.id=user_id;
            ins.fullName=getName(first_name, last_name);
            csc.instructor=ins;
        }else{
            csc.instructor=null;
        }
        csc.location=location;

        return csc;
    }

    public static CourseSectionClass getCourseSectionClass(String data){
        String[] strs=data.split(",");
        int class_id=Integer.parseInt(strs[0]);
        short begin=Short.parseShort(strs[1]);
        short end=Short.parseShort(strs[2]);
        String week_list=strs[3];
        int day_of_week=Integer.parseInt(strs[4]);
        int user_id=Integer.parseInt(strs[5]);
        String first_name=strs[6];
        String last_name=strs[7];
        String location=strs[8];
        return getCourseSectionClass(class_id,begin,end,week_list,day_of_week,user_id,first_name,last_name,location);

    }

}
