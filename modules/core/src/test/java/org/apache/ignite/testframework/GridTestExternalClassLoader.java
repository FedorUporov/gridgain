/*
 *                   GridGain Community Edition Licensing
 *                   Copyright 2019 GridGain Systems, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License") modified with Commons Clause
 * Restriction; you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * Commons Clause Restriction
 * 
 * The Software is provided to you by the Licensor under the License, as defined below, subject to
 * the following condition.
 * 
 * Without limiting other conditions in the License, the grant of rights under the License will not
 * include, and the License does not grant to you, the right to Sell the Software.
 * For purposes of the foregoing, “Sell” means practicing any or all of the rights granted to you
 * under the License to provide to third parties, for a fee or other consideration (including without
 * limitation fees for hosting or consulting/ support services related to the Software), a product or
 * service whose value derives, entirely or substantially, from the functionality of the Software.
 * Any license notice or attribution required by the License must also include this Commons Clause
 * License Condition notice.
 * 
 * For purposes of the clause above, the “Licensor” is Copyright 2019 GridGain Systems, Inc.,
 * the “License” is the Apache License, Version 2.0, and the Software is the GridGain Community
 * Edition software provided with this notice.
 */

package org.apache.ignite.testframework;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

/**
 * Utility classloader that has ability to load classes from external resources.
 */
@SuppressWarnings({"CustomClassloader"})
public class GridTestExternalClassLoader extends URLClassLoader {
    /** */
    private Set<String> excludeClassNames;

    /** */
    private Map<String, byte[]> resourceMap;

    /** */
    private long timeout;

    /**
     * Constructor.
     * @param urls the URLs from which to load classes and resources.
     * @param excludeClassNames list of excluded classes.
     */
    public GridTestExternalClassLoader(URL[] urls, String... excludeClassNames) {
        this(urls, Collections.<String, byte[]>emptyMap(), excludeClassNames);
    }

    /**
     * Constructor.
     * @param urls the URLs from which to load classes and resources.
     * @param resourceMap mapped resources.
     */
    public GridTestExternalClassLoader(URL[] urls, Map<String, byte[]> resourceMap) {
        this(urls, resourceMap, Collections.<String>emptySet());
    }

    /**
     * Constructor.
     * @param urls the URLs from which to load classes and resources.
     * @param resourceMap Resource map.
     * @param excludeClassNames list of excluded classes.
     */
    public GridTestExternalClassLoader(URL[] urls, Map<String, byte[]> resourceMap, String... excludeClassNames) {
        this(urls, resourceMap, new HashSet<>(Arrays.asList(excludeClassNames)));
    }

    /**
     * Constructor.
     * @param urls the URLs from which to load classes and resources.
     * @param resourceMap Resource map.
     * @param excludeClassNames list of excluded classes.
     */
    public GridTestExternalClassLoader(URL[] urls, Map<String, byte[]> resourceMap, Set<String> excludeClassNames) {
        super(urls, GridTestExternalClassLoader.class.getClassLoader());

        this.excludeClassNames = excludeClassNames;

        assert resourceMap != null;

        this.resourceMap = resourceMap;
    }

    /**
     * Sets set of excluded resource paths.
     * @param excludeClassNames excluded resource paths.
     */
    public void setExcludeClassNames(Set<String> excludeClassNames) {
        this.excludeClassNames = excludeClassNames;
    }

    /**
     * Sets set of excluded resource paths.
     * @param excludeClassNames excluded resource paths.
     */
    public void setExcludeClassNames(String... excludeClassNames) {
        setExcludeClassNames(new HashSet<>(Arrays.asList(excludeClassNames)));
    }

    /**
     * @param timeout Timeout.
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Sleep {@code timeout} period of time.
     */
    private void doTimeout() {
        try {
            Thread.sleep(timeout);
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Thread was interrupted", e);
        }
    }

    /**
     * @param resName Resource name.
     * @return Class name.
     */
    private String resNameToClassName(String resName) {
        if (resName.endsWith(".class"))
            resName = resName.substring(0, resName.length() - ".class".length());

        return resName.replace('/', '.');
    }

    /** {@inheritDoc} */
    @Override protected Class<?> findClass(String name) throws ClassNotFoundException {
        for (String s : excludeClassNames)
            if (s.equals(name))
                throw new ClassNotFoundException(name);

        return super.findClass(name);
    }

    /** {@inheritDoc} */
    @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (excludeClassNames.contains(name))
            throw new ClassNotFoundException(name);

        return super.loadClass(name, resolve);
    }

    /** {@inheritDoc} */
    @Nullable @Override public URL findResource(String name) {
        if (excludeClassNames.contains(resNameToClassName(name)))
            return null;

        return super.findResource(name);
    }

    /** {@inheritDoc} */
    @Override public InputStream getResourceAsStream(String name) {
        doTimeout();

        byte[] res = resourceMap.get(name);

        return res == null ? super.getResourceAsStream(name) : new ByteArrayInputStream(res);
    }

    /**
     * @param resourceMap mapped resources.
     */
    public void setResourceMap(Map<String, byte[]> resourceMap) {
        this.resourceMap = resourceMap;
    }

    /**
     * Returns an Enumeration of URLs representing all of the resources on the URL search path having the specified name.
     *
     * @param name the resource name.
     * @return an {@code Enumeration} of {@code URL}s.
     * @throws IOException if an I/O exception occurs.
     */
    @Override public Enumeration<URL> findResources(String name) throws IOException {
        if (excludeClassNames.contains(resNameToClassName(name))) {
            return new Enumeration<URL>() {
                @Override public boolean hasMoreElements() {
                    return false;
                }

                @Override public URL nextElement() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        return super.findResources(name);
    }
}