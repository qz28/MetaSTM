package utils.random;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

public class MathUtil {

	private static RandomDataGenerator rd = new RandomDataGenerator(
			new MersenneTwister());

//	public MathUtil() {
//		// TODO Auto-generated constructor stub
//	}
	
	

	public static double nextFloat(){
		return rd.nextUniform(0, 1);
	}
        
        public static double getNextFloat(double max){
		return rd.nextUniform(0, 1)*max;
	}
        
	public static int getNextInt(int max){
//		System.out.println(max);
		return rd.nextInt(0, max);
	}
	
//	static BinomialDistribution BD = new BinomialDistribution(new MersenneTwister(), 0, 0.5);
//	public static int getNextBinomial(int n, double p){
//		System.out.println(n*p +"\t"+ n +"\t"+ p);
//		return rd.nextBinomial(n, p);
//	}
//	
//	public static int getNextBinomial2(int n, double p){
//		BD = new BinomialDistribution(rd.getRandomGenerator(), n, p);
//		
//		return BD.sample();
//	}
//	
//	static   PsRandom ran = new PsRandom();
//	public static int getNextBinomial3(int n, double p){
//		return (int) ran.nextBinomial(p, n);
////		return BD.sample();
//	}
//	
//	static MersenneTwisterRNG mt = new MersenneTwisterRNG();
//	public static int getNextBinomial4(int n, double p){
//		BinomialGenerator bg = new BinomialGenerator(n, p, mt );
//		int value = bg.nextValue();
//		System.out.println(value);
//		return value; 
////		return BD.sample();
//	}
//	
//	static cern.jet.random.Binomial bb = new cern.jet.random.Binomial(10, 0.5, new cern.jet.random.engine.MersenneTwister());
//	public static int getNextBinomial5(int n, double p){
//		return bb.nextInt(n, p) ;
////		return BD.sample();
//	}
//	
//	public static int getNextBinomial6(int n, double p){
//		
////		public static int getBinomial(int n, double p) {
//		  int x = 0;
//		  for(int i = 0; i < n; i++) {
//		    if(rd.nextUniform(0, 1) < p)
//		      x++;
//		  }
//		  return x;
//	}
//	
////	}
////	public static void setRd(RandomDataGenerator rd) {
////		MathUtils.rd = rd;
////	}

	
}
