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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import utils.Distance;
import utils.DiversityIndex;
import utils.RandomSample;
import utils.VectorAddition;
import utils.random.Binomial;
import utils.random.MathUtil;
import utils.random.Multinomial2;
import utils.random.Pois;

public class Population {
	//FIXME: encapsulation these fields, way too many public
	final int numberOfMicrobePerHost;
	final int numberOfEnvironmentalSpecies;
	final int numberOfIndividual;
	private final double environmentalFactor;
	final double percentageOfpooledOrFixed;
	
	private int numberOfGeneration;
        private int populationSize;
        private double expectedInvasions;
        double[] environmentalContribution;
	double[] initialEnvironment;
	double[] microbiomeSum;
        private double[] initialMicrobiomeSum;
	private double g_diversity;
	private double a_diversity;
	private double b_diversity;
        private double weighted_b_diversity;

	private int sampleReplicates;
	int numberOfSamples;
	private double[] previous_microbiomes;
	private Individual[] compositionOfIndividuals;
	private List<Set<Integer>> samples = new ArrayList<>();
	List<Integer> host_index = new ArrayList<>();
        List<Double> Ad=new ArrayList<>();
        List<Double> Bd=new ArrayList<>();
	private double beta_diversity_coef;
        private double beta_diversity_coef_2;
        private double alpha_diversity_coef;
	
	Multinomial2 multiNomialDist;
        Pois PoissonDist;
        double AbFrequency;
        double AbStrength;
       
	
	public Population(int numberOfMicrobePerHost, double[] environment, 
			double environmentalFactor,
			double pooledOrFixed, double SampleRate, int sampleReplicates, double[][] fr, int timestep, double AbFrequency, double AbStrength, int firstId, int totalSize) throws IOException {
		this.numberOfMicrobePerHost = numberOfMicrobePerHost;
		this.numberOfEnvironmentalSpecies = environment.length;
		this.numberOfIndividual = fr.length;
		this.environmentalFactor = environmentalFactor;
		this.percentageOfpooledOrFixed = pooledOrFixed;
		this.numberOfGeneration = 0;
		this.sampleReplicates = sampleReplicates;				
		this.numberOfSamples= Binomial.binomial_sampling(numberOfIndividual,SampleRate);
                this.AbFrequency=AbFrequency;
                this.AbStrength=AbStrength;
                populationSize=totalSize;
                expectedInvasions=environmentalFactor/(1-environmentalFactor)*numberOfMicrobePerHost/timestep;
		initialEnvironment = environment;
		compositionOfIndividuals = new Individual[numberOfIndividual];
                environmentalContribution = new double[numberOfEnvironmentalSpecies];
		microbiomeSum = new double[numberOfEnvironmentalSpecies];
                initialMicrobiomeSum = new double[numberOfEnvironmentalSpecies];
		previous_microbiomes = new double[numberOfEnvironmentalSpecies];
		
		multiNomialDist = new Multinomial2(numberOfEnvironmentalSpecies);
                PoissonDist=new Pois(expectedInvasions);
		
		beta_diversity_coef = 2.0  / numberOfIndividual
				/ (numberOfIndividual - 1);
                beta_diversity_coef_2 = numberOfSamples
				*(numberOfSamples - 1)
				*sampleReplicates/2.0;
                alpha_diversity_coef=numberOfSamples*sampleReplicates;
		for (int i = 0; i < numberOfIndividual; i++) {
			host_index.add(i);
		}
		for (int i = 0; i < sampleReplicates; i++) {
			samples.add(RandomSample.randomSample(host_index,
					this.numberOfSamples));
		}
		for (int i = 0; i < numberOfIndividual; i++) {
			compositionOfIndividuals[i] = new Individual(
					fr[i], this.numberOfMicrobePerHost,
					numberOfEnvironmentalSpecies, firstId+i);
		}
	}

	public void sumSpecies() {
                //System.out.println(Arrays.toString(environmentalContribution));
		Arrays.fill(microbiomeSum, 0);
		for (Individual host : getIndividuals()) {
			VectorAddition.additionOfVectors(microbiomeSum, 1, 1,
					microbiomeSum, host.getMicrobiome());
		}
		for (int i = 0; i < numberOfEnvironmentalSpecies; i++) {
			microbiomeSum[i] = microbiomeSum[i] / numberOfIndividual;
		}
                if(numberOfGeneration==0)
                    System.arraycopy( microbiomeSum, 0, initialMicrobiomeSum, 0, numberOfEnvironmentalSpecies );   
                //System.out.println(Arrays.toString(environmentalContribution));  
	}


	public double interGenerationDistance() {
		if (getNumberOfGeneration() == 0)
			return 0;
		else {
			return Distance.getDistance(previous_microbiomes, microbiomeSum);
		}
	}

	public double environmentPopulationDistance() {
		if (getNumberOfGeneration() == 0)
			//TODO: can we merge these two??
			return Distance.getDistance(initialEnvironment, microbiomeSum);
		else
			return Distance.getDistance(environmentalContribution,
					microbiomeSum);
	}
        
        public double distanceToInitial(){
            return Distance.getDistance(initialMicrobiomeSum,microbiomeSum);
        }

        public void AntibioticsTreatment(double[] EnvDist){
            int numberOfAffectedHosts=Binomial.binomial_sampling(numberOfIndividual, AbFrequency);
            Set<Integer> affectedHosts=RandomSample.randomSample(host_index, numberOfAffectedHosts);
            for (Integer hostIndex:affectedHosts){
                getIndividuals()[hostIndex].antibioticsTreatment(AbStrength,EnvDist);
            }
            
        }
	public void parentalInheritanceAndEnvironmentalAcquisition(boolean parental_or_not) {
		if (parental_or_not){
                    int birth=MathUtil.getNextInt(numberOfIndividual-1);
                    int death=MathUtil.getNextInt(numberOfIndividual-1);
		
                    multiNomialDist.updateProb(getIndividuals()[birth].getMicrobiome());
                    getIndividuals()[death].parental_acquisition(multiNomialDist);
                    getIndividuals()[death].id=populationSize+numberOfGeneration;
                }
                //System.out.println(Arrays.toString(environmentalContribution));
		VectorAddition.additionOfVectors(environmentalContribution,
				percentageOfpooledOrFixed, 1 - percentageOfpooledOrFixed, microbiomeSum,
				initialEnvironment);
                //System.out.println(environmentalContribution);
                //System.out.println(Arrays.toString(initialEnvironment));
                if (AbFrequency>0 && AbStrength>0){
                    AntibioticsTreatment(environmentalContribution);
                }
                for (int i = 0; i < numberOfIndividual; i++) {
                        getIndividuals()[i].environmental_acquisition(environmentalContribution,PoissonDist.sample());
                }

                //System.out.println(Arrays.toString(environmentalContribution));

	}
        
	
	public void getNextGen(boolean parental_or_not) {
                //System.out.println(Arrays.toString(environmentalContribution));
		parentalInheritanceAndEnvironmentalAcquisition(parental_or_not);
                //System.out.println(Arrays.toString(environmentalContribution));
		for (int i = 0; i < sampleReplicates; i++) {
			samples.set(i,RandomSample.randomSample(host_index, numberOfSamples));
		}
		
//		for (int i = 0; i < numberOfEnvironmentalSpecies; i++) {
//			previous_microbiomes[i] = microbiomeSum[i];
//		}
		//NOTE: System.arraycopy is faster
		System.arraycopy(microbiomeSum, 0, previous_microbiomes, 0, microbiomeSum.length);

		//example of IDE overdo it
//		setNumberOfGeneration(getNumberOfGeneration() + 1);
		numberOfGeneration++;
                //System.out.println(Arrays.toString(environmentalContribution));
	}

        public double[][] sample(int i){
            Set<Integer> sampleId=samples.get(i);
            double[][] sampleMicrobiomes=new double[numberOfSamples][];
            int m=0;
            for (Integer index:sampleId){
                 sampleMicrobiomes[m]=getIndividuals()[index].getMicrobiome();
                 m++;
            }
            return sampleMicrobiomes;
        }
        
        public double[] pooledSample(int i){
            Set<Integer> sampleId=samples.get(i);
            double[] temp_sum = new double[numberOfEnvironmentalSpecies];
            for (Integer index : sampleId) {
		VectorAddition.additionOfVectors(temp_sum, 1, 1.0/numberOfSamples,
							temp_sum,
							getIndividuals()[index].getMicrobiome());
				}
            return temp_sum;
        }

	public double alphaDiversity(boolean sampleOrNot) {
                a_diversity = 0;
		if (sampleOrNot) {
			for (Individual host : getIndividuals()) {
				a_diversity+=DiversityIndex.shannonWienerIndex(host.getMicrobiome());
			}
			a_diversity /=  numberOfIndividual;
		}
                else if (numberOfSamples!=0){
			for (Set<Integer> sample : samples) {
				for (Integer index : sample) {
					a_diversity+=DiversityIndex.shannonWienerIndex(getIndividuals()[index].getMicrobiome());
				}
			}
                        a_diversity /= alpha_diversity_coef;
		}
		return a_diversity;

	}
        
        public void AdOfSamples(){
            Ad.clear();
            if (numberOfSamples!=0){
			for (Set<Integer> sample : samples) {
				for (Integer index : sample){
                                        Ad.add(DiversityIndex.shannonWienerIndex(getIndividuals()[index].getMicrobiome()));
				}
			}
                        a_diversity /= alpha_diversity_coef;
		}
            
        }

	public double betaDiversity(boolean sampleOrNot) {
                b_diversity=0;
		if (sampleOrNot) {
			for (int i = 1; i < numberOfIndividual; i++) {
				for (int j = 0; j < i; j++) {
					b_diversity+=DiversityIndex.PiIndex(getIndividuals()[i].getMicrobiome(), getIndividuals()[j].getMicrobiome());
				}
			}
			
			b_diversity *= beta_diversity_coef;
		} 
                else if (numberOfSamples>1){
                        for (int index=0;index<sampleReplicates;index++){
                            double[][] temp_microbiomes=sample(index);
                            for (int i = 1; i < numberOfSamples; i++) {
                                for (int j = 0; j < i; j++) {
                                    b_diversity += DiversityIndex.PiIndex(temp_microbiomes[i], temp_microbiomes[j]);
                                }
                            }
                        }
                        
			b_diversity /= beta_diversity_coef_2; 
		}
                return b_diversity;
	}

	public double gammaDiversity(boolean sampleOrNot) {
                g_diversity=0;
		if (sampleOrNot) {
			g_diversity = DiversityIndex.shannonWienerIndex(microbiomeSum);
		} else if (numberOfSamples!=0){
                        for(int index=0;index<sampleReplicates;index++){
                            double[] temp_sum=pooledSample(index);
                            g_diversity+=DiversityIndex.shannonWienerIndex(temp_sum);
                        }
			g_diversity = g_diversity / sampleReplicates;	
		}
                return g_diversity;
	}
        
        public double BrayCurtis(boolean sampleOrNot){
            weighted_b_diversity=0;
            if (sampleOrNot){
                for (int i=1;i<numberOfIndividual;i++){
                    for (int j=0;j<i;j++){
                        weighted_b_diversity+= DiversityIndex.BrayCurtis(getIndividuals()[i].getMicrobiome(), getIndividuals()[j].getMicrobiome());
                    }
                }
                weighted_b_diversity*=beta_diversity_coef;
            }
            else if (numberOfSamples>1){
                for (int index=0;index<sampleReplicates;index++){
                            double[][] temp_microbiomes=sample(index);
                            for (int i = 1; i < numberOfSamples; i++) {
                                for (int j = 0; j < i; j++) {
                                    weighted_b_diversity += DiversityIndex.BrayCurtis(temp_microbiomes[i], temp_microbiomes[j]);
                                }
                            }
                        }
                if (numberOfSamples!=0)
			weighted_b_diversity /= beta_diversity_coef_2;
            }
                return weighted_b_diversity;
        }
        
        public void BdOfSamples(){
            Bd.clear();
            if (numberOfSamples>1){
                for (int index=0;index<sampleReplicates;index++){
                            double[][] temp_microbiomes=sample(index);
                            for (int i = 1; i < numberOfSamples; i++) {
                                for (int j = 0; j < i; j++) {
                                    Bd.add(DiversityIndex.BrayCurtis(temp_microbiomes[i], temp_microbiomes[j]));
                                }
                            }
                        }
            }
            
        }

	public String printOut() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < numberOfEnvironmentalSpecies; i++) {
			sb.append(microbiomeSum[i]).append("\t");
		}
		return sb.toString().trim();
	}
        public String printId(){
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < getIndividuals().length; i++) {
                        sb.append(getIndividuals()[i].id).append("\t");
                }
                return sb.toString().trim();

        }

        public String printAd(){
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < getIndividuals().length; i++) {
                        sb.append(DiversityIndex.shannonWienerIndex(getIndividuals()[i].getMicrobiome())).append("\t");
                }
                return sb.toString().trim();
        }
        public int coreMicrobiome(){
                int count=0;
                for (int i=0; i< numberOfEnvironmentalSpecies; i++) {
                      int indicator=1;
                      for (int j=0; j< getIndividuals().length; j++) {
                            if (getIndividuals()[j].getMicrobiome()[i]==0) {
                                 indicator=0;
                                 break;
                            }   
                      }
                      count+=indicator;
                }
                return count;
        }
	public int getNumberOfGeneration() {
		return numberOfGeneration;
	}

	public void resetGeneration() {
		numberOfGeneration = 0;
		
	}
        
        public Individual[] getIndividuals(){
            return compositionOfIndividuals;
        }


}

