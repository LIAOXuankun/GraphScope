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
package com.alibaba.maxgraph.groot.store.executor.jna;

import com.sun.jna.Structure;

import java.io.Closeable;
import java.util.Arrays;
import java.util.List;

public class JnaEngineServerResponse extends Structure implements Closeable {

    public int errCode;
    public String errMsg;
    public String address;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("errCode", "errMsg", "address");
    }

    @Override
    public void close() {
        setAutoSynch(false);
        ExecutorLibrary.INSTANCE.dropJnaServerResponse(this);
    }
}
