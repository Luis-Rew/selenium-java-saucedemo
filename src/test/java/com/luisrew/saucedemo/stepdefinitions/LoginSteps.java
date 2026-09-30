package com.luisrew.saucedemo.stepdefinitions;

import com.luisrew.saucedemo.pages.InventoryPage;
import com.luisrew.saucedemo.pages.LoginPage;
import com.luisrew.saucedemo.utils.DriverFactory;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoginSteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private final InventoryPage inventoryPage = new InventoryPage(DriverFactory.getDriver());

    @Dado("que estou na página de login do SauceDemo")
    public void queEstouNaPaginaDeLoginDoSauceDemo() {
        loginPage.open();
    }

    @Quando("eu faço login com o usuário {string} e senha {string}")
    public void euFacoLoginComOUsuarioESenha(String username, String password) {
        loginPage.login(username, password);
    }

    @Então("eu devo ver a página de produtos")
    public void euDevoVerAPaginaDeProdutos() {
        assertTrue("Esperava estar na página de produtos, mas ela não carregou.", inventoryPage.isLoaded());
    }

    @Então("o título da página deve ser {string}")
    public void oTituloDaPaginaDeveSer(String expectedTitle) {
        assertEquals(expectedTitle, inventoryPage.getPageTitle());
    }

    @Então("eu devo ver a mensagem de erro {string}")
    public void euDevoVerAMensagemDeErro(String expectedMessage) {
        assertEquals(expectedMessage, loginPage.getErrorMessage());
    }
}
