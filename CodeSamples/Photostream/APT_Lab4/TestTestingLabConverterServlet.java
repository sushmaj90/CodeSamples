package TestingLabConverterServlet;
import junit.framework.*;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.mockobjects.servlet.*;
import java.net.URL;
public class TestTestingLabConverterServlet extends TestCase {	

	  public void testInvalidTemp() throws Exception {
		  
		  TestingLabConverterServlet s = new TestingLabConverterServlet();
		    MockHttpServletRequest request = 
		      new MockHttpServletRequest();
		    MockHttpServletResponse response = 
		      new MockHttpServletResponse();
		    
		    request.setupAddParameter("farenheitTemperature", "Blah");
		    response.setExpectedContentType("text/html");
		    s.doGet(request,response);
		    response.verify();
		    String expected = "<html><head><title>Bad Temperature</title>"
					+ "</head><body><h2>Need to enter a valid temperature!"
				    + "Got a NumberFormatException on Blah" 
					+ "</h2></body></html>";
      	    assertEquals("<html><head><title>Bad Temperature</title></head><body><h2>Need to enter a valid temperature!Got a NumberFormatException on Blah</h2></body></html>\n",response.getOutputStreamContents());
		  }
	  
	  public void testforBadParameter() throws Exception {
		  TestingLabConverterServlet s = new TestingLabConverterServlet();
		  MockHttpServletRequest request = new MockHttpServletRequest();
		  request.setupAddParameter("farenheitTemperature", "foo");
		  MockHttpServletResponse response = new MockHttpServletResponse();
		  response.setExpectedContentType("text/html");
		  s.doGet(request,response);
		  response.verify();
		  assertEquals("<html><head><title>Bad Temperature</title>"
		  + "</head><body><h2>Need to enter a valid temperature!"
		  + "Got a NumberFormatException on "
		  + "foo"
		  + "</h2></body></html>\n",
		  response.getOutputStreamContents());
		  }
	  
	  public void testNullTempParameter() throws Exception {
	        TestingLabConverterServlet s = new TestingLabConverterServlet();
	        MockHttpServletRequest request = new MockHttpServletRequest();
	        MockHttpServletResponse response = new MockHttpServletResponse();
	        response.setExpectedContentType("text/html");
	        s.doGet(request,response);
	        response.verify();
	        assertEquals("<html><head><title>No Temperature</title></head><body><h2>Need to enter a temperature!</h2></body></html>\n",
	                     response.getOutputStreamContents());
	      }
	  
	  public void testforNonDecimalNotation() throws Exception {
		  TestingLabConverterServlet s = new TestingLabConverterServlet();
		  MockHttpServletRequest request = new MockHttpServletRequest();
		  request.setupAddParameter("farenheitTemperature", "10.E3E2");
		  MockHttpServletResponse response = new MockHttpServletResponse();
		  response.setExpectedContentType("text/html");
		  s.doGet(request,response);
		  response.verify();
		  assertEquals("<html><head><title>Bad Temperature</title>"
		  + "</head><body><h2>Need to enter a valid temperature!"
		  + "Got a NumberFormatException on "
		  + "10.E3E2"
		  + "</h2></body></html>\n",
		  response.getOutputStreamContents());
		  }
	
	    public static void main(String args[]) {
	        String[] testCaseName = 
	            { TestTestingLabConverterServlet.class.getName() };
	        junit.textui.TestRunner.main(testCaseName);
	    }

}
