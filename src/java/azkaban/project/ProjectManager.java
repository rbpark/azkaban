package azkaban.project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectManager {
    private HashMap<String, Project> projects = new HashMap<String, Project>();
    private ProjectLoader loader;
    
    public ProjectManager(ProjectLoader loader) {
        this.loader = loader;
    }

    public void loadprojects() {
        
    }

    public List<String> getProjectNames() {
        return new ArrayList<String>(projects.keySet());
    }

    public void addProject(String name, File localDir, String uploader, boolean overwrite) {
        
    }
}