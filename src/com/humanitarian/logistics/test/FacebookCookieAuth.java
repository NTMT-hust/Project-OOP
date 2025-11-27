package com.humanitarian.logistics.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FacebookCookieAuth {

    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();

        // Options to make the bot stealthy
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--start-maximized");

        WebDriver driver = new ChromeDriver(options);

        try {
            // 1. PRE-NAVIGATE: Go to Facebook domain so Selenium accepts the cookies
            // We go to a 404 page to avoid triggering a heavy load before auth
            driver.get("https://www.facebook.com/favicon.ico");

            // 2. LOAD COOKIES
            File cookieFile = new File("facebook_cookies.json");
            ObjectMapper mapper = new ObjectMapper();

            // Read JSON as a List of Maps
            List<Map<String, Object>> cookies = mapper.readValue(cookieFile,
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            for (Map<String, Object> cookieData : cookies) {
                String name = (String) cookieData.get("name");
                String value = (String) cookieData.get("value");

                // We only really need name and value for the session to work
                // But adding domain is good practice
                if (name != null && value != null) {
                    Cookie cookie = new Cookie.Builder(name, value)
                            .domain(".facebook.com") // Force the domain to match
                            .path("/")
                            .isSecure(true)
                            .build();

                    driver.manage().addCookie(cookie);
                }
            }

            System.out.println("Cookies injected successfully!");

            // 3. NAVIGATE TO TARGET CONTENT
            // Now when you reload or go to a post, you will be logged in
            driver.get("https://www.facebook.com/zuck/posts/10114333262134201");

            // --- INSERT YOUR SCRAPING LOGIC HERE ---
            // (The parsing logic from the previous answer goes here)
            Thread.sleep(10000); // Just to verify visual login

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error reading cookie file. Make sure path is correct.");
        } finally {
            driver.quit();
        }
    }
}