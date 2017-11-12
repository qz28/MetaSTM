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
public class DifferentElement {
	public static int differentElement(String str1, String str2) {
		int diff = 0;
		if (str1.length() == str2.length()) {
			for (int i = 0; i < str1.length(); i++) {
				if (str1.charAt(i) != str2.charAt(i)) {
					diff++;
				}
			}
		} else {
			System.out.printf("Different lengths for strings");
		}
		return diff;
	}

}
