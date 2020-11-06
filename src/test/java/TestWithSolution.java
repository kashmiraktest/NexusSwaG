import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class TestWithSolution {
 WebDriver driver;
 WebDriverWait wait;
   String URL ="https://www.saucedemo.com/";

    @Before
    public void login() {

        System.setProperty("webdriver.chrome.driver", "Browser\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.get(URL);

    }

    @After
    public void tearDown() {
        driver.close();
    }


    @Test
     /* login, sort and verify
        login with --> standard_user
        wait for page to load or any element then assert and verify that user successfully logged in
        sort the list
        verify the list is sorted correctly
    * */
    public void verifyIsLoginSuccessful() {

        //login
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");

        driver.findElement(By.id("login-button")).click();

        // just in case required, wait for page to load or any element then assert homePage element
        WebElement elementOfHomePage = new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(driver -> driver.findElement(By.id("inventory_filter_container")));

        //candidate can assert with an unique element that is specific to home page or url
        Assert.assertTrue (driver.getCurrentUrl().contains("inventory"));

        Select sortDD= new Select(driver.findElement(By.xpath("//*[@id='inventory_filter_container']/select")));
        sortDD.selectByVisibleText("Price (high to low)");

        /*Now there are many ways to verify that items list is sorted
        1 get the previous price list and them sort match to new one
        2 sort and match each element
        3 sort and match first amd last - as I did, this is tricky may or may not provided valid results (check with problem_user login)the candidate should think of valid checks
        * */
       String firstElement = driver.findElement(By.xpath("(//*[@class='inventory_item_price'])[1]")).getText();

       String lastElement = driver.findElement(By.xpath("(//*[@class='inventory_item_price'])[6]")).getText();
        double  firstValue =  stringToDouble(firstElement);
        double lastValue = stringToDouble(lastElement);
        if(firstValue!=-1 & lastValue!=-1){
            Assert.assertTrue(firstValue> lastValue);
        }
        else Assert.fail("Issues with Items values");
    }

    @Test
    /*
    Add items to cart and if any items fails to get added in cart print the name
    as an example we have 6 items in inventory,
    then only 2 items fails to get added in cart then print the name of those 2 items
    * */
    public void addItemsToCartVerify() {
        int i = 0;
        driver.findElement(By.id("user-name")).sendKeys("problem_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");

        driver.findElement(By.id("login-button")).click();

        List<WebElement> buttons_List = driver.findElements(By.xpath("//*[@id=\"inventory_container\"]//button"));
        for (WebElement el : buttons_List) {
            el.click();

            if (el.getText().equals("ADD TO CART")) {
                i = buttons_List.indexOf(el) + 1; //because list has index start from 0 and xpath lst elements will start from 1
                String nameOfItemWithIssue = driver.findElement(By.xpath(" (//div[@class='inventory_item_name'])[" + i + "]")).getText();
                System.out.println("index is" + i + "Item not added" + nameOfItemWithIssue);
            }
        }
    }

    public static double stringToDouble(String strNum) {
        double d;
        if (strNum == null) {
            return -1;
        }
        try {
            strNum= strNum.replace("$", "");
            d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return -1;

        }
        return d;
    }
}
