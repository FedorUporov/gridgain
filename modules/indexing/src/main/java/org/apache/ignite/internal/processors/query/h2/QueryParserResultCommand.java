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

package org.apache.ignite.internal.processors.query.h2;

import org.apache.ignite.internal.processors.query.h2.sql.GridSqlStatement;
import org.apache.ignite.internal.sql.command.SqlCommand;
import org.jetbrains.annotations.Nullable;

/**
 * Parsing result: command.
 */
public class QueryParserResultCommand {
    /** Command (native). */
    private final SqlCommand cmdNative;

    /** Command (H2). */
    private final GridSqlStatement cmdH2;

    /** Whether this is a no-op command. */
    private final boolean noOp;

    /**
     * Constructor.
     *
     * @param cmdNative Command (native).
     * @param cmdH2 Command (H2).
     * @param noOp Whether this is a no-op command.
     */
    public QueryParserResultCommand(@Nullable SqlCommand cmdNative, @Nullable GridSqlStatement cmdH2, boolean noOp) {
        this.cmdNative = cmdNative;
        this.cmdH2 = cmdH2;
        this.noOp = noOp;
    }

    /**
     * @return Command (native).
     */
    @Nullable public SqlCommand commandNative() {
        return cmdNative;
    }

    /**
     * @return Command (H2).
     */
    @Nullable public GridSqlStatement commandH2() {
        return cmdH2;
    }

    /**
     * @return Whether this is a no-op command.
     */
    public boolean noOp() {
        return noOp;
    }
}
