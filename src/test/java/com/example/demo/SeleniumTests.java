package com.example.demo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SeleniumTests {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
        driver = new ChromeDriver(options);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testAddProduct() {
        driver.get("http://localhost:" + port);

        WebElement nameInput = driver.findElement(By.name("name"));
        WebElement quantityInput = driver.findElement(By.name("quantity"));
        WebElement addButton = driver.findElement(By.xpath("//button[contains(text(),'Add')]"));

        nameInput.sendKeys("TestProduct");
        quantityInput.sendKeys("10");
        addButton.click();

        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
        WebElement addedProduct = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[text()='TestProduct']")));

        assertTrue(addedProduct.isDisplayed());
        assertEquals("10", addedProduct.findElement(By.xpath("./following-sibling::td")).getText());
    }

    @Test
    public void testRegisterSale() {
        driver.get("http://localhost:" + port);

        // First, add a product
        WebElement nameInput = driver.findElement(By.name("name"));
        WebElement quantityInput = driver.findElement(By.name("quantity"));
        WebElement addButton = driver.findElement(By.xpath("//button[contains(text(),'Add')]"));

        nameInput.sendKeys("SaleProduct");
        quantityInput.sendKeys("20");
        addButton.click();

        // Now, register a sale
        WebElement productNameInput = driver.findElement(By.name("productName"));
        WebElement saleQuantityInput = driver.findElement(By.name("quantity"));
        WebElement registerSaleButton = driver.findElement(By.xpath("//button[contains(text(),'Register Sale')]"));

        productNameInput.sendKeys("SaleProduct");
        saleQuantityInput.sendKeys("5");
        registerSaleButton.click();

        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
        WebElement updatedProduct = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[text()='SaleProduct']")));

        assertEquals("15", updatedProduct.findElement(By.xpath("./following-sibling::td")).getText());
    }
}
