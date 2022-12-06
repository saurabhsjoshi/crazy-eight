package org.joshi.acceptance;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        // p4 plays QC
        playCard(driver, "QC");

        driver = players.get(1);
        // Validate skip notification is shown to player 3
        assertNotNull(getSkipNotification(driver));

        var currentTurnLbl = getCurrentTurnLbl(driver);
        assertTrue(validateText(driver, currentTurnLbl, "Current Turn: Player3"));
    }

}
