# -*- coding: utf-8 -*-
"""
Created on Fri Feb 16 15:06:41 2018

@author: qingl
"""

import numpy as np
from sklearn.preprocessing import normalize
from sklearn.cluster import KMeans
import sys

def truncMVN(data, num):
    data=normalize(data, axis=1, norm='l1')
    miu=np.mean(data, axis=0)
    sigma=np.cov(data, rowvar=False)
    simData=np.random.multivariate_normal(miu, sigma, num)
    simData[simData<0]=0
    simData=normalize(simData, axis=1, norm='l1')
    return simData

def kmeans_sub(data):
    n, assignment=data.shape[0], []
    for i in range(1, n+1):
        assignment.append(KMeans(n_clusters=i, n_init=5, max_iter=100, n_jobs=-1).fit_predict(data))
    return np.array(assignment)
    
data=np.loadtxt("samples.txt")
if len(sys.argv)<2: numOfSamples=int(sys.argv[1])
else: numOfSamples=4096
sim=truncMVN(data, numOfSamples)
np.savetxt("simulated_initial.txt", sim, delimiter="\t")
ass=kmeans_sub(sim)
np.savetxt("simulated_sub.txt", ass, fmt='%i', delimiter="\t")