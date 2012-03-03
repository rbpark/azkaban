package azkaban.jobmanager;

import java.util.ArrayList;

public class Project {
    private static final String GROUP_TYPE = "GROUP";
    private static final String USER_TYPE = "USER"; 
    
    private String name;
    private String uploaderId;
    private ArrayList<String> readerPermission;
    private ArrayList<String> writerPermission;

    private class Permission {
        String type;
        String id;
    }
}

