/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.random.MathUtil;

/**
 *
 * @author qz28
 */
public class RandomSample {
	//NOTE: What sampling scheme/algorithm is this? what kind of randomness does it provide
	public static <T> Set<T> randomSample(List<T> items, int m) {
//		Random rnd = new Random();
		HashSet<T> res = new HashSet<>(m);
		int n = items.size();
		for (int i = n - m; i < n; i++) {
//			int pos = rnd.nextInt(i + 1);
			int pos = MathUtil.getNextInt(i);
			T item = items.get(pos);
			if (res.contains(item))
				res.add(items.get(i));
			else
				res.add(item);
		}
		return res;
	}
        
        public static int[] shuffle(int n) {
            int[] array=new int[n];
            for (int i = 0; i < array.length; i++){
                array[i]=i;
            }
            for (int i = 0; i < array.length; i++) {
                // Get a random index of the array past i.
                int random = i + MathUtil.getNextInt(n-i-1);;
                // Swap the random element with the present element.
                int randomElement = array[random];
                array[random] = array[i];
                array[i] = randomElement;
	}
            return array;
    }

}
