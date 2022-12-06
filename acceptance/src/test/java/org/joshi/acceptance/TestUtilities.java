package org.joshi.acceptance;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

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

    public static WebElement getById(WebDriver driver, String id) {
        return new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
    }

    public static WebElement getUserRegisterLbl(WebDriver driver) {
        return getById(driver, "userRegisterLbl");
    }

    public static void playCard(WebDriver driver, String card) {
        var btn = getById(driver, card + "_handBtn");
        btn.click();
    }

    public static WebElement getCurrentTurnLbl(WebDriver driver) {
        return getById(driver, "currentTurnLbl");
    }

    public static WebElement getSkipNotification(WebDriver driver) {
        return getById(driver, "skippedToast");
    }

    public static WebElement getCurrentDirectionLbl(WebDriver driver) {
        return getById(driver, "directionLbl");
    }

    public static WebElement getUserScoreLbl(WebDriver driver, String username) {
        return getById(driver, "scoreLbl_" + username);
    }

    public static WebElement getStartGameBtn(WebDriver driver) {
        return getById(driver, "startGameBtn");
    }

    public static WebElement getUsernameLbl(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("usernameLbl")));
    }

    public static boolean validateText(WebDriver driver, WebElement element, String text) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.textToBePresentInElement(element, text));
            return true;
        } catch (Exception ex) {
            System.err.println("Could not find text '" + text + "' in element instead was '" + element.getText() + "'.");
        }
        return false;
    }

    public static WebElement getConnectionLbl(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("connectionLbl")));
    }

    public static void rigGame(WebDriver driver, String topCard, String rigDeck, List<String> rig) {
        StringBuilder rigCmd = new StringBuilder(topCard + "," + rigDeck);
        for (var r : rig) {
            rigCmd.append(",").append(r);
        }

        var input = new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("rigTxt")));

        input.sendKeys(rigCmd);
        input.sendKeys(Keys.ENTER);
    }
}
