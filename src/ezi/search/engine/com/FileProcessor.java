package ezi.search.engine.com;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import rita.wordnet.RiWordnet;

public class FileProcessor {

	// Zmienne przechowujace dane przed stemmingiem oraz po
	private ArrayList<String> docsBeforeStemming;
	private ArrayList<String> keywordsBeforeStemming;
	private ArrayList<String> docsAfterStemming;
	private ArrayList<String> keywordsAfterStemming;
	private ArrayList<String> docsAfterTokenization;
	private Stemmer stemmer;
	private String queryStemmed;
	private String queryTokenized;

	// Konstruktor
	public FileProcessor() {
		docsBeforeStemming = new ArrayList<String>();
		keywordsBeforeStemming = new ArrayList<String>();
		docsAfterStemming = new ArrayList<String>();
		keywordsAfterStemming = new ArrayList<String>();
		docsAfterTokenization = new ArrayList<String>();
	}

	// Gettery
	public ArrayList<String> getDocsBeforeStemming() {
		return docsBeforeStemming;
	}

	public ArrayList<String> getKeywordsBeforeStemming() {
		return keywordsBeforeStemming;
	}

	public ArrayList<String> getDocsAfterStemming() {
		return docsAfterStemming;
	}

	public ArrayList<String> getKeywordsAfterStemming() {
		return keywordsAfterStemming;
	}

	public ArrayList<String> getDocsAfterTokenization() {
		return docsAfterTokenization;
	}

	public String getQueryStemmed() {
		return queryStemmed;
	}

	public String getQueryTokenized() {
		return queryTokenized;
	}

	// Funkcja wykonujaca operacje na plikach - ladowanie, usuwanie znakow,
	// stemming
	public void processFiles(File documents, File keywords, boolean docsFlag) {
		if (docsFlag) {
			openDocumentsTwo(documents);
		} else {
			openDocuments(documents);
		}
		openKeywords(keywords);
		keywordsStemming(keywordsBeforeStemming);
		keywordsRemoveOrder();
		stemmDocs();
	}

	// Usuwa znaki i stemmuje zapytanie zwracajac string
	public void processQuery(String query) {
		queryStemmed = stemmDocument(removeSigns(query));
		queryTokenized = removeSigns(query);
	}

	public String autocompleteQuery(String queryBeforeTokenization) {
		String query = removeSigns(queryBeforeTokenization);
		String wordsInQuery[] = query.split(" ");
		HashSet<String> completedQuery = new HashSet<String>();
		if (wordsInQuery.length <= 1) {
			String stemmedQueryWord = stemmDocument(query);
			completedQuery.add(query);
			String allSynonyms[] = getSynonyms(query);
			String allHyponyms[] = getHyponyms(query);
			String allHypernyms[] = getHypernyms(query);
			if (allSynonyms != null) {
				for (String syn : allSynonyms) {
					String result = getConfRec(stemmedQueryWord,
							stemmDocument(syn));
					if (result != null) {
						completedQuery.add(syn);
					}
				}
			}
			if (allHyponyms != null) {
				for (String hypo : allHyponyms) {
					String result = getConfRec(stemmedQueryWord,
							stemmDocument(hypo));
					if (result != null) {
						completedQuery.add(hypo);
					}
				}
			}
			if (allHypernyms != null) {
				for (String hypern : allHypernyms) {
					String result = getConfRec(stemmedQueryWord,
							stemmDocument(hypern));
					if (result != null) {
						completedQuery.add(hypern);
					}
				}
			}

		} else {
			String groupedWords = groupWords(query);
			String groups[] = groupedWords.split("\t");
			for (int i = 0; i < groups.length; i++) {
				String wordsInGroup[] = groups[i].split(" ");

				if (wordsInGroup.length == 1) {
					String stemmedQueryWord = stemmDocument(wordsInGroup[0]);
					String allSynonyms[] = getSynonyms(wordsInGroup[0]);
					String allHyponyms[] = getHyponyms(wordsInGroup[0]);
					String allHypernyms[] = getHypernyms(wordsInGroup[0]);
					completedQuery.add(wordsInGroup[0]);
					if (allSynonyms != null) {
						for (String syn : allSynonyms) {
							String result = getConfRec(stemmedQueryWord,
									stemmDocument(syn));
							if (result != null) {
								completedQuery.add(syn);
							}
						}
					}
					if (allHyponyms != null) {
						for (String hypo : allHyponyms) {
							String result = getConfRec(stemmedQueryWord,
									stemmDocument(hypo));
							if (result != null) {
								completedQuery.add(hypo);
							}
						}
					}
					if (allHypernyms != null) {
						for (String hypern : allHypernyms) {
							String result = getConfRec(stemmedQueryWord,
									stemmDocument(hypern));
							if (result != null) {
								completedQuery.add(hypern);
							}
						}
					}
				} else if (wordsInGroup.length == 2) {
					String stemmedQueryWord = stemmDocument(wordsInGroup[0]);
					String allSynonyms[] = getSynonyms(wordsInGroup[0]);
					String allHypernyms[] = getHypernyms(wordsInGroup[0]);
					completedQuery.add(wordsInGroup[0]);
					if (allSynonyms != null) {
						for (String syn : allSynonyms) {
							String result = getConfRec(stemmedQueryWord,
									stemmDocument(syn));
							if (result != null) {
								completedQuery.add(syn);
							}
						}
					}
					if (allHypernyms != null) {
						for (String hypern : allHypernyms) {
							String result = getConfRec(stemmedQueryWord,
									stemmDocument(hypern));
							if (result != null) {
								completedQuery.add(hypern);
							}
						}
					}
					String stemmedQueryWord1 = stemmDocument(wordsInGroup[1]);
					String allSynonyms1[] = getSynonyms(wordsInGroup[1]);
					String allHypernyms1[] = getHypernyms(wordsInGroup[1]);
					completedQuery.add(wordsInGroup[1]);
					if (allSynonyms1 != null) {
						for (String syn : allSynonyms1) {
							String result = getConfRec(stemmedQueryWord1,
									stemmDocument(syn));
							if (result != null) {
								completedQuery.add(syn);
							}
						}
					}
					if (allHypernyms1 != null) {
						for (String hypern : allHypernyms1) {
							String result = getConfRec(stemmedQueryWord1,
									stemmDocument(hypern));
							if (result != null) {
								completedQuery.add(hypern);
							}
						}
					}
				} else {
				}

			}
		}

		String queryExpansion = "";
		for (String str : completedQuery) {
			queryExpansion = queryExpansion + " " + str;
		}
		System.out.println(queryExpansion);
		return queryExpansion;
	}

	// Dzieli zapytanie na grupy slow
	private String groupWords(String query) {
		RiWordnet wordnet = new RiWordnet();
		String words[] = query.split(" ");
		String groups = "";
		int startPosition = words.length;
		int startWord = 0;
		int skipWords = 0;
		while (startPosition > 0) {
			groups = groups + words[startWord + skipWords] + " ";
			for (int i = startWord + skipWords + 1; i < words.length; i++) {
				if (wordnet.getDistance(words[startWord + skipWords], words[i],
						"n") != 1) {
					groups = groups + words[i] + " ";
					skipWords += 2;
				}
			}
			groups = groups + "\t";
			startWord++;
			startPosition = startPosition - startWord - skipWords;
		}
		return groups;
	}

	// Wylicza confidence (min 0.3) oraz recall (min 0.01)
	private String getConfRec(String queryWord, String word) {
		int numberOfDocs = docsBeforeStemming.size();
		int numberOfDocsWithQuery = 0;
		int numberOfDocsWithBoth = 0;
		String queryRegex = "\\b" + queryWord + "\\b";
		String wordRegex = "\\b" + word + "\\b";

		for (String doc : docsAfterStemming) {
			if ((doc.split(queryRegex, -1).length - 1) > 0) {
				numberOfDocsWithQuery++;
				if ((doc.split(wordRegex, -1).length - 1) > 0) {
					numberOfDocsWithBoth++;
				}

			}
		}

		double confidence = numberOfDocsWithBoth
				/ (double) numberOfDocsWithQuery;
		double recall = numberOfDocsWithBoth / (double) numberOfDocs;
		if (confidence >= 0.2 && recall >= 0.01) {
			return word;
		} else
			return null;
	}

	// Wyszukuje synonimy
	private String[] getSynonyms(String word) {
		RiWordnet wordnet = new RiWordnet();
		String pos = wordnet.getBestPos(word);
		return wordnet.getAllSynonyms(word, pos);
	}

	// Wyszukuje hyponymy
	private String[] getHyponyms(String word) {
		RiWordnet wordnet = new RiWordnet();
		String pos = wordnet.getBestPos(word);
		return wordnet.getAllHyponyms(word, pos);
	}

	// Wyszukuje hypernymy
	private String[] getHypernyms(String word) {
		RiWordnet wordnet = new RiWordnet();
		String pos = wordnet.getBestPos(word);
		return wordnet.getAllHypernyms(word, pos);
	}

	// Laduje dokumenty z pliku do ArrayList
	private void openDocuments(File file) {
		try {
			FileInputStream fstream = new FileInputStream(
					file.getAbsolutePath());
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line, input = "";
			try {
				while ((line = br.readLine()) != null) {
					if (line.length() == 0) {
						docsBeforeStemming.add(input);
						input = "";
					} else {
						input = input + line + "\n";
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Laduje plik tekstowy pomijajac pierwsza linie
	private void openDocumentsTwo(File file) {
		try {
			FileInputStream fstream = new FileInputStream(
					file.getAbsolutePath());
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line, input = "";
			boolean isFirst = true;
			try {
				while ((line = br.readLine()) != null) {
					if (line.length() == 0) {
						docsBeforeStemming.add(input);
						input = "";
						isFirst = true;
					} else {
						if (isFirst) {
							isFirst = false;
						} else {
							input = input + line + "\n";
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Laduje slowa kluczowe do ArrayList
	private void openKeywords(File file) {
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(file.getAbsolutePath());
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			try {
				while ((line = br.readLine()) != null) {
					keywordsBeforeStemming.add(line);
				}
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Uzywa stemmera na slowach kluczowych
	private void keywordsStemming(ArrayList<String> data) {
		stemmer = new Stemmer();
		for (String word : data) {
			for (int i = 0; i < word.length(); i++) {
				stemmer.add(word.charAt(i));
			}
			stemmer.stem();
			keywordsAfterStemming.add(stemmer.toString());
		}
	}

	// Usuwa duplikaty z zestawu slow kluczowych & sortuje alfabetycznie
	private void keywordsRemoveOrder() {
		HashSet<String> hashset = new HashSet<String>();
		hashset.addAll(keywordsAfterStemming);
		keywordsAfterStemming.clear();
		keywordsAfterStemming.addAll(hashset);
		Collections.sort(keywordsAfterStemming);
	}

	// Usuwa znaki ze stringa oraz zmienia duze litery na male
	private String removeSigns(String text) {
		String temp = "";
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '.' || text.charAt(i) == ',')
				;
			else if (text.charAt(i) == ':' || text.charAt(i) == ';')
				;
			else if (text.charAt(i) == '(' || text.charAt(i) == ')')
				;
			else if (text.charAt(i) == '!' || text.charAt(i) == '?')
				;
			else if (text.charAt(i) == '=' || text.charAt(i) == '-')
				;
			else if (text.charAt(i) == '"' || text.charAt(i) == '/')
				;
			else if (text.charAt(i) == '|' || text.charAt(i) == '&')
				;
			else if (text.charAt(i) == '+' || text.charAt(i) == '\'')
				;
			else if (text.charAt(i) == '[' || text.charAt(i) == ']')
				;
			else if (text.charAt(i) == '<' || text.charAt(i) == '>')
				;
			else {
				temp = temp + text.charAt(i);
			}
		}
		return temp.toLowerCase().replaceAll("\\s+", " ");
	}

	// Stemmuje podany dokument, dzielac go na slowa, zwraca po stemmingu
	private String stemmDocument(String text) {
		String result = "";
		String[] toStemm = text.split(" ");
		stemmer = new Stemmer();
		for (String current : toStemm) {

			for (int i = 0; i < current.length(); i++) {
				stemmer.add(current.charAt(i));
			}
			stemmer.stem();
			result = result + " " + stemmer.toString();
		}

		return result;
	}

	// Usuwa znaki oraz stemmuje ArrayList dokumentow
	private void stemmDocs() {
		for (String current : docsBeforeStemming) {
			docsAfterStemming.add(stemmDocument(removeSigns(current)));
			docsAfterTokenization.add(removeSigns(current));
		}

	}

}
