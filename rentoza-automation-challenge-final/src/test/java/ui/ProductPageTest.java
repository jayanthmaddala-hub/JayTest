package ui;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.WebDriverFactory;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


        // Validate product price
        WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("h2.yv-product-detail-title")
        ));
        String actualTitle = titleElement.getText().trim();
        System.out.println("Product title: " + actualTitle);

        WebElement priceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(),'" + actualTitle + "')]/following::span[contains(text(),'R')][1]")
        ));
        String price = priceElement.getText().trim();
        System.out.println("Product price: " + price);
        Assert.assertFalse(price.isEmpty(), "Product price should not be empty");


        // Validate product image
        WebElement imageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"media-main-33250485829844\"]/div/a")
        ));

        String imageSrc = imageElement.getAttribute("href");
        System.out.println("Product image source: " + imageSrc);
        Assert.assertNotNull(imageSrc, "Product image should have a valid src attribute");


        // Add to cart
        WebElement addToCart = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"product-form-template--20001939161300__main-product\"]/div[3]/button"))
        );
        if(addToCart.isDisplayed() || addToCart.isEnabled()) {
            System.out.println("Product available");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement totalElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"stack-discounts-subtotal-value\"]")
            ));
            totalElement.getText().trim();

            WebElement subtotalElement = driver.findElement(By.xpath("//*[@id='stack-discounts-subtotal-value']"));
            String subtotalValue = subtotalElement.getAttribute("value");
            System.out.println("Subtotal Value: " + subtotalValue);

        }

    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
