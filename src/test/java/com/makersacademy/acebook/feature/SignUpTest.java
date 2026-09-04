package com.makersacademy.acebook.feature;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.time.Duration;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class SignUpTest {

    WebDriver driver;
    Faker faker;

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
        faker = new Faker();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void successfulSignUpAlsoLogsInUser() {
        String email = faker.name().username() + "@email.com";

        driver.get("http://localhost:8081/");
        driver.findElement(By.linkText("Sign up")).click();
        driver.findElement(By.name("email")).sendKeys(email);
        //D.A. updated password from P@55qw0rd to P@55qw0rd12345678 because it might fail as too short:
        driver.findElement(By.name("password")).sendKeys("P@55qw0rd12345678");
        driver.findElement(By.name("action")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.linkText("Logout"))
        );

        String logoutText = driver.findElement(By.linkText("Logout")).getText();
        assertEquals("Logout", logoutText);
    }
}
