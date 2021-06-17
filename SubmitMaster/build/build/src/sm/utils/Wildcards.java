package sm.utils;

import java.util.Arrays;

public class Wildcards {

	
	
	// The main function that checks if  
	// two given strings match. The first string  
	// may contain wildcard characters 
	public static boolean match(String first, String second)  
	{ 
	  
	    // If we reach at the end of both strings,  
	    // we are done 
	    if (first.length() == 0 && second.length() == 0) 
	        return true; 
	  
	    // Make sure that the characters after '*'  
	    // are present in second string.  
	    // This function assumes that the first 
	    // string will not contain two consecutive '*' 
	    if (first.length() > 1 && first.charAt(0) == '*' &&  
	                              second.length() == 0) 
	        return false; 
	  
	    // If the first string contains '?',  
	    // or current characters of both strings match 
	    if ((first.length() > 1 && first.charAt(0) == '?') ||  
	        (first.length() != 0 && second.length() != 0 &&  
	         first.charAt(0) == second.charAt(0))) 
	        return match(first.substring(1),  
	                     second.substring(1)); 
	  
	    // If there is *, then there are two possibilities 
	    // a) We consider current character of second string 
	    // b) We ignore current character of second string. 
	    if (first.length() > 0 && first.charAt(0) == '*') 
	        return match(first.substring(1), second) ||  
	               match(first, second.substring(1)); 
	    return false; 
	} 
	
	
	
public	static boolean strmatch(String str, String pattern) {
		int n = str.length();
		int m = pattern.length();
// empty pattern can only match with
// empty string
if (m == 0)
return (n == 0);

// lookup table for storing results of
// subproblems
boolean[][] lookup = new boolean[n + 1][m + 1];

// initailze lookup table to false
for (int i = 0; i < n + 1; i++)
Arrays.fill(lookup[i], false);

// empty pattern can match with empty string
lookup[0][0] = true;

// Only '*' can match with empty string
for (int j = 1; j <= m; j++)
if (pattern.charAt(j - 1) == '*')
lookup[0][j] = lookup[0][j - 1];

// fill the table in bottom-up fashion
for (int i = 1; i <= n; i++)
{
for (int j = 1; j <= m; j++)
{
// Two cases if we see a '*'
// a) We ignore '*'' character and move
//    to next  character in the pattern,
//     i.e., '*' indicates an empty
//     sequence.
// b) '*' character matches with ith
//     character in input
if (pattern.charAt(j - 1) == '*')
    lookup[i][j] = lookup[i][j - 1]
                   || lookup[i - 1][j];

// Current characters are considered as
// matching in two cases
// (a) current character of pattern is '?'
// (b) characters actually match
else if (pattern.charAt(j - 1) == '?'
         || str.charAt(i - 1)
                == pattern.charAt(j - 1))
    lookup[i][j] = lookup[i - 1][j - 1];

// If characters don't match
else
    lookup[i][j] = false;
}
}

return lookup[n][m];
}
}
