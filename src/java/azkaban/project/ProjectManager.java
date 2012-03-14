package azkaban.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import azkaban.flow.FlowUtil;
import azkaban.utils.Props;

public class ProjectManager {
    private static final Logger logger = Logger
            .getLogger(ProjectManager.class);
    private static final String GLOBAL_PROJECT_PROPERTIES = "project.global.properties";
    private static final String PROJECT_LOADER_CLASS_PARAM = "project.loader.class";
    private ConcurrentHashMap<String, Project> projects = new ConcurrentHashMap<String, Project>();
    private ProjectLoader projectLoader;
    private FlowUtil flowUtil;

    public ProjectManager(Props props) throws Exception {
        loadLoader(props);
        flowUtil = new FlowUtil();
    }

    private void loadLoader(Props props) throws Exception {
        Class<?> projectLoaderClass = props.getClass(PROJECT_LOADER_CLASS_PARAM,null);
        ProjectLoader loader = null;
        if (projectLoaderClass != null && projectLoaderClass.getConstructors().length > 0 ) {
            loader = (ProjectLoader)projectLoaderClass.getConstructors()[0].newInstance();
            loader.init(props);
        }
        else {
            loader = new FileProjectLoader();
            loader.init(props);
        }
        
        this.projectLoader = loader;
    }
    
    public void loadprojects() {
        
    }

    public List<String> getProjectNames() {
        return new ArrayList<String>(projects.keySet());
    }

    public void installProject(String name, File localDir, String uploader, boolean overwrite) throws IOException {
        Project oldProject = projects.get(name);
        
        if (oldProject != null) {
            if (oldProject.obtainLock(uploader)) {
                // Move projects
                flowUtil.loadAllFlowsFromDir(localDir, parentProps);
                projectLoader.addProject(oldProject, localDir);
                oldProject.releaseLock();
            }
            else {
                Project other = projects.get(name);
                throw new IOException("Project " + name + " is currently being locked by " + oldProject.getLockUser());
            }
        }
        else {
            // Lock table as little as possible.
            synchronized(projects) {
                oldProject = projects.get(name);
                if (oldProject == null) {
                    oldProject = new Project(name);
                    oldProject.setUploaderName(uploader);
                    projects.put(name, oldProject);
                }
                else {
                    Project other = projects.get(name);
                    throw new IOException("Project " + name + " is out of sync. Overwritten by " + other.getUploaderName());
                }
            }

            if (oldProject.obtainLock(uploader)) {
                // Move projects
                
                oldProject.releaseLock();
            }
            else {
                Project other = projects.get(name);
                throw new IOException("Project " + name + " is currently being locked by " + oldProject.getLockUser());
            }
        }
    }
}