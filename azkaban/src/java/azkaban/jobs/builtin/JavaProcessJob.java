/*
 * Copyright 2010 LinkedIn, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package azkaban.jobs.builtin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import azkaban.app.JobDescriptor;

public class JavaProcessJob extends ProcessJob {
    private static final Logger log = Logger
            .getLogger(JavaProcessJob.class);
    
	public static final String CLASSPATH = "classpath";
	public static final String GLOBAL_CLASSPATH = "global.classpaths";
	public static final String JAVA_CLASS = "java.class";
	public static final String INITIAL_MEMORY_SIZE = "Xms";
	public static final String MAX_MEMORY_SIZE = "Xmx";
	public static final String MAIN_ARGS = "main.args";
	public static final String JVM_PARAMS = "jvm.args";
    public static final String GLOBAL_JVM_PARAMS = "global.jvm.args";
    
	public static final String DEFAULT_INITIAL_MEMORY_SIZE = "64M";
	public static final String DEFAULT_MAX_MEMORY_SIZE = "256M";

	public static String JAVA_COMMAND = "java";

	public JavaProcessJob(JobDescriptor descriptor) {
		super(descriptor);
	}

	@Override
	protected List<String> getCommandList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(createCommandLine());
		return list;
	}

	protected String createCommandLine() {
		String command = JAVA_COMMAND + " ";
		command += getJVMArguments() + " ";
		command += "-Xms" + getInitialMemorySize() + " ";
		command += "-Xmx" + getMaxMemorySize() + " ";
		command += "-cp " + createArguments(getClassPaths(), ":") + " ";
		command += getJavaClass() + " ";
		command += getMainArguments();

		return command;
	}

	protected String getJavaClass() {
		return getProps().getString(JAVA_CLASS);
	}

	protected String getClassPathParam() {
		List<String> classPath = getClassPaths();
		if (classPath == null || classPath.size() == 0) {
			return "";
		}

		return "-cp " + createArguments(classPath, ":") + " ";
	}

	protected List<String> getClassPaths() {
		List<String> classPaths = getProps().getStringList(CLASSPATH, null, ",");

	    ArrayList<String> classpathList = new ArrayList<String>();
	    // Adding global properties used system wide.
        if (getProps().containsKey(GLOBAL_CLASSPATH)) {
            List<String> globalClasspath = getProps().getStringList(GLOBAL_CLASSPATH);
            for (String global: globalClasspath) {
                log.info("Adding to global classpath:" + global);
                classpathList.add(global);
            }
        }
		
		if (classPaths == null) {
			File path = new File(getPath());
			File parent = path.getParentFile();
			
			for (File file : parent.listFiles()) {
				if (file.getName().endsWith(".jar")) {
	                //log.info("Adding to classpath:" + file.getName());
	                classpathList.add(file.getName());
				}
			}
		}
		else {
		    classpathList.addAll(classPaths);
		}

		return classpathList;
	}

	protected String getInitialMemorySize() {
		return getProps().getString(INITIAL_MEMORY_SIZE,
				DEFAULT_INITIAL_MEMORY_SIZE);
	}

	protected String getMaxMemorySize() {
		return getProps().getString(MAX_MEMORY_SIZE, DEFAULT_MAX_MEMORY_SIZE);
	}

	protected String getMainArguments() {
		return getProps().getString(MAIN_ARGS, "");
	}

    protected String getJVMArguments() {
        String globalJVMArgs = getProps().getString(GLOBAL_JVM_PARAMS, null);
        
        if (globalJVMArgs == null) {
            return getProps().getString(JVM_PARAMS, "");
        }

        return globalJVMArgs + " " + getProps().getString(JVM_PARAMS, "");
    }

	protected String createArguments(List<String> arguments, String separator) {
		if (arguments != null && arguments.size() > 0) {
			String param = "";
			for (String arg : arguments) {
				param += arg + separator;
			}

			return param.substring(0, param.length() - 1);
		}

		return "";
	}
}
