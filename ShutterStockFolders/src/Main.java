import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


public class Main {

static String user = "glaesarius@mail.ru";
static String pass = "JustAgataKot9";
static WebDriver driver;

	public static void main(String[] args) throws InterruptedException {
	
		System.setProperty("webdriver.chrome.driver", "libs/chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("user-data-dir=c:\\Users\\Mi\\AppData\\Local\\Google\\Chrome\\User Data\\");
		options.addArguments("--profile-directory=Profile 1");
		driver = new ChromeDriver(options);
	    
	    driver.get("https://submit.shutterstock.com/catalog_manager/");
	    
	    driver.findElement(By.xpath("//li[@data-test-ref='Illustration']")).click();
	    Select oSelect =new Select(driver.findElement(By.xpath("//select[@data-test-ref='media-sort']")));
	    oSelect.selectByValue("newest");
	    
	    //Фильтрация по дате
	    String dateFrom = "2020-02-01";
	    String dateTo = "2020-03-31";
	    Select filterSelect =new Select(driver.findElement(By.xpath("//div[@id='content-filter-view']/div/select")));
	    filterSelect.selectByValue("date_uploaded");
	    Thread.sleep(10000);
	    /*
	    WebElement startDateInput = driver.findElement(By.id("media-filter-start-date"));
	    WebElement endDateInput = driver.findElement(By.id("media-filter-end-date"));
	    startDateInput.click();
	    startDateInput.clear();
	    startDateInput.sendKeys(dateFrom);
	    
	    endDateInput.click();
	    endDateInput.clear();
	    endDateInput.sendKeys(dateFrom);
	    //sendTextJavascript(startDateInput, dateFrom);
	    //sendTextJavascript(endDateInput, dateTo);
	    
	    driver.findElement(By.xpath("//div[@id='content-filter-view']/div/button[text()='Фильтр']")).click();
		 */
	    
	   // driver.findElement(By.id("media-filter-start-date")).clear();
	  //  driver.findElement(By.id("media-filter-start-date")).sendKeys(dateFrom);
	  //  driver.findElement(By.id("media-filter-end-date")).clear();
	   // driver.findElement(By.id("media-filter-end-date")).sendKeys(dateTo);
	    
	    int page = 22;
	    if (page>2)
	    	for (int p=2;p<page;p++)
	    		driver.findElement(By.xpath("//ul[@data-test-ref='pagination']/li/a[text()='" + p + "']")).click();
	    
	    for (int i=page;i<153;i++) {
	    	driver.findElement(By.xpath("//ul[@data-test-ref='pagination']/li/a[text()='" + i + "']")).click();
	    	Thread.sleep(6000);
	    	List<WebElement> images = driver.findElements(By.className("cm-media-select"));
	    	images.forEach(im -> im.click());
	    	WebElement buttonAdd = driver.findElement(By.xpath("//button[text()='Добавить в подборку']"));
	    	buttonAdd.click();
	    	Select folderSelect =new Select(driver.findElement(By.xpath("//select[@data-test-ref='input-add-items-modal']")));
	    	folderSelect.selectByVisibleText("Artfabrika2");
	    	driver.findElement(By.xpath("//button/span[@data-test-ref='confirm-add-items-modal']")).click();
	    }
	    
	    
	    /*WebElement userField = driver.findElement(By.name("username"));
	    WebElement passField = driver.findElement(By.name("password"));
	    new WebDriverWait(driver,10).until(ExpectedConditions.presenceOfElementLocated(By.className("recaptcha-checkbox-border")));
	    WebElement captcha = driver.findElement(By.id("recaptcha-anchor"));
	    userField.sendKeys(user);
	    passField.sendKeys(pass);
	    captcha.click();
	    passField.submit();
	    */
	    Thread.sleep(5000);  // Let the user actually see something!
	    
	    driver.quit();

	}
	
	public static void SelectAll() {
		
	}

	
	public static Boolean isElementPresent(By by) {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.MILLISECONDS);
		boolean isFound = driver.findElements(by).size() > 0;
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		return isFound;
	}

	public static  void clickOnInvisibleElement(WebElement element) {
		String script = "var object = arguments[0];"
				+ "var theEvent = document.createEvent(\'MouseEvent\');"
				+ "theEvent.initMouseEvent(\'click\', true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
				+ "object.dispatchEvent(theEvent);";
		((JavascriptExecutor) driver).executeScript(script, element);
	}
	
	
	public static void sendTextJavascript(WebElement webl, String text) {
		JavascriptExecutor js = (JavascriptExecutor)driver;
		 
		js.executeScript("arguments[0].value='" + text+ "';", webl);
	}
	
}
