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
package com.alibaba.maxgraph.groot.common.operation.dml;

import com.alibaba.maxgraph.proto.v2.DataOperationPb;
import com.alibaba.maxgraph.proto.v2.EdgeLocationPb;
import com.alibaba.maxgraph.groot.common.operation.EdgeId;
import com.alibaba.maxgraph.groot.common.schema.EdgeKind;
import com.alibaba.maxgraph.groot.common.operation.Operation;
import com.alibaba.maxgraph.groot.common.operation.OperationType;
import com.alibaba.maxgraph.groot.common.schema.PropertyValue;
import com.google.protobuf.ByteString;

import java.util.Map;

public class OverwriteEdgeOperation extends Operation {

    private EdgeId edgeId;
    private EdgeKind edgeKind;
    private Map<Integer, PropertyValue> properties;
    private boolean forward;

    public OverwriteEdgeOperation(EdgeId edgeId, EdgeKind edgeKind, Map<Integer, PropertyValue> properties,
                                  boolean forward) {
        super(OperationType.OVERWRITE_EDGE);
        this.edgeId = edgeId;
        this.edgeKind = edgeKind;
        this.properties = properties;
        this.forward = forward;
    }

    @Override
    protected long getPartitionKey() {
        if (forward) {
            return edgeId.getSrcId().getId();
        } else {
            return edgeId.getDstId().getId();
        }
    }

    @Override
    protected ByteString getBytes() {
        DataOperationPb.Builder builder = DataOperationPb.newBuilder();
        builder.setKeyBlob(edgeId.toProto().toByteString());
        builder.setLocationBlob(EdgeLocationPb.newBuilder()
                .setEdgeKind(edgeKind.toOperationProto())
                .setForward(this.forward)
                .build().toByteString());
        properties.forEach((propertyId, val) -> builder.putProps(propertyId, val.toProto()));
        return builder.build().toByteString();
    }
}
