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
import com.alibaba.maxgraph.groot.common.operation.LabelId;
import com.alibaba.maxgraph.groot.common.operation.Operation;
import com.alibaba.maxgraph.groot.common.operation.OperationType;
import com.alibaba.maxgraph.groot.common.operation.VertexId;
import com.alibaba.maxgraph.groot.common.schema.PropertyValue;
import com.google.protobuf.ByteString;

import java.util.Map;

public class OverwriteVertexOperation extends Operation {

    private VertexId vertexId;
    private LabelId labelId;
    private Map<Integer, PropertyValue> properties;

    public OverwriteVertexOperation(VertexId vertexId, LabelId labelId, Map<Integer, PropertyValue> properties) {
        super(OperationType.OVERWRITE_VERTEX);
        this.vertexId = vertexId;
        this.labelId = labelId;
        this.properties = properties;
    }

    @Override
    protected long getPartitionKey() {
        return vertexId.getId();
    }

    @Override
    protected ByteString getBytes() {
        DataOperationPb.Builder builder = DataOperationPb.newBuilder();
        builder.setKeyBlob(vertexId.toProto().toByteString());
        builder.setLocationBlob(labelId.toProto().toByteString());
        properties.forEach((propertyId, val) -> builder.putProps(propertyId, val.toProto()));
        return builder.build().toByteString();
    }
}
