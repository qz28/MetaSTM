/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils.random;

import java.util.Arrays;

public class Multinomial2 {
//	static Random generator = new Random();
	
	private double[] distribution;
	private int rangeOne;


	// Constructor
	public Multinomial2(int range){
		this.rangeOne = range - 1;
		distribution = new double[range];
		
	}
	
	public void updateProb(double[] probabilities) {
		double cum_prob = 1;
		for (int i = 0; i < distribution.length; i++) {
			if (probabilities[i] == 0)
				distribution[i] = 0;
			else {
				distribution[i] = probabilities[i] / cum_prob;
                                if (distribution[i]>1)distribution[i]=1;
                                if (distribution[i]<0)distribution[i]=0;
				cum_prob = cum_prob - probabilities[i];
			}
		}
	}

	public void multisample(double[] sampledNumbers, int sampleSize) {
		
		for (int i = 0; i < rangeOne; i++) {
			int rand = Binomial.binomial_sampling(sampleSize, distribution[i]);
			sampledNumbers[i] = rand;
			sampleSize -= rand;

			if(sampleSize==0){
				Arrays.fill(sampledNumbers, i+1, sampledNumbers.length, 0);
				break;
			}
		}
		sampledNumbers[rangeOne] = sampleSize;
	}
        
        public double[] mutisample(int sampleSize){
            double[] sampledNumbers=new double[distribution.length];
            multisample(sampledNumbers,sampleSize);
            for(int i=0;i<distribution.length;i++){
                sampledNumbers[i]=sampledNumbers[i]/sampleSize;
            }
            return sampledNumbers;
        }
        
        public void multisample(int[] sampledNumbers, int sampleSize) {
		
		for (int i = 0; i < rangeOne; i++) {
			int rand = Binomial.binomial_sampling(sampleSize, distribution[i]);
			sampledNumbers[i] = rand;
			sampleSize -= rand;

			if(sampleSize==0){
				Arrays.fill(sampledNumbers, i+1, sampledNumbers.length, 0);
				break;
			}
		}
		sampledNumbers[rangeOne] = sampleSize;
	}
        
        
}


/*
 * 
 * no casting
 * 31.942255827
31.528336549
31.921987934
32.261226736
34.566174233
32.779136309
32.303310276
32.494381214

casting
 * 32.260057271
37.427275299
33.419141852
33.944452794
36.196915714
34.478330924
35.379204314
34.468304218
 */
