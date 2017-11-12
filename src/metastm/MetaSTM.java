/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metastm;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
/**
 *
 * @author John
 */
public class MetaSTM{
	private static final String VERSION = "1.0";
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException, Exception {//Integer.parseInt(parameters[1]);
        int Nm=1000000;//Integer.parseInt(parameters[2]);
        int No=129;//Integer.parseInt(parameters[3]);
        int Nc=4096;
        int Ng=Nc;
        int Obs=128;
        int Rep=1;
        double environmental_factor=0.6144391;//{0,0.1766537,0.3170391,0.4322806,0.5296909,0.6144391,0.6905569,0.7617379,0.8323586,0.9090872,0.990099};
        double pooled_or_fixed=0.5;//{0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1};
        int Ns=4;
        double dispersal_rate=0;
        
		Options options = new Options();

		Option help = new Option("h", "help", false, "print this message");
		Option version = new Option("v", "version", false,
				"print the version information and exit");
		options.addOption(help);
		options.addOption(version);
		
		options.addOption(Option.builder("o").longOpt("obs").hasArg()
				.argName("OBS").desc("Number generation for observation [default: 128]")
				.build());
		options.addOption(Option.builder("r").longOpt("rep").hasArg()
				.argName("REP").desc("Number of replication [default: 1]")
				.build());
					
		Builder C = Option.builder("c").longOpt("config")
				.numberOfArgs(4).argName("Pop Micro Spec Gen")
				.desc("Four Parameters in the following orders: "
						+ "(1) population size, (2) microbe size, (3) number of species, (4) number of generation"
						+ " [default: 4096 1000000 128 1]");
		options.addOption(C.build());

		HelpFormatter formatter = new HelpFormatter();
		String syntax = "microbiosima pctEnv pctPool";
		String header = 
				"\nSimulates the evolutionary and ecological dynamics of microbiomes within a population of hosts.\n\n"+
"required arguments:\n"+
"  pctEnv             Percentage of environmental acquisition\n"+
"  pctPool            Percentage of pooled environmental component\n"+
"  Ns                 Number of subpopulations\n"+
"  Dr      Percentage of hosts who migrate within one time step"
+ "\noptional arguments:\n";
		String footer = "\n";
		
		formatter.setWidth(80);

		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
			String[] pct_config = cmd.getArgs();
			
			if (cmd.hasOption("h") || args.length == 0) {
				formatter.printHelp(syntax, header, options, footer, true);
				System.exit(0);
			}
			if(cmd.hasOption("v")){
				System.out.println("MetaSTM "+VERSION);
				System.exit(0);
			}
			if (pct_config.length != 4){
				System.out
						.println("ERROR! Required exactly four argumennts for pct_env, pct_pool, Ns and Dr. It got "
								+ pct_config.length + ": " + Arrays.toString(pct_config));
				formatter.printHelp(syntax, header, options, footer, true);
				System.exit(3);
			}
			else{
				environmental_factor = Double.parseDouble(pct_config[0]);
				pooled_or_fixed = Double.parseDouble(pct_config[1]);
				Ns=Integer.parseInt(pct_config[2]);
				dispersal_rate=Double.parseDouble(pct_config[3]);
				if(environmental_factor<0 || environmental_factor >1){
					System.out.println(
						"ERROR: pctEnv (Percentage of environmental acquisition) must be between 0 and 1 (pctEnv="
						+ environmental_factor + ")! EXIT");
					System.exit(3);
				}
				if(pooled_or_fixed<0 || pooled_or_fixed >1){
					System.out.println(
						"ERROR: pctPool (Percentage of pooled environmental component must) must be between 0 and 1 (pctPool="
						+ pooled_or_fixed + ")! EXIT");
					System.exit(3);
				}
				if(dispersal_rate<0 || dispersal_rate >1){
					System.out.println(
						"ERROR: pctPool (Host migration rate must) must be between 0 and 1 (Dr="
						+ dispersal_rate + ")! EXIT");
					System.exit(3);
				}
				if(Ns<0 || Ns >Nc){
					System.out.println(
						"ERROR: pctPool (Host migration rate must) must be an integer between 0 and population size:"+Nc+"(Ns="
						+ dispersal_rate + ")! EXIT");
					System.exit(3);
				}
				
			}
			if (cmd.hasOption("config")){
				String[] configs = cmd.getOptionValues("config");
				Nc = Integer.parseInt(configs[0]);
				Nm = Integer.parseInt(configs[1]);
				No = Integer.parseInt(configs[2]);
				Ng = Nc*Integer.parseInt(configs[3]);
			}
			if (cmd.hasOption("obs")){
				Obs= Integer.parseInt(cmd.getOptionValue("obs"));
			}
			if (cmd.hasOption("rep")){
				Rep= Integer.parseInt(cmd.getOptionValue("rep"));
			}			
			
			
			
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(3);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("Configuration Summary:")
			.append("\n\tPopulation size: ").append(Nc)
			.append("\n\tMicrobe size: ").append(Nm)
			.append("\n\tNumber of species: ").append(No)
			.append("\n\tNumber of generation: ").append(Ng/Nc)
			.append("\n\tNumber generation for observation: ").append(Obs)
			.append("\n\tNumber of replication: ").append(Rep)
			.append("\n");
		System.out.println(sb.toString());	
        
        
        double[] environment=new double[No];
        CustomFileReader env = new CustomFileReader("environment.txt",No);
        for (int i=0;i<No;i++){
            environment[i]=Double.parseDouble(env.getCommand(i));
        }
        
        for (int rep=0;rep<Rep;rep++){
        String prefix=Integer.toString(rep)+"_"+Double.toString(environmental_factor)+"_"+Double.toString(pooled_or_fixed)+"_"+Double.toString(Ns)+"_"+Double.toString(dispersal_rate);
        PrintWriter file1= new PrintWriter(new BufferedWriter(new FileWriter(prefix+"_gamma_diversity.txt")));
        PrintWriter file2= new PrintWriter(new BufferedWriter(new FileWriter(prefix+"_alpha_diversity.txt")));
        PrintWriter file3= new PrintWriter(new BufferedWriter(new FileWriter(prefix+"_beta_diversity.txt")));
        PrintWriter file4= new PrintWriter(new BufferedWriter(new FileWriter(prefix+"_sum.txt")));
        PrintWriter file7= new PrintWriter(new BufferedWriter(new FileWriter(prefix+"_individual.txt")));
        PrintWriter file8= new PrintWriter(new BufferedWriter(new FileWriter(prefix+"_environment_difference.txt")));
        PrintWriter file9= new PrintWriter(new BufferedWriter(new FileWriter(prefix+"_host_id.txt")));
        PrintWriter file10= new PrintWriter(new BufferedWriter(new FileWriter(prefix+"_host_ad.txt")));
        PrintWriter file11= new PrintWriter(new BufferedWriter(new FileWriter(prefix+"_core_microbiome.txt")));
        PrintWriter file12= new PrintWriter(new BufferedWriter(new FileWriter(prefix+"_within_gamma.txt")));


//		
        CustomFileReader fr = new CustomFileReader("simulated_initial.txt",Nc);
        double[][] inputMicrobiomes=new double[Nc][environment.length];
        for (int i=0;i<Nc;i++){
            int j=0;
            for (String s:fr.getCommandSpilt(i)){
                inputMicrobiomes[i][j]= Double.parseDouble(s)/Nm;
                j++;
            }
        }
        CustomFileReader sp = new CustomFileReader("simulated_sub.txt",Nc);
        MetaPopulation population=new MetaPopulation(Ns, dispersal_rate, Nm, environment, Nc, 
			environmental_factor, pooled_or_fixed, 0.1 ,3, inputMicrobiomes,sp);

        while (population.getNumberOfGeneration()<=Ng){
            population.sumSpecies();
            if(population.getNumberOfGeneration()%Obs==0){
                file1.println(population.gammaDiversity(false));
                file2.println(population.alphaDiversity(false)); 
                file3.print(population.betaDiversityWithinSubs(false));
                file3.print("\t");
                file3.print(population.betaDiversityAmongSubs(false));
                file3.print("\t");
                file3.println(population.betaDiversityOverall(false));
                file4.println(population.printOut());
                file8.println(population.environmentDifference());
                file9.println(population.printId());
                file10.println(population.printAd());
                file11.print(population.coreMicrobiomeWithinSub());
                file11.print("\t");
                file11.println(population.coreMicrobiomeOverall());
                file12.println(population.gammaDiversityWithin(false));
            }
            population.getNextGen();
        }
        for (Population pop:population.getPopulations()){
           for (Individual ind:pop.getIndividuals()){
                  file7.println(ind.printOut());
            }
        }
        file1.close();
        file2.close();
        file3.close();
        file4.close();
        file7.close();
        file8.close();
        file9.close();
        file10.close();
        file11.close();
        file12.close();

        }
    }
    
}

