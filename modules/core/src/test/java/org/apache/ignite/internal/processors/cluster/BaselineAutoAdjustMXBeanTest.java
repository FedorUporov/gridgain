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

package org.apache.ignite.internal.processors.cluster;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.mxbean.BaselineAutoAdjustMXBean;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;

/** */
public class BaselineAutoAdjustMXBeanTest extends GridCommonAbstractTest {
    /** */
    @Test
    public void testTimeoutAndEnabledFlag() throws Exception {
        Ignite ignite = startGrid();

        try {
            IgniteCluster cluster = ignite.cluster();

            BaselineAutoAdjustMXBean bltMxBean = bltMxBean();

            assertTrue(cluster.isBaselineAutoAdjustEnabled());
            assertTrue(bltMxBean.isAutoAdjustmentEnabled());

            assertEquals(0L, cluster.baselineAutoAdjustTimeout());
            assertEquals(0L, bltMxBean.getAutoAdjustmentTimeout());

            cluster.baselineAutoAdjustEnabled(false);
            assertFalse(bltMxBean.isAutoAdjustmentEnabled());

            cluster.baselineAutoAdjustTimeout(30_000L);
            assertEquals(30_000L, bltMxBean.getAutoAdjustmentTimeout());

            bltMxBean.setAutoAdjustmentEnabled(true);
            assertTrue(cluster.isBaselineAutoAdjustEnabled());

            bltMxBean.setAutoAdjustmentEnabled(false);
            assertFalse(cluster.isBaselineAutoAdjustEnabled());

            bltMxBean.setAutoAdjustmentTimeout(60_000L);
            assertEquals(60_000L, cluster.baselineAutoAdjustTimeout());

            assertEquals("NOT_SCHEDULED", bltMxBean.getTaskState());

            bltMxBean.setAutoAdjustmentEnabled(true);

            startGrid(1);

            long timeUntilAutoAdjust = bltMxBean.getTimeUntilAutoAdjust();
            assertTrue(Long.toString(timeUntilAutoAdjust), timeUntilAutoAdjust > 0 && timeUntilAutoAdjust < 60_000L);

            assertEquals("SCHEDULED", bltMxBean.getTaskState());
        }
        finally {
            stopGrid();
        }
    }

    /**
     *
     */
    private BaselineAutoAdjustMXBean bltMxBean() throws Exception {
        ObjectName mBeanName = U.makeMBeanName(getTestIgniteInstanceName(), "Baseline",
            BaselineAutoAdjustMXBeanImpl.class.getSimpleName());

        MBeanServer mBeanSrv = ManagementFactory.getPlatformMBeanServer();

        assertTrue(mBeanSrv.isRegistered(mBeanName));

        Class<BaselineAutoAdjustMXBean> itfCls = BaselineAutoAdjustMXBean.class;

        return MBeanServerInvocationHandler.newProxyInstance(mBeanSrv, mBeanName, itfCls, true);
    }
}
