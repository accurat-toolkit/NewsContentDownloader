/**
 * 
 */
package com.sheffield.newsContentDownloader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ahmetaker
 *
 */
public class Dictionary {
	
	public static Map<String, String> DICTIONARY_TARGET = new HashMap<String, String>();

	public static void init(String aTargetLang, String aSourceLang) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("dict/" + aTargetLang + "_" + aSourceLang + ".txt"), "UTF8"));
	    String s="";
	    while (true){
	    	s=br.readLine();
	    	if (s==null){
	    		break;
	    	}else{
	    		String t[]=s.split("\\  ");
	    		String w[]=t[0].split("\\ ");
	    		w[0]=w[0].toLowerCase();
	    		if (t.length==1){
	    			DICTIONARY_TARGET.put(w[0],w[1]);
	    		}else{
	    			String u[]=t[1].split("\\ ");
	    			Double v1=Double.parseDouble(w[2]);
	    			Double v2=Double.parseDouble(u[2]);
	    			if (v1>=0.3 &&v2<0.1){
	    				DICTIONARY_TARGET.put(w[0], w[1].trim()); //only keep one translation candidate
	    			}else{
	    				String ss=w[1]+" "+u[1];
	    				DICTIONARY_TARGET.put(w[0], ss);
	    			}
	    		}	
	    	}
	    }
	    br.close();
	}
	
}
