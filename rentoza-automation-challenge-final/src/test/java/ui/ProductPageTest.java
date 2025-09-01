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
    public void testProductDetailsAndAddToCart() throws InterruptedException {
        driver.get(BASE_URL);

        // Handle signup popup if it appears
        try {
            WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[aria-label='Close dialog']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", closeBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);

            System.out.println("Popup closed.");
        } catch (TimeoutException e) {
            System.out.println("Popup close button not found.");
        }

        // Click first product
        WebElement firstProduct = wait.until(
                ExpectedConditions.elementToBeClickable(
                       By.xpath("//a[contains(@href,'/products/tab-s9-ultra')]")
                )
        );
       firstProduct.click();
       System.out.println();
        driver.findElement(By.xpath("//button[.//span[text()='Subscribe Now']]")).click();

        // Validate product title
        WebElement titleElement = driver.findElement(By.cssSelector("h2.yv-product-detail-title"));
        String actualTitle = titleElement.getText().trim();
        String expectedTitle = "Tab S9 Ultra";
        Assert.assertEquals(actualTitle, expectedTitle, "Product title does not match!");
        System.out.println("validate product title is" + " " + actualTitle);

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
