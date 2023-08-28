package Testpackage;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class LetsWriteTest {
	WebDriver driver;
	String ExpectedURL= "https://www.saucedemo.com/inventory.html";
	String URL = "https://www.saucedemo.com/";

	public void login(String UserName, String Password) throws InterruptedException {
		
    WebElement User_name = driver.findElement(By.xpath("//input[@id='user-name']"));
		  User_name.sendKeys(UserName);
		  Thread.sleep(1000);
		  
    WebElement Pass_word = driver.findElement(By.xpath("//input[@id='password']"));
		  Pass_word.sendKeys(Password);
		  Thread.sleep(1000);
		  
    WebElement Login_btn = driver.findElement(By.xpath("//input[@id='login-button']"));
		  Login_btn.click();
	}
	
	public void logout() throws InterruptedException {
		  WebElement OpenMenu  = driver.findElement(By.id("react-burger-menu-btn"));
		  OpenMenu.click();
		  Thread.sleep(1000);
		
    WebElement Logout = driver.findElement(By.xpath("//*[text()='Logout']"));
		  Logout.click();
		  Thread.sleep(1000);
	}
	
	
	@BeforeTest
	public void init() throws InterruptedException {
		  System.setProperty("webdriver.chrome.driver", "Browser\\chromedriver.exe");
		  driver = new ChromeDriver();
		  driver.get(URL);
		  driver.manage().window().maximize();
	}

    @AfterTest
    public void tearDown() {
        driver.close();
    }
    
	public ArrayList<String> addItemsToCart() throws InterruptedException {
		  ArrayList<String> AddedItems = new ArrayList<String>();

		  List<WebElement> Items = driver.findElements(By.className("inventory_item_name"));

		for (int i = 0; i < Items.size(); i++) {
			  String ItemTitleText = Items.get(i).getText();
			
			  //Convert Item name to Button string name, for example, Sauce Labs Backpack to sauce-labs-backpack
			  String AddCartButtonString = ItemTitleText.toLowerCase().replaceAll(" ", "-");
			
			  //Click Add to cart
			  WebElement Addcart_btn = driver.findElement(
					By.xpath("//button[@id='add-to-cart-" + AddCartButtonString + "']"));
			  Addcart_btn.click();
			
			  //Insert added item title to list
			  AddedItems.add(ItemTitleText);
			  System.out.println("Clicked to add Inventory item : " + ItemTitleText );
			  Thread.sleep(1000);
		}
		return AddedItems;

	}

	/*
	 * Login with problem_user, password : secret_sauce Add items to cart and if any
	 * items fails to get added in cart print the name as an example we have 6 items
	 * in inventory, then only 2 items fails to get added in cart then print the
	 * name of those 2 items
	 */
	@Test(priority = 1)
	public void verifyIsLoginSuccessful() throws InterruptedException
	{
		  //Login with problem_user
		  login("problem_user", "secret_sauce");
		
		  //Add all items to cart
		  ArrayList<String> AddedItems = addItemsToCart();
		
		  //Click to navigate to cart
		  driver.findElement(By.className("shopping_cart_link")).click();
		  Thread.sleep(1000);

		  //Verify that all added items are present in cart
		  for (int index = 0; index < 6; index++) {
			   try {
				    driver.findElement(By.xpath("//*[text()='" + AddedItems.get(index) + "']"));
			   } catch (Exception e) {
				    System.out.println("Item not added in cart and item name is " + AddedItems.get(index));
			  }
		}
		
		  //Return to home page
		  driver.findElement(By.xpath("//button[@id='continue-shopping']")).click();
		  Thread.sleep(1000);
		
		  //Logout
		  logout();
	}
	

	
	
	/*
	 * @Test Login with --> standard_user, password : secret_sauce Verify that user
	 * successfully logged in sort the list - choose any option in DropDown. verify
	 * the list is sorted correctly
	 */
	@Test(priority = 2)
	public void addItemsToCartVerify() throws InterruptedException
	{
		  //Login with standard_user
		  login("standard_user", "secret_sauce");
		  String ActualURL=driver.getCurrentUrl();
		  String ExpectedURL= "https://www.saucedemo.com/inventory.html";
		
		  //Verify if login successful
		  Assert.assertEquals(ActualURL, ExpectedURL);
		  System.out.println("Login Successful!");
    	
		  //Sort by using drop down
		  Select drpdown = new Select(driver.findElement(By.xpath("//select[contains(@class,'product_sort_container')]")));
    drpdown.selectByVisibleText("Price (low to high)");
    	 
    //Get all inventory prices in the order on page and verify if they are in order
    List<WebElement> ItemPrices = driver.findElements(By.className("inventory_item_price"));
    	 
    double PreviousPrice = 0.0;
    for (int i = 0; i < ItemPrices.size(); i++) {
    		 String ItemPriceText = ItemPrices.get(i).getText();
    		 
    		 double Value = 0.0;
    		 try{
    			//substring 1 to remove $ and convert to floating number for comparison
      			 Value = Double.valueOf(ItemPriceText.substring(1)); 
    		 }
    		 catch(Exception e) {
    		  System.out.println("Price in incorrect format!");
    			 throw e;
    		 }
    		 
    		 //Compare with previous price in sequence if it is greater or equal
    		 if(PreviousPrice > Value) {
    			  System.out.println("Items were not sorted properly");
    		 }
        Assert.assertTrue( PreviousPrice <= Value);
        	 
        //Assign current price to previous price to compare with next item
        PreviousPrice = Value;
    		}
    	 Thread.sleep(1000);
 		logout();
   }  
}
