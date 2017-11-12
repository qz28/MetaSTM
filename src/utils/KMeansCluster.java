/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.Arrays;
import utils.random.MathUtil;

/**
 *
 * @author U1015181
 */
public class KMeansCluster {
    
    public static double[][] initialPoints(double[][] points, int centers){
        double[][] initialCens=new double[centers][points[0].length];
        System.arraycopy(points[MathUtil.getNextInt(points.length-1)],0,initialCens[0],0,points[0].length);
        double[] weights=new double[points.length];
        for (int i=1;i<centers;i++){
            getWeights(points,weights,initialCens[i-1],i);
            double[] sumWeights=new double[points.length];
            double total=0;
            for (int j=0;j<points.length;j++){
                total+=weights[j];
                sumWeights[j]=total;
            }
            System.arraycopy(points[BinarySearch.binarySearch(sumWeights,MathUtil.getNextFloat(total))],0,initialCens[i],0,points[0].length);;
        }
        return initialCens;
    }
    
    public static void getWeights(double[][] points, double[] weights, double[] newCen, int num){
        for (int i=0;i<points.length;i++){
            double newWeight=Math.pow(Distance.getDistance(points[i],newCen),2);
            if (num>1){
                if (newWeight<weights[i]){
                    weights[i]=newWeight;
                }    
            }else{
                weights[i]=newWeight;
            }
        }
    }
    
    public static int assigning(double[] point, double[][]centroids){
        int numOfCentroids=centroids.length;
        int label=0;
        double distance=Distance.getDistance(point, centroids[0]);
        for (int i=1;i<numOfCentroids;i++){
            double newDistance=Distance.getDistance(point,centroids[i]);
            if (distance>newDistance){
                distance=newDistance;
                label=i;
            }
        }
        return label;
    }
    
    public static boolean checkConvergence(int[] c1, int[] c2){
        for (int i=0;i<c1.length;i++){
            if (c1[i]!=c2[i])
                return false;
        }
        return true;
    }
    
    public static void update(double[][] centroids, double[][] points, int[] cluster){
        for (double[] row:centroids){
            Arrays.fill(row,0);
        }
        int[] count=new int[centroids.length];
        for (int i=0;i<cluster.length;i++){
            VectorAddition.additionOfVectors(centroids[cluster[i]], 1, 1, points[i],centroids[cluster[i]]);
            count[cluster[i]]+=1;
        }
        for (int i=0;i<count.length;i++){
            for (int j=0;j<centroids[0].length;j++){
                centroids[i][j]=centroids[i][j]/count[i];
            }
        }
        
    }
    
    public static int[] clustering(double[][] points, int centers){
        int numOfPoints=points.length;
        int[] cluster=new int[numOfPoints];
        Arrays.fill(cluster,centers);
        int[] newCluster=new int[numOfPoints];
        do{
        double[][] centroids=initialPoints(points,centers);
        for (int j=0;j<1000;j++){
            for (int i=0;i<numOfPoints;i++){
                newCluster[i]=assigning(points[i],centroids);
            }
            if (checkConvergence(cluster,newCluster)) {
                break;
            }
            else{
                System.arraycopy(newCluster, 0, cluster, 0, numOfPoints);
                update(centroids,points,cluster);
            }
        }
        }while(checkEmptyGroups(cluster,centers));
        return cluster;
    }
    
    public static boolean checkEmptyGroups(int[] cluster, int centers){
        int indicator;
        for (int i=0;i<centers;i++){
            indicator=0;
            for (int label:cluster){
                if (i==label) {
                    indicator=1;
                    break;
                }
            }
            if (indicator==0) return true;
        }
        return false;
    }
    
    public static double[][] getSubset(double[][] points, int group, int[] cluster){
        ArrayList<double[]> subset=new ArrayList<>();
        for (int i=0;i<points.length;i++){
            if (cluster[i]==group)
                subset.add(points[i]);
        }
        return subset.toArray(new double[subset.size()][]);
    }
    
}
