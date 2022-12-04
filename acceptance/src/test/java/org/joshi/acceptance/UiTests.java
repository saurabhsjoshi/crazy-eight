package org.joshi.acceptance;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * UI only test cases.
 */
public class UiTests {

    Process server;

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
        server = builder.start();
        var reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
        String line = reader.readLine();

        // Wait for application to start
        while (line != null && !line.contains("Started CrazyEightApplication in")) {
            System.out.println(line);
            line = reader.readLine();
        }
    }

    @AfterEach
    public void teardown() {
        if (server != null) {
            server.destroy();
        }
    }

    /**
     * Test that checks if user can log in via the UI.
     */
    @Test
    void testLogin() {
        WebDriver driver = getDriver();
        driver.get("http://localhost:8080");
        TestUtilities.login(driver, "Player1");

        assertEquals("Username: Player1", TestUtilities.getUsernameLbl(driver).getText());

        var connectionStatus = TestUtilities.getConnectionLbl(driver);
        new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(ExpectedConditions.textToBePresentInElement(connectionStatus, "Connection Status: Connected"));

        var registerState = TestUtilities.getUserRegisterLbl(driver);
        new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(ExpectedConditions.textToBePresentInElement(registerState, "User Registration: Registered"));

        var userScoreLbl = TestUtilities.getUserScoreLbl(driver, "Player1");
        new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(ExpectedConditions.textToBePresentInElement(userScoreLbl, "0"));


        var startGameBtn = TestUtilities.getStartGameBtn(driver);
        assertEquals("true", startGameBtn.getAttribute("disabled"));

        driver.quit();
    }
}
