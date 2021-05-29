package reference.service;

import cn.edu.sustech.cs307.dto.CourseSection;
import cn.edu.sustech.cs307.service.InstructorService;

import java.util.List;

public class ReferenceInstructorService implements InstructorService {
    @Override
    public void addInstructor(int userId, String firstName, String lastName) {

    }

    @Override
    public List<CourseSection> getInstructedCourseSections(int instructorId, int semesterId) {
        return null;
    }
}
