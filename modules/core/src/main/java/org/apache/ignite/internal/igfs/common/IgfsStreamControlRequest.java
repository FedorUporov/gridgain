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

package org.apache.ignite.internal.igfs.common;

import org.apache.ignite.internal.util.tostring.GridToStringExclude;
import org.apache.ignite.internal.util.typedef.internal.S;

/**
 * Read block request.
 */
public class IgfsStreamControlRequest extends IgfsMessage {
    /** Stream id. */
    private long streamId;

    /** Data. */
    @GridToStringExclude
    private byte[] data;

    /** Read position. */
    private long pos;

    /** Length to read. */
    private int len;

    /**
     * @return Stream ID.
     */
    public long streamId() {
        return streamId;
    }

    /**
     * @param streamId Stream ID.
     */
    public void streamId(long streamId) {
        this.streamId = streamId;
    }

    /**
     * @return Data.
     */
    public byte[] data() {
        return data;
    }

    /**
     * @param data Data.
     */
    public void data(byte[] data) {
        this.data = data;
    }

    /**
     * @return Position.
     */
    public long position() {
        return pos;
    }

    /**
     * @param pos Position.
     */
    public void position(long pos) {
        this.pos = pos;
    }

    /**
     * @return Length.
     */
    public int length() {
        return len;
    }

    /**
     * @param len Length.
     */
    public void length(int len) {
        this.len = len;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(IgfsStreamControlRequest.class, this, "cmd", command(),
            "dataLen", data == null ? 0 : data.length);
    }
}