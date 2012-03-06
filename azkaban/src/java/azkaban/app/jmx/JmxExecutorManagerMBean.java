/*
 * Copyright 2011 Adconion, Inc.
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
package azkaban.app.jmx;

import java.util.List;
import java.util.Map;

public interface JmxExecutorManagerMBean {

    @DisplayName("OPERATION: viewExecutingJobNames ")
    List<String> viewExecutingJobNames();
    
    @DisplayName("OPERATION: viewExecutingJobIds ")
    List<String> viewExecutingJobIds();
    
    @DisplayName("OPERATION: kill all jobs with the same name, and remove from list. ")
    String killJobId(
            @ParameterName("jobName : Identifier of the job")
            String jobId
    ); 
    
    @DisplayName("OPERATION: kill all jobs with the same name ")
    String killJobByName(
            @ParameterName("jobName : Name of the job")
            String jobName
    );
    
    @DisplayName("OPERATION: RemoveJobFrom list")
    String removeJobFromExecutingList(
            @ParameterName("id : Id of the executing job")
            String id
    );

    @DisplayName("OPERATION: Find all the permit locks")
    List<String> getAllNamedPermits();
    
    @DisplayName("OPERATION: Find all the permit locks")
    String releasePermitByName(
            @ParameterName("name of the permit")
            String name,
            @ParameterName("number of permits to release")
            int numPermits
    );
    
    @DisplayName("OPERATION: Find all the write locks")
    List<String> getAllReadWriteLocks();
    
    @DisplayName("OPERATION: Find all the write locks")
    Map<String, String> getReadWriteLockData(
            @ParameterName("name of the lock")
            String name
    );
    
    @DisplayName("OPERATION: Find all the write locks")
    String releaseWriteLock(
            @ParameterName("name of the write lock")
            String name
    );
    
    @DisplayName("OPERATION: Find all the read locks")
    String releaseReadLock(
            @ParameterName("name of the read lock")
            String name
    );
}