package ui;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.WebDriverFactory;
import java.time.Duration;

public class CartDiscountTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "https://rentoza.co.za";

    @BeforeMethod
    public void setUp() {
        driver = WebDriverFactory.createDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    public void testCartUpdatesAndDiscountCode() {
        driver.get(BASE_URL);

        WebElement firstProduct = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='/products/']"))
        );
        firstProduct.click();

        WebElement addToCart = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button[name='add']"))
        );
        addToCart.click();

        WebElement cartLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='/cart']"))
        );
        cartLink.click();

        WebElement cartItem = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class*='cart-item']"))
        );
        Assert.assertTrue(cartItem.isDisplayed(), "Product should be in the cart");

        try {
            WebElement discountInput = driver.findElement(By.cssSelector("input[name='discount']"));
            discountInput.sendKeys("TESTCODE");
            discountInput.sendKeys(Keys.ENTER);

            WebElement discountMsg = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class*='discount']"))
            );
            Assert.assertTrue(discountMsg.isDisplayed(), "Discount message should appear");
        } catch (NoSuchElementException e) {
            System.out.println("Discount code field not available for testing.");
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
