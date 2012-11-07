import static java.lang.System.out;

public class TestAssertion {

	public static void main(String[] args) {
		try {
			assert false;
			out.println("Oops, assertions not checked!");
		}
		catch (AssertionError expected) {
			out.println("Okay! fa works fine.");
		}
	}
	
}
