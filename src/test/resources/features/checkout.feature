# language: pt
Funcionalidade: Compra de produtos no SauceDemo
  Como um cliente do SauceDemo
  Eu quero adicionar produtos ao carrinho e finalizar a compra
  Para concluir um pedido

  Contexto:
    Dado que fiz login como "standard_user"

  Cenário: Adicionar produto ao carrinho e validar o contador
    Quando eu adiciono o produto "Sauce Labs Backpack" ao carrinho
    Então o ícone do carrinho deve exibir "1" item

  Cenário: Finalizar compra com sucesso
    Dado que adicionei o produto "Sauce Labs Backpack" ao carrinho
    Quando eu vou para o carrinho e prossigo para o checkout
    E eu preencho os dados de entrega com nome "Luis", sobrenome "Felipe" e cep "01310-000"
    E eu finalizo a compra
    Então eu devo ver a mensagem de confirmação "Thank you for your order!"

  Cenário: Validação visual da página de produtos
    Então a página de produtos deve corresponder visualmente à imagem de referência
