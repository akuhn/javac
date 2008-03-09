
public class TestAssertion {

	public static void main(String[] args) {
		assert (1 == 1) : "this should never happen!"; 
	}
	
	private void bar() {
		if (true) 
			assert true;
		else
			assert false;
	}
	
}
