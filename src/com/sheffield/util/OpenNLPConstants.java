

package com.sheffield.util;

public class OpenNLPConstants extends WebAppConstantCorrector {
	public static String PARSER_PATH = DOCS_FOLDER + "/parser";
	public static String COREF_PATH = DOCS_FOLDER + "/coref";
	public static String NAME_MODELS_PATH = DOCS_FOLDER + "/namefind/";
	public static String[] NAME_TYPES = {"LOCATION", "PERSON", "ORGANIZATION"};
	public static String[] NAME_TYPE_PATH = {NAME_MODELS_PATH + "location.bin.gz", NAME_MODELS_PATH + "person.bin.gz",NAME_MODELS_PATH + "organization.bin.gz"};
	public static String TAG_DICTIONARY = DOCS_FOLDER + "/parser/tagdict2";
	public static String LEMMA_DICTIONARY = DOCS_FOLDER + "/parser/tagdict/";
//	public static String TAG_MODEL = DOCS_FOLDER + "/parser/tag.bin.gz";
	public static String ENGLISH_POS_MAX_ENT_MODEL = DOCS_FOLDER + "/postag/EnglishPOS.bin.gz";
	public static String ENGLISH_CHUNKER_MAX_ENT_MODEL = DOCS_FOLDER + "/chunker/EnglishChunk.bin.gz";
	public static final String ENGLISH_SENT_SPLIT_MAX_ENT_MODEL = DOCS_FOLDER + "/sentdetect/EnglishSD.bin.gz";
	public static final String ENGLISH_TOKEN_MAX_ENT_MODEL = DOCS_FOLDER + "/tokenize/EnglishTok.bin.gz";

	public static final String ITALIEN_SENT_SPLIT_MAX_ENT_MODEL = DOCS_FOLDER + "/italian/itSentence.bin.gz";
	public static final String ITALIAN_TOKEN_MAX_ENT_MODEL = DOCS_FOLDER + "/italian/itTokens.bin.gz";
	public static final String LEMMA_MAPPING_DE = DOCS_FOLDER + "/myDics/de/mapping.txt";
	public static final String LEMMA_DIC_DE = DOCS_FOLDER + "/myDics/de/dictionary.txt";
	public static final String LEMMA_DIC_EN = DOCS_FOLDER + "/myDics/en/dictionary.txt";
	public static final String NE_LOCATION_EN = DOCS_FOLDER + "/myDics/en/locations.txt";
	public static final String NE_LOCATION_EN_EXPAND = RESOURCES_FOLDER + "/place_names.csv";
	public static final String TYPES_EN = DOCS_FOLDER + "/myDics/en/typeInput.properties";
	public static final String NE_OBJECT_EN = DOCS_FOLDER + "/myDics/en/objects.txt";
	public static final String NE_LOCATION_DE = DOCS_FOLDER + "/myDics/de/locations.txt";
	public static final String NE_OBJECT_DE = DOCS_FOLDER + "/myDics/de/objects.txt";
	public static final String NE_LOCATION_IT = DOCS_FOLDER + "/myDics/it/locations.txt";
	public static final String NE_OBJECT_IT = DOCS_FOLDER + "/myDics/it/objects.txt";

	public static final String NE_FIRST_FEMALE_EN_EXPAND = RESOURCES_FOLDER + "/prominent_people.csv";

	public static final String NE_FIRST_FEMALE_EN = RESOURCES_FOLDER + "/femaleNames.txt";
	public static final String NE_FIRST_MALE_EN = RESOURCES_FOLDER + "/maleNames.txt";
	public static final String NE_LAST_NAMES_EN = RESOURCES_FOLDER + "/lastNames.txt";


}
