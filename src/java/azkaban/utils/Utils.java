/*
 * Copyright 2012 LinkedIn, Inc
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

package azkaban.utils;

/**
 * A util helper class full of static methods that are commonly used.
 */
public class Utils {
    /**
     * Private constructor.
     */
    private Utils() {
    }

    /**
     * Equivalent to Object.equals except that it handles nulls. If a and b are
     * both null, true is returned.
     * 
     * @param a
     * @param b
     * @return
     */
    public static boolean equals(Object a, Object b) {
        if (a == null || b == null) {
            return a == b;
        }

        return a.equals(b);
    }

    /**
     * Return the object if it is non-null, otherwise throw an exception
     * 
     * @param <T>
     *            The type of the object
     * @param t
     *            The object
     * @return The object if it is not null
     * @throws IllegalArgumentException
     *             if the object is null
     */
    public static <T> T nonNull(T t) {
        if (t == null) {
            throw new IllegalArgumentException("Null value not allowed.");
        }
        else {
            return t;
        }
    }
    
    /**
     * Print the message and then exit with the given exit code
     * 
     * @param message The message to print
     * @param exitCode The exit code
     */
    public static void croak(String message, int exitCode) {
        System.err.println(message);
        System.exit(exitCode);
    }
}