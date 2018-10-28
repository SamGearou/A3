package a3;

import java.util.ArrayList;

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
		
		grams.clearGrams();
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
