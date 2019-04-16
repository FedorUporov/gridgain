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

#ifndef _IGNITE_NETWORK_TCP_RANGE
#define _IGNITE_NETWORK_TCP_RANGE

#include <stdint.h>
#include <string>

namespace ignite
{
    namespace network
    {
        /**
         * TCP port range.
         */
        struct TcpRange
        {
            /**
             * Default constructor.
             */
            TcpRange() :
                port(0),
                range(0)
            {
                // No-op.
            }

            /**
             * Constructor.
             *
             * @param host Host.
             * @param port Port.
             * @param range Number of ports after the @c port that
             *    should be tried if the previous are unavailable.
             */
            TcpRange(const std::string& host, uint16_t port, uint16_t range = 0) :
                host(host),
                port(port),
                range(range)
            {
                // No-op.
            }

            /**
             * Compare to another instance.
             *
             * @param other Another instance.
             * @return Negative value if less, positive if larger and
             *    zero, if equals another instance.
             */
            int Compare(const TcpRange& other) const
            {
                if (port < other.port)
                    return -1;

                if (port > other.port)
                    return 1;

                if (range < other.range)
                    return -1;

                if (range > other.range)
                    return 1;

                return host.compare(other.host);
            }

            /**
             * Comparison operator.
             *
             * @param val1 First value.
             * @param val2 Second value.
             * @return True if equal.
             */
            friend bool operator==(const TcpRange& val1, const TcpRange& val2)
            {
                return val1.port == val2.port && val1.range == val2.range && val1.host == val2.host;
            }


            /**
             * Comparison operator.
             *
             * @param val1 First value.
             * @param val2 Second value.
             * @return True if not equal.
             */
            friend bool operator!=(const TcpRange& val1, const TcpRange& val2)
            {
                return !(val1 == val2);
            }

            /**
             * Comparison operator.
             *
             * @param val1 First value.
             * @param val2 Second value.
             * @return True if less.
             */
            friend bool operator<(const TcpRange& val1, const TcpRange& val2)
            {
                return val1.Compare(val2) < 0;
            }

            /**
             * Comparison operator.
             *
             * @param val1 First value.
             * @param val2 Second value.
             * @return True if less or equal.
             */
            friend bool operator<=(const TcpRange& val1, const TcpRange& val2)
            {
                return val1.Compare(val2) <= 0;
            }

            /**
             * Comparison operator.
             *
             * @param val1 First value.
             * @param val2 Second value.
             * @return True if gretter.
             */
            friend bool operator>(const TcpRange& val1, const TcpRange& val2)
            {
                return val1.Compare(val2) > 0;
            }

            /**
             * Comparison operator.
             *
             * @param val1 First value.
             * @param val2 Second value.
             * @return True if gretter or equal.
             */
            friend bool operator>=(const TcpRange& val1, const TcpRange& val2)
            {
                return val1.Compare(val2) >= 0;
            }

            /** Remote host. */
            std::string host;

            /** TCP port. */
            uint16_t port;

            /**
             * Number of ports after the port that should be tried if
             * the previous are unavailable.
             */
            uint16_t range;
        };
    }
}

#endif //_IGNITE_NETWORK_TCP_RANGE