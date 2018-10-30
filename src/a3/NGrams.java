package a3;

import java.util.ArrayList;
//Sam Gearou, Josh Gearou
//October 29, 2018
//This class calculates the n-grams of a given String (see the README for an explanation of an n-gram)

//The code in this class is motivated by the paper 'Adaptive Password-Strength Meters from Markov Models'
//The pdf can be found here: https://pdfs.semanticscholar.org/240b/ead78a1564b047b0bbdfb755ddc9808321d8.pdf

public class NGrams {
	private ArrayList<String> ngrams;
	private int maxGramSize = 5;
	
	public NGrams(int maxGramSize) {
		this.maxGramSize = maxGramSize;
		ngrams = new ArrayList<>();
	}
	
	public void calculateNGrams(String password){
		if(password.length() < 5) {
			return;
		}
		for(int i = 0; i<password.length()-maxGramSize+1; i++) {
			for(int j = i; j<i + maxGramSize; j++) {
				ngrams.add(password.substring(i, j+1));
			}
		}
	}
	
	public void clearGrams() {
		ngrams = new ArrayList<>();
	}
	
	public ArrayList<String> getNGrams(){
		return ngrams;
	}
}
