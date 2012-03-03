package azkaban.jobmanager;

import java.util.List;

public interface ProjectManager {
    public Project getProject(String id);

    public List<Project> getAllProjects();

    public List<Project> getProjectByVisibleToUser(String id);
}
