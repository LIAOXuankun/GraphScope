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
package com.alibaba.maxgraph.groot.coordinator;

import java.io.IOException;

/**
 * Distributed, reliable storage used for SnapshotManager to persist Snapshot related meta.
 * We can implement this interface with Zookeeper
 */
public interface MetaStore {

    boolean exists(String path);

    byte[] read(String path) throws IOException;

    void write(String path, byte[] content) throws IOException;

    void delete(String path) throws IOException;
}
