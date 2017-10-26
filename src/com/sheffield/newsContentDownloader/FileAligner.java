package com.sheffield.newsContentDownloader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.knallgrau.utils.textcat.TextCategorizer;

import opennlp.tools.util.InvalidFormatException;

import Jama.Matrix;

import com.sheffield.util.TextProcessingUtilEnglish;
import com.sheffield.util.Util;

public class FileAligner {
	static Vector<String> punishmentList = new Vector<String>();
	static Map<String, Integer> monthMap = new HashMap<String, Integer>();
	static TextProcessingUtilEnglish textProcessingUTil = new TextProcessingUtilEnglish();

	public static void init() throws IOException {
		punishmentList = Util.getFileContentAsVector("stopwords_en.txt");
		
		monthMap.put("Jan", 1);
		monthMap.put("Feb", 2);
		monthMap.put("Mar", 3);
		monthMap.put("Apr", 4);
		monthMap.put("May", 5);
		monthMap.put("Jun", 6);
		monthMap.put("Jul", 7);
		monthMap.put("Aug", 8);
		monthMap.put("Sep", 9);
		monthMap.put("Oct", 10);
		monthMap.put("Nov", 11);
		monthMap.put("Dec", 12);
		
		monthMap.put("01", 1);
		monthMap.put("02", 2);
		monthMap.put("03", 3);
		monthMap.put("04", 4);
		monthMap.put("05", 5);
		monthMap.put("06", 6);
		monthMap.put("07", 7);
		monthMap.put("08", 8);
		monthMap.put("09", 9);
		monthMap.put("10", 10);
		monthMap.put("11", 11);
		monthMap.put("12", 12);

		monthMap.put("1", 1);
		monthMap.put("2", 2);
		monthMap.put("3", 3);
		monthMap.put("4", 4);
		monthMap.put("5", 5);
		monthMap.put("6", 6);
		monthMap.put("7", 7);
		monthMap.put("8", 8);
		monthMap.put("9", 9);
		monthMap.put("10", 10);
		monthMap.put("11", 11);
		monthMap.put("12", 12);

		monthMap.put("Januar", 1);
		monthMap.put("Februar", 2);
		monthMap.put("Maerz", 3);
		monthMap.put("April", 4);
		monthMap.put("Mai", 5);
		monthMap.put("Juni", 6);
		monthMap.put("Juli", 7);
		monthMap.put("August", 8);
		monthMap.put("September", 9);
		monthMap.put("Oktober", 10);
		monthMap.put("October", 10);
		monthMap.put("November", 11);
		monthMap.put("Dezember", 12);
		
		monthMap.put("December", 12);
		monthMap.put("Februarie", 2);

	}
	public static double alignDocuments(String aSourceTitle, String aSourceDate, String aTargetTranslatedTitleToSource, String aTargetDate, String aTargetTitle, double aThreshold) throws IOException, InvalidFormatException {
		String foreignDate = aTargetDate;
		//System.out.println("source title " + aSourceTitle);
		//System.out.println("target " + aTargetTranslatedTitleToSource);
		if (foreignDate.contains("-") && !foreignDate.contains(" -")) {
			foreignDate = foreignDate.replaceAll("-", " ");
		}
		int timeForeign = 0;
		if (foreignDate.startsWith("Sat")) {
			foreignDate = foreignDate.replaceAll("Sat.*,", "").trim();
		}
		if (foreignDate.startsWith("Sun")) {
			foreignDate = foreignDate.replaceAll("Sun.*,", "").trim();
		}
		if (foreignDate.startsWith("Mon")) {
			foreignDate = foreignDate.replaceAll("Mon.*,", "").trim();
		}
		if (foreignDate.startsWith("Tue")) {
			foreignDate = foreignDate.replaceAll("Tue.*,", "").trim();
		}
		if (foreignDate.startsWith("Wed")) {
			foreignDate = foreignDate.replaceAll("Wed.*,", "").trim();
		}
		if (foreignDate.startsWith("Thu")) {
			foreignDate = foreignDate.replaceAll("Thu.*,", "").trim();
		}
		if (foreignDate.startsWith("Fri")) {
			foreignDate = foreignDate.replaceAll("Fri.*,", "").trim();
		}

		
		String dateValues[] = foreignDate.trim().split("\\s");

		try {
			if (foreignDate.startsWith("201")) {
				timeForeign += Integer.parseInt(dateValues[0].trim().replaceAll("\\..*", "").trim()) * 365;
				timeForeign += Integer.parseInt(dateValues[1].trim()) * 30;
				timeForeign += Integer.parseInt(dateValues[2].trim().replaceAll("T.*", "")) ;						
			} else {
				try {
					timeForeign += Integer.parseInt(dateValues[0].trim().replaceAll("\\..*", "").trim());
					timeForeign += monthMap.get(dateValues[1].trim().replaceAll("\"", "").replaceAll("\\.", "")) * 30;
					timeForeign += Integer.parseInt(dateValues[2].trim()) * 365 ;											

				} catch (Exception e) {
					if (foreignDate.contains(",")) {
						foreignDate = foreignDate.replaceAll(".*,", "").trim();
						dateValues = foreignDate.split("\\s");
						timeForeign += Integer.parseInt(dateValues[0].trim().replaceAll("\\..*", "").trim());
						timeForeign += monthMap.get(dateValues[1].trim().replaceAll("\"", "").replaceAll("\\.", "")) * 30;
						timeForeign += Integer.parseInt(dateValues[2].trim()) * 365 ;																	
					}
				}
			}
		} catch (Exception e) {				
			timeForeign = -1;
		}
			
		String titleTranslation = aTargetTranslatedTitleToSource;
		titleTranslation = titleTranslation.replaceAll("„", "");
		titleTranslation = titleTranslation.replaceAll("\"", "");
		
		String foreigntitle = aTargetTitle;
		foreigntitle = foreigntitle.replaceAll("<b>...</b>", "").replaceAll("<b>", "").replaceAll("</b>", "");
		if (foreigntitle.equals(titleTranslation)) {
			return -1;
		}
		titleTranslation = titleTranslation.replaceAll("\"", "");
		Vector<String> words = new Vector<String>();
		String wordsArray[] = textProcessingUTil.tokenize(titleTranslation);;
		Vector<String> openClassWords = textProcessingUTil.getOpenClassTokens(titleTranslation);					

		
		for (int j = 0; j < wordsArray.length; j++) {
			String word = wordsArray[j].trim();
			if (!word.trim().equals("") && word.length() > 2 && openClassWords.contains(word)) {
				words.add(word.toLowerCase());
			}
		}
		int foreignWordsSize = words.size();
		
		String englishDate = aSourceDate;

		if (englishDate.contains("-") && !englishDate.contains(" -")) {
			englishDate = englishDate.replaceAll("-", " ");
		}
		if (englishDate.startsWith("Sat")) {
			englishDate = englishDate.replaceAll("Sat.*,", "").trim();
		}
		if (englishDate.startsWith("Sun")) {
			englishDate = englishDate.replaceAll("Sun.*,", "").trim();
		}
		if (englishDate.startsWith("Mon")) {
			englishDate = englishDate.replaceAll("Mon.*,", "").trim();
		}
		if (englishDate.startsWith("Tue")) {
			englishDate = englishDate.replaceAll("Tue.*,", "").trim();
		}
		if (englishDate.startsWith("Wed")) {
			englishDate = englishDate.replaceAll("Wed.*,", "").trim();
		}
		if (englishDate.startsWith("Thu")) {
			englishDate = englishDate.replaceAll("Thu.*,", "").trim();
		}
		if (englishDate.startsWith("Fri")) {
			englishDate = englishDate.replaceAll("Fri.*,", "").trim();
		}
		dateValues = englishDate.trim().split("\\s");
		if (dateValues.length == 1) {
			dateValues = (englishDate).split("/");
		}
		int timeEnglish = 0;
		try {
			if (englishDate.startsWith("201")) {
				timeEnglish += Integer.parseInt(dateValues[0].trim()) * 365;
				timeEnglish += Integer.parseInt(dateValues[1].trim()) * 30;
				timeEnglish += Integer.parseInt(dateValues[2].trim().replaceAll("T.*", ""));					
			} else {
				try {
					timeEnglish += Integer.parseInt(dateValues[0].trim());
					timeEnglish += monthMap.get(dateValues[1].trim().replaceAll("\"", "")) * 30;
					timeEnglish += Integer.parseInt(dateValues[2].trim()) * 365;																		
				} catch (Exception e) {
					if (englishDate.contains(",")) {
						englishDate = englishDate.replaceAll(".*,", "").trim();
						dateValues = englishDate.split("\\s");
						timeEnglish += Integer.parseInt(dateValues[0].trim().replaceAll("\\..*", "").trim());
						timeEnglish += monthMap.get(dateValues[1].trim().replaceAll("\"", "").replaceAll("\\.", "")) * 30;
						timeEnglish += Integer.parseInt(dateValues[2].trim()) * 365 ;																	
					}

				}
			}
		} catch (Exception e) {
			timeEnglish = -1;
		}

		
		int timeDif = timeEnglish - timeForeign;
		if (timeDif < 0) {
			timeDif *= -1;
		}
			
		String englishTitle = aSourceTitle;
		englishTitle = englishTitle.replaceAll("„", "");
		englishTitle = englishTitle.replaceAll("\"", "");
		englishTitle = englishTitle.replaceAll("<b>...</b>", "").replaceAll("<b>", "").replaceAll("</b>", "");
		englishTitle = englishTitle.replaceAll("\"", "");
		Vector<String> words2 = new Vector<String>();
		
		wordsArray = textProcessingUTil.tokenize(englishTitle);
		openClassWords = textProcessingUTil.getOpenClassTokens(englishTitle);					
		//System.out.println(openClassWords);
		//wordsArray = englishTitle.split("\\s");
		for (int s = 0; s < wordsArray.length; s++) {
			String word = wordsArray[s].trim();
			if (!word.trim().equals("") && word.length() > 2 && openClassWords.contains(word)) {
				words2.add(word.toLowerCase());
			}
		}

		int englishWordSize = words2.size();
		int difWordSize = englishWordSize - foreignWordsSize;
		
		if (difWordSize < 0) {
			difWordSize *= -1;
		}
						
		double score = getCosine(words, words2);
		//System.out.println(score);
		if (score >= aThreshold) {
			return score; //+= (1.0/(difWordSize + 1) + 1.0/(timeDif + 1)); 
		} 
		return -1;
	}
	
	public static void main(String args[]) throws IOException, InvalidFormatException {
		String a = "„In pictures: Putin inaugurated”";
		String b = "„Khl's in pictures: Belarus v Italy” Aber nacher werde ich nache Hause fahren. Was machst du dann?";
		TextCategorizer categorizer = new TextCategorizer();
		//System.out.println(categorizer.categorize(b));
		a = a.replaceAll("\"", "");
		a = a.replaceAll("„", "");
		b = b.replaceAll("\"", "");
		b = b.replaceAll("„", "");
		String bTokens[] = textProcessingUTil.tokenize(b);;
		Vector<String> openClassWords = textProcessingUTil.getOpenClassTokens(b);					
		Vector<String> listB = new Vector<String>();
		Vector<String> listA = new Vector<String>();
		
		for (int s = 0; s < bTokens.length; s++) {
			String word = bTokens[s].trim();
			if (!word.trim().equals("") && openClassWords.contains(word)) {
				listB.add(word.toLowerCase());
			}
		}

		String aTokens[] = textProcessingUTil.tokenize(a);;
		openClassWords = textProcessingUTil.getOpenClassTokens(a);					
		for (int s = 0; s < aTokens.length; s++) {
			String word = aTokens[s].trim();
			if (!word.trim().equals("") && openClassWords.contains(word)) {
				listA.add(word.toLowerCase());
			}
		}

		System.out.println(listA + " " + listB);
		
		System.out.println(getCosine(listA, listB));
	}
	
	
	private static double getCosine(Vector<String> words, Vector<String> words2) {
		Vector<String> listSmall = new Vector<String>();
		Map<String, Double> countWords1 = new HashMap<String, Double>();
		Map<String, Double> countWords2 = new HashMap<String, Double>();
		Map<String, Double> intersection = new HashMap<String, Double>();
		//System.out.println(words + " " + words2);
		for (int k = 0; k < words.size(); k++) {
			listSmall.add(words.get(k).toLowerCase());
			Double count = countWords1.get(words.get(k).toLowerCase());
			if (count == null) {
				count = new Double(0);
			}
			count = new Double (count.doubleValue() + 1);
			countWords1.put(words.get(k).toLowerCase(), count);
		}
		
		for (int k = 0; k < words2.size(); k++) {
			if (listSmall.contains(words2.get(k).toLowerCase())) {
				Double count = intersection.get(words2.get(k).toLowerCase());
				if (count == null) {
					count = new Double(0);
				}
				count = new Double (count.doubleValue() + 1);
				intersection.put(words2.get(k).toLowerCase(), count);
			}
			Double count = countWords2.get(words2.get(k).toLowerCase());
			if (count == null) {
				count = new Double(0);
			}
			count = new Double (count.doubleValue() + 1);
			countWords2.put(words2.get(k).toLowerCase(), count);
		}
		
		double zaehler = 0;
		Iterator<String> it = intersection.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			double value1 = intersection.get(key);
			double value2 = intersection.get(key);
			zaehler += value1*value2;
		}
		
		double nenner1 = 0;
		it = countWords1.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			double value = countWords1.get(key);
			nenner1 += value*value;
		}
		double nenner2 = 0;
		it = countWords2.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			double value = countWords2.get(key);
			nenner2 += value*value;
		}
		//System.out.println(zaehler + " " + nenner1 + " " + nenner2);

		double score = zaehler / (Math.sqrt(nenner1) * Math.sqrt(nenner2));
		return score;
	}


}
