/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.platform.dotnet;

import org.apache.ignite.internal.processors.platform.dotnet.PlatformDotNetCacheStore;

import javax.cache.configuration.Factory;
import java.util.Map;

/**
 * Wrapper for .NET cache store implementations.
 * <p>
 * This wrapper should be used if you have an implementation of
 * {@code GridGain.Cache.IGridCacheStore} interface in .NET and
 * would like to configure it a persistence storage for your cache.
 * To do tis you will need to configure the wrapper via
 * {@link org.apache.ignite.configuration.CacheConfiguration#setCacheStoreFactory(javax.cache.configuration.Factory)} property
 * and provide assembly name and class name of your .NET store
 * implementation (both properties are mandatory):
 * <pre name="code" class="xml">
 * &lt;bean class="org.apache.ignite.cache.CacheConfiguration"&gt;
 *     ...
 *     &lt;property name="cacheStoreFactory"&gt;
 *         &lt;bean class="org.gridgain.grid.interop.dotnet.InteropDotNetCacheStoreFactory"&gt;
 *             &lt;property name="assemblyName" value="MyAssembly"/&gt;
 *             &lt;property name="className" value="MyApp.MyCacheStore"/&gt;
 *         &lt;/bean&gt;
 *     &lt;/property&gt;
 *     ...
 * &lt;/bean&gt;
 * </pre>
 * If properly configured, this wrapper will instantiate an instance
 * of your cache store in .NET and delegate all calls to that instance.
 * To create an instance, assembly name and class name are passed to
 * <a target="_blank" href="http://msdn.microsoft.com/en-us/library/d133hta4.aspx">System.Activator.CreateInstance(String, String)</a>
 * method in .NET during node startup. Refer to its documentation for
 * details.
 */
public class PlatformDotNetCacheStoreFactory implements Factory<PlatformDotNetCacheStore> {
    /** */
    private static final long serialVersionUID = 0L;

    /** .Net type name. */
    private String typName;

    /** Properties. */
    private Map<String, ?> props;

    /** Instance. */
    private transient PlatformDotNetCacheStore instance;

    /**
     * Gets .NET type name.
     *
     * @return .NET type name.
     */
    public String getTypeName() {
        return typName;
    }

    /**
     * Sets .NET type name.
     *
     * @param typName .NET type name.
     */
    public void setTypeName(String typName) {
        this.typName = typName;
    }

    /**
     * Get properties.
     *
     * @return Properties.
     */
    public Map<String, ?> getProperties() {
        return props;
    }

    /**
     * Set properties.
     *
     * @param props Properties.
     */
    public void setProperties(Map<String, ?> props) {
        this.props = props;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override public PlatformDotNetCacheStore create() {
        synchronized (this) {
            if (instance == null) {
                instance = new PlatformDotNetCacheStore();

                instance.setTypeName(typName);
                instance.setProperties(props);
            }

            return instance;
        }
    }
}