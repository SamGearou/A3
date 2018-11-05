package a3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
//Sam Gearou, Josh Gearou
//October 29, 2018
//This class prompt the user for a password, and returns to the user either 'weak' (for a weak password) or
//'strong' (for a strong password)

public class PopulateDB {

	public static void main(String[] args) {
		try {
		NGrams grams = new NGrams(5);
		RedisClientDB redis = new RedisClientDB("localhost", 6379);
		MarkovModel model = new MarkovModel(grams, redis);
		File wordList = new File("words.txt");
		BufferedReader reader = new BufferedReader(new FileReader(wordList));
		String word = null;
		//for each word, add word the redis set, generate all of its nGrams,
		//update the database, and clear the nGrams ArrayList for the next iteration 
		while ((word = reader.readLine()) != null) {
			redis.getCommands().sadd("wordList", word);
			grams.calculateNGrams(word);
			redis.updateEntries(grams.getNGrams());
			grams.clearGrams();
		}
		Scanner scan = new Scanner(System.in);
		String input;
		
		//determine if the password is weak or strong
		while((input  = scan.nextLine()) != null && !input.equals("quit")) {
			if(input.length() < 5 || redis.getCommands().sismember("wordList", input)) {
				System.out.println("weak");
			}
			else if(redis.getCommands().sismember("wordList", input.toLowerCase())) {
				System.out.println("weak");
			}
			else {
				double score = model.passwordProbability(input);
				if(score > 65) {
					System.out.println("strong");
				}
				else {
					System.out.println("weak");
				}
			}
		}
		redis.getConnection().close();
		reader.close();
		scan.close();
	}
		catch(Exception e) {
			//do nothing
		}
	}
}
