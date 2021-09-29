/** Copyright 2020 Alibaba Group Holding Limited.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

#ifndef ANALYTICAL_ENGINE_APPS_DCORE_DCOREOPTIMIZED_CONTEXT_H_
#define ANALYTICAL_ENGINE_APPS_DCORE_DCOREOPTIMIZED_CONTEXT_H_

#include <iomanip>
#include <iostream>
#include <limits>

#include <grape/grape.h>


namespace gs {

/**
 * @brief Context for the parallel version of DCORE_OPTIMIZED.
 *
 * @tparam FRAG_T
 */
    template <typename FRAG_T>
    class DCoreOptimizedContext : public VertexDataContext<FRAG_T, int> {
    public:
        using oid_t = typename FRAG_T::oid_t;
        using vid_t = typename FRAG_T::vid_t;

        explicit DCoreOptimizedContext(const FRAG_T& fragment)
                : VertexDataContext<FRAG_T, int>(fragment, true)/*,
        partial_result(this->data())*/ {}

        void Init(ParallelMessageManager& messages, oid_t source_id) {
            auto& frag = this->fragment();
            auto vertices = frag.Vertices();
            v2index.Init(vertices);
            partial_result.Init(vertices);
            cache_result.Init(vertices);
            h_neighbor_value.Init(vertices);
#ifdef PROFILING
    preprocess_time = 0;
    exec_time = 0;
    postprocess_time = 0;
#endif
        }

        void Output(std::ostream& os) override {
            // The output of each vertex is the skyline coreness of
            // it, in the form of .k1.l1.k2.l2, which means this vertex
            // is in (k1,l1)-core, (k2,l2)-core...
            auto& frag = this->fragment();
            auto inner_vertices = frag.InnerVertices();
            for (auto v : inner_vertices) {
                auto shIndex = partial_result[v];
                os << frag.GetId(v) << " ";
                for (auto index: shIndex){
                    os << "." << index;
                }
                os << std::endl;

            }
#ifdef PROFILING
    VLOG(2) << "preprocess_time: " << preprocess_time << "s.";
    VLOG(2) << "exec_time: " << exec_time << "s.";
    VLOG(2) << "postprocess_time: " << postprocess_time << "s.";
#endif
        }

        typename FRAG_T::template vertex_array_t<std::vector<int>> partial_result;
        typename FRAG_T::template vertex_array_t<std::vector<int>> cache_result;
        typename FRAG_T::template vertex_array_t<std::unordered_map<typename FRAG_T::vertex_t, int>> v2index;
        typename FRAG_T::template vertex_array_t<std::vector<int>> h_neighbor_value;


#ifdef PROFILING
        double preprocess_time = 0;
  double exec_time = 0;
  double postprocess_time = 0;
#endif
    };
}  // namespace grape

#endif  // ANALYTICAL_ENGINE_APPS_DCORE_DCOREFIRST_CONTEXT_H_