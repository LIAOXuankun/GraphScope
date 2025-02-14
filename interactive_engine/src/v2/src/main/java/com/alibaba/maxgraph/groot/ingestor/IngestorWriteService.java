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
package com.alibaba.maxgraph.groot.ingestor;

import com.alibaba.maxgraph.proto.v2.IngestorWriteGrpc;
import com.alibaba.maxgraph.proto.v2.WriteIngestorRequest;
import com.alibaba.maxgraph.proto.v2.WriteIngestorResponse;
import com.alibaba.maxgraph.groot.common.OperationBatch;
import io.grpc.stub.StreamObserver;

public class IngestorWriteService extends IngestorWriteGrpc.IngestorWriteImplBase {

    private IngestService ingestService;

    public IngestorWriteService(IngestService ingestService) {
        this.ingestService = ingestService;
    }

    @Override
    public void writeIngestor(WriteIngestorRequest request, StreamObserver<WriteIngestorResponse> responseObserver) {
        try {
            int queueId = request.getQueueId();
            String requestId = request.getRequestId();
            OperationBatch operationBatch = OperationBatch.parseProto(request.getOperationBatch());
            this.ingestService.ingestBatch(requestId, queueId, operationBatch, new IngestCallback() {
                @Override
                public void onSuccess(long snapshotId) {
                    WriteIngestorResponse response = WriteIngestorResponse.newBuilder()
                            .setSnapshotId(snapshotId)
                            .build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }

                @Override
                public void onFailure(Exception e) {
                    responseObserver.onError(e);
                }
            });
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
