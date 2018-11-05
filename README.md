Steps to run our 'classify.jar' file (on a mac running OS X 10.13 (High Sierra)):
1. If Redis is not installed on the mac machine, install Redis by going to the following URL: https://redis.io/ and then under the 'Download it' section, there is a link to download Redis.
2. Once Redis is downloaded, extract the tar or zip files to a location of your choice
3. Once Redis is installed, you need to run the server executable with the 'redis.conf' file as an argument.  The directory of the executable is in redis-5.0.0 (or whatever you named your directory when you extracted the files from step 2), and within the src directory is the 'redis-server' executable. From the redis-5.0.0 directory, run the following command: src/redis-server redis.conf. The argument 'redis.conf' makes sure that redis logging is piped to a file and not standard output.
4. Once the server is running, you can run our 'classify.jar' on the command prompt with the command 'java -jar classify.jar' 
5. It will take around six minutes for the database to be populated with our word list. After this completes, you can type in a password.  
6.  The program continues to prompt you for passwords, until you type in 'quit', at which point, the program terminates


Explanation of our program:
The motivation for our password-strength algorithm comes from the paper 'Adaptive Password-Strength Meters
from Markov Models' by Claude Castelluccia, Markus Durmuth , and Daniele Perito. The paper can be found at the following URL: https://pdfs.semanticscholar.org/240b/ead78a1564b047b0bbdfb755ddc9808321d8.pdf.

The basic idea is that when a user types in a password, one naive way of looking at a password is that (for simplicity, assume the alphabet of a password can only consist of lowercase letters, for a total of 26 options for each character) if a password is of length n, the number of unique passwords is 26 to the power of n.  So, in the worst case, if you wanted to crack a password of size n, you would need to check 26 to the power of n passwords.  In reality, this isn't really how people create passwords. Oftentimes, people create passwords using english words, so there aren't really 26 to the power of n unique passwords.  There would be less.  I password that starts with 'th' probably wont be followed by the letter 'q', while 'th' would more likely be followed by an 'a' or 'i'. This motivates the idea for our algorithm.

The basis of our algorithm is the construction of n-grams. An n-gram is a substring of a specific string. For example, given the string 'password', a 2-gram for this string would be 'pa', 'as' or 'sw', however 'ps' would not be an n-gram because it does not use consecutive letters.  We generated 5-grams, 4-grams, 3-grams, 2-grams, and 1-grams for each password in the word list (every word in the words.txt file), and every password that the user types in. More specifically, we store the frequency of each n-gram in the database. So in the Redis database, we store key, value pairs of the form 'th', 4. This means that the 2-gram 'th' occurs with frequency 4.  In addition, each individual password from the word.txt file, as well as each password that a user types in is stored in the Redis database as a set.

After the database is populated, a user is prompted to type in a password.  Then, we calculate a probability for this password, where the probability of this password is (essentially) the likeness of this password to other passwords in the Redis database (to get a better sense of what this probability represents, refer to the paper linked above).  A lower probability indicates a stronger password than a password with a higher probability.  After the password probability is calculated, we standardize this value (since these probabilities are extremely small) by taking the -log base 2 of the probability.  We use a threshold value of 65, so strong passwords are any password 'p' such that -log base 2 of (probability(p)) is greater than 65, and a weak password is any value less than or equal to 65. Once a password is classified as 'weak' or 'strong', we add the password to the Redis database, and all of the n-grams of the password (for all n = 1, 2, 3, 4, and 5)

Special cases:
1. A password that already exists in the database (originating from the original word list, or from a previous typed in password from the user) is considered weak
2. A password that, when lowercasing each character of the password, is a password that exists in the database is a weak password. For example, if 'password123' exists in the database, and a user types in 'PASSword123', this password is considered weak
3. A password that is less than 5 characters is considered weak

