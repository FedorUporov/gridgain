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

package org.apache.ignite.internal.processors.query.h2.twostep;

import java.util.Collection;
import java.util.Collections;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * Join sql test helper
 */
public class JoinSqlTestHelper {
    /** */
    static final String ORG = "org";

    /** */
    static final int ORG_COUNT = 100;

    /** */
    private static final int PERSON_PER_ORG_COUNT = 10;

    /** */
    static final String JOIN_SQL = "select * from Person, \"org\".Organization as org " +
        "where Person.orgId = org.id " +
        "and lower(org.name) = lower(?)";

    /**
     * @return Query entity for Organization.
     */
    static Collection<QueryEntity> organizationQueryEntity() {
        QueryEntity entity = new QueryEntity(String.class, Organization.class);

        entity.setKeyFieldName("ID");
        entity.getFields().put("ID", String.class.getName());

        return Collections.singletonList(entity);
    }

    /**
     * @return Query entity for Organization.
     */
    static Collection<QueryEntity> personQueryEntity() {
        QueryEntity entity = new QueryEntity(String.class, Person.class);

        entity.setKeyFieldName("ID");
        entity.getFields().put("ID", String.class.getName());

        return Collections.singletonList(entity);
    }

    /**
     * Populate organization cache with test data
     * @param cache @{IgniteCache}
     */
    static void populateDataIntoOrg(IgniteCache<String, Organization> cache) {
        for (int i = 0; i < ORG_COUNT; i++) {
            Organization org = new Organization();

            org.setName("Organization #" + i);

            cache.put(ORG + i, org);
        }
    }

    /**
     * Populate person cache with test data
     * @param cache @{IgniteCache}
     */
    static void populateDataIntoPerson(IgniteCache<String, Person> cache) {
        int personId = 0;

        for (int i = 0; i < ORG_COUNT; i++) {
            String orgId = ORG + i;

            for (int j = 0; j < PERSON_PER_ORG_COUNT; j++) {
                Person prsn = new Person();

                prsn.setOrgId(orgId);

                prsn.setName("Person name #" + personId);

                cache.put("pers" + personId, prsn);

                personId++;
            }
        }
    }

    /**
     *
     */
    public static class Person {
        /** */
        @QuerySqlField(index = true)
        private String orgId;

        /** */
        @QuerySqlField(index = true)
        private String name;

        /** */
        public String getOrgId() {
            return orgId;
        }

        /** */
        public void setOrgId(String orgId) {
            this.orgId = orgId;
        }

        /** */
        public String getName() {
            return name;
        }

        /** */
        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     *
     */
    public static class Organization {
        /** */
        @QuerySqlField(index = true)
        private String name;

        /** Debt capital. */
        @QuerySqlField
        private Integer debtCapital;

        /** */
        public String getName() {
            return name;
        }

        /** */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return Debt capital.
         */
        public Integer debtCapital() {
            return debtCapital;
        }

        /**
         * @param debtCapital Debt capital.
         */
        public void debtCapital(Integer debtCapital) {
            this.debtCapital = debtCapital;
        }
    }
}
