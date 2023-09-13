public class Polynomial{
    double[] coefficients; // Static so we don't need an object

    public Polynomial(){
        coefficients = new double[1]; // Default value of element is 0
    }
    public Polynomial(double[] coefficients){
        this.coefficients = coefficients;
    }

    public Polynomial add(Polynomial poly){
        // Save the bigger polynomial size into polySize
        int argPolySize = poly.coefficients.length;
        int thisPolySize = this.coefficients.length;
        int polySize;
        if(argPolySize >= thisPolySize) polySize = argPolySize;
        else polySize = thisPolySize;
        // Add
        double[] coefficientsTemp = new double[polySize];
        for(int i=0; i<polySize; i++){
            if(i + 1 > argPolySize){
                coefficientsTemp[i] = this.coefficients[i];
            }else if(i + 1 > thisPolySize){
                coefficientsTemp[i] = poly.coefficients[i];
            }else{
                coefficientsTemp[i] = poly.coefficients[i] + this.coefficients[i];
            }
        }
        // Return
        poly.coefficients = coefficientsTemp;
        return poly;
    }
    public double evaluate(double x){
        int polySize = this.coefficients.length;
        int curPower = 0;
        double sum = 0;
        for(int i=0; i<polySize; i++){
            double term = this.coefficients[i] * Math.pow(x, curPower);
            sum += term;
            curPower++;
        }
        return sum;
    }
    public boolean hasRoot(double value){
        double res = evaluate(value);
        if(res == 0) return true;
        else return false;
    }
}