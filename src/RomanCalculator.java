package romanCalculator;

import romanCalculator.exception.BadExpressionException;

/**
 * This class consists of static methods that calculate and operate on expressions that contain Roman numbers.
 * Methods of this class that take Roman numbers as parameters can throw a BadExpressionException.
 * @author Vlad-Cosmin Sandu
 *
 */
public class RomanCalculator {

	/**
	 * A boolean that controls if the operations will be written to the console.
	 */
	private static boolean debugMode = false;

	/**
	 * Strings used to validate the given expression.
	 */
	private static final String operators = "+-/*()^";
	private static final String romanNumerals = "IVXLCDM";

	/**
	 * Calculates the specified expression and returns the result in Roman format.
	 * 
	 * The expression must not contain any invalid characters. It can contain any
	 * number of spaces.
	 * The Roman numbers must respect the order of the numerals (from the biggest value to the lowest)
	 * The Roman numbers must not contain more than 3 consecutive, identical numerals.
	 * The number of parenthesis must match.
	 * 
	 * @param expression the expression to be calculated
	 * @return the result of the expression in a Roman number format
	 * @throws BadExpressionException if the expression contains invalid elements
	 */

	public static String calculate(String expression) throws BadExpressionException {
		if(expression == null || expression.isEmpty())
			return null;

		String arabicExpression = convertExpressionToArabic(expression);
		int result = calculateExpressionWithoutParenthesis(arabicExpression);

		return arabicToRoman(result);
	}

	/**
	 * Converts the Roman numerals found in the specified String into their Arabic form. 
	 * The method deletes all spaces and leaves the operators and parenthesis unchanged.
	 * The method also checks for any invalid characters.
	 * 
	 * @param expression the expression to be converted
	 * @return the expression containing Arabic numbers
	 * @throws BadExpressionException if the expression contains invalid characters
	 */
	private static String convertExpressionToArabic(String expression) throws BadExpressionException {

		StringBuilder builder = new StringBuilder();
		StringBuilder romanNumber = new StringBuilder();

		for(int i = 0; i < expression.length(); i++){

			char currentChar = expression.charAt(i);

			if(isRomanNumeral(currentChar)){
				romanNumber.append(currentChar);
			}
			else if(isValidOperator(currentChar) || currentChar == ' '){
				if(romanNumber.length() > 0){
					builder.append(romanToArabic(romanNumber.toString()));
					romanNumber.setLength(0);
				}
				if(currentChar != ' ')
					builder.append(currentChar);
			}
			else{
				throw new BadExpressionException("Invalid character in expression.");
			}
		}

		if(romanNumber.length() > 0){
			builder.append(romanToArabic(romanNumber.toString()));
			romanNumber.setLength(0);
		}

		return builder.toString();
	}

	/**
	 * Checks if the specified character is an operator character (including parenthesis)
	 * 
	 * @param currentChar the character to be checked
	 * @return true if the character is an operator
	 */

	private static boolean isValidOperator(char currentChar) {
		if(operators.indexOf(currentChar) == -1)
			return false;
		else
			return true;
	}

	/**
	 * Checks if the specified character is a Roman numeral
	 * 
	 * @param currentChar the character to be checked
	 * @return true if the character is a Roman numeral
	 */

	private static boolean isRomanNumeral(char currentChar) {
		if(romanNumerals.indexOf(currentChar) == -1)
			return false;
		else
			return true;
	}

	/**
	 * Calculates the expression found in the specified String and returns the result as an Integer.
	 * 
	 * The method uses recursive calls in order to solve the parenthesis. When iterating the String,
	 * if a parenthesis is found, the method searches for its end (taking into account inner parenthesis)
	 * and then calls this method again with the new input. The result is appended to a StringBuilder.
	 * 
	 * When all parenthesis have been solved, the method calls the solveOperationForOperator(char) method 
	 * for every operator (in the proper order of operations).
	 * The resulting String is then parsed into an Integer.
	 * 
	 * @param expression the expression to be calculated
	 * @return the result of the expression in an Integer form
	 * @throws BadExpressionException if the expression format is invalid
	 */

	private static int calculateExpressionWithoutParenthesis(String expression) throws BadExpressionException {

		StringBuilder finalExpression = new StringBuilder();

		for(int i = 0; i < expression.length(); i++){
			if(expression.charAt(i) == '('){
				int countDown = 1;
				int firstParenthesisPos = i;

				while(countDown > 0 && i < expression.length()-1){
					i++;
					if(expression.charAt(i) == '(')
						countDown++;
					else if(expression.charAt(i) == ')')
						countDown--;
				}

				if(countDown == 0){
					finalExpression.append(
							calculateExpressionWithoutParenthesis(expression.substring(firstParenthesisPos+1, i)));
				}
				else{
					throw new BadExpressionException("Parenthesis number doesn't match.");
				}

			}
			else if(expression.charAt(i) == ')'){
				throw new BadExpressionException("Parenthesis number doesn't match.");
			}
			else
				finalExpression.append(expression.charAt(i));
		}

		if(debugMode) System.out.println("Expression: " + finalExpression.toString());

		solveOperationsForOperator(finalExpression, '^');
		solveOperationsForOperator(finalExpression, '*');
		solveOperationsForOperator(finalExpression, '/');
		solveOperationsForOperator(finalExpression, '-');	
		solveOperationsForOperator(finalExpression, '+');

		return Integer.parseInt(finalExpression.toString());
	}

	/**
	 * Searches the String found in the specified StringBuilder for any operators matching the character parameter 
	 * and replaces the operator and the left and right members with the result of the operation.
	 * 
	 * The method obtains the start position (where the left operand starts) and the end position (where the right
	 * operator ends) and passes the String to the calculateOperation method.
	 * 
	 * The method also solves any sign conflicts ( ++, --, +-, -+ ) found in the expression.
	 * 
	 * @param expression the expression in the form of a StringBuilder
	 * @param operator the character representing the operator type
	 */

	private static void solveOperationsForOperator(StringBuilder expression, char operator) {
		solveSignConflicts(expression);

		while(expression.indexOf(String.valueOf(operator), 1) != -1) {
			int operatorPos = expression.indexOf(String.valueOf(operator), 1);
			int startPos = getOperationStartPos(expression.toString(), operatorPos);
			int endPos = getOperationEndPos(expression.toString(), operatorPos);
			expression.replace(startPos, endPos+1, calculateOperation(expression.substring(startPos, endPos+1), operator));
			solveSignConflicts(expression);

			if(debugMode) System.out.println("Operation: " + expression.toString());
		}
	}

	/**
	 * Searches the contents of the specified StringBuilder and replaces all occurances of
	 * sign conflicts such as: +-, -+, ++, -- with the resulting operator.
	 * 
	 * @param expression the expression in the form of a StringBuilder
	 */

	private static void solveSignConflicts(StringBuilder expression) {
		while(expression.indexOf("+-") != -1){
			int conflictPos = expression.indexOf("+-");
			expression.replace(conflictPos, conflictPos+2, "-");
		}

		while(expression.indexOf("++") != -1){
			int conflictPos = expression.indexOf("++");
			expression.replace(conflictPos, conflictPos+2, "+");
		}

		while(expression.indexOf("--") != -1){
			int conflictPos = expression.indexOf("--");
			expression.replace(conflictPos, conflictPos+2, "+");
		}

		while(expression.indexOf("-+") != -1){
			int conflictPos = expression.indexOf("-+");
			expression.replace(conflictPos, conflictPos+2, "-");
		}
	}

	/**
	 * Finds the start position of the left operand of the operator found at the specified position.
	 * 
	 * The method searches the given String in the left direction of the given position until it finds 
	 * a character matching an operator. 
	 * If the position found is 1 and the position 0 is a minus, the position becomes 0, signaling that
	 * the left operand is a negative number.
	 * 
	 * @param expression the expression to search in
	 * @param operatorPos the position of the operator in the expression
	 * @return the position of the start of the left operand
	 */

	private static int getOperationStartPos(String expression, int operatorPos) {
		int position = 0;

		for(int i = operatorPos-1; i >= 0; i--){
			if(operators.indexOf(expression.charAt(i)) != -1){
				position = i+1;
				break;
			}
		}

		if(position == 1 && expression.charAt(0) == '-')
			position = 0;

		return position;
	}

	/**
	 * Finds the end position of the right operand of the operator found at the specified position.
	 * 
	 * The method searches the given String in the right direction of the given position until it finds 
	 * a character matching an operator. The search starts at operatorPos + 2 in order to take any negative
	 * numbers into account.
	 *  
	 * @param expression the expression to search in
	 * @param operatorPos the position of the operator in the expression
	 * @return the position of the end of the right operand
	 */

	private static int getOperationEndPos(String expression, int operatorPos) {
		int position = expression.length()-1;

		for(int i = operatorPos+2; i < expression.length(); i++){
			if(operators.indexOf(expression.charAt(i)) != -1){
				position = i-1;
				break;
			}
		}

		return position;
	}

	/**
	 * Calculates the result of the operation, parsing the Strings 
	 * representing the left and right operands into Integers and then using the given
	 * operator to store the result into an Integer.
	 *  
	 * @param expression the expression to calculate
	 * @param operator the operator used in the expression
	 * @return the result of the operation in the form of a String
	 */

	private static String calculateOperation(String expression, char operator) {
		int value = 0;
		int operatorPos = expression.indexOf(operator);

		if(operatorPos == -1)
			return new String();

		int leftMember = 0, rightMember = 0;

		if(operatorPos != 0){
			leftMember = Integer.parseInt(expression.substring(0, operatorPos));
			rightMember = Integer.parseInt(expression.substring(operatorPos+1));
		}

		switch(operator){
		case '^':	value = calculateExponent(leftMember, rightMember); 	break;
		case '*':	value = leftMember * rightMember;						break;
		case '/':	value = leftMember / rightMember; 						break;
		case '+':	value = leftMember + rightMember;						break;
		case '-':	value = leftMember - rightMember; 						break;
		}
		return Integer.toString(value);

	}

	/**
	 * Calculates the exponent of a number. 
	 * It supports negative exponents and bases.
	 * 
	 * @param base the base of the operation
	 * @param exponent the exponent
	 * @return the result as an Integer
	 */

	private static int calculateExponent(int base, int exponent){
		int result = 1;

		for(int i = 0; i < exponent; i++){
			result *= base;
		}

		return result;
	}

	/**
	 * Converts the Roman number from the specified String into an integer.
	 * 
	 * The method loops until the number String is empty and at every iteration, 
	 * it searches for the biggest value numeral. It then adds it to a sum which 
	 * represents the actual Arabic number.
	 * 
	 * The Roman numerals have to respect the order of Roman numbers (from the 
	 * biggest to the smallest).
	 * There must not be more than 3 consecutive, identical numerals in the number.
	 * The String must not be empty
	 * The number must be smaller than 3000.
	 * 
	 * @param number the String containing the Roman number
	 * @return the value of the Roman number as an Integer
	 * @throws BadExpressionException if the number format is invalid
	 */

	public static int romanToArabic(String number) throws BadExpressionException {

		if(!isRomanNumberSyntaxCorrect(number))
			throw new BadExpressionException("Roman number format is invalid.");

		StringBuilder romanNumber = new StringBuilder(number);
		int arabicNumber = 0;

		while(romanNumber.length() > 0){

			int maxNumeralPosition = 0;
			int maxNumeralValue = 0;

			for(int i = 0; i < romanNumber.length(); i++){
				int value = getValueOfNumeral(romanNumber.charAt(i));

				if(value > maxNumeralValue){
					maxNumeralValue = value;
					maxNumeralPosition = i;
				}
			}

			if(maxNumeralPosition > 1){
				throw new BadExpressionException("Roman number format is invalid.");
			}

			if(maxNumeralPosition == 1){
				if(!isNumeralOrderCorrect(romanNumber.charAt(0), romanNumber.charAt(1)))
					throw new BadExpressionException("Roman number format is invalid.");

				arabicNumber += getValueOfNumeral(romanNumber.charAt(1)) - getValueOfNumeral(romanNumber.charAt(0));
			}
			else
				arabicNumber += getValueOfNumeral(romanNumber.charAt(0));

			romanNumber.delete(0, maxNumeralPosition + 1);
		}

		if(arabicNumber >= 3000 || arabicNumber == 0)
			throw new BadExpressionException("Roman number format is invalid.");

		return arabicNumber;
	}
	
	/**
	 * Checks if there are more than 3 consecutive, identical numerals in the Roman number.
	 * 
	 * @param number the String containing the number
	 * @return true if the number format is correct
	 */

	private static boolean isRomanNumberSyntaxCorrect(String number){
		int countDown = 3;
		char currentNumeral = '-';

		for(char c : number.toCharArray()){
			if(!isRomanNumeral(c))
				return false;

			if(c == currentNumeral){
				countDown--;

				if(countDown == 0)
					return false;
			}
			else{
				countDown = 3;
				currentNumeral = c;
			}
		}

		return true;
	}

	/**
	 * Checks if two numerals represent a valid composed numeral. 
	 * Example: XC (90) , CM (900)
	 * 
	 * @param first the first numeral
	 * @param second the second numeral
	 * @return true if the numerals represent a valid composed numeral
	 */
	
	private static boolean isNumeralOrderCorrect(char first, char second) {
		if(first == 'I' && (second == 'V' || second == 'X'))
			return true;
		if(first == 'X' && (second == 'L' || second == 'C'))
			return true;
		if(first == 'C' && (second == 'D' || second == 'M'))
			return true;

		return false;
	}

	/**
	 * Transforms the given Integer into a Roman representation. It loops
	 * until the number is equal to 0 and at every iteration it checks what is the highest
	 * value numeral that can be subtracted from it and then it is appended to a StringBuilder.
	 * 
	 * Negative numbers are supported.
	 * 
	 * @param value the number to transform
	 * @return the Roman representation of the given value
	 */
	
	public static String arabicToRoman(int value) {
		int number = value;
		StringBuilder romanNumber = new StringBuilder();

		if(number < 0){
			number *= -1;
			romanNumber.append("-");
		}

		while(number > 0){
			if(number >= 1000){
				romanNumber.append("M");
				number -= 1000;
			}
			else if(number >= 900){
				romanNumber.append("CM");
				number -= 900;
			}
			else if(number >= 500){
				romanNumber.append("D");
				number -= 500;
			}
			else if(number >= 400){
				romanNumber.append("CD");
				number -= 400;
			}
			else if(number >= 100){
				romanNumber.append("C");
				number -= 100;
			}
			else if(number >= 90){
				romanNumber.append("XC");
				number -= 90;
			}
			else if(number >= 50){
				romanNumber.append("L");
				number -= 50;
			}
			else if(number >= 40){
				romanNumber.append("XL");
				number -= 40;
			}
			else if(number >= 10){
				romanNumber.append("X");
				number -= 10;
			}
			else if(number >= 9){
				romanNumber.append("IX");
				number -= 9;
			}
			else if(number >= 5){
				romanNumber.append("V");
				number -= 5;
			}
			else if(number >= 4){
				romanNumber.append("IV");
				number -= 4;
			}
			else{
				romanNumber.append("I");
				number -= 1;
			}
		}

		return romanNumber.toString();
	}

	/**
	 * Returns the Integer value of the given Roman numeral character.
	 * 
	 * @param character the Roman numeral
	 * @return the Arabic value of the Roman numeral
	 */
	
	private static int getValueOfNumeral(char character) {
		int value = 0;

		switch(character){
		case 'I': value = 1; 	break;
		case 'V': value = 5; 	break;
		case 'X': value = 10; 	break;
		case 'L': value = 50; 	break;
		case 'C': value = 100; 	break;
		case 'D': value = 500; 	break;
		case 'M': value = 1000; break;
		}

		return value;
	}

	/**
	 * Sets the debug mode
	 * 
	 * @param debugMode the value of the DebugMode
	 */
	
	public static void setDebugMode(boolean debugMode) {
		RomanCalculator.debugMode = debugMode;
	}
}
