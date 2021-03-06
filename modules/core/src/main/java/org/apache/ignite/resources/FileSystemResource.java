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

package org.apache.ignite.resources;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a field or a setter method for injection of primary Ignite filesystem to a secondary
 * filesystem implementation.
 *
 * <p>
 * Here is how injection would typically happen:
 * <pre name="code" class="java">
 * public class MySecondaryFS implements IgfsSecondaryFileSystem {
 *      ...
 *      // Inject instance of primary filesystem.
 *      &#64;FileSystemResource
 *      private IgniteFileSystem igfs;
 *      ...
 *  }
 * </pre>
 * or attach the same annotations to methods:
 * <pre name="code" class="java">
 * public class MySecondaryFS implements IgfsSecondaryFileSystem {
 *     ...
 *     private IgniteFileSystem igfs;
 *     ...
 *      // Inject instance of primary filesystem.
 *      &#64;FileSystemResource
 *     public void setIgfs(IgniteFileSystem igfs) {
 *          this.igfs = igfs;
 *     }
 *     ...
 * }
 * </pre>
 * <p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface FileSystemResource {
    // No-op.
}