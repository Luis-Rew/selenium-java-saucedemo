# language: pt
Funcionalidade: Login no SauceDemo
  Como um usuário do SauceDemo
  Eu quero fazer login no sistema
  Para acessar o catálogo de produtos

  Contexto:
    Dado que estou na página de login do SauceDemo

  Cenário: Login com credenciais válidas
    Quando eu faço login com o usuário "standard_user" e senha "secret_sauce"
    Então eu devo ver a página de produtos
    E o título da página deve ser "Products"

  Cenário: Login com credenciais inválidas
    Quando eu faço login com o usuário "usuario_invalido" e senha "senha_errada"
    Então eu devo ver a mensagem de erro "Epic sadface: Username and password do not match any user in this service"

  Cenário: Login com usuário bloqueado
    Quando eu faço login com o usuário "locked_out_user" e senha "secret_sauce"
    Então eu devo ver a mensagem de erro "Epic sadface: Sorry, this user has been locked out."
