import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Polynomial {
	double[] nonZCoefficients; // Non-zero coefficients of polynomial
	int[] exponents; // Exponents of polynomial
	
	public Polynomial() {
		this.nonZCoefficients = null;
		this.exponents = null;
	}

	public Polynomial(double[] nonZCoefficients, int[] exponents) { // Two arguments now
		this.nonZCoefficients = nonZCoefficients;
		this.exponents = exponents;
	}
	public Polynomial(File f) throws FileNotFoundException {
		// Read polynomial from file
		Scanner sc = new Scanner(f);
		String data = sc.next();
		// Split based on "+" or "-", but keep delimiters
		String[] dataSplit = data.split("(?=\\+)|(?=\\-)");
		// Create arrays
		nonZCoefficients = new double[dataSplit.length];
		exponents = new int[dataSplit.length];
		// Loop through dataSplit array
		for(int i=0; i<dataSplit.length; i++) {
			// Split the element to have the coefficient and exponent separately
			String[] elementSplit = dataSplit[i].split("x");
			// Save coefficient
			nonZCoefficients[i] = Double.parseDouble(elementSplit[0]);
			// If there is only a coefficient (no exponent)
			if(elementSplit.length == 1) {
				exponents[i] = 0;
			// If there is both a coefficient and exponent
			}else {
				exponents[i] = Integer.parseInt(elementSplit[1]); 
			}
		}
		sc.close();
	}

	public Polynomial add(Polynomial poly) {
		// Error checking
		if(poly == null 
				|| poly.nonZCoefficients != null && poly.nonZCoefficients.length != poly.exponents.length) {
			return null;
		}
		// Check for 0 polynomial
		if(poly.nonZCoefficients == null) {
			return new Polynomial(nonZCoefficients, exponents);
		}else if(nonZCoefficients == null) {
			return new Polynomial(poly.nonZCoefficients, poly.exponents);
		}
		// Sort exponents
		sortTwoArrays(nonZCoefficients, exponents);
		sortTwoArrays(poly.nonZCoefficients, poly.exponents);
		// Count how many terms in total
		int numTerms = countMaxNumTerms(poly, 0);
		// Create new object
		Polynomial p = new Polynomial(new double[numTerms], new int[numTerms]);
		// Add
		int argPolyIndex = 0;
		int thisPolyIndex = 0;
		for (int i = 0; i < numTerms; i++) {
			if (argPolyIndex >= poly.exponents.length) {
				p.nonZCoefficients[i] = nonZCoefficients[thisPolyIndex];
				p.exponents[i] = exponents[thisPolyIndex];
				thisPolyIndex++;
			} else if (thisPolyIndex >= exponents.length) {
				p.nonZCoefficients[i] = poly.nonZCoefficients[argPolyIndex];
				p.exponents[i] = poly.exponents[argPolyIndex];
				argPolyIndex++;
			} else {
				if (poly.exponents[argPolyIndex] < exponents[thisPolyIndex]) {
					p.nonZCoefficients[i] = poly.nonZCoefficients[argPolyIndex];
					p.exponents[i] = poly.exponents[argPolyIndex];
					argPolyIndex++;
				} else if (poly.exponents[argPolyIndex] > exponents[thisPolyIndex]) {
					p.nonZCoefficients[i] = nonZCoefficients[thisPolyIndex];
					p.exponents[i] = exponents[thisPolyIndex];
					thisPolyIndex++;
				} else {
					p.nonZCoefficients[i] = poly.nonZCoefficients[argPolyIndex] + nonZCoefficients[thisPolyIndex];
					p.exponents[i] = poly.exponents[argPolyIndex];
					argPolyIndex++;
					thisPolyIndex++;
				}
			}
		}
		// Check for coefficients == 0 and remove them from arrays
		Object[] object = removeZeroCoefficients(p.nonZCoefficients, p.exponents);
		p.nonZCoefficients = (double[])object[0];
		p.exponents = (int[])object[1];
		// Set arrays to null if they are empty
		if(p.nonZCoefficients.length == 0) {
			p.nonZCoefficients = null;
			p.exponents = null;
		}
		return p;
	}

	public double evaluate(double x) {
		if(nonZCoefficients == null) {
			return 0;
		}
		int polySize = exponents.length;
		double sum = 0;
		for (int i = 0; i < polySize; i++) {
			sum += nonZCoefficients[i] * Math.pow(x, exponents[i]);
		}
		return sum;
	}

	public boolean hasRoot(double value) {
		double res = evaluate(value);
		if (res == 0)
			return true;
		else
			return false;
	}

	public Polynomial multiply(Polynomial poly) {
		// Error checking
		if(poly == null 
				|| poly.nonZCoefficients != null && poly.nonZCoefficients.length != poly.exponents.length) {
			return null;
		}
		// Check for 0 polynomial
		if(poly.nonZCoefficients == null || nonZCoefficients == null) {
			return new Polynomial();
		}
		// Count how many terms in total (at the end)
		int numTerms = countMaxNumTerms(poly, 1);
		// Create new object
		Polynomial p = new Polynomial(new double[numTerms], new int[numTerms]);
		// Find the exponents
		int numTermsImm = exponents.length * poly.exponents.length; // Total number of terms immediately after multiplying
		int[] exponentsImm = new int[numTermsImm]; // Exponents immediately after multiplying
		int x = 0;
		for(int i=0; i<exponents.length; i++) {
			for(int j=0; j<poly.exponents.length; j++) {
				exponentsImm[x] = exponents[i] + poly.exponents[j];
				x++;
			}
		}
		Arrays.sort(exponentsImm);
		int[] exponentsImmReversed = new int[numTermsImm];
		for(int i=0; i<numTermsImm; i++) {
			exponentsImmReversed[i] = exponentsImm[numTermsImm-i-1];
		}
		exponentsImm = exponentsImmReversed;
		x = 0;
		int prevExponent = -1;
		for(int i=0; i<numTermsImm; i++) {
			if(exponentsImm[i] != prevExponent) {
				p.exponents[x] = exponentsImm[i];
				prevExponent = exponentsImm[i];
				x++;
			}
		}
		// Find the coefficients
		for(int i=0; i<exponents.length; i++) {
			for(int j=0; j<poly.exponents.length; j++) {
				double coefficient = nonZCoefficients[i] * poly.nonZCoefficients[j];
				int exponent = exponents[i] + poly.exponents[j];
				int indexOfExponent = findIndexofElement(p.exponents, exponent);
				p.nonZCoefficients[indexOfExponent] += coefficient;
			}
		}
		// Check for coefficients == 0 and remove them from arrays
		Object[] object = removeZeroCoefficients(p.nonZCoefficients, p.exponents);
		p.nonZCoefficients = (double[])object[0];
		p.exponents = (int[])object[1];
		return p;
	}

	public void saveToFile(String filename) throws IOException {
		FileWriter w = new FileWriter(filename);
		String poly = "";
		if(nonZCoefficients == null) {
			poly = "0";
		}else {
			for(int i=0; i<exponents.length; i++) {
				if(nonZCoefficients[i] > 0 && i != 0) {
					poly += "+";
				}
				poly += Long.toString(Math.round(nonZCoefficients[i]));
				if(exponents[i] != 0) {
					poly += "x" + Integer.toString(exponents[i]);
				}
			}
		}
		w.write(poly);
		w.close();
	}
	
	// Helper functions
	public int countMaxNumTerms(Polynomial poly, int option) {
		int numTerms;
		if(option == 0) { // Addition
			// Count how many terms in total by checking how many different
			// exponents there are between the two polynomials
			numTerms = exponents.length;
			for (int i = 0; i < poly.exponents.length; i++) {
				boolean notExist = true;
				for (int j = 0; j < exponents.length; j++) {
					if (poly.exponents[i] == exponents[j]) {
						notExist = false;
						break;
					}
				}
				if(notExist) numTerms++;
			}
		}else { // Multiplication
			// Count how many terms in total by finding the difference between
			// the lowest exponent and the highest exponent
			int lowestExpo = exponents[0] + poly.exponents[0];
			int highestExpo = exponents[exponents.length-1] + poly.exponents[poly.exponents.length-1];
			numTerms = highestExpo - lowestExpo + 1;
		}
		return numTerms;
	}
	public int findIndexofElement(int[] arr, int val) {
		for(int i=0; i<arr.length; i++) {
			if(arr[i] == val) {
				return i;
			}
		}
		return -1;
	}
	public int findIndexofElement(double[] arr, int val) {
		for(int i=0; i<arr.length; i++) {
			if(arr[i] == val) {
				return i;
			}
		}
		return -1;
	}
	public void sortTwoArrays(double[] nonZCoeff, int[] expo) {
		for(int i=0; i<expo.length; i++) {
			for(int j=0; j<expo.length-1; j++) {
				if(expo[j] > expo[j+1]) {
					int temp = expo[j];
					expo[j] = expo[j+1];
					expo[j+1] = temp;
					
					double temp2 = nonZCoeff[j];
					nonZCoeff[j] = nonZCoeff[j+1];
					nonZCoeff[j+1] = temp2;
				}
			}
		}
	}
	public Object[] removeZeroCoefficients(double[] nonZCoeff, int[] expo) {
		int indexOfZero = findIndexofElement(nonZCoeff, 0);
		while(indexOfZero != -1) {
			double[] newArrA = new double[nonZCoeff.length - 1];
			int[] newArrB = new int[expo.length - 1];
			for(int x=0, y=0; x<nonZCoeff.length; x++) {
				if(x == indexOfZero) {
					continue;
				}
				newArrA[y] = nonZCoeff[x];
				newArrB[y] = expo[x];
				y++;
			}
			nonZCoeff = newArrA;
			expo = newArrB;
			indexOfZero = findIndexofElement(nonZCoeff, 0);
		}
		return new Object[] {nonZCoeff, expo};
	}
}