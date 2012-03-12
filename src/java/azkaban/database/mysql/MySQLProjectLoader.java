package azkaban.database.mysql;

import java.io.File;
import java.util.Map;

import azkaban.project.Project;
import azkaban.project.ProjectLoader;
import azkaban.utils.Props;

public class MySQLProjectLoader implements ProjectLoader {
    public MySQLProjectLoader(Props props) {
        MySQLConnection.init(props);
    }

    @Override
    public Map<String, Project> loadAllProjects() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addProject(Project project, File directory) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean removeProject(Project project) {
        // TODO Auto-generated method stub
        return false;
    }

}
