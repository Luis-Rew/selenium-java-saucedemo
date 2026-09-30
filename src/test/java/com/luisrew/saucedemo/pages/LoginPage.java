package com.luisrew.saucedemo.pages;

import com.luisrew.saucedemo.utils.Interactions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private static final String URL = "https://www.saucedemo.com/";

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("h3[data-test='error']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void open() {
        driver.get(URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
    }

    public void login(String username, String password) {
        WebElement usernameField = driver.findElement(usernameInput);
        Interactions.typeReliably(usernameField, username);

        WebElement passwordField = driver.findElement(passwordInput);
        Interactions.typeReliably(passwordField, password);

        Interactions.clickUntil(
                driver,
                () -> wait.until(ExpectedConditions.elementToBeClickable(loginButton)),
                () -> !driver.getCurrentUrl().equals(URL) || !driver.findElements(errorMessage).isEmpty()
        );
    }

    public String getErrorMessage() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
        return element.getText();
    }
}
