package com.tracker.toilet.selenium;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 09: Selenium Automated Browser Testing Suite
 * Project: Containerized Public Toilet Maintenance Tracker
 * Student: Tamanna Shaikh | Roll No: 24102A2006 | VIT Mumbai
 *
 * Implements end-to-end automated UI test cases across 5 critical user journeys:
 *  1. Operational KPI Dashboard Load & Summary Rendering
 *  2. Tab Navigation & Facility Directory Search / Ward Filtering
 *  3. Citizen Defect Ticket Submission Workflow
 *  4. Ticket Status Drill-Down & Lifecycle State Transition
 *  5. Sanitation Cleaning Shift Log Submission
 */
@ExtendWith(ScreenshotOnFailureExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PublicToiletSeleniumTest {

    public static String getScreenshotDir() {
        return System.getProperty("test.screenshot.dir", "docs/screenshots/Task-09");
    }
    private static String baseUrl;
    private static int targetPort = 8081;

    private WebDriver driver;
    private WebDriverWait wait;

    public WebDriver getDriver() {
        return this.driver;
    }

    @BeforeAll
    public static void verifyEnvironmentAndServer() {
        // Resolve application port: prioritize test.server.port system property, default to 8081
        String configuredPort = System.getProperty("test.server.port");
        if (configuredPort != null && !configuredPort.isBlank()) {
            try {
                targetPort = Integer.parseInt(configuredPort.trim());
            } catch (NumberFormatException ignored) {}
        }

        // Check if server is running on targetPort, fallback to 8082 if 8081 is unavailable
        boolean serverRunning = isServerHealthy(targetPort);
        if (!serverRunning && targetPort == 8081 && isServerHealthy(8082)) {
            targetPort = 8082;
            serverRunning = true;
        }

        Assumptions.assumeTrue(serverRunning, 
                "Public Toilet Maintenance Tracker must be running locally on port " + targetPort + 
                " before executing Selenium browser tests.");

        baseUrl = "http://localhost:" + targetPort;
        System.out.println("[SELENIUM] Verified target application running at: " + baseUrl);

        // Ensure screenshot directory exists
        try {
            Files.createDirectories(Paths.get(getScreenshotDir()));
        } catch (IOException e) {
            System.err.println("[SELENIUM] Could not create screenshot directory: " + e.getMessage());
        }
    }

    private static boolean isServerHealthy(int port) {
        try {
            URL url = new URL("http://localhost:" + port + "/api/v1/health");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            int code = conn.getResponseCode();
            return code == 200;
        } catch (Exception e) {
            return false;
        }
    }

    @BeforeEach
    public void setUp() {
        // Configure headless Chrome with Windows-compatible flags, with fallback to Edge
        driver = initializeWebDriver();
        assertNotNull(driver, "WebDriver initialization failed: neither Chrome nor Edge could be started.");
        
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private WebDriver initializeWebDriver() {
        // Attempt Chrome initialization first via Selenium Manager
        try {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless=new");
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--window-size=1920,1080");
            chromeOptions.addArguments("--remote-allow-origins=*");
            chromeOptions.addArguments("--disable-extensions");
            return new ChromeDriver(chromeOptions);
        } catch (Throwable t) {
            System.err.println("[SELENIUM] Chrome initialization error: " + t.getMessage() + ". Attempting Microsoft Edge fallback...");
        }

        // Fallback to Microsoft Edge
        try {
            EdgeOptions edgeOptions = new EdgeOptions();
            edgeOptions.addArguments("--headless=new");
            edgeOptions.addArguments("--disable-gpu");
            edgeOptions.addArguments("--no-sandbox");
            edgeOptions.addArguments("--disable-dev-shm-usage");
            edgeOptions.addArguments("--window-size=1920,1080");
            edgeOptions.addArguments("--remote-allow-origins=*");
            return new EdgeDriver(edgeOptions);
        } catch (Throwable t) {
            System.err.println("[SELENIUM] Edge initialization error: " + t.getMessage());
            return null;
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {}
        }
    }

    /**
     * Helper to capture visual checkpoint evidence for the academic report.
     */
    public void captureCheckpoint(String checkpointName) {
        if (driver instanceof TakesScreenshot) {
            try {
                byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                String fileName = checkpointName;
                if (getScreenshotDir().contains("Task-10") && fileName.startsWith("task09")) {
                    fileName = fileName.replace("task09", "task10");
                }
                File file = Paths.get(getScreenshotDir(), fileName + ".png").toFile();
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(bytes);
                }
                System.out.println("[SELENIUM EVIDENCE] Captured checkpoint screenshot: " + file.getPath());
            } catch (IOException e) {
                System.err.println("[SELENIUM EVIDENCE] Error saving checkpoint: " + e.getMessage());
            }
        }
    }

    // =========================================================================
    // USER JOURNEY 1: OPERATIONAL KPI DASHBOARD LOAD
    // =========================================================================

    @Test
    @Order(1)
    @DisplayName("Journey 1: Operational KPI Dashboard Load & Metric Rendering")
    public void testOperationalKpiDashboardLoad() {
        driver.get(baseUrl + "/index.html");

        // 1. Verify Page Title
        assertEquals("Containerized Public Toilet Maintenance Tracker", driver.getTitle(),
                "Page title should match project specification.");

        // 2. Verify Academic & Candidate Attribution Header
        WebElement academicBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("academic-badge")));
        assertTrue(academicBadge.getText().contains("Tamanna Shaikh"), "Academic badge must contain candidate name.");
        assertTrue(academicBadge.getText().contains("24102A2006"), "Academic badge must contain Roll Number 24102A2006.");

        // 3. Verify KPI Summary Metric Cards (Total Facilities, Active Tickets, SLA Breaches, Cleanliness Score)
        WebElement totalFacilitiesEl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("kpiTotalFacilities")));
        wait.until(d -> !totalFacilitiesEl.getText().equals("-") && !totalFacilitiesEl.getText().isBlank());
        
        int totalFacilities = Integer.parseInt(totalFacilitiesEl.getText().trim());
        assertTrue(totalFacilities >= 3, "Total facilities count in dashboard must be at least 3 municipal units.");

        WebElement activeTicketsEl = driver.findElement(By.id("kpiActiveTickets"));
        assertFalse(activeTicketsEl.getText().equals("-"), "Active tickets KPI should be loaded.");

        WebElement cleanlinessScoreEl = driver.findElement(By.id("kpiCleanlinessScore"));
        assertTrue(cleanlinessScoreEl.getText().contains("%"), "Cleanliness score KPI should render as a percentage.");

        // 4. Verify Default Tickets Table is populated
        WebElement ticketsTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ticketsTableBody")));
        List<WebElement> rows = ticketsTable.findElements(By.tagName("tr"));
        assertFalse(rows.isEmpty(), "Initial maintenance tickets table must contain populated rows.");
        assertFalse(rows.get(0).getText().contains("Loading tickets..."), "Tickets table must have completed data load.");

        // Visual Checkpoint Capture
        captureCheckpoint("task09_journey1_dashboard_kpis");
    }

    // =========================================================================
    // USER JOURNEY 2: TAB NAVIGATION & FACILITY DIRECTORY SEARCH / WARD FILTER
    // =========================================================================

    @Test
    @Order(2)
    @DisplayName("Journey 2: Tab Navigation & Facility Directory Search and Ward Filter")
    public void testFacilityDirectoryNavigationAndSearch() {
        driver.get(baseUrl + "/index.html");

        // 1. Navigate to Facility Directory Tab
        WebElement facilityTabBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'tab-btn') and contains(text(), 'Facility Directory')]")));
        facilityTabBtn.click();

        // 2. Verify Facility Directory section becomes active
        WebElement facilitiesView = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("facilitiesView")));
        assertTrue(facilitiesView.getAttribute("class").contains("active"), "facilitiesView should be active tab.");

        // 3. Verify Initial Facilities List renders all 3 facilities
        WebElement tbody = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("facilitiesTableBody")));
        wait.until(d -> tbody.findElements(By.tagName("tr")).size() >= 3);
        List<WebElement> facilityRows = tbody.findElements(By.tagName("tr"));
        assertTrue(facilityRows.size() >= 3, "Facility directory must list at least 3 municipal public toilets.");

        // 4. Search Filter Test: Query "Dadar"
        WebElement searchInput = driver.findElement(By.id("facilitySearchInput"));
        searchInput.clear();
        searchInput.sendKeys("Dadar");
        searchInput.sendKeys(Keys.SPACE, Keys.BACK_SPACE); // trigger onkeyup

        // Assert table filters to Dadar facility only
        wait.until(d -> tbody.findElements(By.tagName("tr")).size() == 1);
        WebElement filteredRow = tbody.findElements(By.tagName("tr")).get(0);
        assertTrue(filteredRow.getText().contains("FAC-MUM-001"), "Search result should match Dadar facility code FAC-MUM-001.");
        assertTrue(filteredRow.getText().contains("Dadar"), "Search result should mention Dadar.");

        // 5. Ward Dropdown Filter Test: Select "K/East" (Andheri)
        searchInput.clear();
        searchInput.sendKeys(Keys.SPACE, Keys.BACK_SPACE); // clear search query
        
        Select wardSelect = new Select(driver.findElement(By.id("facilityWardFilter")));
        wardSelect.selectByValue("K/East");

        wait.until(d -> tbody.findElements(By.tagName("tr")).size() == 1);
        WebElement wardFilteredRow = tbody.findElements(By.tagName("tr")).get(0);
        assertTrue(wardFilteredRow.getText().contains("FAC-MUM-003"), "Ward filter should return Andheri facility code FAC-MUM-003.");
        assertTrue(wardFilteredRow.getText().contains("K/East"), "Row should show Ward K/East.");

        // Reset Ward filter to All
        wardSelect.selectByValue("");

        // Visual Checkpoint Capture
        captureCheckpoint("task09_journey2_facility_search");
    }

    // =========================================================================
    // USER JOURNEY 3: CITIZEN DEFECT TICKET SUBMISSION WORKFLOW
    // =========================================================================

    @Test
    @Order(3)
    @DisplayName("Journey 3: Citizen Defect Ticket Submission Workflow via Modal")
    public void testCitizenDefectTicketSubmission() {
        driver.get(baseUrl + "/index.html");

        // 1. Click "+ Log New Defect" Action Button
        WebElement logDefectBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'tab-btn') and contains(text(), '+ Log New Defect')]")));
        logDefectBtn.click();

        // 2. Verify Modal Display
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createTicketModal")));
        assertTrue(modal.isDisplayed(), "Create Ticket modal should open upon clicking button.");

        // 3. Wait for Facility Dropdown options to populate
        WebElement facilityDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createTicketFacility")));
        Select facilitySelect = new Select(facilityDropdown);
        wait.until(d -> facilitySelect.getOptions().size() > 1);
        facilitySelect.selectByIndex(1); // Select first real facility (Dadar)

        // 4. Fill in Defect Ticket Form Fields
        String uniqueSuffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        String testTitle = "Leaking Flush Cistern Cabin A (" + uniqueSuffix + ")";
        String testDesc = "Automated test defect logged during Task 9 Selenium test validation suite.";

        WebElement titleInput = driver.findElement(By.id("createTicketTitle"));
        titleInput.clear();
        titleInput.sendKeys(testTitle);

        WebElement descInput = driver.findElement(By.id("createTicketDesc"));
        descInput.clear();
        descInput.sendKeys(testDesc);

        Select prioritySelect = new Select(driver.findElement(By.id("createTicketPriority")));
        prioritySelect.selectByValue("HIGH");

        WebElement reporterInput = driver.findElement(By.id("createTicketReporter"));
        reporterInput.clear();
        reporterInput.sendKeys("Inspector Tamanna Shaikh");

        // 5. Submit Form
        WebElement submitBtn = driver.findElement(By.cssSelector("#createTicketForm button[type='submit']"));
        submitBtn.click();

        // 6. Assert Toast Confirmation & Modal Dismissal
        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("toast")));
        assertTrue(toast.getText().contains("Maintenance Ticket logged successfully!"),
                "Toast notification must confirm successful ticket creation.");

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("createTicketModal")));

        // 7. Verify New Ticket Appears in the Main Tickets Table
        WebElement ticketsTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ticketsTableBody")));
        wait.until(d -> ticketsTable.getText().contains(testTitle));
        assertTrue(ticketsTable.getText().contains(testTitle), "Newly submitted ticket title must appear in table.");
        assertTrue(ticketsTable.getText().contains("HIGH"), "Priority HIGH badge must be visible in table.");

        // Visual Checkpoint Capture
        captureCheckpoint("task09_journey3_ticket_created");
    }

    // =========================================================================
    // USER JOURNEY 4: STATUS DRILL-DOWN & TICKET LIFECYCLE STATE TRANSITION
    // =========================================================================

    @Test
    @Order(4)
    @DisplayName("Journey 4: Ticket Status Drill-Down & Lifecycle State Transition")
    public void testTicketStatusLifecycleTransition() {
        driver.get(baseUrl + "/index.html");

        // 1. Wait for tickets table to load
        WebElement tbody = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ticketsTableBody")));
        wait.until(d -> !tbody.findElements(By.tagName("tr")).isEmpty() &&
                !tbody.findElements(By.tagName("tr")).get(0).getText().contains("Loading"));

        // 2. Locate an active ticket row (either newly created or seeded)
        List<WebElement> rows = tbody.findElements(By.tagName("tr"));
        WebElement targetRow = rows.get(0);

        // Click "Drill Down" on the target ticket row
        WebElement drillDownBtn = targetRow.findElement(By.xpath(".//button[contains(text(), 'Drill Down')]"));
        drillDownBtn.click();

        // 3. Verify Ticket Detail Modal is displayed with metadata
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ticketDetailModal")));
        assertTrue(modal.isDisplayed(), "Ticket Detail modal should be visible.");

        WebElement modalTicketNo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalTicketNumber")));
        wait.until(d -> !modalTicketNo.getText().isBlank());
        String ticketNumber = modalTicketNo.getText();
        assertTrue(ticketNumber.startsWith("TKT-"), "Modal must show valid ticket identifier starting with TKT-.");

        // 4. Perform Transition to IN_PROGRESS (Start Work)
        WebElement startWorkBtn = modal.findElement(By.xpath(".//button[contains(text(), 'Start Work')]"));
        startWorkBtn.click();

        // Wait until toast message specifically contains IN_PROGRESS
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("toast"), "Ticket status updated to IN_PROGRESS"));

        // Modal automatically closes on status update; wait for invisibility
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("ticketDetailModal")));

        // 5. Reopen Drill Down and Transition to RESOLVED (Mark Resolved)
        WebElement refreshedTbody = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ticketsTableBody")));
        WebElement currentTargetRow = refreshedTbody.findElements(By.tagName("tr")).get(0);
        currentTargetRow.findElement(By.xpath(".//button[contains(text(), 'Drill Down')]")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ticketDetailModal")));

        WebElement resolveBtn = driver.findElement(By.xpath("//div[@id='ticketDetailModal']//button[contains(text(), 'Mark Resolved')]"));
        resolveBtn.click();

        // Wait until toast message specifically contains RESOLVED
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("toast"), "Ticket status updated to RESOLVED"));

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("ticketDetailModal")));

        // 6. Assert Resolved Status in Main Tickets Table
        wait.until(d -> {
            WebElement updatedTbody = driver.findElement(By.id("ticketsTableBody"));
            return updatedTbody.getText().contains("RESOLVED");
        });
        assertTrue(driver.findElement(By.id("ticketsTableBody")).getText().contains("RESOLVED"),
                "Main table must reflect RESOLVED badge for the updated ticket.");

        // Visual Checkpoint Capture
        captureCheckpoint("task09_journey4_ticket_resolved");
    }

    // =========================================================================
    // USER JOURNEY 5: SANITATION CLEANING SHIFT LOG SUBMISSION
    // =========================================================================

    @Test
    @Order(5)
    @DisplayName("Journey 5: Sanitation Cleaning Shift Log Submission")
    public void testCleaningShiftLogSubmission() {
        driver.get(baseUrl + "/index.html");

        // 1. Click "+ Log Cleaning Shift" Action Button
        WebElement logCleanBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'tab-btn') and contains(text(), '+ Log Cleaning Shift')]")));
        logCleanBtn.click();

        // 2. Verify Cleaning Shift Modal display
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createCleaningModal")));
        assertTrue(modal.isDisplayed(), "Cleaning shift modal should open.");

        // 3. Wait for Facility select options
        WebElement facilitySelectEl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cleanFacility")));
        Select cleanFacilitySelect = new Select(facilitySelectEl);
        wait.until(d -> cleanFacilitySelect.getOptions().size() > 1);
        cleanFacilitySelect.selectByIndex(1);

        // 4. Fill in Cleaning Record Form Fields
        String uniqueStaffName = "Lead Sanitarian Suresh " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm"));
        WebElement staffInput = driver.findElement(By.id("cleanerName"));
        staffInput.clear();
        staffInput.sendKeys(uniqueStaffName);

        WebElement waterCheck = driver.findElement(By.id("cleanWater"));
        if (!waterCheck.isSelected()) waterCheck.click();

        WebElement soapCheck = driver.findElement(By.id("cleanSoap"));
        if (!soapCheck.isSelected()) soapCheck.click();

        Select ratingSelect = new Select(driver.findElement(By.id("cleanRating")));
        ratingSelect.selectByValue("5"); // 5 - Spotless & Sanitized

        WebElement remarksInput = driver.findElement(By.id("cleanRemarks"));
        remarksInput.clear();
        remarksInput.sendKeys("Automated sanitation audit: high pressure wash and chemical disinfectant completed.");

        // 5. Submit Cleaning Record
        WebElement submitBtn = driver.findElement(By.cssSelector("#createCleaningForm button[type='submit']"));
        submitBtn.click();

        // 6. Assert Toast Confirmation & Modal Dismissal
        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("toast")));
        assertTrue(toast.getText().contains("Cleaning shift recorded!"),
                "Toast notification must confirm cleaning shift creation.");

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("createCleaningModal")));

        // 7. Navigate to "Cleaning Shift Logs" Tab and Assert New Entry
        WebElement cleaningTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'tab-btn') and contains(text(), 'Cleaning Shift Logs')]")));
        cleaningTab.click();

        WebElement cleaningTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cleaningTableBody")));
        wait.until(d -> cleaningTable.getText().contains(uniqueStaffName));

        assertTrue(cleaningTable.getText().contains(uniqueStaffName),
                "Submitted cleaner staff name must appear in shift logs table.");
        assertTrue(cleaningTable.getText().contains("5/5"),
                "Hygiene rating 5/5 must appear in shift logs table.");
        assertTrue(cleaningTable.getText().contains("Automated sanitation audit"),
                "Remarks should appear in shift logs table.");

        // Visual Checkpoint Capture
        captureCheckpoint("task09_journey5_cleaning_shift_logged");
    }
}
