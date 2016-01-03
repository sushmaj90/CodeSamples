import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

public class RationalTest extends TestCase {

    protected Rational HALF;

    protected void setUp() {
      HALF = new Rational( 1, 2 );
    }

    // Create new test
    public RationalTest (String name) {
        super(name);
    }

    public void testEquality() {
    	System.out.println("\nTest for Equality....");
        assertEquals(new Rational(1,3), new Rational(1,3));
        assertEquals(new Rational(1,3), new Rational(2,6));
        assertEquals(new Rational(3,3), new Rational(1,1));
    }

    // Test for nonequality
    public void testNonEquality() {
    	System.out.println("\nTest for InEquality....");
        assertFalse(new Rational(2,3).equals(
            new Rational(1,3)));
    }

    public void testAccessors() {
    	System.out.println("\nTest accessors....");
    	assertEquals(new Rational(2,3).numerator(), 2);
    	assertEquals(new Rational(2,3).denominator(), 3);
    }

    public void testRoot() {
    	System.out.println("\nTest for Root Computation....");
        Rational s = new Rational( 1, 4 );
        Rational sRoot = null;
        try {
            sRoot = s.root();
       //     System.out.println("SQUARE ROOT"+sRoot.toString());
        } catch (IllegalArgumentToSquareRootException e) {
            e.printStackTrace();
        }
        assertTrue( sRoot.isLessThan( HALF.plus( Rational.getTolerance() ) ) 
                        && HALF.minus( Rational.getTolerance() ).isLessThan( sRoot ) );
    }
    
    // Test numerator() and denominator()
    public void testNumdDenConst(){
    	System.out.println("\nTest Num and Den Constructor....");
    	Rational c = new Rational(3,9);
    	assertEquals(c.numerator(),1);
    	assertEquals(c.denominator(),3);
    }
    
    //Test gcd function
    public void testgcd(){
    	System.out.println("\nTest gcd computation....");
    	Rational g = new Rational(3,9);
    	assertEquals(g.numerator()*3,3);
    	assertEquals(g.denominator()*3,9);
    }
    
    //Test for addition of rationals
    public void testPlus(){
    	System.out.println("\nTest Addition....");
    	Rational p1 = new Rational(1,4);
    	Rational p2 = new Rational(1,4);
    	Rational sum = p1.plus(p2);
    	assertEquals(sum,new Rational(1,2));
    }
    
    //Test for subtraction of rationals
    public void testMinus(){
    	System.out.println("\nTest Subtraction....");
    	Rational p1 = new Rational(3,4);
    	Rational p2 = new Rational(1,4);
    	Rational sub = p1.minus(p2);
    	assertEquals(sub,new Rational(1,2));
    }
    
  //Test for multiplication of rationals
    public void testTimes(){
    	System.out.println("\nTest multiplication....");
    	Rational p1 = new Rational(3,-3);
    	Rational p2 = new Rational(1,5);
    	Rational prod = p1.times(p2);
    	assertEquals(prod,new Rational(1,-5));
    	
    }
    
    //Test for division of rationals
    public void testDivides(){
    	System.out.println("\nTest division...");
    	Rational p1 = new Rational(1,2);
    	Rational p2 = new Rational(1,4);
    	Rational div = p1.divides(p2);
    	assertEquals(div,new Rational(2,1));
    	
    }
    
    public void testAbs(){
    	System.out.println("\nTest abs...");
    	assertEquals(new Rational(-1,-2), new Rational(-1,2).abs());
    	assertEquals(new Rational(3,0), new Rational(-3,0).abs());
    	}
    

    // Test setTolerance and getTolerance
    public void testSetTolerance(){
    	System.out.println("\nTest get/set tolerance....");
    	Rational tol = new Rational(1,150);
    	Rational.setTolerance(tol);
    	assertEquals(Rational.getTolerance(),tol);
    }
    
    //Test isLessThan
    public void testIsLessThan(){
    	System.out.println("\nTest isLessThan....");
    	Rational r1 = new Rational(3,8);
    	Rational r2 = new Rational(5,12);
    	assertEquals(r1.isLessThan(r2), true);
    }

    
    //Test for 0 denominator
    public void testzeroDenominator(){
    	System.out.println("\nTest for denominator not equal to 0");
    	Rational r = new Rational(2,0);
    	assertTrue("Denominator cannot be equal to 0",r.denominator()!=0);
    }
    
    // Test for nonequality2
    public void testNullEquality1() {
    	System.out.println("\nTest for InEquality2....");
        assertFalse(new Rational(2,3).equals(null));
    }
    
    
    //Test lcm
    public void testlcm(){
    	System.out.println("\nTesting lcm....");
    	Rational p1 = new Rational(1,2);
    	Rational p2 = new Rational(-1,4);
    	Rational sum = p1.plus(p2);
    	assertEquals(sum,new Rational(1,4));
    }
     
    //root of 4/25
    public void testRootcomputation2() {
    	System.out.println("\nTesting for Root 4/25");
        Rational s = new Rational( 4, 25 );
        Rational sRoot = null;
        try {
            sRoot = s.root();
        } catch (IllegalArgumentToSquareRootException e) {
            e.printStackTrace();
        }
        assertEquals("Root Computation Method is incorrect: sqrt(9/16)",sRoot, new Rational(2,5));
    }
    

    //Test when sum results in overflow
    public void testAddOverflow(){
    	System.out.println("\nTest Addition Overflow....");
    	Rational p1 = new Rational(2147483647,1);
    	Rational p2 = new Rational(2147483647,1);
    	Rational sum = p1.plus(p2);
    	assertFalse("Integer overflow not handled for addtition",new Rational(-2,1).equals(sum));
    }
    
    
    // Test for setting denominator to an integer overflow value
    public void testDen(){
    	System.out.println("\nTesting denominator ");
    	Rational r = new Rational(1,2147483647*2);
    	assertFalse("Denominator out of range not handled", r.denominator()==-2);
    	
    }
    public static void main(String args[]) {
        String[] testCaseName = 
            { RationalTest.class.getName() };
        // junit.swingui.TestRunner.main(testCaseName);
        junit.textui.TestRunner.main(testCaseName);
    }
}
