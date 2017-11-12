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
public class Distance {
	public static double getDistance(double[] v1, double[] v2) {

		double distance = 0;
		for (int i = 0; i < v1.length; i++) {
			double diff = v1[i] - v2[i];
			distance += diff * diff;
		}
		//TODO: check length
		return Math.sqrt(distance);
	}
}
