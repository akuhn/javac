//  Copyright (c) 2008 Adrian Kuhn <akuhn(a)iam.unibe.ch>
//  
//  This file is part of Roman Numerals.
//  
//  Roman Numerals is free software: you can redistribute it and/or modify it
//  under the terms of the GNU Affero General Public License as published by the
//  Free Software Foundation, either version 3 of the License, or (at your
//  option) any later version.
//  
//  Roman Numerals is distributed in the hope that it will be useful, but
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
//  License for more details.
//  
//  You should have received a copy of the GNU Affero General Public License
//  along with Roman Numerals. If not, see <http://www.gnu.org/licenses/>.
//  
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