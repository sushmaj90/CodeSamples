import java.util.HashMap;
import java.util.ArrayList;
 
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SelTest {

	private static final String URL_LOGIN = "http://apt-public.appspot.com/testing-lab-login.html";
    private static final String URL_TEMP_CALC = "http://apt-public.appspot.com/testing-lab-calculator.html";
    private static final String URL_CONVERT_FARENHEIT = "http://apt-public.appspot.com/testing-lab-conversion?";
    private static HashMap<String,String> passwordList;
    private static ArrayList<String> userList;
    private static ArrayList<String> nonUserList;
    private static HashMap<String,String> invalidPasswordList;
 
    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new FirefoxDriver();
        createInvalidLoginList();
        InvalidLoginCheck(driver);
        System.out.println("Delaying execution by 60 seconds due to incorrect logins");
        Thread.sleep(60000);
        createLoginList();
        validLoginCheck(driver);
        System.out.println("Delaying execution by 10 seconds to prevent continous logins");
        Thread.sleep(10000);
        testTempCalculator(driver);
        testWhitespaceAndIgnoreCase(driver);
        testIgnoreCaseFahrenheit(driver);
        testNumberFormatException(driver);
        driver.close();
    }

     private static void createLoginList(){
    	userList = new ArrayList<String>();
    	passwordList = new HashMap<String,String>();
        userList.add("andy");
        userList.add("bob");
        userList.add("charley");
        passwordList.put("andy", "apple");
        passwordList.put("bob", "bathtub");
        passwordList.put("charley", "china");
    }


     private static void validLoginCheck(WebDriver driver){
         System.out.println("Password validation in progress...");
         for(String user : userList){
             driver = redirectToLoginPage(driver);
             String password = passwordList.get(user);
             WebElement userNameField = driver.findElement(By.name("userId"));
             userNameField.clear();
             userNameField.sendKeys(user);
             WebElement passwordField = driver.findElement(By.name("userPassword"));
             passwordField.clear();
             passwordField.sendKeys(password);
             System.out.println(" User = " + user + "  Password = " + password);
             passwordField.submit();
             if(!driver.getTitle().contains("Online temperature conversion")){
                 System.out.println("Invalid login attempt " + user + "," + password);
             }
         }
         System.out.println("Password Validation completed for Andy, Bob and Charley");
     }
       
       private static void createInvalidLoginList()
       {
	    	nonUserList = new ArrayList<String>();
	    	invalidPasswordList = new HashMap<String,String>();
	       	nonUserList.add("andys");
	       	nonUserList.add("bobs");
	       	nonUserList.add("charleys");
	       	invalidPasswordList.put("andys", "apples");
	       	invalidPasswordList.put("bobs", "bathtubs");
	       	invalidPasswordList.put("charleys", "chinas");
       }
       
       private static void InvalidLoginCheck(WebDriver driver){
           System.out.println("Invalid login Validation in progress");
           for(String user : nonUserList){
               driver = redirectToLoginPage(driver);
               String password = invalidPasswordList.get(user);
               WebElement inputUserName = driver.findElement(By.name("userId"));
               inputUserName.clear();
               inputUserName.sendKeys(user);
               WebElement inputPassword = driver.findElement(By.name("userPassword"));
               inputPassword.clear();
               inputPassword.sendKeys(password);
               System.out.println("User Name= " + user + " password= " + password);
               inputPassword.submit();
               if(!driver.getTitle().contains("Online temperature conversion")){
                   System.out.println("Invalid login attempt " + user + "," + password);
               }
           }
           System.out.println("Incorrect Login validation completed");
       }
       
       private static void testTempCalculator(WebDriver driver){
           tempConv(driver,"-200");
           tempConv(driver,"212");
           tempConv(driver,"0");
           tempConv(driver,"-32");
           tempConv(driver,"300");
           tempConv(driver,"-212");
       }
    
       private static void tempConv(WebDriver driver,String input){
           System.out.println("Converting Temperature= " + input);
           double inputNumber = Double.parseDouble(input);
           driver = redirectToConverterPage(driver);
           WebElement userNameField = driver.findElement(By.name("farenheitTemperature"));
           userNameField.clear();
           userNameField.sendKeys(input);
           userNameField.submit();
           String[] lines = driver.getPageSource().split("\n");
           String outputLine = "";
           for(String line: lines){
               if (line.contains(input)){
                   outputLine = line;
                   boolean isDouble = (line.split("\\s+")[6]).split("\\.").length > 1;
                   if(isDouble){
                       if((inputNumber < 0 || inputNumber > 212) && (line.split("\\s+")[6]).split("\\.")[1].length() != 1){
                       	String beforeDecimal = line.split("\\s+")[6].split("\\.")[0];
                       	char afterDecimal = line.split("\\s+")[6].split("\\.")[1].charAt(0);
                       	String singlePrecision = beforeDecimal+"."+afterDecimal;
                           System.out.println("For temperature " + input + " output with single precision digit is. " +singlePrecision);
                           return;
                       }
                   }else{
                       System.out.println("For temperature " + input + " output is " +line.split("\\s+")[6]);
                       return;
                   }
               }
           }
           System.out.println("Output Temperature is " + outputLine.split("\\s+")[6]);
       }

     private static void testWhitespaceAndIgnoreCase(WebDriver driver){
        System.out.println("Check for Username case sensitivity and white space ignore started...");
        userList.add("aNDy ");
        userList.add("BOb");
        userList.add("chARLEY");
        passwordList.put("aNDy ", "  apple   ");
        passwordList.put("BOb", "Bathtub");
        passwordList.put("chARLEY", "china ");
        for(int i=3;i<6;i++){
            String user = userList.get(i);
            driver = redirectToLoginPage(driver);
            String password = passwordList.get(user);
            WebElement inputUserName = driver.findElement(By.name("userId"));
            inputUserName.clear();
            inputUserName.sendKeys(user);
            WebElement inputPassword = driver.findElement(By.name("userPassword"));
            inputPassword.clear();
            inputPassword.sendKeys(password);
            System.out.println(" User= " + user + "Password= " + password);
            inputPassword.submit();
            if(!driver.getTitle().contains("Online temperature conversion")){
                if(user.equals("BOb")){
                    System.out.println("Failure due to password case sensitivity");
                }else{
                    System.out.println("Invalid Login attempt" + user + "," + password);
                }
            }
        }
        System.out.println("Verfication of case insensitive username and whitespace complete");
    }

 
    private static void testNumberFormatException(WebDriver driver){
    	testNumber(driver,"9.73E2");
    	testNumber(driver,"APT");
    	
    }
 
    private static void testNumber(WebDriver driver, String input){
        System.out.println("Testing for NumberFormatException on" + input);
        driver = redirectToConverterPage(driver);
        WebElement userName = driver.findElement(By.name("farenheitTemperature"));
        userName.clear();
        userName.sendKeys(input);
        userName.submit();
        if(!driver.getPageSource().contains("NumberFormatException")){
            System.out.println("Found NumberFormatException");
        }else{
            System.out.println("No NumberFormatException");
        }
    }

    private static void testIgnoreCaseFahrenheit(WebDriver driver){
        parameterCaseTest(driver,"farenheitTemperature=-40");
        parameterCaseTest(driver,"FARENHEitTemperature=-40");
    }
    
    private static WebDriver redirectToLoginPage(WebDriver driver){
        System.out.println("Redirecting to the login page...");
        driver.get(URL_LOGIN);
        return driver;
    }
     
    private static void parameterCaseTest(WebDriver driver,String input){
        System.out.println("Testing for case insensitivity of parameter input " + input);
        driver.get(URL_CONVERT_FARENHEIT + input);
        if(!driver.getPageSource().contains(input.split("=")[1])){
            System.out.println("Parameter check for Fahrenheit failed- case sensitive!");
        }
    }
 
    private static WebDriver redirectToConverterPage(WebDriver driver){
        System.out.println("Redirecting to the temperature converter page.");
        driver.get(URL_TEMP_CALC);
        return driver;
    }

}
