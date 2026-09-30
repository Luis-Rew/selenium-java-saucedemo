# Selenium + Java + Cucumber + JUnit — SauceDemo

Projeto de automação de testes de UI, em BDD, rodando contra o [saucedemo.com](https://www.saucedemo.com/) (site público, oficial para prática de automação).

Cobre login, carrinho, checkout e uma validação visual (comparação de screenshot) — tudo escrito em Gherkin (`.feature`), com step definitions em Java, Page Object Model, e assertions via JUnit.

> Projeto de portfólio, feito para demonstrar organização de um framework de testes de UI do zero. Sinta-se à vontade para clonar, rodar e usar como referência.

---

## Stack

| Camada | Ferramenta |
|---|---|
| Linguagem | Java 17 |
| Build | Maven (com Maven Wrapper — não precisa instalar Maven) |
| Automação de navegador | Selenium WebDriver 4 |
| BDD | Cucumber-JVM (Gherkin em português) |
| Runner / Assertions | JUnit 4 |
| Gerenciamento de driver | WebDriverManager (baixa o chromedriver certo sozinho) |
| Comparação de imagem | image-comparison |
| CI | GitHub Actions |

---

## Estrutura do projeto

```
selenium-java-saucedemo/
├── src/test/java/com/luisrew/saucedemo/
│   ├── runner/TestRunner.java       # liga JUnit + Cucumber
│   ├── hooks/Hooks.java             # abre/fecha o navegador, tira print em falha
│   ├── pages/                       # Page Object Model (1 classe por página)
│   ├── stepdefinitions/             # implementação dos passos do Gherkin
│   └── utils/
│       ├── DriverFactory.java       # cria o WebDriver (headless por padrão)
│       └── VisualComparisonUtil.java# assert visual (compara screenshot x baseline)
├── src/test/resources/
│   ├── features/                    # os arquivos .feature (Gherkin)
│   └── baseline/                    # screenshots de referência (gerados localmente, git-ignored)
├── .github/workflows/ci.yml         # roda os testes a cada push/PR
├── pom.xml
└── mvnw / mvnw.cmd                  # Maven Wrapper
```

---

## Como rodar

Pré-requisitos: **Java 17+** instalado e **Google Chrome** instalado. Não precisa instalar Maven — o wrapper cuida disso.

```bash
# Windows
./mvnw.cmd test

# Linux / Mac
./mvnw test
```

Isso builda o projeto, sobe um Chrome headless, roda todos os `.feature` e imprime o resultado no terminal.

**Relatórios gerados após a execução:**
- `target/cucumber-reports/report.html` — relatório navegável do Cucumber
- `target/visual-checks/` — screenshots usados no assert visual e a imagem de diferença (quando houver)

**Quer ver o navegador rodando (não headless)?**
```bash
./mvnw test -Dheadless=false
```

**Rodar direto pela IDE:** se você tem o plugin "Cucumber for Java" (IntelliJ) instalado, dá pra abrir qualquer arquivo `.feature` e clicar em "Run as Cucumber Feature" direto na aba do editor.

---

## Como os testes estão organizados (BDD na prática)

Cada `.feature` descreve o comportamento em linguagem natural (Gherkin), por exemplo:

```gherkin
Cenário: Login com credenciais inválidas
  Quando eu faço login com o usuário "usuario_invalido" e senha "senha_errada"
  Então eu devo ver a mensagem de erro "Epic sadface: Username and password do not match any user in this service"
```

Cada linha (`Quando`, `Então`...) tem um método Java correspondente em `stepdefinitions/`, que por sua vez usa uma classe de `pages/` (Page Object) para interagir com o site. As assertions (`assertEquals`, `assertTrue`) usam JUnit e comparam o texto que apareceu na tela com o texto esperado.

### O assert visual

O cenário "Validação visual da página de produtos" tira um screenshot da tela e compara pixel a pixel com uma imagem de referência (`baseline`), usando a lib `image-comparison`. Se a baseline ainda não existe (primeira execução no seu ambiente), ela é criada automaticamente e o teste passa — da segunda execução em diante, ele realmente compara. Isso evita falso-negativo entre ambientes diferentes (sua máquina x GitHub Actions), já que fontes e renderização variam um pouco entre sistemas operacionais.

---

## Aulinha: como criar um projeto desse do zero, sem IA

Se você quer entender/replicar esse setup na mão, sem depender de nada gerado automaticamente, aqui vai o caminho:

### 1. Instale o Java (JDK)

- Baixe o **JDK 17 (ou mais recente)** em [adoptium.net](https://adoptium.net/) (build Temurin, gratuito).
- Instale normalmente (Next, Next, Finish).
- Confirme no terminal:
  ```bash
  java -version
  ```

### 2. Instale o Git

- Baixe em [git-scm.com](https://git-scm.com/downloads).
- Instale com as opções padrão.
- Confirme:
  ```bash
  git --version
  ```
- Configure seu nome e e-mail (só precisa fazer uma vez por máquina):
  ```bash
  git config --global user.name "Seu Nome"
  git config --global user.email "seu-email@exemplo.com"
  ```

### 3. Instale uma IDE

- **IntelliJ IDEA Community** (gratuita) é a mais usada para projetos Java. Baixe em [jetbrains.com/idea](https://www.jetbrains.com/idea/download/).
- Depois de instalar, adicione o plugin **"Cucumber for Java"** em `File > Settings > Plugins` — é ele que permite rodar `.feature` diretamente.

### 4. Crie a estrutura do projeto Maven

Você pode gerar um projeto Maven vazio pela própria IDE (`New Project > Maven`), ou criar a pasta na mão:

```
meu-projeto/
├── pom.xml
└── src/
    └── test/
        └── java/
```

O `pom.xml` é o arquivo que descreve as dependências (bibliotecas) do projeto — é nele que você declara Selenium, Cucumber, JUnit, etc, com suas versões. Sem Maven (ou Gradle), você teria que baixar cada `.jar` na mão e configurar o classpath manualmente — por isso quase ninguém faz projeto Java sem uma ferramenta de build.

### 5. Adicione as dependências no `pom.xml`

Dentro da tag `<dependencies>`, adicione (numa busca rápida em [mvnrepository.com](https://mvnrepository.com/) você acha o XML de cada uma):
- `selenium-java` — controla o navegador
- `io.github.bonigarcia:webdrivermanager` — baixa o driver certo do navegador sozinho
- `io.cucumber:cucumber-java` e `io.cucumber:cucumber-junit` — BDD
- `junit:junit` — framework de testes e assertions
- `com.github.romankh3:image-comparison` — comparação de imagens (opcional, só se quiser asserts visuais)

### 6. Escreva um `.feature`

Crie `src/test/resources/features/login.feature` e escreva um cenário em Gherkin (`Dado`, `Quando`, `Então`).

### 7. Crie o Runner

Uma classe simples com duas anotações conecta JUnit ao Cucumber:

```java
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features", glue = "seu.pacote.stepdefinitions")
public class TestRunner {}
```

### 8. Implemente os steps

Para cada linha do `.feature`, crie um método Java anotado (`@Dado`, `@Quando`, `@Então`) com o texto correspondente. É aqui que você chama o Selenium de fato (`driver.findElement(...)`) e faz as assertions com JUnit (`Assert.assertEquals(...)`).

### 9. Rode

```bash
mvn test
```

(Se não tiver o Maven instalado globalmente, instale em [maven.apache.org](https://maven.apache.org/download.cgi) e adicione a pasta `bin` na variável de ambiente `PATH` — ou use o Maven Wrapper, que baixa o Maven certo sozinho: rode `mvn wrapper:wrapper` uma vez, com Maven já instalado, para gerar `mvnw`/`mvnw.cmd` no projeto.)

### 10. Crie o repositório no GitHub

1. Acesse [github.com/new](https://github.com/new).
2. Dê um nome ao repositório (ex: `selenium-java-saucedemo`), marque como **Public**, **não** marque "Add a README" (você já tem um localmente).
3. Clique em **Create repository**. O GitHub vai te mostrar os comandos — mas o resumo é o passo 11.

### 11. Suba o projeto (primeira vez)

Na pasta do projeto, no terminal:

```bash
git init                                   # inicia o repositório local
git add .                                  # adiciona todos os arquivos
git commit -m "primeiro commit"            # salva um ponto no histórico
git branch -M main                         # garante que a branch principal se chama "main"
git remote add origin https://github.com/SEU_USUARIO/selenium-java-saucedemo.git
git push -u origin main                    # envia pro GitHub
```

Na primeira vez, o Git vai pedir pra você autenticar — hoje em dia o mais comum é abrir uma janela do navegador pra você logar (o **Git Credential Manager**, que já vem junto com o Git para Windows, cuida disso sozinho).

### 12. Próximas alterações

Depois do primeiro push, o dia a dia é só:

```bash
git status              # ver o que mudou
git add .                # adicionar as mudanças
git commit -m "descrição da mudança"
git push                 # enviar pro GitHub
```

### 13. (Opcional) Configure CI

Crie `.github/workflows/ci.yml` no seu repositório configurando um job que faz checkout do código, instala o Java, e roda `./mvnw test`. O GitHub Actions roda esse workflow sozinho a cada `git push` — é assim que aparece aquele "check verde" no seu repositório.

---

## Por que esses usuários de teste?

O SauceDemo disponibiliza usuários fixos para teste (documentados na própria página de login):
- `standard_user` / `secret_sauce` — login normal
- `locked_out_user` / `secret_sauce` — simula usuário bloqueado
- `problem_user`, `performance_glitch_user` — simulam bugs de propósito (bons para outros cenários futuros)
