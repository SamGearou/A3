package a3;

import java.util.ArrayList;
//Sam Gearou, Josh Gearou
//October 29, 2018
//This class calculates the probability of a password using a Markov Model.  The probability of a password
//is analogous to the 'strength' of the password, where a smaller probability is a stronger password

//The code in this class is motivated by the paper 'Adaptive Password-Strength Meters from Markov Models'
//The pdf can be found here: https://pdfs.semanticscholar.org/240b/ead78a1564b047b0bbdfb755ddc9808321d8.pdf

public class MarkovModel {
	private NGrams grams;
	private RedisClientDB redis;

	public MarkovModel(NGrams grams, RedisClientDB redis) {
		this.grams = grams;
		this.redis = redis;
	}

	public double passwordProbability(String password) {
		grams.calculateNGrams(password);
		ArrayList<String> gramList = grams.getNGrams();
		double probability = 1;
		int size = grams.getNGrams().size();
		for (int i = 0; i < size; i++) {
			String gram = grams.getNGrams().get(i);
			int gramSize = gram.length();
			char c = gram.charAt(gramSize - 1);
			String prefix = gram.substring(0, gramSize - 1);
			probability *= conditionalProbability(c, prefix);
		}
		probability = -(Math.log10(probability) / Math.log10(2));
		
		//update the database with the new password
		redis.updateEntries(gramList);
		
		//clear the gram ArrayList, and add the word to the redis set
		grams.clearGrams();
		redis.getCommands().sadd("wordList", password);
		return probability;
	}

	public double conditionalProbability(char c, String prefix) {
		if (redis.getCommands().get(prefix + c) == null) {
			// return a small probability, since the numerator is equal to zero
			return .0002;
		}
		double numerator = Integer.parseInt(redis.getCommands().get(prefix + c));
		double denominator = summationProbability(prefix);
		return numerator / denominator;
	}

	public double summationProbability(String prefix) {
		double summation = 0;
		String alphabet = Alphabet.getAlphabet();
		int alphabetSize = alphabet.length();
		for (int i = 0; i < alphabetSize; i++) {
			if (redis.getCommands().get(prefix + alphabet.charAt(i)) == null) {
				continue;
			} else {
				summation += Integer.parseInt(redis.getCommands().get(prefix + alphabet.charAt(i)));
			}
		}
		return summation;
	}
}
