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
        ProcessBuilder builder = new ProcessBuilder(getJavaPath(), "-jar", "crazy-eight.jar", "--game.rigged=true");
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

    @Test
    void R62() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "7C", "6D 8H",
                List.of(
                        "3H",
                        "4S 6S KC 7D 10D",
                        "9H 6C 9C JD 7H",
                        "3H JH QC 1C 1H"
                ));

        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "6D"));
        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "8H"));

        playCard(driver, "8H");

        // Validate suit selection is shown to the user
        assertNotNull(getById(driver, "suitsBtnGrp"));

        // Select diamond as current suit
        selectSuit(driver, "Diamonds");
        // Validate suit has changed
        validateText(driver, getCurrentSuitLbl(driver), "Current Suit DIAMONDS");

        // Validate turn complete
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player2"));
    }

    @Test
    void R63() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "7C", "6C",
                List.of(
                        "KS 3C",
                        "4S 6S KC 7D 10D",
                        "9H 6D 9C JD 7H",
                        "3H JH QC 1C 1H"
                ));

        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "6C"));

        playCard(driver, "6C");

        // Validate turn complete
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player2"));
    }

    @Test
    void R67() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "1C", "6C 9D",
                List.of(
                        "KS 2C",
                        "4H",
                        "9H 6D 9C JD 7H",
                        "3H JH QC 1C 1H"
                ));

        playCard(driver, "2C");

        driver = players.get(1);
        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "6C"));
        assertNotNull(getCardInHandBtn(driver, "9D"));

        playCard(driver, "6C");

        // Validate turn complete
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player3"));
    }

    @Test
    void R68() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "1C", "6S 9D 9H 6C",
                List.of(
                        "KS 2C",
                        "4H",
                        "10H 6D 9C JD 7H",
                        "3H JH QC 1C 1H"
                ));

        playCard(driver, "2C");

        driver = players.get(1);
        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "6S"));
        assertNotNull(getCardInHandBtn(driver, "9D"));

        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "9H"));
        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "6C"));

        playCard(driver, "6C");

        // Validate turn complete
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player3"));
    }

    @Test
    void R69() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "1C", "6S 9D 9H 7S 5H",
                List.of(
                        "KS 2C",
                        "4H",
                        "10H 6D 9C JD 7H",
                        "3H JH QC 1C 1H"
                ));

        playCard(driver, "2C");

        driver = players.get(1);
        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "6S"));
        assertNotNull(getCardInHandBtn(driver, "9D"));

        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "9H"));
        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "7S"));
        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "5H"));

        skipTurn(driver);

        // Validate turn complete
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player3"));
    }

    @Test
    void R71() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "1C", "2H 9D 5S 6D 6H 7C",
                List.of(
                        "KS 2C",
                        "4H",
                        "7D",
                        "3H JH QC 1C 1H"
                ));

        playCard(driver, "2C");

        driver = players.get(1);
        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "2H"));
        assertNotNull(getCardInHandBtn(driver, "9D"));
        playCard(driver, "2H");

        // Validate turn complete
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player3"));

        driver = players.get(2);
        drawCard(driver);
        assertNotNull(getCardInHandBtn(driver, "5S"));
        assertNotNull(getCardInHandBtn(driver, "6D"));
        assertNotNull(getCardInHandBtn(driver, "6H"));
        assertNotNull(getCardInHandBtn(driver, "7C"));

        playCard(driver, "6H");

        // Validate turn complete
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player4"));
    }

    @Test
    void R72() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "1C", "",
                List.of(
                        "KS 2C",
                        "4C 6C 9D",
                        "7D",
                        "3H JH QC 1C 1H"
                ));

        playCard(driver, "2C");

        driver = players.get(1);
        playCard(driver, "4C");
        playCard(driver, "6C");

        // Validate turn complete
        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player3"));
    }

    @Test
    void R73() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "1C", "",
                List.of(
                        "KS 2C",
                        "4C 6C",
                        "5C 7C 9D",
                        "7D",
                        "3H JH QC 1H"
                ));

        playCard(driver, "2C");

        driver = players.get(1);
        playCard(driver, "4C");
        playCard(driver, "6C");

        var winnerMsg = getWinnerFromNotification(driver);
        assertTrue(validateText(driver, winnerMsg, "Player Player2 has won this round!"));
    }

    @Test
    void R78() {
        var driver = players.get(0);
        var startGame = getStartGameBtn(driver);
        startGame.click();

        // SETUP GAME
        rigGame(driver, "4C", "",
                List.of(
                        "1S 4S",
                        "3S",
                        "8H JH 6H KH KS",
                        "8C 8D 2D"
                ));

        playCard(driver, "4S");

        assertTrue(validateText(driver, getCurrentTurnLbl(driver), "Current Turn: Player2"));

        driver = players.get(1);
        playCard(driver, "3S");

        // Validate game winner msg
        var winnerMsg = getWinnerFromNotification(driver);
        assertTrue(validateText(driver, winnerMsg, "Player Player2 has won the game!"));

        // Validate scores
        assertTrue(validateUserScore(driver, "Player1", "1"));
        assertTrue(validateUserScore(driver, "Player2", "0"));
        assertTrue(validateUserScore(driver, "Player3", "86"));
        assertTrue(validateUserScore(driver, "Player4", "102"));
    }

    @Test
    void R80() {
        var player1 = players.get(0);
        var player2 = players.get(1);
        var player3 = players.get(2);
        var player4 = players.get(3);
        var startGame = getStartGameBtn(player1);
        startGame.click();

        // SETUP GAME
        rigGame(player1, "4D", "2C 3C 4C 10C JC 7C",
                List.of(
                        "4H 7S 5D 6D 9D",
                        "4S 6S KC 8H 10D",
                        "9S 6C 9C JD 3H",
                        "7D JH QH KH 5C"
                ));

        playCard(player1, "4H");

        assertTrue(validateText(player2, getCurrentTurnLbl(player2), "Current Turn: Player2"));
        playCard(player2, "4S");

        assertTrue(validateText(player3, getCurrentTurnLbl(player3), "Current Turn: Player3"));
        playCard(player3, "9S");

        assertTrue(validateText(player3, getCurrentTurnLbl(player3), "Current Turn: Player4"));
        drawCard(player4);
        assertNotNull(getCardInHandBtn(player4, "2C"));
        drawCard(player4);
        assertNotNull(getCardInHandBtn(player4, "3C"));
        drawCard(player4);
        assertNotNull(getCardInHandBtn(player4, "4C"));
        skipTurn(player4);

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player1"));
        playCard(player1, "7S");

        assertTrue(validateText(player2, getCurrentTurnLbl(player2), "Current Turn: Player2"));
        playCard(player2, "6S");

        assertTrue(validateText(player3, getCurrentTurnLbl(player3), "Current Turn: Player3"));
        playCard(player3, "6C");

        assertTrue(validateText(player4, getCurrentTurnLbl(player4), "Current Turn: Player4"));
        playCard(player4, "2C");

        assertTrue(validateText(player4, getCurrentTurnLbl(player4), "Current Turn: Player1"));
        drawCard(player1);
        assertNotNull(getCardInHandBtn(player1, "10C"));
        assertNotNull(getCardInHandBtn(player1, "JC"));
        playCard(player1, "JC");

        assertTrue(validateText(player2, getCurrentTurnLbl(player2), "Current Turn: Player2"));
        playCard(player2, "KC");

        assertTrue(validateText(player2, getCurrentTurnLbl(player2), "Current Turn: Player3"));
        playCard(player3, "9C");

        assertTrue(validateText(player4, getCurrentTurnLbl(player4), "Current Turn: Player4"));
        playCard(player4, "3C");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player1"));
        drawCard(player1);
        assertNotNull(getCardInHandBtn(player1, "7C"));
        playCard(player1, "7C");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player2"));
        playCard(player2, "8H");
        selectSuit(player2, "Diamonds");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player3"));
        playCard(player3, "JD");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player4"));
        playCard(player4, "7D");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player1"));
        playCard(player1, "9D");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player2"));
        playCard(player2, "10D");

        var winnerMsg = getWinnerFromNotification(player2);
        assertTrue(validateText(player2, winnerMsg, "Player Player2 has won this round!"));

        // Validate scores
        validateUserScore(player1, "Player1", "21");
        validateUserScore(player1, "Player2", "0");
        validateUserScore(player1, "Player3", "3");
        validateUserScore(player1, "Player4", "39");

        rigGame(player1, "10D", "KS QS KH 6D QD JD 6S JS 10S",
                List.of(
                        "7D 4S 7C 4H 5D",
                        "9D 3S 9C 3H JC",
                        "3D 9S 3C 9H 5H",
                        "4D 7S 4C 5S 8D"
                ));

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player2"));
        playCard(player2, "9D");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player3"));
        playCard(player3, "3D");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player4"));
        playCard(player4, "4D");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player1"));
        playCard(player1, "4S");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player2"));
        playCard(player2, "3S");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player3"));
        playCard(player3, "9S");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player4"));
        playCard(player4, "7S");


        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player1"));
        playCard(player1, "7C");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player2"));
        playCard(player2, "9C");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player3"));
        playCard(player3, "3C");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player4"));
        playCard(player4, "4C");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player1"));
        playCard(player1, "4H");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player2"));
        playCard(player2, "3H");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player3"));
        playCard(player3, "9H");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player4"));
        drawCard(player4);
        assertNotNull(getCardInHandBtn(player4, "KS"));
        drawCard(player4);
        assertNotNull(getCardInHandBtn(player4, "QS"));
        drawCard(player4);
        assertNotNull(getCardInHandBtn(player4, "KH"));
        playCard(player4, "KH");

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player1"));
        drawCard(player1);
        assertNotNull(getCardInHandBtn(player1, "6D"));
        drawCard(player1);
        assertNotNull(getCardInHandBtn(player1, "QD"));
        drawCard(player1);
        assertNotNull(getCardInHandBtn(player1, "JD"));
        skipTurn(player1);

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player2"));
        drawCard(player2);
        assertNotNull(getCardInHandBtn(player2, "6S"));
        drawCard(player2);
        assertNotNull(getCardInHandBtn(player2, "JS"));
        drawCard(player2);
        assertNotNull(getCardInHandBtn(player2, "10S"));
        skipTurn(player2);

        assertTrue(validateText(player1, getCurrentTurnLbl(player1), "Current Turn: Player3"));
        playCard(player3, "5H");

        // Validate game winner msg
        winnerMsg = getWinnerFromNotification(player3);
        assertTrue(validateText(player3, winnerMsg, "Player Player3 has won the game!"));

        validateUserScore(player1, "Player1", "59");
        validateUserScore(player1, "Player2", "36");
        validateUserScore(player1, "Player3", "3");
        validateUserScore(player1, "Player4", "114");
    }
}
