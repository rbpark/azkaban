package azkaban.project;

import java.io.File;
import java.util.Map;

public interface ProjectLoader {
    public Map<String, Project> loadAllProjects();

    public void addProject(Project project, File directory);

    public boolean removeProject(Project project);
}
