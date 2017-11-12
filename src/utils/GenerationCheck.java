/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author U1015181
 */
public class GenerationCheck {
    
    public static List<Integer> generationCheck(int[][]generations, int gen){
        List<Integer> timePoints=new ArrayList<>();
        for (int i=0;i<generations.length;i++){
            if (gen>=generations[i][0]&&gen<=generations[i][1])
                timePoints.add(i);
        }
        return timePoints;
        
    }
    
    public static boolean generationEndCheck(int[][]generations, int gen){
        for (int i=0; i<generations.length;i++){
            if (gen==generations[i][1])
                return true;
        }
        return false;
        
    }
    
    public static boolean generationLoopStop(int[][]generations, int gen, int[] checkResults){
        for (int i=0;i<generations.length;i++){
            if (gen>generations[i][1]&&checkResults[i]==0)
                return true;
        }
        return false;
    }
    
    public static boolean statusCheck(int[] toBeChecked, int status){
        for (int i=0;i<toBeChecked.length;i++){
            if (toBeChecked[i]!=status)
                return false;
        }
        return true;
    }
    
}
