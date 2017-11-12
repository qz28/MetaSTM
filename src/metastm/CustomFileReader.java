/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metastm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CustomFileReader {
	private String[] commands;
        public int numberOfLines;

	public CustomFileReader(String file_path, int number_of_line) throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(file_path));
		String aLine;
		commands = new String[number_of_line];
		int i = 0;
		while ((aLine = bf.readLine()) != null) {
			commands[i] = aLine;
			i++;
                        if (i==number_of_line)break;
		}
		bf.close();
	}
        
        public CustomFileReader(String file_path) throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(file_path));
		String aLine;
                List<String> commandList=new ArrayList<>();
		int i = 0;
		while ((aLine = bf.readLine()) != null) {
			commandList.add(aLine);
			i++;
		}
                commands = new String[i];
                commandList.toArray(commands);
                numberOfLines=i;
		bf.close();
	}

	public String getCommand(int index) {
		return commands[index];
	}
	
	public String[] getCommandSpilt(int index){
		return commands[index].split("\t");
	}
        
        public double[] getNumericArray(int index){
            String[] stringArray=getCommandSpilt(index);
            double[] numericArray=new double[stringArray.length];
            for (int i=0;i<stringArray.length;i++){
                numericArray[i]=Double.parseDouble(stringArray[i]);
            }
            return numericArray;
        }
        
        public double[] getColumn(int index){
            double[] column=new double[numberOfLines];
            for (int i=0;i<numberOfLines;i++){
                column[i]=getNumericArray(i)[index];
            }
            return column;
        }
        
        public int[] getNumericIntArray(int index){
            String[] stringArray=getCommandSpilt(index);
            int[] numericArray=new int[stringArray.length];
            for (int i=0;i<stringArray.length;i++){
                numericArray[i]=Integer.parseInt(stringArray[i]);
            }
            return numericArray;
        }
	
        public List<Double> getNumericList(int index){
            String[] stringArray=getCommandSpilt(index);
            List<Double> numericList=new ArrayList<>();
            for (int i=0;i<stringArray.length;i++){
                numericList.add(Double.parseDouble(stringArray[i]));
            }
            return numericList;
        }
	

}