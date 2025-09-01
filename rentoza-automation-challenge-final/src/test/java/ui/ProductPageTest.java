package ui;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.WebDriverFactory;
import java.time.Duration;

public class ProductPageTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "https://rentoza.co.za";

    @BeforeMethod
    public void setUp() {
        driver = WebDriverFactory.createDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    public void testProductDetailsAndAddToCart() {
        driver.get(BASE_URL);

        WebElement firstProduct = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='/products/']"))
        );
        firstProduct.click();

        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1"))
        );
        Assert.assertFalse(title.getText().isEmpty(), "Product title should not be empty");

        WebElement price = driver.findElement(By.cssSelector("span[class*='price']"));
        Assert.assertFalse(price.getText().isEmpty(), "Product price should not be empty");

        WebElement image = driver.findElement(By.cssSelector("img"));
        Assert.assertNotNull(image.getAttribute("src"), "Product image src should exist");

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
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
