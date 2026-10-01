package com.luisrew.saucedemo.utils;

import org.openqa.selenium.JavascriptExecutor;
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

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration RETRY_WINDOW = Duration.ofSeconds(5);
    private static final long SETTLE_MILLIS = 300;

    private Interactions() {
    }

    public static void typeReliably(WebDriver driver, WebElement field, String text) {
        int attempts = 0;
        while (!text.equals(field.getAttribute("value")) && attempts < MAX_ATTEMPTS) {
            sleep(SETTLE_MILLIS);
            // Usamos sendKeys nativo (não setar "value" via JS): a página usa um
            // framework JS com inputs controlados, e setar o valor diretamente
            // não atualiza o estado interno dele mesmo disparando eventos
            // "input"/"change" manualmente — a validação continua achando o
            // campo vazio. sendKeys nativo aciona os mesmos eventos de teclado
            // que um usuário real geraria, então o framework reconhece a mudança.
            field.clear();
            field.sendKeys(text);
            attempts++;
        }

        if (!text.equals(field.getAttribute("value"))) {
            throw new IllegalStateException(
                    "O campo não recebeu o texto \"" + text + "\" depois de " + MAX_ATTEMPTS + " tentativas.");
        }
    }

    public static void clickUntil(WebDriver driver, Supplier<WebElement> elementSupplier, BooleanSupplier effectHappened) {
        int attempts = 0;
        while (!safeCheck(effectHappened) && attempts < MAX_ATTEMPTS) {
            // Pequena pausa antes de clicar: mesmo com o elemento já "clicável",
            // o JS da página às vezes ainda não terminou de anexar os listeners
            // de evento no exato instante em que o Selenium age. Um pequeno
            // acomodamento aqui reduz bastante essa corrida, de forma pragmática.
            sleep(SETTLE_MILLIS);
            try {
                // Clique via JS em vez de clique nativo: em Chrome headless o clique
                // nativo é despachado por coordenadas de tela, que podem ficar
                // levemente diferentes do elemento real dependendo do ambiente
                // (viewport, escala, SO). O clique via JS dispara o evento
                // diretamente no elemento, sem depender de coordenadas.
                WebElement element = elementSupplier.get();
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block: 'center'}); arguments[0].click();", element);
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

        if (!safeCheck(effectHappened)) {
            throw new IllegalStateException(
                    "O clique não teve o efeito esperado depois de " + MAX_ATTEMPTS + " tentativas.");
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
