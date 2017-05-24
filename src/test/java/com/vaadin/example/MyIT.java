package com.vaadin.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.TextFieldElement;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT)
public class MyIT extends TestBenchTestCase {

    @LocalServerPort
    int port;

    @Before
    public void setup() {
        String headless =
                System.getProperty("headless");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        if (Boolean.TRUE.equals(
                Boolean.valueOf(headless))) {
            options.addArguments("headless");
        }
        WebDriver driver = TestBench.createDriver(
                new ChromeDriver(options));
        setDriver(driver);
    }

    @Test
    public void test() {
        getDriver().get("http://localhost:" + port);
        String name = $(TextFieldElement.class)
                .caption("Name:").first()
                .getValue();
        assertEquals("3m Co", name);
    }

    @After
    public void teardown() {
        getDriver().quit();
    }
}
