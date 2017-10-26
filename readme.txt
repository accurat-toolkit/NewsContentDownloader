The new NewsContentDownloader works with an integrated dictionary functionality. Thus it does not require a pre-translation of target titles into the source language. However, it is also
possible to switch off the dictionary translation if there exist existing translation of the target text into the sourece language. In that case the user can use the
existing translation file as input to the tool.

The NewsContentDownloader can be run using the following command:

java.exe -jar NewsContentDownloader.jar sourceRSSCollectionFilePath targetRSSCollectionFilePath pathToSaveResults sourceLanguageCode targetLanguageCode threshold TRANSOPTION

The threshold argument is used by the tool as a minimum similarity score between the titles.

The TRANSOPTION argument controls the switch between the choices of dictionary translation and own translation. For dictionary translation the tool uses "DICT"

e.g. 

java.exe -jar NewsContentDownloader.jar d:\testing\sourceRSSCollection.txt d:\testing\targetRSSCollection.txt d:\testing\html en de 0.5 DICT

If the user selects to use DICT then a dictionary file must be added to the folder NewsContentDownloader/dict. The file name must be sourceLangCode_targetLangCode.txt, e.g. en_de.txt for
English German dictionary. The format of the dictionaries are as in DicMetric.

For own translation the tool uses "EXIST" + translation file path, e.g.

java.exe -jar NewsContentDownloader.jar d:\testing\sourceRSSCollection.txt d:\testing\targetRSSCollection.txt d:\testing\html en de 0.5 EXIST d:\testing\targetRSSCollectionTextTranslation.txt
