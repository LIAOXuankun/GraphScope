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
package com.alibaba.maxgraph.groot.common.schema.ddl;

import com.alibaba.maxgraph.proto.v2.DataLoadTargetPb;
import com.alibaba.maxgraph.groot.common.operation.LabelId;
import com.alibaba.maxgraph.groot.common.operation.Operation;
import com.alibaba.maxgraph.groot.common.operation.ddl.PrepareDataLoadOperation;
import com.alibaba.maxgraph.groot.common.schema.EdgeKind;
import com.alibaba.maxgraph.groot.common.schema.GraphDef;
import com.alibaba.maxgraph.groot.common.schema.TypeDef;
import com.alibaba.maxgraph.groot.common.schema.TypeEnum;
import com.alibaba.maxgraph.groot.common.schema.request.DdlException;
import com.alibaba.maxgraph.groot.sdk.DataLoadTarget;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.List;

public class PrepareDataLoadExecutor extends AbstractDdlExecutor {
    @Override
    public DdlResult execute(ByteString ddlBlob, GraphDef graphDef, int partitionCount) throws InvalidProtocolBufferException {
        DataLoadTargetPb dataLoadTargetPb = DataLoadTargetPb.parseFrom(ddlBlob);
        DataLoadTarget dataLoadTarget = DataLoadTarget.parseProto(dataLoadTargetPb);
        String label = dataLoadTarget.getLabel();
        String srcLabel = dataLoadTarget.getSrcLabel();
        String dstLabel = dataLoadTarget.getDstLabel();

        long version = graphDef.getSchemaVersion();
        if (!graphDef.hasLabel(label)) {
            throw new DdlException("label [" + label + "] not exists, schema version [" + version + "]");
        }

        GraphDef.Builder graphDefBuilder = GraphDef.newBuilder(graphDef);
        TypeDef typeDef = graphDef.getTypeDef(label);
        long tableIdx = graphDef.getTableIdx();
        tableIdx++;
        DataLoadTarget.Builder targetBuilder = DataLoadTarget.newBuilder(dataLoadTarget);
        if (srcLabel == null || srcLabel.isEmpty()) {
            // Vertex type
            if (typeDef.getTypeEnum() != TypeEnum.VERTEX) {
                throw new DdlException("invalid data load target [" + dataLoadTarget + "], label is not a vertex");
            }
            graphDefBuilder.putVertexTableId(typeDef.getTypeLabelId(), tableIdx);
            targetBuilder.setLabelId(typeDef.getLabelId());
        } else {
            // Edge kind
            if (typeDef.getTypeEnum() != TypeEnum.EDGE) {
                throw new DdlException("invalid data load target [" + dataLoadTarget + "], label is not an edge");
            }
            EdgeKind.Builder edgeKindBuilder = EdgeKind.newBuilder();
            LabelId edgeLabelId = graphDef.getLabelId(label);
            if (edgeLabelId == null) {
                throw new DdlException("invalid edgeLabel [" + label + "], schema version [" + version + "]");
            }
            edgeKindBuilder.setEdgeLabelId(edgeLabelId);
            targetBuilder.setLabelId(edgeLabelId.getId());
            LabelId srcVertexLabelId = graphDef.getLabelId(srcLabel);
            if (srcVertexLabelId == null) {
                throw new DdlException("invalid srcVertexLabel [" + srcLabel + "], schema version [" + version + "]");
            }
            edgeKindBuilder.setSrcVertexLabelId(srcVertexLabelId);
            targetBuilder.setSrcLabelId(srcVertexLabelId.getId());
            LabelId dstVertexLabelId = graphDef.getLabelId(dstLabel);
            if (dstVertexLabelId == null) {
                throw new DdlException("invalid dstVertexLabel [" + dstLabel + "], schema version [" + version + "]");
            }
            edgeKindBuilder.setDstVertexLabelId(dstVertexLabelId);
            targetBuilder.setDstLabelId(dstVertexLabelId.getId());
            EdgeKind edgeKind = edgeKindBuilder.build();
            if (!graphDef.hasEdgeKind(edgeKind)) {
                throw new DdlException("invalid data load target [" + dataLoadTarget + "], edgeKind not exists");
            }
            graphDefBuilder.putEdgeTableId(edgeKind, tableIdx);
        }
        version++;
        graphDefBuilder.setTableIdx(tableIdx);
        graphDefBuilder.setVersion(version);
        GraphDef newGraphDef = graphDefBuilder.build();
        List<Operation> operations = new ArrayList<>(partitionCount);
        for (int i = 0; i < partitionCount; i++) {
            operations.add(new PrepareDataLoadOperation(i, version, targetBuilder.build(), tableIdx));
        }
        return new DdlResult(newGraphDef, operations);
    }
}
