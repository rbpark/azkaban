package azkaban.flow;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import azkaban.utils.Props;

public class FlowManager {
    private static final Logger logger = Logger.getLogger(FlowManager.class);
    private static final String PROPERTIES = ".properties";
    private static final String JOBS = ".jobs";
    private static final String DEPENDENCIES = "dependencies";
    private Props parentProps = null;

    public FlowManager(Props props) {

    }

    public List<String> loadAllFlowsFromDir(File dir) throws IOException {
        logger.info("Loading flows from " + dir.getPath());
        if (!dir.exists()) {
            throw new IOException("Directory " + dir.getPath() + " doesn't exist.");
        }
        else if (!dir.isDirectory()) {
            throw new IOException("Path " + dir.getPath() + " isn't a directory");
        }

        HashMap<String, Node> nodes = new HashMap<String, Node>();
        ArrayList<String> errors = new ArrayList<String>();
        loadJobsFromDir(dir, parentProps, nodes);
        if (nodes.size() == 0) {
            errors.add("No jobs found.");
            return errors;
        }

        setupDependencies(nodes, errors);
        ArrayList<Node> headJobs = findHeadJobs(nodes);

        if (headJobs.size() == 0) {
            errors.add("There are no starting dependency. Is there a cyclical dependency?");
        }

        ArrayList<Flow> flows = new ArrayList<Flow>();
        for (Node head: headJobs) {
            Flow flow = new Flow(head.getId());
            HashSet<String> nameStack = new HashSet<String>();
            createFlow(head, flow, nameStack);
            flows.add(flow);
        }
 
        return errors;
    }

    private void createFlow(Node node, Flow flow, HashSet<String> nameStack) {
        flow.addNode(node);
        nameStack.add(node.getId());

        for (Edge edge: node.getInEdges()) {
            flow.addEdge(edge);
            Node from = edge.getFrom();
            if (nameStack.contains(from.getId())) {
                edge.setState(Edge.State.CYCLE);
                flow.addErrors("Cycle detected.");
            }
            else {
                createFlow(from, flow, nameStack);
            }
        }

        nameStack.remove(node.getId());
    }

    private void setupDependencies(HashMap<String, Node> nodes, List<String> errors) {
        for(Node toNode : nodes.values()) {
            Props nodeProps = toNode.getProps();
            List<String> dependencies = nodeProps.getStringList(DEPENDENCIES);
            HashSet<String> dedupedDependencies = new HashSet<String>(dependencies);
            
            for (String dependency: dedupedDependencies) {
                Node fromNode = nodes.get(dependency);
                if (fromNode == null) {
                    String error = "Missing dependency: Job " + toNode.getId() + " depends on " + dependency;
                    logger.info("Error found: " + error);
                    errors.add(error);
                    toNode.addMissingDependency(dependency);
                }
                else {
                    Edge edge = new Edge(fromNode, toNode);
                    toNode.addInEdges(edge);
                    fromNode.addOutEdges(edge);
                }
            }
        }
    }

    private ArrayList<Node> findHeadJobs(HashMap<String, Node> nodes) {
        ArrayList<Node> headJobs = new ArrayList<Node>();

        for (Node node : nodes.values()) {
            if (node.getOutEdges().size() == 0) {
                headJobs.add(node);
            }
        }

        return headJobs;
    }

    private void loadJobsFromDir(File dir, Props parent, Map<String, Node> jobNodes) throws IOException {
        File[] propertyFiles = dir.listFiles(new ExtensionFileFilter(PROPERTIES));
        Props currentParent = parent;
        for (File file: propertyFiles) {
            logger.info("Loading property from " + file.getPath());
            currentParent = new Props(currentParent, file);
        }

        File[] jobFiles = dir.listFiles(new ExtensionFileFilter(JOBS));
        for (File file: jobFiles) {
            logger.info("Loading job from " + file.getPath());
            String filename = file.getName();
            String jobName = filename.substring(0, filename.length() - JOBS.length());
            if (jobNodes.containsKey(jobNodes)) {
                throw new IOException("Job " + jobName + " already exists. Cannot have two jobs of the same name in the same project.");
            }
            
            Props job = new Props(currentParent, file);
            jobNodes.put(jobName, new Node(jobName, job));
        }
        
        File[] subDirectories = dir.listFiles(new DirectoryFileFilter());
        for (File file: subDirectories) {
            logger.info("Recursing into directory " + file.getPath());
            loadJobsFromDir(file, currentParent, jobNodes);
        }
    }

    private class ExtensionFileFilter implements FileFilter {
        private final String extension;
        public ExtensionFileFilter(String extension) {
            this.extension = extension;
        }

        @Override
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(extension);
        }
    }
    
    private class DirectoryFileFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    }
}
