import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Driver {
    public static void main(String[] args) throws IOException {
    	double[] c1c = {1, -9.7, 3, 2};
    	int[] c1e = {2, 0, 3, 1};
    	Polynomial p1 = new Polynomial(c1c, c1e);
    	
    	p1.saveToFile("someFile");
    	
    	double[] c2c = {2, 1};
    	int[] c2e = {2, 1};
    	Polynomial p2 = new Polynomial(c2c, c2e);
    	
    	Polynomial s = p1.add(p2);
    	System.out.println(Arrays.toString(s.nonZCoefficients) + " " + Arrays.toString(s.exponents));
    	
    	System.out.println(p1.evaluate(5.2));
    	System.out.println(p1.hasRoot(-3));
    	
    	Polynomial t = p1.multiply(p2);
    	System.out.println(Arrays.toString(t.nonZCoefficients) + " " + Arrays.toString(t.exponents));
    	
    	File f = new File("samplePoly");
    	Polynomial p3 = new Polynomial(f);
    	System.out.println(Arrays.toString(p3.nonZCoefficients) + " " + Arrays.toString(p3.exponents));
    }
}