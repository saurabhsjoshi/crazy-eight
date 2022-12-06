package org.joshi.acceptance;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.joshi.acceptance.TestUtilities.*;
import static org.junit.jupiter.api.Assertions.*;

public class AcceptanceTests {
    Process server;

    List<ChromeDriver> players = new ArrayList<>();

    private String getJavaPath() {
        return ProcessHandle.current()
                .info()
                .command()
                .orElseThrow();
    }

    public static ChromeDriver getDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        // Check if headless (Useful for CI pipelines like on GitHub)
        var headless = System.getenv("seleniumHeadless");
        if (headless != null && !headless.isEmpty()) {
            options.addArguments("--headless");
        }

        return new ChromeDriver(options);
    }

    @BeforeAll
    public static void setupSuite() {
        // Automatically setup chrome driver
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setup() throws IOException {
        ProcessBuilder builder = new ProcessBuilder(getJavaPath(), "-jar", "crazy-eight.jar");
        builder.directory(new File(Path.of("").toAbsolutePath().toString()));
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        server = builder.start();

        var reader = new BufferedReader(new InputStreamReader(server.getErrorStream()));
        String line = reader.readLine();

        // Wait for application to start
        while (line != null && !line.contains("APPLICATION STARTUP SUCCESS")) {
            System.out.println(line);
            line = reader.readLine();
        }

        for (int i = 0; i < 4; i++) {
            var driver = getDriver();
            driver.get("http://localhost:8080");
            players.add(driver);
            TestUtilities.login(driver, "Player" + (i + 1));
        }

        // Validate all players are registered with the backend
        for (var p : players) {
            var registerState = TestUtilities.getUserRegisterLbl(p);
            new WebDriverWait(p, Duration.ofSeconds(1))
                    .until(ExpectedConditions.textToBePresentInElement(
                            registerState, "User Registration: Registered"));
        }
    }

    @AfterEach
    public void teardown() throws InterruptedException {
        if (server != null) {
            server.destroy();
            server.waitFor();
        }
        for (var p : players) {
            p.quit();
        }
    }

    @Test
    void R41() {
        var startGame = getStartGameBtn(players.get(0));
        startGame.click();

        rigGame(players.get(0), "1C", "",
                List.of(
                        "3C 7S 5D 6D 9D",
                        "4S 6S KC 8H 10D",
                        "9S 6C 9C JD 3H",
                        "7D JH QH KH 5C"
                ));

        // p1 plays 3C
        playCard(players.get(0), "3C");
        var currentTurnLbl = getCurrentTurnLbl(players.get(0));
        assertTrue(validateText(players.get(0), currentTurnLbl, "Current Turn: Player2"));
    }

    @Test
    void R43() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        rigGame(driver, "1C", "",
                List.of(
                        "3C 7S 1H 6D 9D",
                        "4S 6S KC 7D 10D",
                        "9S 6C 9C JD 3H",
                        "7H JH QH KH 5C"
                ));

        // p1 plays 1H
        playCard(driver, "1H");

        var currentTurnLbl = getCurrentTurnLbl(driver);
        assertTrue(validateText(driver, currentTurnLbl, "Current Turn: Player4"));

        assertTrue(validateText(driver, getCurrentDirectionLbl(driver), "<-"));

        driver = players.get(3);
        // p4 plays 1H
        playCard(driver, "7H");

        // Validate it is p3's turn
        currentTurnLbl = getCurrentTurnLbl(driver);
        assertTrue(validateText(driver, currentTurnLbl, "Current Turn: Player3"));
    }

    @Test
    void R44() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        rigGame(driver, "1C", "",
                List.of(
                        "QC 7S 1H 6D 9D",
                        "4S 6S KC 7D 10D",
                        "9S 6C 9C JD 3H",
                        "7H JH QH KH 5C"
                ));
        // p1 plays QC
        playCard(driver, "QC");

        driver = players.get(1);
        // Validate skip notification is shown to player 3
        assertNotNull(getSkipNotification(driver));

        var currentTurnLbl = getCurrentTurnLbl(driver);
        assertTrue(validateText(driver, currentTurnLbl, "Current Turn: Player3"));
    }

    @Test
    void R45() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        rigGame(driver, "1C", "",
                List.of(
                        "QC 7S 1H 6D 9D",
                        "4S 6S KC 7D 10D",
                        "9S 6C 9C JD 3H",
                        "7H JH QH KH 3C"
                ));
        // p1 plays QC
        playCard(driver, "QC");

        // p3 plays 6C
        driver = players.get(2);
        playCard(driver, "6C");

        // p4 plays 3c
        driver = players.get(3);
        playCard(driver, "3C");

        // Validate next turn is p1
        var currentTurnLbl = getCurrentTurnLbl(driver);
        assertTrue(validateText(driver, currentTurnLbl, "Current Turn: Player1"));
    }

    @Test
    void R47() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "4H", "",
                List.of(
                        "QH 7S 3C 6D 9D",
                        "4S 6S KC 7D 10D",
                        "9H 6C 9C JD 7H",
                        "3H JH QD KH 1H"
                ));
        // p1 plays QH // p2 skipped
        playCard(driver, "QH");
        // p3 plays 7H
        driver = players.get(2);
        playCard(driver, "9H");

        // A-TEST STARTS HERE
        // p4 plays 1H
        driver = players.get(3);
        playCard(driver, "1H");

        // Validate next turn is p3
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player3"));
        // Validate direction is opposite
        assertTrue(validateText(driver, getCurrentDirectionLbl(driver), "<-"));

        //p3 plays 7H
        driver = players.get(2);
        playCard(driver, "7H");

        // Validate next turn is p2
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player2"));
    }

    @Test
    void R48() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "1C", "",
                List.of(
                        "QH 7S 3C 6D 9D",
                        "4S 6S KC 7D 10D",
                        "9H 6C 9C JD 7H",
                        "3H JH QC KH 1H"
                ));
        playCard(driver, "3C");
        driver = players.get(1);
        playCard(driver, "KC");
        driver = players.get(2);
        playCard(driver, "9C");

        // A-TEST STARTS HERE
        // p4 plays QC
        driver = players.get(3);
        playCard(driver, "QC");

        // Validate next turn is p2
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player2"));
    }

    @Test
    void R51() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "KC", "",
                List.of(
                        "KH 7S 3C 6D 9D",
                        "4S 6S KC 7D 10D",
                        "9H 6C 9C JD 7H",
                        "3H JH QC 1C 1H"
                ));
        playCard(driver, "KH");

        // Validate next turn is p2
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player2"));
    }

    @Test
    void R52() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "KC", "",
                List.of(
                        "7C 7S 3C 6D 9D",
                        "4S 6S KC 7D 10D",
                        "9H 6C 9C JD 7H",
                        "3H JH QC 1C 1H"
                ));
        playCard(driver, "7C");

        // Validate next turn is p2
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player2"));
    }

    @Test
    void R53() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "KC", "",
                List.of(
                        "8H 7S 3C 6D 9D",
                        "4S 6S KC 7D 10D",
                        "9H 6C 9C JD 7H",
                        "3H JH QC 1C 1H"
                ));
        playCard(driver, "8H");

        // Validate suit selection is shown to the user
        assertNotNull(getById(driver, "suitsBtnGrp"));
    }

    @Test
    void R54() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "KC", "",
                List.of(
                        "8H 7S 3C 5S 9D",
                        "4S 6S KC 7D 10D",
                        "9H 6C 9C JD 7H",
                        "3H JH QC 1C 1H"
                ));

        // Get the button corresponding to card 5S
        var btn = getCardInHandBtn(driver, "5S");
        // Validate it is disabled
        assertFalse(btn.isEnabled());
    }

    @Test
    void R58() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "7C", "6C",
                List.of(
                        "3H",
                        "4S 6S KC 7D 10D",
                        "9H 6C 9C JD 7H",
                        "3H JH QC 1C 1H"
                ));

        drawCard(driver);

        playCard(driver, "6C");

        // Validate turn complete
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player2"));
    }

    @Test
    void R59() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "7C", "6D 5C",
                List.of(
                        "3H",
                        "4S 6S KC 7D 10D",
                        "9H 6C 9C JD 7H",
                        "3H JH QC 1C 1H"
                ));

        drawCard(driver);
        // Validate 6D was drawn
        assertNotNull(getCardInHandBtn(driver, "6D"));

        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "5C"));

        playCard(driver, "5C");

        // Validate turn complete
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player2"));
    }

    @Test
    void R60() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "7C", "6D 5S 7H",
                List.of(
                        "3H",
                        "4S 6S KC 7D 10D",
                        "9H 6C 9C JD 8H",
                        "3H JH QC 1C 1H"
                ));

        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "6D"));
        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "5S"));
        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "7H"));

        playCard(driver, "7H");

        // Validate turn complete
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player2"));
    }

    @Test
    void R61() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "7C", "6D 5S 4H",
                List.of(
                        "3H",
                        "4S 6S KC 7D 10D",
                        "9H 6C 9C JD 8H",
                        "3H JH QC 1C 1H"
                ));

        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "6D"));
        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "5S"));
        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "4H"));

        // Validate that the draw card button is disabled and user cannot draw more cards
        assertFalse(getDrawCardBtn(driver).isEnabled());

        // Skip turn
        skipTurn(driver);

        // Validate turn complete
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player2"));
    }
}
