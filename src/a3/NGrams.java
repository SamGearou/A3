package a3;

import java.util.ArrayList;

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
	
	public static void main(String[] args) {
		
	}
}
