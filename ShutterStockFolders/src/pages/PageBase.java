package pages;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;



public class PageBase {

	protected final WebDriver driver;
	
	public PageBase(WebDriver driver) {
		PageFactory.initElements(driver, this);
		this.driver = driver;
	}

	
	public void clickOnInvisibleElement(WebElement element) {
		String script = "var object = arguments[0];"
				+ "var theEvent = document.createEvent(\'MouseEvent\');"
				+ "theEvent.initMouseEvent(\'click\', true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
				+ "object.dispatchEvent(theEvent);";
		((JavascriptExecutor) driver).executeScript(script, element);
	}
	
	
}
