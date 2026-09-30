package com.luisrew.saucedemo.stepdefinitions;

import com.luisrew.saucedemo.pages.CheckoutPage;
import com.luisrew.saucedemo.utils.DriverFactory;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;

import static org.junit.Assert.assertEquals;

public class CheckoutSteps {

    private final CheckoutPage checkoutPage = new CheckoutPage(DriverFactory.getDriver());

    @E("eu preencho os dados de entrega com nome {string}, sobrenome {string} e cep {string}")
    public void euPreenchoOsDadosDeEntregaComNomeSobrenomeECep(String firstName, String lastName, String postalCode) {
        checkoutPage.fillDeliveryInfo(firstName, lastName, postalCode);
    }

    @E("eu finalizo a compra")
    public void euFinalizoACompra() {
        checkoutPage.finishOrder();
    }

    @Então("eu devo ver a mensagem de confirmação {string}")
    public void euDevoVerAMensagemDeConfirmacao(String expectedMessage) {
        assertEquals(expectedMessage, checkoutPage.getConfirmationMessage());
    }
}
