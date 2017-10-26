package com.sheffield.newsContentDownloader;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.knallgrau.utils.textcat.TextCategorizer;

import com.sheffield.util.Util;

import de.l3s.boilerpipe.extractors.CommonExtractors;

public class NewsContentDownloader {
	static TextCategorizer guesser = new TextCategorizer();

	public static void main(String[] args) {
		
		try {
			if (args.length < 7) {
				System.out.println("ERROR in argument list: java -jar newsContentDownloader pathToSourceInputFile pathToTargetInputFile pathToSaveOutput sourceLangCode targetLangCode threshold TRANSOPTION");
				return;
			}
			String fileNameSource = args[0];
			//fileNameSource = "d:\\testing\\google1.txt";
			String fileNameTarget = args[1];
			//fileNameTarget = "d:\\testing\\google.txt";
			String whereToSave = args[2];
			//whereToSave = "d:\\testing\\html\\";
			String sourceLang = args[3].toLowerCase();
			
			String targetLang = args[4].toLowerCase();
			double threshold = Double.parseDouble(args[5]);
			
			String transoption = args[6];
			
			Vector<String> charsTarget = null;
			Vector<String> wordsTarget = null;

			if (targetLang.equalsIgnoreCase("el") || targetLang.equals("lv") || targetLang.equals("lt") || targetLang.equals("et") || targetLang.equals("hr") || targetLang.equals("ro") || targetLang.equals("sl")) {
				charsTarget = Util.getFileContentAsVectorUTF(targetLang + "Char.txt");
				if (targetLang.equals("ro")) {
					wordsTarget = Util.getFileContentAsVectorUTF(targetLang + "Words.txt");
				}
			}

			Vector<String> charsSource = null;
			Vector<String> wordsSource = null;

			if (sourceLang.equalsIgnoreCase("el") || sourceLang.equals("lv") || sourceLang.equals("lt") || sourceLang.equals("et") || sourceLang.equals("hr") || sourceLang.equals("ro") || sourceLang.equals("sl")) {
				charsSource = Util.getFileContentAsVectorUTF(sourceLang + "Char.txt");
				if (sourceLang.equals("ro")) {
					wordsSource = Util.getFileContentAsVectorUTF(sourceLang + "Words.txt");
				}
			}

			
			
			boolean toTranslate = false;
			String translationFile = null;
			
			if (transoption.equalsIgnoreCase("EXIST")) {
				if (args.length < 8) {
					System.out.println("ERROR in argument list: Please specify a translation file to read from.");
					return;
				}
				translationFile = args[7];
			} else if (transoption.equalsIgnoreCase("DICT")) {
				toTranslate = true;
			} else {
				System.out.println("ERROR in argument list: Please specify the translation option correctly -- DICT or EXIST.");				
			}
			Vector<String> sourceLines = Util.getFileContentAsVectorUTF(fileNameSource);
			Vector<String> targetLines = Util.getFileContentAsVectorUTF(fileNameTarget);
			Vector<String> lines2Trans = null;
			Map<String, String> mapping = new HashMap<String, String>();

			StringBuffer alignment = new StringBuffer("sourceFile \t targetFile\t score\n");
			
			FileAligner.init();
			
			if (toTranslate) {
				Dictionary.init(targetLang, sourceLang);	
			} else {
				lines2Trans = Util.getFileContentAsVectorUTF(translationFile);
			}			
			for (int i = 0; i < sourceLines.size(); i++) {
				String sourceValues[] = sourceLines.get(i).split("\t");
				//Vector<Integer> sourceSaved = new Vector<Integer>();
				//Vector<Integer> targetSaved = new Vector<Integer>();
				for (int j = 0; j < targetLines.size(); j++) {
					String targetValues[] = targetLines.get(j).split("\t");
					String targetTitle = targetValues[2].toLowerCase();
					String targtToSourceTranslation = null;
					
					String targetTitleToTranslate[] = targetTitle.split("\\s");
					StringBuffer translation = new StringBuffer();
					if (toTranslate) {
						for (int k = 0; k < targetTitleToTranslate.length; k++) {
							String token = targetTitleToTranslate[k];
							token = token.replaceAll("\\.", "").replaceAll("'", "").replaceAll("\"", "");
							
			             	String candidate = Dictionary.DICTIONARY_TARGET.get(token);
		           	      if (candidate != null){
	           	    		  String str[]=candidate.split("\\ ");
	           	    		   for (int s=0;s < str.length;s++){
	            				 translation.append(str[s]).append(" ");
	           	    		   }
	           	    	  } else {
	           	    		  translation.append(token).append(" ");
	           	    	  }
						}						
						targtToSourceTranslation = translation.toString().trim();
					} else {
						targtToSourceTranslation = lines2Trans.get(j);
					}
					String sourceCode = sourceValues[1];
					String targetCode = targetValues[1];
					String path = sourceCode + "-" + targetCode;
					File folder = new File(whereToSave + "/" + path);
					folder.mkdir();
					File sourcePath = new File(folder.getAbsolutePath() + "/" + sourceCode);
					sourcePath.mkdir();
					File targetPath = new File(folder.getAbsolutePath() + "/" + targetCode);
					targetPath.mkdir();
					
					
					double score = FileAligner.alignDocuments(sourceValues[2], sourceValues[3], targtToSourceTranslation, targetValues[3], targetValues[2], threshold);
					//System.out.println(score);
					if (score != -1) {
						String text1 = null;
						String text2 = null;
						try {
							//if (!sourceSaved.contains(i)) {
								text1 = CommonExtractors.LARGEST_CONTENT_EXTRACTOR.getText(new URL(sourceValues[0]));
							//}
						} catch (Exception e) {
							System.out.println("Cannot extract text from the URL: " + sourceValues[0]);
						}
						try {
							//if (!targetSaved.contains(j)) {
								text2 = CommonExtractors.LARGEST_CONTENT_EXTRACTOR.getText(new URL(targetValues[0]));
							//}							
						} catch (Exception e) {
							System.out.println("Cannot extract text from the URL: " + targetValues[0]);							
						}
						//System.out.println(text1 + " " + text2);
						if (text1 != null && text2 != null && !"".equals(text1.trim()) && !"".equals(text2.trim())) {
							
							boolean isTargetOK = isLangOK(targetLang, charsTarget, wordsTarget, text2);
							boolean isSourceOK = isLangOK(sourceLang, charsSource, wordsSource, text1);
							//System.out.println(isTargetOK + " " + isSourceOK);
							if (isTargetOK && isSourceOK) {
								String englishFileName = mapping.get(sourceValues[0] + sourceValues[2].toLowerCase());
								String foreignFileName = mapping.get(targetValues[0] + targetValues[2].toLowerCase());
								boolean isToAdd = false;
								if (englishFileName == null) {
									englishFileName = sourcePath.getAbsolutePath() + "/html-" + i + ".txt";
									mapping.put(sourceValues[0] + sourceValues[2].toLowerCase(), englishFileName);
									Util.doSaveUTF(sourcePath.getAbsolutePath() + "/html-" + i + ".txt",  text1);
									//sourceSaved.add(i);
									isToAdd = true;
								}
								if (foreignFileName == null) {
									foreignFileName = targetPath.getAbsolutePath() + "/html-" + j + ".txt";								
									mapping.put(targetValues[0] + targetValues[2].toLowerCase(), foreignFileName);
									Util.doSaveUTF(targetPath.getAbsolutePath() + "/html-" + j + ".txt",  text2);
									//targetSaved.add(j);
									isToAdd = true;
								}
								if (isToAdd) {
									alignment.append(englishFileName + "\t" + foreignFileName + "\t" + score + "\n");
									Util.doSave(folder.getAbsolutePath() + "/alignment.txt", alignment.toString());																	
								}
							}

						}
					}
				}				
			}			
		} catch (Exception e) {
			System.out.println("It occurred an unexpected error! The message is : " + e);
		}
		System.out.println("Done");

	}

	private static boolean isLangOK(String aLang, Vector<String> charsTarget, Vector<String> wordsTarget, String aText) {
		 boolean isTargetOK = false;
		if (aLang.equalsIgnoreCase("el") || aLang.equals("lv") || aLang.equals("lt") || aLang.equals("et") || aLang.equals("hr") || aLang.equals("ro") || aLang.equals("sl")) {
			for (int s = 0; s < charsTarget.size() && !isTargetOK; s++) {
				String character = charsTarget.get(s);
				if (aText.contains(character)) {
					isTargetOK = true;
				}
			}
			
			if (!isTargetOK) {
				if (aLang.equals("ro")) {
					int countMatch = 0;
					for (int s = 0; s < wordsTarget.size() && countMatch <= 5; s++) {
						String lvChar = wordsTarget.get(s).replaceAll("=.*", "").trim();
						if (aText.contains(" " + lvChar + " ")) {
							countMatch++;
						}
					}
					if (countMatch >= 5) {
						isTargetOK = true;
					}
				}
				
			}
		} else if (aLang.equals("de")){
			if (aText.contains("Der ") || aText.contains(" der ") || aText.contains("Die ") || aText.contains(" die ")
					|| aText.contains("Das ") || aText.contains(" das ") || aText.contains(" den ") || aText.contains(" dem ") || aText.contains(" eine ")
					|| aText.contains(" ein ") || aText.contains(" einem ") || aText.contains(" eines ") || aText.contains(" einer ") || aText.contains("Ein ") || 
					aText.contains("Eine ")) {
				isTargetOK = true;
			}
		} else if (aLang.equals("en")){
			String category = guesser.categorize(aText);
			if (category.equalsIgnoreCase("english")) {
				return true;
			}
			return false;
		}
		return isTargetOK;
	}
	
}
