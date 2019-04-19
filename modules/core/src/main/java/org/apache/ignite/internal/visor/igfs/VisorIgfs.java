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

package org.apache.ignite.internal.visor.igfs;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.ignite.IgniteFileSystem;
import org.apache.ignite.igfs.IgfsMode;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.internal.visor.VisorDataTransferObject;

/**
 * Data transfer object for {@link IgniteFileSystem}.
 */
public class VisorIgfs extends VisorDataTransferObject {
    /** */
    private static final long serialVersionUID = 0L;

    /** IGFS instance name. */
    private String name;

    /** IGFS instance working mode. */
    private IgfsMode mode;

    /** IGFS metrics. */
    private VisorIgfsMetrics metrics;

    /** Whether IGFS has configured secondary file system. */
    private boolean secondaryFsConfigured;

    /**
     * Default constructor.
     */
    public VisorIgfs() {
        // No-op.
    }

    /**
     * Create IGFS configuration transfer object.
     *
     * @param name IGFS instance name.
     * @param mode IGFS instance working mode.
     * @param metrics IGFS metrics.
     * @param secondaryFsConfigured Whether IGFS has configured secondary file system.
     */
    public VisorIgfs(String name, IgfsMode mode, VisorIgfsMetrics metrics, boolean secondaryFsConfigured) {
        this.name = name;
        this.mode = mode;
        this.metrics = metrics;
        this.secondaryFsConfigured = secondaryFsConfigured;
    }

    /**
     * Create data transfer object.
     *
     * @param igfs Source IGFS.
     */
    public VisorIgfs(IgniteFileSystem igfs) {
        assert igfs != null;

        name = igfs.name();
        mode = igfs.configuration().getDefaultMode();
        metrics = new VisorIgfsMetrics(igfs);
        secondaryFsConfigured = igfs.configuration().getSecondaryFileSystem() != null;
    }

    /**
     * @return IGFS instance name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return IGFS instance working mode.
     */
    public IgfsMode getMode() {
        return mode;
    }

    /**
     * @return IGFS metrics.
     */
    public VisorIgfsMetrics getMetrics() {
        return metrics;
    }

    /**
     * @return Whether IGFS has configured secondary file system.
     */
    public boolean isSecondaryFileSystemConfigured() {
        return secondaryFsConfigured;
    }

    /** {@inheritDoc} */
    @Override protected void writeExternalData(ObjectOutput out) throws IOException {
        U.writeString(out, name);
        U.writeEnum(out, mode);
        out.writeObject(metrics);
        out.writeBoolean(secondaryFsConfigured);
    }

    /** {@inheritDoc} */
    @Override protected void readExternalData(byte protoVer, ObjectInput in) throws IOException, ClassNotFoundException {
        name = U.readString(in);
        mode = IgfsMode.fromOrdinal(in.readByte());
        metrics = (VisorIgfsMetrics)in.readObject();
        secondaryFsConfigured = in.readBoolean();
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(VisorIgfs.class, this);
    }
}
