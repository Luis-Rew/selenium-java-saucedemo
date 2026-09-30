package com.luisrew.saucedemo.stepdefinitions;

import com.luisrew.saucedemo.pages.CartPage;
import com.luisrew.saucedemo.pages.InventoryPage;
import com.luisrew.saucedemo.pages.LoginPage;
import com.luisrew.saucedemo.utils.DriverFactory;
import com.luisrew.saucedemo.utils.VisualComparisonUtil;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class InventorySteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private final InventoryPage inventoryPage = new InventoryPage(DriverFactory.getDriver());
    private final CartPage cartPage = new CartPage(DriverFactory.getDriver());

    @Dado("que fiz login como {string}")
    public void queFizLoginComo(String username) {
        loginPage.open();
        loginPage.login(username, "secret_sauce");
    }

    @Quando("eu adiciono o produto {string} ao carrinho")
    @Dado("que adicionei o produto {string} ao carrinho")
    public void euAdicionoOProdutoAoCarrinho(String productName) {
        inventoryPage.addProductToCart(productName);
    }

    @Então("o ícone do carrinho deve exibir {string} item")
    public void oIconeDoCarrinhoDeveExibirItem(String expectedCount) {
        assertEquals(expectedCount, inventoryPage.getCartBadgeCount());
    }

    @Quando("eu vou para o carrinho e prossigo para o checkout")
    public void euVouParaOCarrinhoEProssigoParaOCheckout() {
        inventoryPage.goToCart();
        cartPage.goToCheckout();
    }

    @Então("a página de produtos deve corresponder visualmente à imagem de referência")
    public void aPaginaDeProdutosDeveCorresponderVisualmenteAImagemDeReferencia() throws IOException {
        VisualComparisonUtil.assertVisualMatch(DriverFactory.getDriver(), "inventory_page", 5.0);
    }
}
