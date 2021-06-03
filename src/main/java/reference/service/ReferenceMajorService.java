package reference.service;

import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.service.MajorService;

import java.util.List;

public class ReferenceMajorService implements MajorService {
    @Override
    public int addMajor(String name, int departmentId) {
        return 0;
    }

    @Override
    public void removeMajor(int majorId) {

    }

    @Override
    public List<Major> getAllMajors() {
        return null;
    }

    @Override
    public Major getMajor(int majorId) {
        return null;
    }

    @Override
    public void addMajorCompulsoryCourse(int majorId, String courseId) {

    }

    @Override
    public void addMajorElectiveCourse(int majorId, String courseId) {

    }
}
