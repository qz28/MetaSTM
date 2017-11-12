/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;

/**
 *
 * @author qz28
 */
public class PrintList {
    public static String printList(ArrayList<Double> a){
        StringBuilder sb=new StringBuilder();
        for (double s:a){
            sb.append(s).append("\t");
        }
        return sb.toString().trim();
    }
    
}