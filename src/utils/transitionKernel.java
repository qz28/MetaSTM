/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 *
 * @author John
 */
public class transitionKernel {
    
    private static NormalDistribution nr=new NormalDistribution(0,1);
    
    public static double normalTransition(double mean, double sd, double lowBound, double highBound){
        double sample;
        do{
            sample=mean+nr.sample()*sd;
        }while(sample<lowBound||sample>highBound);
        return sample;
    }
    
    public static double normalDensity(double mean, double sd, double x){
        return nr.density((x-mean)/sd);
    }

    
}
