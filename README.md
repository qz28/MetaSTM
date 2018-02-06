# MetaSTM

Simulates the short-term dynamics of microbiomes within a population of hosts with host subpopulation structure and dispersal limitation.


### Parameters

Under our neutral model, several parameters are adjustable:
  1. pct_evn: Percentage of environmental acquisition, 1-pct_evn is the proportion of parental inheritance.
  2. pct_pool: Percentage of pooled component in the environment.
  3. Ns: number of host subpopulations.
  4. Dr: exected percentage of hosts who migrate within one time step.
  5. population size: the population size of hosts.
  6. microbe size: number of microbes associated with one host.
  7. number of species: the total number of species in the environment.
  8. number of generations: the number of host generations that will be simulated.
  9. number time steps for observation: every this many time steps the diversities and other summary statistics are calculated
  10. replication: the number of simulation with the same parameters you want to repeat  


### Requirement:
   * [**Java 8**](https://www.java.com/)

### Install
#### Option 1:
Download `MetaSTM_*.tar.gz` and uncompress it. Run it from terminal with
```bash
./bin/metastm
```

#### Option 2:
Clone git repository and the jar file can be found under the `release` folder.
Alternative, you can recompile it with [ant](http://ant.apache.org/).
```bash
cd MetaSTM
ant release
cd release/MetaSTM*
./bin/metastm
```


### Usage
##### Help Menu
```bash
./bin/metastm -h
```

##### Case 1

Four arguments provided for percentage of environmental acquisition, and percentage of pooled component in the environment, number of host subpopulation and host dispersal rate.
```bash
./bin/microbiosima 0.2 0.5 16 0.001
#./bin/microbiosima  pct_env pct_pool Ns Dr
#Effectively equals
#./bin/microbiosima  0.2 0.5 16 0.001 5000 1000000 129 1
```
the default settings for other parameters are following:
  - population size=5000
  - microbe size=1000,000
  - number of species=129
  - number of generations=1

##### Case 2

```bash
./bin/microbiosima 0.2 0.5 16 0.001 -c 500 200000 128 5
#./bin/microbiosima pctEnv pctPool Nc Dr -c Pop Micro Spec Gen
```
To run the simulation from terminal with six arguments taken.
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
