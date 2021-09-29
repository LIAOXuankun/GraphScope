Given a directed graph, out dcore decomposition algorithm can calculate the skyline coreness of each vertices, which is mentioned in our paper.

To run the algorithm, you may first run the getincoreness and getoutcoreness algorithm, then you merge the results and the following format:
 ID incoreness.outcoreness
Then you use this file as input to run the dcore_optimized algorithm.

An example graph can be found under the datasets folder, it is downloaded from http://snap.stanford.edu/data/email-Eu-core-temporal.html and generated using email data from a large European research institution.
each row in the .v file is in the format as vertex ID .indegree.outdegree and each row in the .e file is in the format as src vertex ID / des vertex ID / default edge value(not used)

A scripts used to run the whole procedure is given in the "scripts" folder