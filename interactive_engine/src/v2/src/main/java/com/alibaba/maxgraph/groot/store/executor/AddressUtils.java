/**
 * Copyright 2020 Alibaba Group Holding Limited.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.maxgraph.groot.store.executor;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class AddressUtils {
    private static final String SPLIT = ":";

    public static Pair<String, Integer> parseAddress(String address) {
        String[] array = StringUtils.splitByWholeSeparator(address, SPLIT);
        return Pair.of(array[0], Integer.parseInt(array[1]));
    }

    public static String joinAddress(String host, int port) {
        return host + SPLIT + port;
    }
}
