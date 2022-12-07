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

    public static WebElement getCardInHandBtn(WebDriver driver, String card) {
        return getById(driver, card + "_handBtn");
    }

    public static void playCard(WebDriver driver, String card) {
        var btn = getCardInHandBtn(driver, card);
        new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(ExpectedConditions.elementToBeClickable(btn));
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

    public static WebElement getWinnerFromNotification(WebDriver driver) {
        return getById(driver, "roundWinnerToastBody");
    }

    public static WebElement getStartGameBtn(WebDriver driver) {
        return getById(driver, "startGameBtn");
    }

    public static WebElement getCurrentSuitLbl(WebDriver driver) {
        return getById(driver, "currentSuitLbl");
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

    public static WebElement getDrawCardBtn(WebDriver driver) {
        return getById(driver, "drawCardBtn");
    }

    public static void skipTurn(WebDriver driver) {
        getById(driver, "passBtn").click();
    }

    public static void drawCard(WebDriver driver) {
        getDrawCardBtn(driver).click();
    }

    public static void selectSuit(WebDriver driver, String suit) {
        getById(driver, "Suits" + suit + "Btn").click();
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
