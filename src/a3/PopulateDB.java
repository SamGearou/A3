package a3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class PopulateDB {

	private NGrams grams;
	private RedisClientDB redis;

	public PopulateDB(NGrams grams, RedisClientDB redis) {
		this.grams = grams;
		this.redis = redis;
	}

	public static void main(String[] args) throws IOException {
		NGrams grams = new NGrams(5);
		RedisClientDB redis = new RedisClientDB("localhost", 6379);
		PopulateDB populate = new PopulateDB(grams, redis);
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
		System.out.println("Please type in a password:");
		String input;
		//determine if the password is weak or strong
		while((input  = scan.nextLine()) != null && !input.equals("quit")) {
			if(input.length() < 5 || redis.getCommands().sismember("wordList", input)) {
				System.out.println("weak");
			}
			else {
				System.out.println(model.passwordProbability(input));
			}
			System.out.println("Please type in a password:");
		}
		System.out.println("Program has terminated");
		redis.getConnection().close();
	}
}
