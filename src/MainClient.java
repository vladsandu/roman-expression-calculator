package romanCalculator;

import romanCalculator.exception.BadExpressionException;

/**
 * This class shows an example of how the RomanCalculator is used to calculate an expression.
 * 
 * @author Vlad-Cosmin Sandu
 *
 */
public class MainClient {

	public static void main(String[] args) {

		//RomanCalculator.setDebugMode(true);
		String expression = "((I + II * III - IV) * V + VI * (VII + VIII) + IX - X + XI + XII / III) * II";
		
		try {
			System.out.println(RomanCalculator.calculate(expression));
		} catch (BadExpressionException e) {
			// TODO: Add logs here
			e.printStackTrace();
		}
	}

}
