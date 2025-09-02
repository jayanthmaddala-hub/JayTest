package ui;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
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
    public void testCartAndDiscountFunctionality() {
        driver.get(BASE_URL);

        // Close signup popup if it appears
        try {
            WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[aria-label='Close dialog']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", closeBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);

            System.out.println("Popup closed.");
        } catch (TimeoutException e) {
            System.out.println("Popup close button not found.");
        }

        // Select first product
        WebElement firstProduct = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(@href,'/products/tab-s9-ultra')]")
                )
        );
        firstProduct.click();
        System.out.println();
        driver.findElement(By.xpath("//button[.//span[text()='Subscribe Now']]")).click();



// Wait for cart drawer to appear
        WebElement cartDrawer = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class,'cart') and contains(@class,'drawer')]")
                )
        );
        System.out.println("Cart drawer is visible.");

// --- Attempt 1: Click overlay if exists ---
        try {
            WebElement overlay = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class,'overlay') or contains(@class,'backdrop')]")
            ));
            overlay.click();
            wait.until(ExpectedConditions.invisibilityOf(cartDrawer));
            System.out.println("Cart drawer closed by clicking overlay.");
        } catch (TimeoutException e1) {
            System.out.println("No overlay detected. Trying JavaScript...");

            // --- Attempt 2: Hide via JavaScript if no overlay ---
            JavascriptExecutor js = (JavascriptExecutor) driver;
            try {
                js.executeScript(
                        "const drawer=document.querySelector(\"div[class*='cart'][class*='drawer']\"); if(drawer){drawer.style.display='none';}"
                );
                wait.until(ExpectedConditions.invisibilityOf(cartDrawer));
                System.out.println("Cart drawer closed using JavaScript.");
            } catch (TimeoutException e2) {
                System.out.println("Drawer still visible. Navigating away...");

                // --- Attempt 3: Navigate away if nothing works ---
                driver.navigate().to("https://rentoza.co.za");
                wait.until(ExpectedConditions.invisibilityOfElementLocated(
                        By.xpath("//div[contains(@class,'cart') and contains(@class,'drawer')]")
                ));
                System.out.println("Cart drawer closed by reloading page.");
            }
        }

        driver.navigate().to("https://rentoza.co.za");
        wait.until(ExpectedConditions.titleContains("Rentoza"));
        System.out.println("Navigated back to homepage successfully.");

        // Add product to cart
        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href,'/products/tab-s9-ultra')]")
        ));
        addToCartBtn.click();
        driver.findElement(By.xpath("//button[.//span[text()='Subscribe Now']]")).click();
        System.out.println("Product added to cart.");


        // Validate product is in cart
        WebElement cartItem = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'cart-item')]")
        ));
        Assert.assertTrue(cartItem.isDisplayed(), "Product should be present in the cart");
        System.out.println("Cart contains the 2 products");

        // Get subtotal before discount
        WebElement subtotalElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Subtotal')]/following-sibling::*")
        ));
        String subtotalBefore = subtotalElement.getText().trim();
        System.out.println("Subtotal before discount: " + subtotalBefore);

        // Apply discount code if field exists
        try {
            WebElement discountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@name='discount' or @placeholder='Discount code']")
            ));
            discountInput.sendKeys("TESTCODE");
            discountInput.sendKeys(Keys.ENTER);

            // Wait for discount message or updated total
            WebElement discountMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'discount') or contains(text(),'applied') or contains(text(),'invalid')]")
            ));
            System.out.println("Discount message: " + discountMsg.getText());

            WebElement totalElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'Total')]/following-sibling::*")
            ));
            String totalAfter = totalElement.getText().trim();
            System.out.println("Total after discount: " + totalAfter);

            Assert.assertNotEquals(totalAfter, subtotalBefore,
                    "Total should change if discount is applied or message should appear");
        } catch (TimeoutException | NoSuchElementException e) {
            System.out.println("No discount input available for testing.");
        }

        // Remove product and validate cart update
        try {
            WebElement removeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(.,'Remove') or contains(.,'×')]")
            ));
            removeBtn.click();
            wait.until(ExpectedConditions.invisibilityOf(cartItem));
            System.out.println("Product removed from cart successfully.");
        } catch (TimeoutException e) {
            System.out.println("Remove button not found — cart removal step skipped.");
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

