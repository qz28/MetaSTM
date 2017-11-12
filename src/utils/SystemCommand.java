/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author U1015181
 */
public class SystemCommand {
    
    public static void runSysCom(String[] a){
        try {
            Runtime.getRuntime().exec(a);
        } catch (IOException e) {
            e.printStackTrace();
    }
    }
    
    public static List<String> runSysComWithOutput(String[] a){
        List<String> output=new ArrayList<>();
        try {
            Process p = Runtime.getRuntime().exec(a);
            BufferedReader in = new BufferedReader(
                                new InputStreamReader(p.getInputStream()));
            String line = null; 
            while ((line = in.readLine()) != null) {
                output.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }
    
    public static void runSysComGetOutput(String[] a){
        //List<String> output=new ArrayList<>();
        try {
            Process p = Runtime.getRuntime().exec(a);
            BufferedReader in = new BufferedReader(
                                new InputStreamReader(p.getInputStream()));
            String line = null; 
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return output;
    } 
    
}
