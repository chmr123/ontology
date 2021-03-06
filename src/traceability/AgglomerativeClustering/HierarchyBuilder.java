/*******************************************************************************
 * Copyright 2013 Lars Behnke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package traceability.AgglomerativeClustering;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HierarchyBuilder {

   /* private DistanceMap distances;
    private List<Cluster> clusters;

    public DistanceMap getDistances() {
        return distances;
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public HierarchyBuilder(List<Cluster> clusters, DistanceMap distances) {
        this.clusters = clusters;
        this.distances = distances;
    }*/

	
    public List<Cluster> agglomerate1(LinkageStrategy linkageStrategy,List<Cluster> clusters,DistanceMap distances) throws IOException {
    	FileWriter fw = new FileWriter("hiararchy.txt",true);
        ClusterPair minDistLink = distances.removeFirst();
        if (minDistLink!=null) {
            clusters.remove(minDistLink.getrCluster());
            clusters.remove(minDistLink.getlCluster());

            Cluster oldClusterL = minDistLink.getlCluster();
            Cluster oldClusterR = minDistLink.getrCluster();
            Cluster newCluster = minDistLink.agglomerate(null);

            for (Cluster iClust : clusters) {
                ClusterPair link1 = findByClusters(iClust, oldClusterL,distances);
                ClusterPair link2 = findByClusters(iClust, oldClusterR,distances);
                ClusterPair newLinkage = new ClusterPair();
                newLinkage.setlCluster(iClust);
                newLinkage.setrCluster(newCluster);
                Collection<Double> distanceValues = new ArrayList<Double>();
                if (link1 != null) {
                    distanceValues.add(link1.getLinkageDistance());
                    distances.remove(link1);
                }
                if (link2 != null) {
                    distanceValues.add(link2.getLinkageDistance());
                    distances.remove(link2);
                }
                Double newDistance = linkageStrategy.calculateDistance(distanceValues);
                newLinkage.setLinkageDistance(newDistance);
                distances.add(newLinkage);

            }
            System.out.println(distances.list().size());
            clusters.add(newCluster);
            
            //System.out.println(clusters);
            //fw.write("Result: " + clusters.toString() + "\n");
            //fw.flush();
            //fw.close();
        }
        return clusters;
    }
    
   /* public List<Cluster> agglomerate(LinkageStrategy linkageStrategy) throws IOException {
    	FileWriter fw = new FileWriter("hiararchy.txt",true);
        ClusterPair minDistLink = distances.removeFirst();
        if (minDistLink!=null) {
            clusters.remove(minDistLink.getrCluster());
            clusters.remove(minDistLink.getlCluster());

            Cluster oldClusterL = minDistLink.getlCluster();
            Cluster oldClusterR = minDistLink.getrCluster();
            Cluster newCluster = minDistLink.agglomerate(null);

            for (Cluster iClust : clusters) {
                ClusterPair link1 = findByClusters(iClust, oldClusterL);
                ClusterPair link2 = findByClusters(iClust, oldClusterR);
                ClusterPair newLinkage = new ClusterPair();
                newLinkage.setlCluster(iClust);
                newLinkage.setrCluster(newCluster);
                Collection<Double> distanceValues = new ArrayList<Double>();
                if (link1 != null) {
                    distanceValues.add(link1.getLinkageDistance());
                    distances.remove(link1);
                }
                if (link2 != null) {
                    distanceValues.add(link2.getLinkageDistance());
                    distances.remove(link2);
                }
                Double newDistance = linkageStrategy.calculateDistance(distanceValues);
                newLinkage.setLinkageDistance(newDistance);
                distances.add(newLinkage);

            }
            System.out.println(distances.list().size());
            clusters.add(newCluster);
            
            //System.out.println(clusters);
            //fw.write("Result: " + clusters.toString() + "\n");
            //fw.flush();
            //fw.close();
        }
        return clusters;
    }*/

    private ClusterPair findByClusters(Cluster c1, Cluster c2, DistanceMap distances) {
        return distances.findByCodePair(c1,c2);
    }

    public boolean isTreeComplete(List<Cluster> clusters) {
        return clusters.size() == 1;
    }

    /*
    public Cluster getRootCluster() {
        if (!isTreeComplete()) {
            throw new RuntimeException("No root available");
        }
        return clusters.get(0);
    }*/

}
