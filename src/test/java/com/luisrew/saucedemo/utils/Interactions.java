package com.luisrew.saucedemo.utils;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Em ambientes mais lentos (ex.: runners de CI), a primeira interação em uma
 * página recém-carregada às vezes acontece antes da página terminar de
 * anexar seus listeners de evento — o clique/digitação "acontece", mas não
 * tem efeito nenhum. Além disso, várias ações do SauceDemo substituem o
 * elemento no DOM depois do clique (ex.: botão "Add to cart" vira "Remove"
 * trocando o nó inteiro), o que invalida qualquer referência antiga
 * (StaleElementReferenceException).
 *
 * Por isso essas ajudantes sempre re-localizam o elemento via Supplier em
 * vez de reaproveitar uma referência guardada, e confirmam que a ação teve
 * o efeito esperado antes de desistir.
 */
public final class Interactions {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration RETRY_WINDOW = Duration.ofSeconds(5);

    private Interactions() {
    }

    public static void typeReliably(WebElement field, String text) {
        int attempts = 0;
        while (!text.equals(field.getAttribute("value")) && attempts < MAX_ATTEMPTS) {
            field.clear();
            field.sendKeys(text);
            attempts++;
        }
    }

    public static void clickUntil(WebDriver driver, Supplier<WebElement> elementSupplier, BooleanSupplier effectHappened) {
        int attempts = 0;
        while (!safeCheck(effectHappened) && attempts < MAX_ATTEMPTS) {
            try {
                elementSupplier.get().click();
            } catch (StaleElementReferenceException ignored) {
                // o elemento já mudou por causa dessa tentativa (ou de uma anterior);
                // deixa a condição de efeito decidir se precisamos tentar de novo
            }
            try {
                new WebDriverWait(driver, RETRY_WINDOW).until(d -> safeCheck(effectHappened));
            } catch (TimeoutException ignored) {
                // tenta de novo no próximo loop
            }
            attempts++;
        }
    }

    private static boolean safeCheck(BooleanSupplier effectHappened) {
        try {
            return effectHappened.getAsBoolean();
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }
}
