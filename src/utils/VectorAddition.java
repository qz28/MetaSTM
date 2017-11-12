/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

/**
 *
 * @author John
 */
public class VectorAddition {
	public static void additionOfVectors(double[] sumResults, double weight1,
			double weight2, double[] v1, double[] v2) {
		
		int dim1 = v1.length;
		int dim2 = v2.length;
		if (dim1 >= dim2) {
			for (int i = 0; i < dim2; i++) {
				sumResults[i] = weight1 * v1[i] + weight2 * v2[i];
			}
			for (int i = dim2; i < dim1; i++) {
				sumResults[i] = weight1 * v1[i];
			}
		} else {
			for (int i = 0; i < dim1; i++) {
				sumResults[i] = weight1 * v1[i] + weight2 * v2[i];
			}
			for (int i = dim1; i < dim2; i++) {
				sumResults[i] = weight2 * v2[i];
			}
		}
		//NOTE: Usually you return new double[], but need to check length
		//There are two special cases of this, both of them sumResults==v1, w1==w2==1, can build a special case
	}
	public static void additionOfVectors(double[] sumResults, double weight1,
			double weight2, double[] v1, int[] v2) {
		
		int dim1 = v1.length;
		int dim2 = v2.length;
		if (dim1 >= dim2) {
			for (int i = 0; i < dim2; i++) {
				sumResults[i] = weight1 * v1[i] + weight2 * v2[i];
			}
			for (int i = dim2; i < dim1; i++) {
				sumResults[i] = weight1 * v1[i];
			}
		} else {
			for (int i = 0; i < dim1; i++) {
				sumResults[i] = weight1 * v1[i] + weight2 * v2[i];
			}
			for (int i = dim1; i < dim2; i++) {
				sumResults[i] = weight2 * v2[i];
			}
		}
		//NOTE: Usually you return new double[], but need to check length
		//There are two special cases of this, both of them sumResults==v1, w1==w2==1, can build a special case
	}
        
        public static double innerProduct(double[] v1, double[] v2){
            double result=0;
            for (int i=0;i<v1.length;i++){
                result+=v1[i]*v2[i];
            }
            return result;
        }
        
        public static double mean(double[] v){
            double miu=0;
            for(int i=0;i<v.length;i++){
                miu+=v[i];
            }
            return miu/v.length;
        }
        
        public static double variance(double[] v){
            double sigma=0;
            for (int i=0;i<v.length;i++){
                sigma+=v[i]*v[i];
            }
            double miu=mean(v);
            return sigma/v.length-miu*miu;
        }
        

}
