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
package azkaban.util;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class SecurityUtils {
  // Secure Hadoop proxy user params
  public static final String ENABLE_PROXYING = "azkaban.should.proxy"; // boolean
  public static final String PROXY_KEYTAB_LOCATION = "proxy.keytab.location";
  public static final String PROXY_USER = "proxy.user";
  public static final String TO_PROXY = "user.to.proxy";

  public static UserGroupInformation getProxiedUser(Properties prop, Logger l) throws IOException {
    String keytab = verifySecureProperty(prop, PROXY_KEYTAB_LOCATION, l);
    String proxyUser = verifySecureProperty(prop, PROXY_USER, l);
    String toProxy = verifySecureProperty(prop, TO_PROXY, l);

    SecurityUtil.login(new Configuration(), keytab, proxyUser);
    UserGroupInformation loginUser = UserGroupInformation.getLoginUser();

    return UserGroupInformation.createProxyUser(toProxy, loginUser);
  }

  public static String verifySecureProperty(Properties properties, String s, Logger l) throws IOException {
    String value = properties.getProperty(s);

    if(value == null) throw new IOException(s + " not set in properties. Cannot use secure proxy");
    l.info("Secure proxy configuration: Property " + s + " = " + value);
    return value;
  }

  public static boolean shouldProxy(Properties prop) {
    String shouldProxy = prop.getProperty(ENABLE_PROXYING);

    return shouldProxy != null && shouldProxy.equals("true");
  }
}
