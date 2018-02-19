# MetaSTM

Simulates the short-term dynamics of microbiomes within a population of hosts with host subpopulation structure and dispersal limitation.


### Parameters

Under our neutral model, several parameters are adjustable:
  1. pct_evn: Percentage of environmental acquisition,  (1-exp(-a))/a is the expected average percentage of parental microbes over one host generation, where a=pct_evn/(1-pct_evn).
  2. pct_pool: Percentage of pooled component in the environment.
  3. Ns: number of host subpopulations.
  4. Dr: the expected percentage of hosts who migrate within one time step.
  5. population size: the population size of hosts.
  6. microbe size: number of microbes associated with one host.
  7. number of species: the total number of species in the environment.
  8. number of generations: the number of host generations that will be simulated.
  9. number time steps for observation: every this many time steps the diversities and other summary statistics are calculated
  10. replication: the number of simulation with the same parameters you want to repeat  

### Inputs
  1. environment.txt: a text file specifying the abundance distribution in the fixed environmental component. The number of lines must be equal to the number of species parameter.
  2. simulated_initial.txt: a text file specifying the initial abundance distribution in the host population. Each line corresponds to the microbiome composition of one host and the number of lines is equal to host population size parameter. 
  3. simulated_sub.txt: a text file specifying the initial assignment of hosts to subpopulations. The ith line corresponds to the assignment when Ns=i and the number of lines is equal to host population size parameter.
  
We also provide a python script called "simulate_initial_sub.py" which takes a input file "samples.txt" (containing empirical microbiome compositional data) to simulate 4096 or a given number of microbiome samples and also generate a simulated_sub.txt file for you.
Run it from terminal with
 ```bash
python simulate_initial_sub.py <numberOfHosts>
```
The argument numberOfHosts is optional, the default value is 4096 if it is not provided. 

Note: this script requires your python has numpy and sklearn packages installed and may take up to two days to finish since generating host assignment is computationally intensive. But it only needs to be run for once as long as your samples.txt is not updated. 

### Requirement:
   * [**Java 8**](https://www.java.com/)

### Install
#### Option 1:
Download the released version `MetaSTM_*.zip` and uncompress it. Run it from terminal with
```bash
./bin/metastm
```
or (for windows users)
```bash
.\bin\win_metastm
```

#### Option 2:
Clone git repository and the jar file can be found under the `release` folder.
Alternative, you can recompile it with [ant](http://ant.apache.org/).
```bash
cd MetaSTM
ant release
./release/MetaSTM_v1.0/bin/metastm
```
or (for windows users)
```bash
cd MetaSTM
ant release
.\release\MetaSTM_v1.0\bin\win_metastm
```


### Usage
##### Help Menu
```bash
./bin/metastm -h
```

##### Case 1

Four arguments provided for percentage of environmental acquisition, and percentage of pooled component in the environment, number of host subpopulation and host dispersal rate.
```bash
./bin/metastm 0.2 0.5 16 0.001
#./bin/metastm  pct_env pct_pool Ns Dr
#Effectively equals
#./bin/metastm  0.2 0.5 16 0.001 -c 4096 1000000 129 1
```
the default settings for other parameters are following:
  - population size=4096
  - microbe size=1000,000
  - number of species=129
  - number of generations=1

##### Case 2

```bash
./bin/metastm 0.2 0.5 16 0.001 -c 500 200000 128 5
#./bin/metastm pctEnv pctPool Ns Dr -c Pop Micro Spec Gen
```
To run the simulation from terminal with eight arguments taken.
- pctEnv: percentage of environmental acquisition
- pctPool: percentage of pooled component in the environment
- Ns: number of subpopulation
- Dr: host migration rate
- Pop: population size
- Micro: microbe size
- Spce: number of species
- Gen: number of generations



##### Additional parameters
  - `--obs` Number generation for observation [default: 128]
  - `--rep`Number of replication [default: 1]




## Development

Our selection and horizontal gene transfer (HGT) model are still under developing process.
