/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.random;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import static org.apache.commons.math3.util.FastMath.sqrt;

public class Pois {
    
    private PoissonDistribution pr;
    private NormalDistribution nr;
    private double Lamda;
    
    public Pois(double lamda){
        Lamda=lamda;
        if (lamda<=20&&lamda>0)
            pr=new PoissonDistribution(lamda);
        else if(lamda>20)
            nr=new NormalDistribution(lamda,sqrt(lamda));
        
    }
    
    public int sample(){
        if(Lamda==0)
            return 0;
        else if(Lamda<=20){
            return pr.sample();
        }
        else{
            int x=(int)nr.sample();
            if (x<0)return 0;
            else return x;
        }
    }
    
}
