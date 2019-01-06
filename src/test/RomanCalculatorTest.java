package test;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import romanCalculator.RomanCalculator;
import romanCalculator.exception.BadExpressionException;

/**
 * A class containing unit tests used to test the implementation of the RomanCalculator class.
 * The tests are written using the JUnit testing framework.
 * 
 * @author Vlad-Cosmin Sandu
 *
 */
public class RomanCalculatorTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testArabicToRoman() throws BadExpressionException {
		assertEquals("Zero Test", "", RomanCalculator.arabicToRoman(0));
		assertEquals("Single Digit Test", "V", RomanCalculator.arabicToRoman(5));
		assertEquals("Multiple Digit Test", "MMXV", RomanCalculator.arabicToRoman(2015));
	}

	@Test
	public void testRomanToArabic() throws BadExpressionException {
		assertEquals("Single Digit Test", 7, RomanCalculator.romanToArabic("VII"));
		assertEquals("Single Numeral Test", 5, RomanCalculator.romanToArabic("V"));
		assertEquals("Multiple Digit Test", 2015, RomanCalculator.romanToArabic("MMXV"));
	}
	
	@Test
	public void testRomanToArabicEmptyString() throws BadExpressionException {
		thrown.expect(BadExpressionException.class);
	    RomanCalculator.romanToArabic("");   
	}
	
	@Test
	public void testRomanToArabicInvalidCharacter() throws BadExpressionException {
		thrown.expect(BadExpressionException.class);
	    RomanCalculator.romanToArabic("MXII3");   
	}
	
	@Test
	public void testRomanToArabicInvalidNumeralOrder() throws BadExpressionException {
		thrown.expect(BadExpressionException.class);
	    RomanCalculator.romanToArabic("IM");   
	}
	
	@Test
	public void testRomanToArabicMoreThanThreeIdenticalNumerals() throws BadExpressionException {
		thrown.expect(BadExpressionException.class);
	    RomanCalculator.romanToArabic("XXXX");   
	}
	
	@Test
	public void testRomanToArabicNumberGreaterThanLimit() throws BadExpressionException {
		thrown.expect(BadExpressionException.class);
	    RomanCalculator.romanToArabic("MMMI"); //More than (or equal to) 3000  
	}
	
	@Test
	public void testCalculateAnEmptyString() throws BadExpressionException {
		assertEquals(null, RomanCalculator.calculate(""));
	}
	
	@Test
	public void testCalculateASingleNumber() throws BadExpressionException {
		assertEquals("V", RomanCalculator.calculate("V"));
		assertEquals("-V", RomanCalculator.calculate("-V"));
	}

	@Test
	public void testCalculateASingleNumberInParenthesis() throws BadExpressionException {
		assertEquals("VII", RomanCalculator.calculate("(VII)"));
		assertEquals("VII", RomanCalculator.calculate("((VII))"));
		assertEquals("-VII", RomanCalculator.calculate("(-VII)"));
		assertEquals("-VII", RomanCalculator.calculate("-(VII)"));
	}
	
	@Test
	public void testCalculateASimpleOperation() throws BadExpressionException {
		assertEquals("VI", RomanCalculator.calculate("V + I"));
		assertEquals("V", RomanCalculator.calculate("X - V"));
		assertEquals("L", RomanCalculator.calculate("X * V"));
		assertEquals("V", RomanCalculator.calculate("L / X"));
		assertEquals("C", RomanCalculator.calculate("X ^ II"));
	}
	
	@Test
	public void testCalculateSimpleParenthesis() throws BadExpressionException {
		assertEquals("VII", RomanCalculator.calculate("(I + II) * III - (IV / II)"));
		assertEquals("VIII", RomanCalculator.calculate("(III * II - V) * IV + (II * II)"));
	}
	
	@Test
	public void testCalculateInnerParenthesis() throws BadExpressionException {
		String expression = "((I + II * III - IV) * V + VI * (VII + VIII) + IX - X + XI + XII / III) * II";
		assertEquals(RomanCalculator.arabicToRoman(238), RomanCalculator.calculate(expression));
		
		expression = "I + ( II + III + ( IV + V + ( VI + VII + ( VIII + IX))))";
		assertEquals(RomanCalculator.arabicToRoman(45), RomanCalculator.calculate(expression));
	}
	
	@Test
	public void testCalculateWithAndWithoutSpaces() throws BadExpressionException {
		assertEquals("III", RomanCalculator.calculate("  (    I   +   II  )  "));
		assertEquals("III", RomanCalculator.calculate("(I+II)"));
	}
	
	@Test
	public void testCalculateWrongParenthesis() throws BadExpressionException {
		thrown.expect(BadExpressionException.class);
	    RomanCalculator.calculate("(V + I * ( X + I )");   
	}
	
	@Test
	public void testCalculateSignConflicts() throws BadExpressionException {
		assertEquals("-IV", RomanCalculator.calculate(" I + - V "));
		assertEquals("-IV", RomanCalculator.calculate(" I - + V "));
		assertEquals("VI", RomanCalculator.calculate("  I - - V "));
		assertEquals("VI", RomanCalculator.calculate("  I + + V "));
	}
}
