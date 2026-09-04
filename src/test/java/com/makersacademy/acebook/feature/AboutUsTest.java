package com.makersacademy.acebook.feature;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
//starts real app on an actual port, rather than a simulated in-memory request
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//tell springboot to start the entire app:
@ActiveProfiles("test")
public class AboutUsTest {
    //this test runs against test db:
    //driver -automated chrome browser, every action goes through this object:
    WebDriver driver;
    //faker generates random realistic fake data so each test runs on fresh data
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
    public void loggedInUserCanNavigateToAboutUsPage() {
        String email = faker.name().username() + "@email.com";
//        sign up, that logs in the user:
        driver.get("http://localhost:8081/");
        driver.findElement(By.linkText("Sign up")).click();
        driver.findElement(By.name("email")).sendKeys(email);
        driver.findElement(By.name("password")).sendKeys("P@55qw0rd1234567");
        //finds the form and types text into it , simulating a real user, then finds the submit and simulates it
        driver.findElement(By.name("action")).click();

        // wait for the post login page to actually finish loading
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // if this line throws, I'll be able to see what's really on the page
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("About")));
        } catch (Exception e) {
            System.out.println("Current URL was: " + driver.getCurrentUrl());
            System.out.println("Page source was:\n" + driver.getPageSource());
            throw e;
        }

        //navigate to about us, and search for About:
        driver.findElement(By.linkText("About")).click();
        //finds first h1 and checks it's equal to About Us:
        String heading = driver.findElement(By.tagName("h1")).getText();
        assertEquals("About Us", heading);
    }
}
