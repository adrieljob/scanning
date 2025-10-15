package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URLDecoder;
import java.time.Duration;
import java.util.*;

@RestController
public class buscaAutomatica {

    @GetMapping("/login")
    public String fazerLogin(@RequestParam(name = "cidade", required = false) String cidade) {
        System.out.println("=== /login chamado ===");
        System.out.println("Cidade recebida: " + cidade);

        if (cidade == null || cidade.isEmpty()) {
            cidade = "29";
            System.out.println("Cidade não informada, usando padrão: " + cidade);
        }

        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        System.out.println("URL que será acessada: " + urlBase);

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        //options.addArguments("--headless");
        options.addArguments("--incognito");
        options.addArguments("--disable-cache");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        Map<String, Object> resultado = new HashMap<>();

        try {
            driver.get(urlBase);
            driver.manage().window().maximize();

            WebElement campoUsuario = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_user")));
            campoUsuario.clear();
            campoUsuario.sendKeys("factory");

            WebElement campoSenha = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_pw")));
            campoSenha.clear();
            campoSenha.sendKeys("f@ct0ry");

            WebElement botaoLogin = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_loginBtn")));
            botaoLogin.click();

            wait.until(ExpectedConditions.urlContains("mainPage.html"));

            String urlAtual = driver.getCurrentUrl();
            System.out.println("URL atual após login: " + urlAtual);
            String sess = extrairParametroSess(urlAtual);

            resultado.put("status", "Login realizado com sucesso");
            resultado.put("urlAtual", urlAtual);
            resultado.put("sess", sess);

        } catch (Exception e) {
            System.err.println("Erro no /login: " + e.toString());
            resultado.put("status", "Erro no login");
            resultado.put("erro", e.toString());
        } finally {
            driver.quit();
            System.out.println("Driver finalizado no /login");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(resultado);
        } catch (Exception e) {
            System.err.println("Erro ao gerar JSON no /login: " + e.toString());
            return "{\"erro\":\"Falha ao gerar JSON\"}";
        }
    }

    @GetMapping("/desligarAlarme")
    public String desligarAlarmePsu1(@RequestParam(name = "cidade", required = false) String cidade) {
        System.out.println("=== /desligarAlarme chamado ===");
        System.out.println("Cidade recebida: " + cidade);

        if (cidade == null || cidade.isEmpty()) {
            cidade = "29";
            System.out.println("Cidade não informada, usando padrão: " + cidade);
        }

        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        System.out.println("URL que será acessada: " + urlBase);

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        //options.addArguments("--headless");
        options.addArguments("--incognito");
        options.addArguments("--disable-cache");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        Map<String, Object> resultado = new HashMap<>();

        try {
            driver.get(urlBase);
            driver.manage().window().maximize();

            // Login (mesmo código do /login)
            WebElement campoUsuario = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_user")));
            campoUsuario.clear();
            campoUsuario.sendKeys("factory");

            WebElement campoSenha = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_pw")));
            campoSenha.clear();
            campoSenha.sendKeys("f@ct0ry");

            WebElement botaoLogin = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_loginBtn")));
            botaoLogin.click();

            wait.until(ExpectedConditions.urlContains("mainPage.html"));

            // Sequência para desligar o alarme PSU1

            // Clica no botão para abrir o log de eventos (event log)
            WebElement eventLogBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#event_button > a.eventLog.btn100x40_fault")));
            eventLogBtn.click();

            // Clica no botão setup dentro do log
            WebElement setupBtn = new WebDriverWait(driver, Duration.ofSeconds(60))
                    .until(ExpectedConditions.elementToBeClickable(By.cssSelector("#btnLogSetup > a#faultlogSet")));
            setupBtn.click();

            Thread.sleep(1500);

            // Localiza o checkbox para desligar o alarme PSU1
            WebElement checkboxEnable = new WebDriverWait(driver, Duration.ofSeconds(60))
                    .until(ExpectedConditions.elementToBeClickable(By.cssSelector(
                            "#logCfgTable > div.tableContent > div:nth-child(5) > div.col1.thickBrdrBoth > div.checkbox.wp[name='enable']"
                    )));

            String classes = checkboxEnable.getAttribute("class");
            boolean estaMarcado = classes.contains("checked");

            if (estaMarcado) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", checkboxEnable);

                new WebDriverWait(driver, Duration.ofSeconds(40)).until(ExpectedConditions.not(
                        ExpectedConditions.attributeContains(checkboxEnable, "class", "checked")
                ));

                resultado.put("alarmePsu1", "Desligado com sucesso");
            } else {
                resultado.put("alarmePsu1", "Já estava desligado");
            }

        } catch (Exception e) {
            System.err.println("Erro no /desligarAlarme: " + e.toString());
            resultado.put("status", "Erro ao desligar alarme");
            resultado.put("erro", e.toString());
        } finally {
            driver.quit();
            System.out.println("Driver finalizado no /desligarAlarme");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(resultado);
        } catch (Exception e) {
            System.err.println("Erro ao gerar JSON no /desligarAlarme: " + e.toString());
            return "{\"erro\":\"Falha ao gerar JSON\"}";
        }
    }

    @GetMapping("/alarmess")
    public String buscarAlarmesAtivos(@RequestParam(name = "cidade", required = false) String cidade) {
        System.out.println("=== /alarmess chamado ===");
        System.out.println("Cidade recebida: " + cidade);

        // Define cidade padrão caso não seja informada
        if (cidade == null || cidade.isEmpty()) {
            cidade = "29";
            System.out.println("Cidade não informada, usando padrão: " + cidade);
        }

        // Monta a URL base para acesso
        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        System.out.println("URL que será acessada: " + urlBase);

        // Configura o driver do Chrome
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        //options.addArguments("--headless"); // descomente para rodar sem abrir navegador
        options.addArguments("--incognito");
        options.addArguments("--disable-cache");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        Map<String, Object> resultado = new HashMap<>();

        try {
            // Acessa a página de login
            driver.get(urlBase);
            driver.manage().window().maximize();

            // Preenche o campo usuário
            WebElement campoUsuario = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_user")));
            campoUsuario.clear();
            campoUsuario.sendKeys("factory");

            // Preenche o campo senha
            WebElement campoSenha = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_pw")));
            campoSenha.clear();
            campoSenha.sendKeys("f@ct0ry");

            // Clica no botão de login
            WebElement botaoLogin = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_loginBtn")));
            botaoLogin.click();

            // Aguarda até que a URL contenha "mainPage.html" indicando login bem-sucedido
            wait.until(ExpectedConditions.urlContains("mainPage.html"));

            // Clica no botão para abrir o log de eventos (event log)
            WebElement eventLogBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#event_button > a.eventLog.btn100x40_fault")));
            eventLogBtn.click();

            // Aguarda o conteúdo do log estar presente
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("log_content")));

            // Busca todos os elementos que representam alarmes ativos (com classe contendo 'ACT')
            List<WebElement> alarmesAtivos = driver.findElements(By.cssSelector("#log_content > div.c1[class*='ACT']"));

            List<Map<String, String>> listaAlarmes = new ArrayList<>();

            // Itera sobre os alarmes encontrados para extrair informações
            for (WebElement alarme : alarmesAtivos) {
                String classe = alarme.getAttribute("class");
                String tipo = "";
                if (classe.contains("WarningACT")) {
                    tipo = "Warning";
                } else if (classe.contains("FaultACT")) {
                    tipo = "Fault";
                } else {
                    continue; // ignora elementos que não são alarmes Warning ou Fault
                }

                String msg = "";
                String date = "";
                String status = "";

                try {
                    WebElement msgElem = alarme.findElement(By.cssSelector("div.msg"));
                    msg = msgElem.getText();
                } catch (org.openqa.selenium.NoSuchElementException ignored) {}

                try {
                    WebElement dateElem = alarme.findElement(By.cssSelector("div.date"));
                    date = dateElem.getText();
                } catch (org.openqa.selenium.NoSuchElementException ignored) {}

                try {
                    WebElement statusElem = alarme.findElement(By.cssSelector("div.status"));
                    status = statusElem.getText();
                } catch (org.openqa.selenium.NoSuchElementException ignored) {}

                Map<String, String> alarmeInfo = new HashMap<>();
                alarmeInfo.put("tipo", tipo);
                alarmeInfo.put("mensagem", msg);
                alarmeInfo.put("data", date);
                alarmeInfo.put("status", status);
                listaAlarmes.add(alarmeInfo);
            }

            resultado.put("status", "Sucesso");
            resultado.put("alarmess", listaAlarmes);

        } catch (Exception e) {
            System.err.println("Erro no /alarmess: " + e.toString());
            resultado.put("status", "Erro ao buscar alarmes");
            resultado.put("erro", e.toString());
        } finally {
            driver.quit();
            System.out.println("Driver finalizado no /alarmess");
        }

        // Retorna o resultado em JSON formatado
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(resultado);
        } catch (Exception e) {
            System.err.println("Erro ao gerar JSON no /alarmess: " + e.toString());
            return "{\"erro\":\"Falha ao gerar JSON\"}";
        }
    }

    @GetMapping("/alarmessSetupFaults")
    public String buscarAlarmeSetupEspecifico(@RequestParam(name = "cidade", required = false) String cidade) {
        System.out.println("=== /alarmessSetupFaults chamado (focado no alarme específico) ===");
        System.out.println("Cidade recebida: " + cidade);

        // Define cidade padrão caso não seja informada
        if (cidade == null || cidade.isEmpty()) {
            cidade = "29";
            System.out.println("Cidade não informada, usando padrão: " + cidade);
        }

        // Monta a URL base para acesso
        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        System.out.println("URL que será acessada: " + urlBase);

        // Configura o driver do Chrome
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        //options.addArguments("--headless"); // descomente para rodar sem abrir navegador
        options.addArguments("--incognito");
        options.addArguments("--disable-cache");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        Map<String, Object> resultado = new HashMap<>();

        try {
            // Acessa a página de login
            driver.get(urlBase);
            driver.manage().window().maximize();

            // Preenche o campo usuário
            WebElement campoUsuario = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_user")));
            campoUsuario.clear();
            campoUsuario.sendKeys("factory");

            // Preenche o campo senha
            WebElement campoSenha = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_pw")));
            campoSenha.clear();
            campoSenha.sendKeys("f@ct0ry");

            // Clica no botão de login
            WebElement botaoLogin = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_loginBtn")));
            botaoLogin.click();

            // Aguarda até que a URL contenha "mainPage.html" indicando login bem-sucedido
            wait.until(ExpectedConditions.urlContains("mainPage.html"));

            // Clica no botão para abrir o log de eventos (event log)
            WebElement eventLogBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#event_button > a.eventLog.btn100x40_fault")));
            eventLogBtn.click();

            // Clica no botão setup dentro do log
            WebElement setupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#btnLogSetup > a#faultlogSet")));
            setupBtn.click();

            // Aguarda 5 segundos para o conteúdo carregar
            Thread.sleep(5000);

            // Seleciona a linha específica (5ª div dentro de tableContent)
            WebElement linhaEspecifica = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("#logCfgTable > div.tableContent > div:nth-child(5)")
            ));

            // Verifica se a linha está visível (não oculta via CSS)
            String style = linhaEspecifica.getAttribute("style");
            if (style != null && style.contains("display: none")) {
                resultado.put("status", "Linha específica está oculta");
                resultado.put("fault", Collections.emptyMap());
            } else {
                // Pega o status do LED dentro da coluna 3
                WebElement statusElem = linhaEspecifica.findElement(By.cssSelector("div.col3.thickBrdrRt > div[name='status']"));
                String classesLed = statusElem.getAttribute("class");

                // Pega a mensagem da coluna 2
                WebElement msgElem = linhaEspecifica.findElement(By.cssSelector("div.col2[name='message']"));
                String mensagem = msgElem.getText();

                // Categoria da linha (atributo data-category)
                String categoria = linhaEspecifica.getAttribute("data-category");
                if (categoria == null) categoria = "";

                // Tipo de log selecionado (valor do select)
                WebElement selectTipo = linhaEspecifica.findElement(By.cssSelector("select[name='log_type']"));
                String tipoLog = selectTipo.getAttribute("value");

                Map<String, String> fault = new HashMap<>();
                fault.put("mensagem", mensagem);
                fault.put("categoria", categoria);
                fault.put("tipoLog", tipoLog);
                fault.put("statusLed", classesLed);

                resultado.put("status", "Sucesso");
                resultado.put("fault", fault);
            }

        } catch (Exception e) {
            System.err.println("Erro no /alarmessSetupFaults: " + e.toString());
            resultado.put("status", "Erro ao buscar alarme específico");
            resultado.put("erro", e.toString());
        } finally {
            driver.quit();
            System.out.println("Driver finalizado no /alarmessSetupFaults");
        }

        // Retorna o resultado em JSON formatado
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(resultado);
        } catch (Exception e) {
            System.err.println("Erro ao gerar JSON no /alarmessSetupFaults: " + e.toString());
            return "{\"erro\":\"Falha ao gerar JSON\"}";
        }
    }

    @GetMapping("/funcaoRemux")
    public String funcaoRemux(@RequestParam(name = "cidade", required = false) String cidade) {
        System.out.println("=== /funcaoRemux chamado ===");
        System.out.println("Cidade recebida: " + cidade);

        if (cidade == null || cidade.isEmpty()) {
            cidade = "29"; // valor padrão
            System.out.println("Cidade não informada, usando padrão: " + cidade);
        }

        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        System.out.println("URL que será acessada: " + urlBase);

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        //options.addArguments("--headless"); // descomente para rodar sem abrir navegador
        options.addArguments("--incognito");
        options.addArguments("--disable-cache");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        Map<String, Object> resultado = new HashMap<>();

        try {
            driver.get(urlBase);
            driver.manage().window().maximize();

            // Login
            WebElement campoUsuario = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_user")));
            campoUsuario.clear();
            campoUsuario.sendKeys("factory");

            WebElement campoSenha = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_pw")));
            campoSenha.clear();
            campoSenha.sendKeys("f@ct0ry");

            WebElement botaoLogin = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_loginBtn")));
            botaoLogin.click();

            wait.until(ExpectedConditions.urlContains("mainPage.html"));

            // 1. Clicar em "Go To"
            WebElement goToBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a.btn60x22.link_btn[onclick*='modBtnClick(2)']")));
            goToBtn.click();
            Thread.sleep(1500);

            // 2. Clicar em "Exciter"
            WebElement exciterDiv = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div#h_exciter")));
            exciterDiv.click();
            Thread.sleep(1500);

            // 3. Clicar em "ISDB-T Modulator"
            WebElement modulatorLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("estatus_modulator")));
            modulatorLink.click();
            Thread.sleep(1500);

            // 4. Clicar em "IsdbT Config"
            WebElement configLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@class,'btn120_default') and contains(text(),'IsdbT Config')]")));
            configLink.click();
            Thread.sleep(1500);

            // 5. Alterar o select "Input Mode" para value "3"
            WebElement inputModeSelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("isdbt_mode")));
            Select select = new Select(inputModeSelect);

            select.selectByValue("3");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", inputModeSelect);
            Thread.sleep(1500);

            // 6. Alterar o select "Input Mode" para value "2" , volta para a configuração de REMUX
            select.selectByValue("2");
            js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", inputModeSelect);
            Thread.sleep(1500);

            resultado.put("status", "Sequência executada com sucesso");
            resultado.put("inputModeAlterado", "Valor 3 (Async BTS) selecionado e depois voltou para 2 (Remux)");

        } catch (Exception e) {
            System.err.println("Erro no /funcaoRemux: " + e.toString());
            resultado.put("status", "Erro ao executar sequência");
            resultado.put("erro", e.toString());
        } finally {
            driver.quit();
            System.out.println("Driver finalizado no /funcaoRemux");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(resultado);
        } catch (Exception e) {
            System.err.println("Erro ao gerar JSON no /funcaoRemux: " + e.toString());
            return "{\"erro\":\"Falha ao gerar JSON\"}";
        }
    }

    @GetMapping("/funcaoTaxa")
    public String funcaoTaxa(@RequestParam(name = "cidade", required = false) String cidade) {
        System.out.println("=== /funcaoTaxa chamado ===");
        System.out.println("Cidade recebida: " + cidade);

        if (cidade == null || cidade.isEmpty()) {
            cidade = "29"; // valor padrão
            System.out.println("Cidade não informada, usando padrão: " + cidade);
        }

        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        System.out.println("URL que será acessada: " + urlBase);

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        //options.addArguments("--headless"); // descomente para rodar sem abrir navegador
        options.addArguments("--incognito");
        options.addArguments("--disable-cache");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        Map<String, Object> resultado = new HashMap<>();

        try {
            driver.get(urlBase);
            driver.manage().window().maximize();

            // Login
            WebElement campoUsuario = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_user")));
            campoUsuario.clear();
            campoUsuario.sendKeys("factory");

            WebElement campoSenha = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_pw")));
            campoSenha.clear();
            campoSenha.sendKeys("f@ct0ry");

            WebElement botaoLogin = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_loginBtn")));
            botaoLogin.click();

            wait.until(ExpectedConditions.urlContains("mainPage.html"));

            // 1. Clicar em "Go To"
            WebElement goToBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a.btn60x22.link_btn[onclick*='modBtnClick(2)']")));
            goToBtn.click();
            Thread.sleep(1500);

            // 2. Clicar em "Exciter"
            WebElement exciterDiv = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div#h_exciter")));
            exciterDiv.click();
            Thread.sleep(1500);

            // 3. Clicar em "Input"
            WebElement inputLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("estatus_inputs")));
            inputLink.click();
            Thread.sleep(1500);

            // 4. Pegar o valor da taxa de ASI e ETH
            WebElement ethDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("in_stat_inputs_br_1")));
            String ethValue = ethDiv.getText();

            WebElement asiDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("in_stat_inputs_br_3")));
            String asiValue = asiDiv.getText();

            // Coloca os valores no resultado para retornar
            resultado.put("status", "Sequência executada com sucesso");
            resultado.put("taxa_ETH", ethValue);
            resultado.put("taxa_ASI", asiValue);

        } catch (Exception e) {
            System.err.println("Erro no /funcaoTaxa: " + e.toString());
            resultado.put("status", "Erro ao executar sequência");
            resultado.put("erro", e.toString());
        } finally {
            driver.quit();
            System.out.println("Driver finalizado no /funcaoTaxa");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(resultado);
        } catch (Exception e) {
            System.err.println("Erro ao gerar JSON no /funcaoTaxa: " + e.toString());
            return "{\"erro\":\"Falha ao gerar JSON\"}";
        }
    }

    @GetMapping("/funcaoPids")
    public String funcaoPids(@RequestParam(name = "cidade", required = false) String cidade) {
        System.out.println("=== /funcaoPids chamado ===");
        System.out.println("Cidade recebida: " + cidade);

        if (cidade == null || cidade.isEmpty()) {
            cidade = "29"; // valor padrão
            System.out.println("Cidade não informada, usando padrão: " + cidade);
        }

        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        System.out.println("URL que será acessada: " + urlBase);

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        //options.addArguments("--headless"); // descomente para rodar sem abrir navegador
        options.addArguments("--incognito");
        options.addArguments("--disable-cache");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        Map<String, Object> resultado = new HashMap<>();

        try {
            // Acessa a página de login
            driver.get(urlBase);
            driver.manage().window().maximize();

            // Login
            WebElement campoUsuario = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_user")));
            campoUsuario.clear();
            campoUsuario.sendKeys("factory");

            WebElement campoSenha = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_pw")));
            campoSenha.clear();
            campoSenha.sendKeys("f@ct0ry");

            WebElement botaoLogin = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_loginBtn")));
            botaoLogin.click();

            // Aguarda login bem-sucedido
            wait.until(ExpectedConditions.urlContains("mainPage.html"));

            // 1. Clicar em "Go To"
            WebElement goToBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a.btn60x22.link_btn[onclick*='modBtnClick(2)']")));
            goToBtn.click();
            Thread.sleep(1500);

            // 2. Clicar em "Exciter"
            WebElement exciterDiv = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div#h_exciter")));
            exciterDiv.click();
            Thread.sleep(1500);

            // 3. Clicar em "Input"
            WebElement inputLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("estatus_inputs")));
            inputLink.click();
            Thread.sleep(1500);

            // 4. Clicar em "TS Stats" (corrigido o XPath)
            WebElement tsStatsBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("ts_stats_btn")));
            tsStatsBtn.click();
            Thread.sleep(2000); // aguarda carregamento

            // 5. Aguardar o container dos PIDs estar presente
            WebElement psiInfoContainer = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("psi_info_tbl")));

            // 6. Extrair informações dos PIDs
            List<Map<String, String>> listaProgramas = new ArrayList<>();

            // Pegar TS ID
            try {
                WebElement tsIdElem = psiInfoContainer.findElement(By.cssSelector("div.rowA div.psiData"));
                String tsId = tsIdElem.getText();
                resultado.put("tsId", tsId);
            } catch (Exception e) {
                resultado.put("tsId", "Não encontrado");
            }

            // Buscar todos os programas (linhas com classe rowB)
            List<WebElement> programas = psiInfoContainer.findElements(By.cssSelector("div.rowB"));

            for (WebElement programa : programas) {
                Map<String, String> programaInfo = new HashMap<>();

                try {
                    WebElement programaData = programa.findElement(By.cssSelector("div.psiData"));
                    String programaTexto = programaData.getText();
                    programaInfo.put("programa", programaTexto);

                    // Buscar PIDs associados a este programa (próximas linhas rowC após este rowB)
                    List<String> pidsPrograma = new ArrayList<>();
                    WebElement proximoElemento = programa;

                    // Pega os próximos elementos irmãos que são rowC (PIDs do programa)
                    while (true) {
                        try {
                            proximoElemento = proximoElemento.findElement(By.xpath("following-sibling::div[1]"));
                            if (proximoElemento.getAttribute("class").contains("rowC")) {
                                WebElement pidData = proximoElemento.findElement(By.cssSelector("div.psiData"));
                                String pidTexto = pidData.getText();

                                // Verifica se tem ícone associado (video, audio, PCR)
                                String icone = "";
                                try {
                                    WebElement iconeElem = proximoElemento.findElement(By.cssSelector("div.icon"));
                                    icone = iconeElem.getAttribute("class").replace("icon", "").trim();
                                } catch (Exception ignored) {}

                                if (!icone.isEmpty()) {
                                    pidTexto += " (" + icone + ")";
                                }

                                pidsPrograma.add(pidTexto);
                            } else {
                                break; // próxima linha não é rowC, fim dos PIDs deste programa
                            }
                        } catch (Exception e) {
                            break; // não há mais elementos ou erro
                        }
                    }

                    programaInfo.put("pids", String.join(", ", pidsPrograma));
                    listaProgramas.add(programaInfo);

                } catch (Exception e) {
                    // Ignora programas que não conseguir processar
                }
            }

            resultado.put("status", "Sequência executada com sucesso");
            resultado.put("programas", listaProgramas);

        } catch (Exception e) {
            System.err.println("Erro no /funcaoPids: " + e.toString());
            resultado.put("status", "Erro ao executar sequência");
            resultado.put("erro", e.toString());
        } finally {
            driver.quit();
            System.out.println("Driver finalizado no /funcaoPids");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(resultado);
        } catch (Exception e) {
            System.err.println("Erro ao gerar JSON no /funcaoPids: " + e.toString());
            return "{\"erro\":\"Falha ao gerar JSON\"}";
        }
    }

    @GetMapping("/funcaoAlc")
    public String funcaoAlc(@RequestParam(name = "cidade", required = false) String cidade) {
        System.out.println("=== /funcaoAlc chamado ===");
        System.out.println("Cidade recebida: " + cidade);

        if (cidade == null || cidade.isEmpty()) {
            cidade = "29"; // valor padrão
            System.out.println("Cidade não informada, usando padrão: " + cidade);
        }

        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        System.out.println("URL que será acessada: " + urlBase);

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        //options.addArguments("--headless"); // descomente para rodar sem abrir navegador
        options.addArguments("--incognito");
        options.addArguments("--disable-cache");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        Map<String, Object> resultado = new HashMap<>();

        try {
            driver.get(urlBase);
            driver.manage().window().maximize();

            // Login
            WebElement campoUsuario = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_user")));
            campoUsuario.clear();
            campoUsuario.sendKeys("factory");

            WebElement campoSenha = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_pw")));
            campoSenha.clear();
            campoSenha.sendKeys("f@ct0ry");

            WebElement botaoLogin = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_loginBtn")));
            botaoLogin.click();

            wait.until(ExpectedConditions.urlContains("mainPage.html"));

            // 1. Clicar em "Go To"
            WebElement goToBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a.btn60x22.link_btn[onclick*='modBtnClick(2)']")));
            goToBtn.click();
            Thread.sleep(1500);

            // 2. Clicar em "AMP"
            WebElement exciterDiv = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("smTri_warn")));
            exciterDiv.click();
            Thread.sleep(1500);

            // 4. Pegar o estado do ALC
            WebElement ethDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("pa_stat_alc_stat")));
            Thread.sleep(1500);
            String ethValue = ethDiv.getText();

            // Coloca os valores no resultado para retornar
            resultado.put("status", "Checagem executada com sucesso");
            resultado.put("Status ALC ", ethValue);

        } catch (Exception e) {
            System.err.println("Erro no /funcaoAlc: " + e.toString());
            resultado.put("status", "Erro ao executar Checagem");
            resultado.put("erro", e.toString());
        } finally {
            driver.quit();
            System.out.println("Driver finalizado no /funcaoAlc");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(resultado);
        } catch (Exception e) {
            System.err.println("Erro ao gerar JSON no /funcaoAlc: " + e.toString());
            return "{\"erro\":\"Falha ao gerar JSON\"}";
        }
    }

    @GetMapping("/funcaoModulator")
    public String funcaoModulator(@RequestParam(name = "cidade", required = false) String cidade) {
        System.out.println("=== /funcaoModulator chamado ===");
        System.out.println("Cidade recebida: " + cidade);

        if (cidade == null || cidade.isEmpty()) {
            cidade = "29"; // valor padrão
            System.out.println("Cidade não informada, usando padrão: " + cidade);
        }

        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        System.out.println("URL que será acessada: " + urlBase);

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        //options.addArguments("--headless"); // descomente para rodar sem abrir navegador
        options.addArguments("--incognito");
        options.addArguments("--disable-cache");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        Map<String, Object> resultado = new HashMap<>();

        try {
            driver.get(urlBase);
            driver.manage().window().maximize();

            // Login
            WebElement campoUsuario = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_user")));
            campoUsuario.clear();
            campoUsuario.sendKeys("factory");

            WebElement campoSenha = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_pw")));
            campoSenha.clear();
            campoSenha.sendKeys("f@ct0ry");

            WebElement botaoLogin = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_loginBtn")));
            botaoLogin.click();

            wait.until(ExpectedConditions.urlContains("mainPage.html"));

            // 1. Clicar em "Go To"
            WebElement goToBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a.btn60x22.link_btn[onclick*='modBtnClick(2)']")));
            goToBtn.click();
            Thread.sleep(1500);

            // 2. Clicar em "Exciter"
            WebElement exciterDiv = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div#h_exciter")));
            exciterDiv.click();
            Thread.sleep(1500);

            // 3. Clicar em "ISDB-T Modulator"
            WebElement modulatorLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("estatus_modulator")));
            modulatorLink.click();
            Thread.sleep(1500);

            // 4. Clicar em "IsdbT Config"
            WebElement configLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@class,'btn120_default') and contains(text(),'IsdbT Config')]")));
            configLink.click();
            Thread.sleep(1500);

            // 5. Alterar o select "Input Mode" para value "3"
            WebElement inputModeSelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("isdbt_mode")));
            Select select = new Select(inputModeSelect);

            select.selectByValue("3");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", inputModeSelect);
            Thread.sleep(1500);

            // 6. Alterar o select "Input Mode" para value "2" , volta para a configuração de REMUX
            select.selectByValue("2");
            js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", inputModeSelect);
            Thread.sleep(1500);

            resultado.put("status", "Sequência executada com sucesso");
            resultado.put("inputModeAlterado", "Valor 3 (Async BTS) selecionado e depois voltou para 2 (Remux)");

        } catch (Exception e) {
            System.err.println("Erro no /funcaoRemux: " + e.toString());
            resultado.put("status", "Erro ao executar sequência");
            resultado.put("erro", e.toString());
        } finally {
            driver.quit();
            System.out.println("Driver finalizado no /funcaoRemux");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(resultado);
        } catch (Exception e) {
            System.err.println("Erro ao gerar JSON no /funcaoRemux: " + e.toString());
            return "{\"erro\":\"Falha ao gerar JSON\"}";
        }
    }

    private String extrairParametroSess(String url) {
        try {
            URI uri = new URI(url);
            String query = uri.getQuery();
            if (query == null) return null;
            for (String param : query.split("&")) {
                String[] par = param.split("=");
                if (par.length == 2 && par[0].equals("sess")) {
                    return URLDecoder.decode(par[1], "UTF-8");
                }
            }
        } catch (Exception e) {
            // Ignorar erros e retornar null
        }
        return null;
    }
}
