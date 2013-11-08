package ezi.search.engine.com;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CosCalculator {

	private ArrayList<String> docsAfterStemming;
	private ArrayList<String> keywordsAfterStemming;
	private ArrayList<String> docsBeforeStemming;
	private int[][] bagOfWords;
	private int[] bagOfWordsMax;
	private double[][] tf;
	private double idf[];
	private double tfIdf[][];

	public CosCalculator(ArrayList<String> docsBeforeStemming,
			ArrayList<String> docsAfterStemming,
			ArrayList<String> keywordsAfterStemming) {
		this.docsBeforeStemming = docsBeforeStemming;
		this.docsAfterStemming = docsAfterStemming;
		this.keywordsAfterStemming = keywordsAfterStemming;

		bagOfWords = new int[docsAfterStemming.size()][keywordsAfterStemming
				.size()];
		bagOfWordsMax = new int[docsAfterStemming.size()];
		tf = new double[docsAfterStemming.size()][keywordsAfterStemming.size()];
		idf = new double[keywordsAfterStemming.size()];
		tfIdf = new double[docsAfterStemming.size()][keywordsAfterStemming
				.size()];
	}

	public void calculateTfIdf() {
		calculateBagOfWords();
		calculateTf();
		calculateIdf();
		calculateTFIDF();
	}

	public Map<String, Double> calculateCos(String query) {
		double[] queryTfIdf = calculateQueryTfidf(query);
		HashMap<String, Double> result = new HashMap<String, Double>();
		double queryVector = 0.0;
		double documentVector = 0.0;
		double sum = 0.0;

		// Liczy wektor zapytania
		for (int i = 0; i < queryTfIdf.length; i++) {
			queryVector = queryVector + queryTfIdf[i] * queryTfIdf[i];
			// System.out.println("TF-IDF= "+ queryTfIdf[i] +" :: Query*Query= "
			// + queryTfIdf[i]*queryTfIdf[i]);
		}
		queryVector = Math.sqrt(queryVector);

		// Wylicza na biezaco vektor dokumentu oraz miare cos, dodaje do mapy
		// dokument przed stemmingiem oraz wartosc miary kosinusowej
		for (int i = 0; i < docsAfterStemming.size(); i++) {
			for (int j = 0; j < keywordsAfterStemming.size(); j++) {
				documentVector = documentVector + tfIdf[i][j] * tfIdf[i][j];
				// if(tfIdf[i][j]>0) { System.out.println( " !!! " +
				// tfIdf[i][j]); }
				sum = sum + queryTfIdf[j] * tfIdf[i][j];
			}
			documentVector = Math.sqrt(documentVector);
			// System.out.println("SUM= " +sum+ " COS= "+divisionInteger(sum,
			// documentVector*queryVector)+" Documents Vector= " +documentVector
			// + " queryVector= "+ queryVector + " SUUMAA: " + documentVector *
			// queryVector);
			result.put(docsBeforeStemming.get(i),
					divisionInteger(sum, documentVector * queryVector));
			sum = 0.0;
			documentVector = 0.0;
		}

		// return produceFinalResult(sortMapByValues(result));
		return sortMapByValues(result);
	}

	// Sortuje mape po values
	private static <K, V extends Comparable<V>> Map<K, V> sortMapByValues(
			final Map<K, V> map) {
		Comparator<K> valueComparator = new Comparator<K>() {
			public int compare(K k1, K k2) {
				int compare = map.get(k2).compareTo(map.get(k1));
				if (compare == 0)
					return 1;
				else
					return compare;
			}
		};
		Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
		sortedByValues.putAll(map);
		return sortedByValues;
	}

	// Wylicza zaokraglony wynik cos
	private static double divisionInteger(double a, double b) {
		double sum = (double) Math.round(a * 1000000) / 1000000;
		double divider = (double) Math.round(b * 1000000) / 1000000;
		double result = sum / divider;
		return result;
	}

	// Wylicza miare TF-IDF dla zapytania
	private double[] calculateQueryTfidf(String query) {
		int[] queryBagOfWords = new int[keywordsAfterStemming.size()]; // bagOfWords
																		// zapytania
		double[] queryTf = new double[keywordsAfterStemming.size()]; // Tf
																		// zapytania
		double[] queryTfIdf = new double[keywordsAfterStemming.size()]; // Tf-idf
																		// zapytania
		int max = 0; // Maks do liczenia miary tf

		// Wyliczenie bag of words
		for (int i = 0; i < keywordsAfterStemming.size(); i++) {
			String regex = "\\b" + keywordsAfterStemming.get(i) + "\\b";
			int number = query.split(regex, -1).length - 1;
			queryBagOfWords[i] = number;
			if (number > max) {
				max = number;
			}
		}

		// Wyliczenie tf
		for (int i = 0; i < queryBagOfWords.length; i++) {
			queryTf[i] = queryBagOfWords[i] / (double) max;
		}
		// Wyliczenie tf-idf
		for (int i = 0; i < queryTf.length; i++) {
			queryTfIdf[i] = queryTf[i] * idf[i];
		}
		return queryTfIdf;
	}

	// Wylicza macierz bag of words oraz maksymalne wartosci w kazdym rzedzie
	// potrzebne do tf
	private void calculateBagOfWords() {
		int max = 0;
		for (int i = 0; i < docsAfterStemming.size(); i++) {
			for (int j = 0; j < keywordsAfterStemming.size(); j++) {
				String regex = "\\b" + keywordsAfterStemming.get(j) + "\\b";
				int number = docsAfterStemming.get(i).split(regex, -1).length - 1;
				bagOfWords[i][j] = number;
				if (number > max) {
					max = number;
				}
			}
			bagOfWordsMax[i] = max;
			max = 0;
		}
	}

	// Wylicza macierz TF
	private void calculateTf() {
		for (int i = 0; i < docsAfterStemming.size(); i++) {
			for (int j = 0; j < keywordsAfterStemming.size(); j++) {
				tf[i][j] = bagOfWords[i][j] / (double) bagOfWordsMax[i];
			}
		}
	}

	// Wylicza wartosc idf dla kazdego termu
	private void calculateIdf() {
		double numberOfDocs = (double) docsAfterStemming.size();
		int docsWithWord = 0;
		for (int i = 0; i < keywordsAfterStemming.size(); i++) {
			for (int j = 0; j < docsAfterStemming.size(); j++) {
				if (bagOfWords[j][i] > 0) {
					docsWithWord++;
				}
			}
			if (docsWithWord > 0) {
				idf[i] = calculateLogarithm(numberOfDocs / docsWithWord);
			} else {
				idf[i] = 0;
			}
			docsWithWord = 0;
		}
	}

	// Wylicza wartosci tf-idf na podstawie tabel tf oraz idf
	private void calculateTFIDF() {
		for (int i = 0; i < docsAfterStemming.size(); i++) {
			for (int j = 0; j < keywordsAfterStemming.size(); j++) {
				tfIdf[i][j] = tf[i][j] * idf[j];
			}
		}
	}

	// Liczy logarytm przy podstawie 2
	private double calculateLogarithm(double value) {
		return (Math.log(value) / Math.log(2) + 1e-10);
	}

}
