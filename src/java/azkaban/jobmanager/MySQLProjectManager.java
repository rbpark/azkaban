package azkaban.jobmanager;

import java.util.List;

import azkaban.database.MySQLConnection;
import azkaban.utils.Props;

public class MySQLProjectManager implements ProjectManager {
    public MySQLProjectManager(Props props) {
        MySQLConnection.init(props);
    }

    private void setup() {
        MySQLConnection conn = MySQLConnection.getInstance();
        
    }
    
    @Override
    public Project getProject(String id) {
        
        return null;
    }

    @Override
    public List<Project> getAllProjects() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Project> getProjectByVisibleToUser(String id) {
        // TODO Auto-generated method stub
        return null;
    }

}
