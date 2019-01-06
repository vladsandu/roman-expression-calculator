package romanCalculator.exception;

/**
 * A custom exception thrown by the RomanCalculator
 * 
 * @author Vlad-Cosmin Sandu
 *
 */

public class BadExpressionException extends Exception{

	private static final long serialVersionUID = 1L;

	public BadExpressionException(String message){
		super(message);
	}
		
}
