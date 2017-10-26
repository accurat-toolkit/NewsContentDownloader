/**
 * 
 */
package com.sheffield.util;

/**
 * @author Ahmet
 *
 */
public class WebAppConstantCorrector {
	protected static String DOCS_FOLDER = "docs";
	protected static String RESOURCES_FOLDER = "WebContent\\resources";
	protected static String CACHE = "WebContent\\cache";
	protected static String LANG_MODELS = "WebContent\\langModels";

	/**
	 * @param aDocs_folder the dOCS_FOLDER to set
	 */
	public static final void setDOCS_FOLDER(String aDocs_folder) {
		DOCS_FOLDER = aDocs_folder;
	}

	/**
	 * @param aSymbol_property_folder the sYMBOL_PROPERTY_FOLDER to set
	 */
	public static final void setRESOURCES_FOLDER(String aSymbol_property_folder) {
		RESOURCES_FOLDER = aSymbol_property_folder;
	}

	public static String getDOCS_FOLDER() {
		return DOCS_FOLDER;
	}

	public static String getRESOURCES_FOLDER() {
		return RESOURCES_FOLDER;
	}

	public static String getCACHE() {
		return CACHE;
	}

	public static void setCACHE(String aCache) {
		CACHE = aCache;
	}

	/**
	 * @return the lANG_MODELS
	 */
	public static String getLANG_MODELS() {
		return LANG_MODELS;
	}

	/**
	 * @param aLang_models the lANG_MODELS to set
	 */
	public static void setLANG_MODELS(String aLang_models) {
		LANG_MODELS = aLang_models;
	}
	
	

}
