package test;


import com.github.javafaker.Faker;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;

public class AuthorizationTest {
    static WebDriver driver;
    static ChromeOptions options;
    static Faker faker;

    @BeforeAll
    static void setup() {
        options = new ChromeOptions();
        options.addArguments("--headless=new", "--remote-allow-origins=*", "ignore-certificate-errors");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
    }

    @BeforeEach
    void createDate() {
        faker = new Faker();
    }

    @AfterAll
    static void tearDown() {
        driver.quit();
    }

    @Test
    void negativeAuthorizationTest() {
        driver.get("https://gisogd.gov.ru");
        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(driver -> driver.findElement(By.cssSelector(".ba-w-4")))
                .click();
        switchTo();
        new WebDriverWait(driver, Duration.ofSeconds(25))
                .until(driver -> driver.findElement(By.cssSelector("[autocomplete='username email']")))
                .sendKeys(faker.name().username());
        driver.findElement(By.cssSelector("[type='password']"))
                .sendKeys(faker.internet().password());
        driver.findElement(By.cssSelector(".btn--login")).click();

        String err = new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(driver -> driver.findElement(By.cssSelector(".error"))).getText();
        assertThat(err, equalToIgnoringCase("Введено неверное имя пользователя или пароль"));
    }

    private void switchTo() {
        String originalWindow = driver.getWindowHandle();
        for (String windowHandle : driver.getWindowHandles()) {
            if (!originalWindow.contentEquals(windowHandle)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
    }
}
