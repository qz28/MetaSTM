/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metastm;

/**
 *
 * @author qz28
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import utils.random.Multinomial2;
import utils.VectorAddition;
import utils.random.Binomial;

public class Individual implements Serializable{

	private double[] microbiome;//NOTE: is that possible to use int[]
	
	int numberEnvironmentalSpecies;
	int numberMicrobePerHost;
        transient Multinomial2 selfDist;
        double coef;
	transient double[] newMicrobes;
        int id;
        
	public Individual(double[] initial_microbiome, int nomph, int noes, int idNumber) {
		numberEnvironmentalSpecies = noes;
                id=idNumber;
                microbiome=new double[noes];
		System.arraycopy(initial_microbiome, 0, microbiome, 0, numberEnvironmentalSpecies);
		numberMicrobePerHost = nomph;
                coef=1.0/(numberMicrobePerHost);
                newMicrobes=new double[numberEnvironmentalSpecies];
                selfDist = new Multinomial2(numberEnvironmentalSpecies);
	}

	public String microbial_sequences() {
		char[] microbiome_sequence = new char[numberEnvironmentalSpecies];
		for (int i = 0; i < numberEnvironmentalSpecies; i++) {
			if (microbiome[i]*numberMicrobePerHost >= 1) {
				microbiome_sequence[i] = '1';
			} else {
				microbiome_sequence[i] = '0';
			}
		}
		return new String(microbiome_sequence);
	}
        
        public void environmental_acquisition(double[] EnvDist, int NumberOfAcquisition){
                   double prop=NumberOfAcquisition*1.0/(NumberOfAcquisition+numberMicrobePerHost);
                   double[] newEnvDist=new double[numberEnvironmentalSpecies];
                   VectorAddition.additionOfVectors(newEnvDist,1-prop,prop,microbiome,EnvDist);
                   selfDist.updateProb(newEnvDist);
                   selfDist.multisample(newMicrobes, numberMicrobePerHost);
               for (int i=0;i<numberEnvironmentalSpecies;i++){
                   microbiome[i]=newMicrobes[i]*coef;
               }
        }
        
        public void parental_acquisition(Multinomial2 ParentalDist){
               ParentalDist.multisample(microbiome,numberMicrobePerHost);
               for (int i=0;i<numberEnvironmentalSpecies;i++){
                   microbiome[i]=microbiome[i]*coef;
               }
        }


	public double[] getMicrobiome() {
		return microbiome;
	}
        
	public String printOut() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < numberEnvironmentalSpecies; i++) {
			sb.append(microbiome[i]).append("\t");
		}
		return sb.toString().trim();
	}

        private void readObject(ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException{
            inputStream.defaultReadObject();
            coef=1.0/(numberMicrobePerHost);
            newMicrobes=new double[numberEnvironmentalSpecies];
            selfDist = new Multinomial2(numberEnvironmentalSpecies);
        }
        
        public void antibioticsTreatment(double strengthOfAb, double[] EnvDist){
            double[] remainingMicrobes=new double[numberEnvironmentalSpecies];
            for (int i=0;i<numberEnvironmentalSpecies;i++){
                //if (strengthOfAb>0)
                remainingMicrobes[i]=Binomial.binomial_sampling(Math.round(numberMicrobePerHost*(float)microbiome[i]),1-strengthOfAb);
                //else
                //remainingMicrobes[i]=numberMicrobePerHost*microbiome[i];
            }
            double totalRemainingMicrobes=0;
            for (double abundance:remainingMicrobes){
                totalRemainingMicrobes+=abundance;
            }
            if (totalRemainingMicrobes>0){
                for (int i=0;i<numberEnvironmentalSpecies;i++){
                    microbiome[i]=remainingMicrobes[i]/totalRemainingMicrobes;
                }   
            }else{
                 selfDist.updateProb(EnvDist);
                 selfDist.multisample(microbiome, 1);
            }
        }

}
