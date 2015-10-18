package com.FPM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class FPM {
	public static void main( String[] args ) throws IOException
    {
		System.out.println( "----Start FPM----" );
        String inputfiledata = "./data/input.csv";
        Double minsup_percent = 0.5;
        Double minconf_percent = 0.6;
    	Integer count = 1;
    	Hashtable<Set<String>, Integer> table = new Hashtable<Set<String>, Integer>();
    	Hashtable<Integer, Set<String>> DataBase = new Hashtable<Integer, Set<String>>();
        try {
    		System.out.println( "----1st scan----" );
            FileReader fr = new FileReader(inputfiledata);
            BufferedReader br = new BufferedReader(fr);
        	String line = "";
        	String cvsSplitBy = ",";
        	while((line=br.readLine())!=null){
                // My columns are split by tabs with each entry in a new line as rows
                String spl[] = line.split(cvsSplitBy);
                DataBase.put(count, new HashSet<String>(Arrays.asList(spl)));
                for(int i=0;i<spl.length;i++){
                	Set<String> temp = new HashSet<String>(Arrays.asList(spl[i]));
                	if(table.containsKey(temp)){
                		table.put(temp, table.get(temp)+1);
                	} else {
                		table.put(temp, 1);
                	}
                }
                count++;
            }
        	br.close();
        } catch (Exception e) {
    		e.printStackTrace();
        }
        System.out.println("----Initial DataBase----");
		System.out.println(DataBase);
        System.out.println("----C1----");		
		System.out.println(table);
		Double minsup = minsup_percent*DataBase.size();
        for (Iterator<Map.Entry<Set<String>, Integer>> i = table.entrySet().iterator();i.hasNext();) {
            Map.Entry<Set<String>, Integer> entry = i.next();

            if(entry.getValue()<minsup){
                i.remove();
            }
        }
        System.out.println("----L1----");		
		System.out.println(table);
        for(int i=2;i<5;i++) {
        	if(i==2){
        		System.out.println("----2nd scan----");
        	} else {
        		System.out.println("----"+ i +"th scan----");
        	}
	        List<Set<String>> candidate = new ArrayList<>();
	        candidate.addAll(table.keySet());
	        table.clear();
        	//join step
        	ArrayList<Set<String>> subset = getSubset(candidate, candidate.size(), i, 2); 
            System.out.println("----Candidate----");		
    		System.out.println(subset);
        	//prune step
        	for (Iterator<Set<String>> s = subset.iterator(); s.hasNext();) {
        		Set<String> temp_sub = s.next();
        		ArrayList<String> temp_entry = new ArrayList<String>();
        		temp_entry.addAll(temp_sub);
        		ArrayList<Set<String>> splitted = new ArrayList<Set<String>>();
        		for (String temp_string : temp_entry) {
        			splitted.add(new HashSet<String>(Arrays.asList(temp_string)));
        		}
        		ArrayList<Set<String>> subsub = getSubset(splitted, splitted.size(), i-1, i-1);
        		for (Set<String> temp_subsub : subsub) {
        			if(!candidate.contains(temp_subsub)){
        				//System.out.println(temp_subsub);
        				s.remove();
        				break;
        			}
        		}
        	}
        	//System.out.println(subset);
        	candidate = new ArrayList<Set<String>>();
        	//supp computation
			for (Set<String> temp : subset) {
	        	int exist_flag = 0;
		        Set<Integer> DB = DataBase.keySet();
		        for(Integer key: DB){
		        	if(DataBase.get(key).containsAll(temp)){
		        		exist_flag = 1;
		        		if(table.containsKey(temp)){
	                		table.put(temp, table.get(temp)+1);
	                	} else {
	                		table.put(temp, 1);
	                	}
		        	}
		        }
		        if(exist_flag==0){
		        	table.put(temp, 0);
		        }
			}
            System.out.println("----C"+i+"----");		
    		System.out.println(table);
	        for (Iterator<Map.Entry<Set<String>, Integer>> s = table.entrySet().iterator();s.hasNext();) {
	            Map.Entry<Set<String>, Integer> entry = s.next();

	            if(entry.getValue()<minsup){
	                s.remove();
	            }
	        }
            System.out.println("----L"+i+"----");
			System.out.println(table);
        }
    }
	
    private static ArrayList<Set<String>> getSubset(List<Set<String>> input, int size, int subsize, int combinesize) {
		
    	ArrayList<Set<String>> output = new ArrayList<Set<String>>();
    	int[] binary = new int[(int) Math.pow(2, size)];
        for (int i = 0; i < Math.pow(2, size); i++) 
        {
            int b = 1;
            binary[i] = 0;
            int num = i, count = 0;
            while (num > 0) 
            {
                if (num % 2 == 1)
                    count++;
                binary[i] += (num % 2) * b;
                num /= 2;
                b = b * 10;
            }
            if (count == combinesize) 
            {
                Set<String> temp = new HashSet<>();
                for (int j = 0; j < size; j++) 
                {
                    if (binary[i] % 10 == 1) {
                        	temp.addAll(input.get(j));                     
                    }
                    binary[i] /= 10;
                }
	            if(temp.size()<=subsize&&!output.contains(temp)){
	                	output.add(temp);
	            }  
            }
        }
        //System.out.println(output);
        return output;
    }
}
