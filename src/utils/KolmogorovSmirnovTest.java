/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author John
 */
public class KolmogorovSmirnovTest {
    
    public static double getSupDistance(List<Integer> combinedSamples, int sampleSize1, int sampleSize2){
        double supDistance=0;
        double distance=0;
        double distancePerStep1=1.0/sampleSize1;
        double distancePerStep2=1.0/sampleSize2;
        for (Integer label:combinedSamples){
            if (label==0)
                distance+=distancePerStep2;
            else if (label==1)
                distance-=distancePerStep1;
            else if (label==2)
                distance+=(distancePerStep2-distancePerStep1);
            double absoluteDistance=Math.abs(distance);
            if (supDistance<absoluteDistance)
                supDistance=absoluteDistance;
        }
        return supDistance;
    }
    
    public static double cAlpha(double alpha){
        if (alpha==0.10)
            return 1.22;
        if (alpha==0.05)
            return 1.36;
        if (alpha==0.025)
            return 1.48;
        if (alpha==0.01)
            return 1.63;
        if (alpha==0.005)
            return 1.73;
        if (alpha==0.001)
            return 1.95;
        else
            return alpha;
    }
    
    public static boolean finalTest(double distance, double alpha, int sampleSize1, int sampleSize2){
        double criteria;
        double ca=cAlpha(alpha);
        if (ca<1) criteria=ca;
        else criteria=cAlpha(alpha)*Math.sqrt((double)(sampleSize1+sampleSize2)/sampleSize1/sampleSize2);
        return distance <= criteria;
    }
    
    public static List<Integer> merge(List<Double> sample1, List<Double> sample2){
        List<Integer> order=new ArrayList<>();
        while (true){
            double element1=sample1.get(0);
            double element2=sample2.get(0);
            if (element1>element2){
                order.add(0);
                sample2.remove(0);
            }else if (element1<element2){
                order.add(1);
                sample1.remove(0);
            }else{
                order.add(2);
                sample1.remove(0);
                sample2.remove(0);
            }
            if (sample1.isEmpty()){
                if (sample2.isEmpty())
                    break;
                else{
                    order.addAll(Collections.nCopies(sample2.size(),0));
                    break;
                }
            }else if (sample2.isEmpty()){
                order.addAll(Collections.nCopies(sample1.size(),1));
                break;
            }                
        }
        return order;
    }
    
    public static boolean KSTest(List<Double> sample1, List<Double> sample2, double alpha){
        int sampleSize1=sample1.size();
        int sampleSize2=sample2.size();
        double distance=getSupDistance(merge(sample1,sample2),sampleSize1,sampleSize2);
        return finalTest(distance, alpha, sampleSize1, sampleSize2);
    }
    
    public static boolean checkLastElements(List<Double> series, int n){
        int sampleSize=series.size();
        double value=series.get(sampleSize-1);
        for (int i=n;i>1;i--){
            if (value!=series.get(sampleSize-i))
                return false;
        }
        return true;
    }
    
    
    public static boolean KSTest(List<Double> series1, List<Double> series2, double alpha, int n){
        if (checkLastElements(series2,n))
            return true;
        else{
            int sampleSize1=series1.size();
            int sampleSize2=series2.size();
            Collections.sort(series1);
            Collections.sort(series2);
            double distance=getSupDistance(merge(series1,series2),sampleSize1,sampleSize2);
            return finalTest(distance,alpha,sampleSize1,sampleSize2);
        }
    }
    
    public static boolean KSTest(List<List<Double>> sampleCollection1, List<List<Double>> sampleCollection2, int num, double alpha){
        for (int i=0;i<num;i++){
            List<Double> copy=new ArrayList<Double>();
            copy.addAll(sampleCollection1.get(i));
            if (!KSTest(copy, sampleCollection2.get(i), alpha)) return false;
        }
        return true;
    }
    
    public static boolean KSTest2(List<List<Double>> sampleCollection1, List<List<Double>> sampleCollection2, int num, double alpha){
        for (int i=0;i<num;i++){
            List<Double> copy=new ArrayList<Double>();
            List<Double> copy2=new ArrayList<Double>();
            copy.addAll(sampleCollection1.get(i));
            copy2.addAll(sampleCollection2.get(i));
            if (!KSTest(copy, copy2, alpha)) return false;
        }
        return true;
    }
}
