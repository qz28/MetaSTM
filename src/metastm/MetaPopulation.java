/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metastm;

/**
 *
 * @author John
 */
import java.util.Arrays;
import java.io.IOException;
import java.util.Set;
import utils.RandomSample;
import utils.ObjectCloner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import utils.BinarySearch;
import utils.DiversityIndex;
import utils.KMeansCluster;
import utils.VectorAddition;
import utils.random.Binomial;
import utils.random.MathUtil;
import utils.random.Multinomial2;

/**
 *
 * @author qz28
 */
public class MetaPopulation {
    
    private int numberOfSubpopulation;
    private Population[] subpopulations;
    private double dispersalRate;
    private Multinomial2 migrantsDist;
    private int sampleReplicates;
    private double beta_coef1;
    private double beta_coef2;
    private int numberOfTotalIndividual;
    private int noOfMicrobePerHost;
    private double beta_diversity1;
    private double beta_diversity2;
    private double beta_diversity3;
    private double[] metaSum;
    private int[] numberOfHosts;
    private double[] cumHostNoInSp;
    private List<Double> BdSamples=new ArrayList<>();
    
    public MetaPopulation(int nosp, double dr, int numberOfMicrobePerHost, double[] environment, int noOfTIndividual, 
			double environmentalFactor,double pooledOrFixed, double SampleRate, int sampleReplicates0, double[][] inputMicrobiomes, CustomFileReader sp) throws IOException{
        numberOfSubpopulation=nosp;
        subpopulations=new Population[nosp];
        numberOfHosts=new int[nosp];
        dispersalRate=dr;
        migrantsDist=new Multinomial2(nosp);
        noOfMicrobePerHost=numberOfMicrobePerHost;
        numberOfTotalIndividual=noOfTIndividual;
        sampleReplicates=sampleReplicates0;
        metaSum=new double[environment.length];
        cumHostNoInSp=new double[nosp];
        int[] cluster=sp.getNumericIntArray(nosp-1);
        int firstId=0;
        Multinomial2 fixEnvDist=new Multinomial2(environment.length);
        fixEnvDist.updateProb(environment);
        for(int i=0;i<nosp;i++){
            double[][] groupOfMicrobiome=KMeansCluster.getSubset(inputMicrobiomes,i,cluster);

            subpopulations[i]=new Population(numberOfMicrobePerHost, fixEnvDist.mutisample(1000000000/nosp),
			environmentalFactor,pooledOrFixed, SampleRate , sampleReplicates0, groupOfMicrobiome, noOfTIndividual, 0,0,firstId,inputMicrobiomes.length);
            numberOfHosts[i]=groupOfMicrobiome.length;
            firstId+=groupOfMicrobiome.length;
            cumHostNoInSp[i]=firstId;
        }    
    }
    
//   public ArrayList<Individual> migration(int j) throws Exception{
//        ArrayList<Individual> migrants=new ArrayList<>();
//        for (int i=0;i<numberOfSubpopulation;i++){
//            Binomial.binomial_sampling(numberOfHosts[i],dispersalRate);
//        }
//        int num=Binomial.binomial_sampling(getPopulations()[j].numberOfIndividual, dispersalRate);
//        if (num>0)
//            migrantsDist.multisample(migrantsCount, num);
//        for (int i=0;i<numberOfSubpopulation;i++){
//            if (migrantsCount[i]>0){
//                Set<Integer> out = null;
//                if (migrantsCount[i]<=getPopulations()[i].numberOfIndividual)
//                    out=RandomSample.randomSample(getPopulations()[i].host_index,migrantsCount[i]);
//                else
//                    out=RandomSample.randomSample(getPopulations()[i].host_index,getPopulations()[i].numberOfIndividual);
//                for (Integer index:out){
//                        migrants.add((Individual)ObjectCloner.deepCopy(getPopulations()[i].getIndividuals()[index]));
//                    }
//            }
//        }
//        return migrants;
//    }
    
    public void dispersal() throws Exception{
        List<Individual> migrants=new ArrayList<>();
        List<Set<Integer>> migrantsIndex=new ArrayList<>();
        for (int i=0;i<numberOfSubpopulation;i++){
            int num=Binomial.binomial_sampling(numberOfHosts[i],dispersalRate);
            migrantsIndex.add(RandomSample.randomSample(getPopulations()[i].host_index, num));
            for (Integer id:migrantsIndex.get(i)){
                migrants.add((Individual)ObjectCloner.deepCopy(getPopulations()[i].getIndividuals()[id]));
            }
        }
        int[] shuffledIndex=RandomSample.shuffle(migrants.size());
        int index=0;
        for (int i=0;i<numberOfSubpopulation;i++){
            for (Integer id:migrantsIndex.get(i)){
                getPopulations()[i].getIndividuals()[id]=migrants.get(shuffledIndex[index]);
                index++;
            }
        }
        
    }
    
    public void getNextGen() throws Exception{
        int index=BinarySearch.binarySearch(cumHostNoInSp, MathUtil.getNextFloat(numberOfTotalIndividual));;
        for(int i=0;i<numberOfSubpopulation;i++){
            if (i==index){
                getPopulations()[i].getNextGen(true);
            }else{
                getPopulations()[i].getNextGen(false);
            }
        }
        if (numberOfSubpopulation>1&&dispersalRate>0){
            dispersal();
        }
    }
    
    public int getNumberOfGeneration(){
        return getPopulations()[0].getNumberOfGeneration();
    }
    
    public void sumSpecies(){
        Arrays.fill(metaSum,0);
        for(Population pop:getPopulations()){
            pop.sumSpecies();
            VectorAddition.additionOfVectors(metaSum, 1, 1.0*pop.numberOfIndividual/numberOfTotalIndividual,
					metaSum, pop.microbiomeSum);
        }
    }
    
    public double alphaDiversity(boolean sampleOrNot){
        double alpha_diversity=0;
        double alpha_coef=0;
        for (Population pop:getPopulations()){
            if (sampleOrNot){
                alpha_diversity+=pop.alphaDiversity(sampleOrNot)*pop.numberOfIndividual;
                alpha_coef+=pop.numberOfIndividual;}
            else{
                alpha_diversity+=pop.alphaDiversity(sampleOrNot)*pop.numberOfSamples;
                alpha_coef+=pop.numberOfSamples;
            }
        }
        return alpha_diversity/alpha_coef;
    }
    public double gammaDiversityWithin(boolean sampleOrNot){
         double gamma_total=0;
         for (Population pop:getPopulations()){
              gamma_total+=pop.gammaDiversity(sampleOrNot);
         }
         return gamma_total/numberOfSubpopulation;

    }   
    public double gammaDiversity(boolean sampleOrNot){
        double gamma_diversity;
        if (sampleOrNot)
             gamma_diversity=DiversityIndex.shannonWienerIndex(metaSum);
        else{
            double[] temp_sum=new double[metaSum.length];
            double count=0;
            for (int index=0;index<sampleReplicates;index++){
                for (Population pop:getPopulations()){
                    VectorAddition.additionOfVectors(temp_sum, 1.0, pop.numberOfSamples, temp_sum, pop.pooledSample(index));
                    count+=pop.numberOfSamples;
                }
            }
            gamma_diversity=DiversityIndex.shannonWienerIndex(temp_sum, count);
        }
        return gamma_diversity;
        
    }
    
    public double betaDiversityWithinSubs(boolean sampleOrNot){
        beta_diversity1=0;
        beta_coef1=0;
        int count;
        for (Population pop: getPopulations()){
            if (sampleOrNot)
                count=pop.numberOfIndividual*(pop.numberOfIndividual-1)/2;
            else
                count=pop.numberOfSamples*(pop.numberOfSamples-1)/2;
            beta_diversity1+=pop.BrayCurtis(sampleOrNot)*count;
            beta_coef1+=count;
    }
        beta_diversity1/=beta_coef1;
        return beta_diversity1;        
    }
    
    public double betaDiversityAmongSubs(boolean sampleOrNot){
        beta_diversity2=0;
        beta_coef2=0;
        if (numberOfSubpopulation>1){
        if (sampleOrNot){
            for (int i=1;i<numberOfSubpopulation;i++){
                for (int j=0; j<i;j++){
                    for (int m=0;m<getPopulations()[i].numberOfIndividual;m++){
                        for (int n=0;n<getPopulations()[j].numberOfIndividual;n++){
                            beta_diversity2+=DiversityIndex.BrayCurtis(getPopulations()[i].getIndividuals()[m].getMicrobiome(), getPopulations()[j].getIndividuals()[n].getMicrobiome());
                            beta_coef2+=1;
                        }  
                    }
                }
            }
        }
        else{
            for (int index=0;index<sampleReplicates;index++){
                beta_coef2=0;
                for (int i=1;i<numberOfSubpopulation;i++){
                    double[][] temp_microbiomes1=getPopulations()[i].sample(index);
                    if (temp_microbiomes1.length==0) continue;
                    for (int j=0;j<i;j++){
                        double[][] temp_microbiomes2=getPopulations()[j].sample(index);
                        if (temp_microbiomes2.length==0) continue;
                        for (int m=0;m<temp_microbiomes1.length;m++){
                            for (int n=0;n<temp_microbiomes2.length;n++){
                                beta_diversity2+=DiversityIndex.BrayCurtis(temp_microbiomes1[m],temp_microbiomes2[n]);
                                beta_coef2+=1;
                            }
                        }
                    }
                }
            }
            beta_diversity2/=sampleReplicates;
        }
        beta_diversity2/=beta_coef2;}
        return beta_diversity2;
        }
    
    public void betaSamplesAmongSub(){
        if (numberOfSubpopulation>1){
            for (int index=0;index<sampleReplicates;index++){
                for (int i=1;i<numberOfSubpopulation;i++){
                    double[][] temp_microbiomes1=getPopulations()[i].sample(index);
                    if (temp_microbiomes1.length==0) continue;
                    for (int j=0;j<i;j++){
                        double[][] temp_microbiomes2=getPopulations()[j].sample(index);
                        if (temp_microbiomes2.length==0) continue;
                        for (int m=0;m<temp_microbiomes1.length;m++){
                            for (int n=0;n<temp_microbiomes2.length;n++){
                                BdSamples.add(DiversityIndex.BrayCurtis(temp_microbiomes1[m],temp_microbiomes2[n]));
                            }
                        }
                    }
                }
            }
        }   
    }
    
    public List<List<Double>> metaSampleCollection(){
        List<List<Double>> metaSample=new ArrayList<>();
        for (int i=0;i<metaSum.length;i++){
            List<Double> newList=new ArrayList<>();
            metaSample.add(newList);
        }
        for (Population pop:getPopulations()){
            for (double[] microbiome: pop.sample(0)){
                for(int i=0;i<metaSum.length;i++){
                    metaSample.get(i).add(microbiome[i]);
                }
            }
        }
        return metaSample;
    }
    
    public double betaDiversityOverall(boolean sampleOrNot){
        beta_diversity3=(beta_diversity1*beta_coef1+beta_diversity2*beta_coef2)/(beta_coef1+beta_coef2);
        return beta_diversity3;
    }
    
    public double environmentDifference(){
        double environment_difference=0;
        int env_coef=0;
        for (int i=1;i<numberOfSubpopulation;i++){
            for (int j=0;j<i;j++){
                environment_difference+=DiversityIndex.BrayCurtis(getPopulations()[i].environmentalContribution, getPopulations()[j].environmentalContribution);
                //System.out.println(getPopulations()[i].environmentalContribution);
                //System.out.println(getPopulations()[j].environmentalContribution);  
                env_coef+=1;
            }
        }
        environment_difference/=env_coef;
        return environment_difference;
    }
    
    public String printOut(){
        StringBuilder sb = new StringBuilder();
	for (int i = 0; i < metaSum.length; i++) {
		sb.append(metaSum[i]).append("\t");
	}
	return sb.toString().trim();
    }

    public String printId(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getPopulations().length; i++) {
                sb.append(getPopulations()[i].printId()).append("\t");
        }
        return sb.toString().trim();
    }

    public String printAd(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getPopulations().length; i++) {
                sb.append(getPopulations()[i].printAd()).append("\t");
        }
        return sb.toString().trim();
    }

    public Population[] getPopulations(){
        return subpopulations;
    }
    
    public List<Double> AdOfSamples(){
        List<Double> AdSamples=new ArrayList<>();
        for (Population sp:getPopulations()){
            sp.AdOfSamples();
            AdSamples.addAll(sp.Ad);
        }
        Collections.sort(AdSamples);
        return AdSamples;
    }

    public List<Double> BdOfSamples(){
        BdSamples.clear();
        for (Population sp:getPopulations()){
            sp.BdOfSamples();
            BdSamples.addAll(sp.Bd);
        }
        betaSamplesAmongSub();
        Collections.sort(BdSamples);
        return BdSamples;
    }

    public double coreMicrobiomeWithinSub(){
        double count=0;
        for (int i=0;i<getPopulations().length; i++) {
            count+=getPopulations()[i].coreMicrobiome();
        }
        return count/getPopulations().length;

    }

    public int coreMicrobiomeOverall(){
        int count=0;
        for (int i=0; i< metaSum.length; i++) {
        int indicator=1;
        for (int z=0; z<getPopulations().length; z++){
        for (int j=0; j<getPopulations()[z].getIndividuals().length; j++) {
             if (getPopulations()[z].getIndividuals()[j].getMicrobiome()[i]==0) {
                indicator=0;
                break;
                }
        }
             if (indicator==0) break;
        }
             count+=indicator;
        }
        return count;

    }


}

