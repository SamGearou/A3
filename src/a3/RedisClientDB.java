package a3;

import java.util.ArrayList;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisClientDB {
	private RedisClient client;
	private StatefulRedisConnection<String, String> connection;
	private RedisCommands<String, String> commands;

	public RedisClientDB(String host, int port) {
		client = RedisClient.create(RedisURI.create(host, port));
		connection = client.connect();
		commands = connection.sync();
	}
	
	public void updateEntry(String gram) {
		if(commands.get(gram) == null) {
			commands.set(gram, "1");
		}
		else {
			int value = Integer.parseInt(commands.get(gram));
			commands.set(gram, (value + 1) + "");
		}
	}
	
	public void updateEntries(ArrayList<String> entries) {
		int size = entries.size();
		for(int i = 0; i<size; i++) {
			updateEntry(entries.get(i));
		}
	}
	
	public RedisClient getClient() {
		return client;
	}
	
	public StatefulRedisConnection<String, String> getConnection(){
		return connection;
	}
	
	public RedisCommands<String, String> getCommands(){
		return commands;
	}
	
	public static void main(String[] args) {
		RedisClientDB redis = new RedisClientDB("localhost", 6379);
		NGrams grams = new NGrams(5);
		grams.calculateNGrams("password");
		redis.updateEntries(grams.getNGrams());
		redis.updateEntries(grams.getNGrams());
		System.out.println(redis.commands.get("swor"));
	}
}
