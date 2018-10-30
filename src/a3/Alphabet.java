package a3;
//Sam Gearou, Josh Gearou
//October 29, 2018
//This class stores an alphabet of valid characters. Passwords can only contain characters in the 
//ALPHABET variable

public class Alphabet {
	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "`1234567890-=~!@#$%^&*()_+,./<>?;':\"[]\\{}|";
	
	public static String getAlphabet() {
		return ALPHABET;
	}
}
