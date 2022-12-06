package org.joshi.acceptance;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
            TestUtilities.login(driver, "Player" + i);
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

}
