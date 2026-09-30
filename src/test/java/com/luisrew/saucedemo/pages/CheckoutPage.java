package com.luisrew.saucedemo.pages;

import com.luisrew.saucedemo.utils.Interactions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CheckoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By firstNameInput = By.id("first-name");
    private final By lastNameInput = By.id("last-name");
    private final By postalCodeInput = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By finishButton = By.id("finish");
    private final By confirmationHeader = By.className("complete-header");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void fillDeliveryInfo(String firstName, String lastName, String postalCode) {
        WebElement firstNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput));
        Interactions.typeReliably(firstNameField, firstName);

        WebElement lastNameField = driver.findElement(lastNameInput);
        Interactions.typeReliably(lastNameField, lastName);

        WebElement postalCodeField = driver.findElement(postalCodeInput);
        Interactions.typeReliably(postalCodeField, postalCode);

        Interactions.clickUntil(
                driver,
                () -> wait.until(ExpectedConditions.elementToBeClickable(continueButton)),
                () -> !driver.findElements(finishButton).isEmpty()
        );
    }

    public void finishOrder() {
        Interactions.clickUntil(
                driver,
                () -> wait.until(ExpectedConditions.elementToBeClickable(finishButton)),
                () -> !driver.findElements(confirmationHeader).isEmpty()
        );
    }

    public String getConfirmationMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationHeader)).getText();
    }
}
