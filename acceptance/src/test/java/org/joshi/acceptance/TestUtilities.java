package org.joshi.acceptance;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtilities {

    /**
     * Function that will log the user in.
     */
    public static void login(WebDriver driver, String username) {
        driver.get("http://localhost:8080");

        var element = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("usernameTxt")));

        element.sendKeys(username);
        driver.findElement(By.id("startBtn")).click();

        // Wait for username label to be visible to confirm login.
        getUsernameLbl(driver);
    }

    public static WebElement getUsernameLbl(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("usernameLbl")));
    }
}