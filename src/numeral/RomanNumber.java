package numeral;

public class RomanNumber {

	   private final static String[] LETTERS = { "M", "CM", "D", "CD",
	         "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };
	   private final static int[] VALUES = { 1000, 900, 500, 400, 100, 90,
	         50, 40, 10, 9, 5, 4, 1 };

	   public final static String romanize(int value) {
		   String roman = "";
		   int n = value;
		   for (int i = 0; i < LETTERS.length; i++) {
			   while (n >= VALUES[i]) {
				   roman += LETTERS[i];
		           n -= VALUES[i];
		       }
		   }
		   return roman;
	   }
	   
	   public final static int numberize(String roman) {
		  int start = 0, value = 0;
	      for (int i = 0; i < LETTERS.length; i++) {
	         while (roman.startsWith(LETTERS[i], start)) {
	            value += VALUES[i];
	            start += LETTERS[i].length();
	         }
	      }
	      return start == roman.length() ? value : -1;
	  }
	   
	   public final static boolean isRoman(String roman) {
		   return roman.equals(romanize(numberize(roman)));
	   }

	  public static void main(String[] args) {
		System.out.println(isRoman("CMCM"));
		System.out.println(romanize(1977));	}
	   
} 