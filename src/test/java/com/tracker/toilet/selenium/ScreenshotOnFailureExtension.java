package com.tracker.toilet.selenium;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * JUnit 5 Extension that automatically captures full-page browser screenshots
 * when a Selenium test encounters an assertion error or unexpected failure.
 *
 * Implements AfterTestExecutionCallback so that screenshots are taken immediately
 * when a test fails, while the browser driver is still active and before @AfterEach teardown.
 *
 * Screenshots are saved to docs/screenshots/Task-09/ as required by Task 9.
 */
public class ScreenshotOnFailureExtension implements AfterTestExecutionCallback, TestWatcher {

    private static final String SCREENSHOT_DIR = "docs/screenshots/Task-09";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isPresent()) {
            captureFailureScreenshot(context, context.getExecutionException().get());
        }
    }

    private void captureFailureScreenshot(ExtensionContext context, Throwable cause) {
        String testName = context.getRequiredTestMethod().getName();
        String className = context.getRequiredTestClass().getSimpleName();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String fileName = String.format("failure_%s_%s_%s.png", className, testName, timestamp);

        Optional<Object> testInstance = context.getTestInstance();
        if (testInstance.isPresent() && testInstance.get() instanceof PublicToiletSeleniumTest) {
            WebDriver driver = ((PublicToiletSeleniumTest) testInstance.get()).getDriver();
            if (driver != null && driver instanceof TakesScreenshot) {
                try {
                    Path dirPath = Paths.get(SCREENSHOT_DIR);
                    if (!Files.exists(dirPath)) {
                        Files.createDirectories(dirPath);
                    }
                    File screenshotFile = dirPath.resolve(fileName).toFile();
                    byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    try (FileOutputStream fos = new FileOutputStream(screenshotFile)) {
                        fos.write(screenshotBytes);
                    }
                    System.err.println("[SELENIUM FAILURE HANDLER] Captured failure screenshot: " + screenshotFile.getAbsolutePath());
                } catch (IOException e) {
                    System.err.println("[SELENIUM FAILURE HANDLER] Failed to save screenshot: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        System.out.println("[SELENIUM] Test PASSED: " + context.getDisplayName());
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        System.err.println("[SELENIUM] Test FAILED: " + context.getDisplayName() + " - Reason: " + cause.getMessage());
    }
}
