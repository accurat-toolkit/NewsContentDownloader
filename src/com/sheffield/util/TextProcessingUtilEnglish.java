package com.sheffield.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import kea.main.KEAKeyphraseExtractor;
import kea.main.KEAModelBuilder;
import kea.stemmers.PorterStemmer;
import kea.stopwords.StopwordsEnglish;
import opennlp.maxent.io.PooledGISModelReader;
import opennlp.tools.lang.english.NameFinder;
import opennlp.tools.lang.english.PosTagger;
import opennlp.tools.lang.english.SentenceDetector;
import opennlp.tools.lang.english.Tokenizer;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.util.InvalidFormatException;


public class TextProcessingUtilEnglish {
	private static SentenceDetectorME SENTENCE_DETECTOR = null;

	private static NameFinder[] FINDERS = null;
	private static Tokenizer TOKENIZER = null;




    public String[] detectSentences(String aText) throws IOException {
		if (SENTENCE_DETECTOR == null) {
			SENTENCE_DETECTOR = new SentenceDetector(OpenNLPConstants.ENGLISH_SENT_SPLIT_MAX_ENT_MODEL);				
		}
		return SENTENCE_DETECTOR.sentDetect(aText);

    }
    public static void main(String[] args) throws Exception {
    	TextProcessingUtilEnglish util = new TextProcessingUtilEnglish();
    	//util.trainMoelForTE();
    	System.out.println(util.getOpenClassTokens("let us go to find barack obama who signed economic stimulus legislation."));
	}
    
    public void trainMoelForTE() throws Exception {
    	KEAModelBuilder km = new KEAModelBuilder();
		
		// A. required arguments (no defaults):
		
		// 1. Name of the directory -- give the path to your directory with documents and keyphrases
		//    documents should be in txt format with an extention "txt"
		//    keyphrases with the same name as documents, but extension "key"
		//    one keyphrase per line!
		km.setDirName("testdocs/en/train");
		
		// 2. Name of the model -- give the path to where the model is to be stored and its name
		km.setModelName("testdocs/model/model.model");
		 
		// 3. Name of the vocabulary -- name of the file (without extension) that is stored in VOCABULARIES
		//    or "none" if no Vocabulary is used (free keyphrase extraction).
		km.setVocabulary("none");
		
		// 4. Format of the vocabulary in 3. Leave empty if vocabulary = "none", use "skos" or "txt" otherwise.
		km.setVocabularyFormat("");
		
//		 B. optional arguments if you want to change the defaults
		// 5. Encoding of the document
		km.setEncoding("UTF-8");
		
		// 6. Language of the document -- use "es" for Spanish, "fr" for French
		//    or other languages as specified in your "skos" vocabulary 
		km.setDocumentLanguage("en"); // es for Spanish, fr for French
		
		// 7. Stemmer -- adjust if you use a different language than English or if you want to alterate results
		// (We have obtained better results for Spanish and French with NoStemmer) 		
		km.setStemmer(new PorterStemmer()); 
		
		// 8. Stopwords -- adjust if you use a different language than English!
		km.setStopwords(new StopwordsEnglish());
		
		// 9. Maximum length of a keyphrase
		km.setMaxPhraseLength(5);
		
		// 10. Minimum length of a keyphrase
		km.setMinPhraseLength(1);
		
		// 11. Minumum occurrence of a phrase in the document -- use 2 for long documents!
		km.setMinNumOccur(2);
	
	//  Optional: turn off the keyphrase frequency feature	
		km.setUseKFrequency(false);
		km.buildModel(km.collectStems());
		km.saveModel();

    }
    
    public Vector<String> detectTE(String aText) throws Exception {
    	Util.doSave("workingFolderForTE/text.txt", aText);
    	KEAKeyphraseExtractor ke = new KEAKeyphraseExtractor();
		
		// A. required arguments (no defaults):
		
		// 1. Name of the directory -- give the path to your directory with documents
		//    documents should be in txt format with an extention "txt".
		//    Note: keyphrases with the same name as documents, but extension "key"
		//    one keyphrase per line!
		
		ke.setDirName("workingFolderForTE");
		
		// 2. Name of the model -- give the path to the model 
		ke.setModelName("testdocs/model/model.model");
		 
		// 3. Name of the vocabulary -- name of the file (without extension) that is stored in VOCABULARIES
		//    or "none" if no Vocabulary is used (free keyphrase extraction).
		ke.setVocabulary("none");
		
		// 4. Format of the vocabulary in 3. Leave empty if vocabulary = "none", use "skos" or "txt" otherwise.
		ke.setVocabularyFormat("txt");
		
//		 B. optional arguments if you want to change the defaults
		// 5. Encoding of the document
		//ke.setEncoding("UTF-8");
		
		// 6. Language of the document -- use "es" for Spanish, "fr" for French
		//    or other languages as specified in your "skos" vocabulary 
		ke.setDocumentLanguage("en"); // es for Spanish, fr for French
		
		// 7. Stemmer -- adjust if you use a different language than English or want to alterate results
		// (We have obtained better results for Spanish and French with NoStemmer)
		ke.setStemmer(new PorterStemmer());
		
		
		// 8. Stopwords
		ke.setStopwords(new StopwordsEnglish());
		
		// 9. Number of Keyphrases to extract
		ke.setNumPhrases(100);
		
		// 10. Set to true, if you want to compute global dictionaries from the test collection
		ke.setBuildGlobal(true);		
		
		ke.loadModel();
		ke.extractKeyphrases(ke.collectStems());
		Vector<String> tes = Util.getFileContentAsVector("workingFolderForTE/text.key");
		File delete = new File("workingFolderForTE/text.key");
		if (delete.exists()) {
			delete.delete();
		}
		delete = new File("workingFolderForTE/text.txt");
		if (delete.exists()) {
			delete.delete();
		}
		return tes;
	}
    
    public Map<String, String> detectNamedEntities(String aSentence) throws IOException {
    	Map<String, String> map = new HashMap<String, String>();
    	
    	if (FINDERS == null) {
            FINDERS = new NameFinder[OpenNLPConstants.NAME_TYPE_PATH.length];
            for (int i = 0; i < OpenNLPConstants.NAME_TYPE_PATH.length; i++) {
              String modelName = OpenNLPConstants.NAME_TYPE_PATH[i];
              FINDERS[i] = new NameFinder(new PooledGISModelReader(new File(modelName)).getModel());
            }    
    	}
    	
        String annotatedSentence = NameFinder.processText(FINDERS, OpenNLPConstants.NAME_TYPES, aSentence);
        for (int i = 0; i < OpenNLPConstants.NAME_TYPES.length; i++) {
        	StringBuffer buffer = new StringBuffer("<");
        	String featureName = OpenNLPConstants.NAME_TYPES[i];
        	buffer.append(featureName).append(">");
        	String bufferStr = buffer.toString();
        	if (annotatedSentence.contains(bufferStr)) {
        		String toSaves[] = annotatedSentence.split(bufferStr);
        		for (int j = 1; j < toSaves.length; j++) {
            		String toSave = toSaves[j].replaceAll("</.*", "");
            		toSave = toSave.replaceAll("<.*>", "").replaceAll("\\n", "");
            		map.put(toSave, featureName);
				}
        	}
        }
        return map;
    }
    

    public Vector<String> getNgramNEWithoutFirstWord(String aSentence, int aNumberOfTokens, boolean isInSmallLetter) throws IOException {
    	Vector<String> ngrams = new Vector<String>();
    	String []tokens = tokenize(aSentence);
    	tokens[0] = tokens[0].toLowerCase();
    	outer:
    	for (int i = 0; i < tokens.length - (aNumberOfTokens - 1); i++) {
			StringBuffer ngram = new StringBuffer();
    		for (int j = 0, k=i; j < aNumberOfTokens; j++, k++) {
    			if (tokens[k].trim().toLowerCase().equals(tokens[k].trim())) {
    				continue outer;
    			}
				ngram.append(tokens[k].trim()).append(" ");
			}
    		
    		String ngramString = ngram.toString().trim();
    		if (isInSmallLetter) {
    			ngramString = ngramString.toLowerCase();    			
    		}
    		ngrams.add(ngramString);
		}
    	return ngrams;
    }

    
    public Vector<String> getNgramNE(String aSentence, int aNumberOfTokens, boolean isInSmallLetter) throws IOException {
    	Vector<String> ngrams = new Vector<String>();
    	String []tokens = tokenize(aSentence);
    	outer:
    	for (int i = 0; i < tokens.length - (aNumberOfTokens - 1); i++) {
			StringBuffer ngram = new StringBuffer();
    		for (int j = 0, k=i; j < aNumberOfTokens; j++, k++) {
    			if (tokens[k].trim().toLowerCase().equals(tokens[k].trim())) {
    				continue outer;
    			}
				ngram.append(tokens[k].trim()).append(" ");
			}
    		
    		String ngramString = ngram.toString().trim();
    		if (isInSmallLetter) {
    			ngramString = ngramString.toLowerCase();    			
    		}
    		ngrams.add(ngramString);
		}
    	return ngrams;
    }

    public Vector<String> getNgramNE(Vector<String> aList, int aNumberOfTokens, boolean isInSmallLetter) throws IOException {
    	Vector<String> ngrams = new Vector<String>();
    	for (int i = 0; i < aList.size() - (aNumberOfTokens - 1); i++) {
			StringBuffer ngram = new StringBuffer();
    		for (int j = 0, k=i; j < aNumberOfTokens; j++, k++) {
    			if (!aList.get(k).trim().equals("")) {
    				ngram.append(aList.get(k).trim()).append(" ");    				
    			}
			}
    		
    		String ngramString = ngram.toString().trim();
    		if (ngramString.split("\\s").length < aNumberOfTokens || ngramString.equals("")) {
    			continue;
    		}
    		if (isInSmallLetter) {
    			ngramString = ngramString.toLowerCase();    			
    		}
    		ngrams.add(ngramString);
		}
    	return ngrams;
    }

    public String[] tokenize(String aSentence) throws IOException {
		if (TOKENIZER == null) {
			TOKENIZER = new Tokenizer(OpenNLPConstants.ENGLISH_TOKEN_MAX_ENT_MODEL);				
		}
		if (aSentence == null) {
			return new String[0];
		}
    	return TOKENIZER.tokenize(aSentence.trim());
    }
    
	private static Properties ALLOWED_TAGS = null;
	private static POSDictionary POS_DICTIONARY = null;
	private static POSTagger POS_TAGGER = null;

    public static Properties readPropertyFile(String aFileName) throws FileNotFoundException, IOException {
        Properties properties = new Properties();        
        properties.load(new InputStreamReader(new FileInputStream(new File(aFileName)),"UTF-8"));
        return properties;
    }

    public String posTag(String aToken) throws IOException, InvalidFormatException {
    	if (POS_DICTIONARY == null) {
    		POS_DICTIONARY = new POSDictionary(OpenNLPConstants.TAG_DICTIONARY, false); 
    	}
    	if (POS_TAGGER == null) {
    		POS_TAGGER = new PosTagger(OpenNLPConstants.ENGLISH_POS_MAX_ENT_MODEL, POS_DICTIONARY);
    	}
        return POS_TAGGER.tag(aToken);
    }

    public Vector<String> getOpenClassTokens(String aSentece) throws IOException, InvalidFormatException {
    	String tokens[] = tokenize(aSentece);
    	Vector<String> openClassTokens = new Vector<String>();
    	StringBuffer tokensInSentence = new StringBuffer();
    	for (int i = 0; i < tokens.length; i++) {
    		if (!"".equals(tokens[i].trim())) {
        		tokensInSentence.append(tokens[i]).append(" ");    			
    		}
		}
    	if (ALLOWED_TAGS == null) {
    		ALLOWED_TAGS = readPropertyFile(MDSConstants.ALLOWED_TAGS);
    	}
    	tokens = posTag(tokensInSentence.toString()).split(" ");
    	for (int i = 0; i < tokens.length; i++) {
    		String type = tokens[i].replaceFirst(".*/", "");
    		if (ALLOWED_TAGS.containsKey(type)) {
    			openClassTokens.add(tokens[i].replaceFirst("/.*", ""));
    		}
		}
    	return openClassTokens;
    }

 
}
