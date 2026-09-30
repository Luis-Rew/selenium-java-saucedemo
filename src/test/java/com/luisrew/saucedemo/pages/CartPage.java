package com.luisrew.saucedemo.pages;

import com.luisrew.saucedemo.utils.Interactions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By checkoutButton = By.id("checkout");
    private final By checkoutFirstNameField = By.id("first-name");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void goToCheckout() {
        Interactions.clickUntil(
                driver,
                () -> wait.until(ExpectedConditions.elementToBeClickable(checkoutButton)),
                () -> !driver.findElements(checkoutFirstNameField).isEmpty()
        );
    }
}
