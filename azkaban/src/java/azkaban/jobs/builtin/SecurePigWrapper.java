/*
 * Copyright 2011 LinkedIn, Inc
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

import org.apache.log4j.Logger;
import org.apache.pig.Main;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;

import static azkaban.util.SecurityUtils.PROXY_KEYTAB_LOCATION;
import static azkaban.util.SecurityUtils.PROXY_USER;
import static azkaban.util.SecurityUtils.TO_PROXY;
import static azkaban.util.SecurityUtils.getProxiedUser;
import static azkaban.util.SecurityUtils.verifySecureProperty;

public class SecurePigWrapper {
  public static void main(final String[] args) throws IOException, InterruptedException {
    Logger logger = Logger.getRootLogger();
    Properties p = new Properties();
    // No need to check if we should should.proxy - if we're called, we should.
    for(String s : new String [] {PROXY_KEYTAB_LOCATION, PROXY_USER, TO_PROXY}) {
      p.put(s, verifySecureProperty(System.getProperties(), s, logger));
    }

    getProxiedUser(p, logger).doAs(new PrivilegedExceptionAction<Void>() {
      @Override
      public Void run() throws Exception {
        Main.main(args);
        return null;
      }
    });

  }
}
