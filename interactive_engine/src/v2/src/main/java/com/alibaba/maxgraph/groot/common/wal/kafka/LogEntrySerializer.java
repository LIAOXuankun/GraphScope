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
package com.alibaba.maxgraph.groot.common.wal.kafka;

import com.alibaba.maxgraph.groot.common.wal.LogEntry;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class LogEntrySerializer implements Serializer<LogEntry> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, LogEntry data) {
        if (data == null) {
            return null;
        }
        return data.toProto().toByteArray();
    }

    @Override
    public void close() {

    }
}
