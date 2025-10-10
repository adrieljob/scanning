package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.net.URLDecoder;
import java.time.Duration;
import java.util.*;

@RestController
public class buscaAutomatica {

    private static final Logger logger = LoggerFactory.getLogger(buscaAutomatica.class);
    private final ObjectMapper mapper = new ObjectMapper();  // Compartilhado para eficiência

    // Integração com application.properties
    @Value("${app.selenium.timeout:40}")
    private int seleniumTimeout;

    @Value("${app.selenium.chrome.headless:false}")
    private boolean headless;

    @Value("${app.selenium.chrome.window-size:1920,1080}")
    private String windowSize;

    @Value("${app.selenium.chrome.args:--no-sandbox,--disable-dev-shm-usage,--disable-gpu,--disable-extensions,--incognito,--disable-cache,--disable-blink-features=AutomationControlled,--remote-allow-origins=*}")
    private String chromeArgs;

    @Value("${app.login.username:factory}")
    private String username;

    @Value("${app.login.password:f@ct0ry}")
    private String password;

    @GetMapping("/login")
    public String fazerLogin(@RequestParam(defaultValue = "29") String cidade) {
        logger.info("=== /login chamado para cidade: {} ===", cidade);

        if (cidade == null || cidade.isEmpty()) {
            cidade = "29";
            logger.info("Cidade não informada, usando padrão: {}", cidade);
        }

        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        logger.info("URL que será acessada: {}", urlBase);

        Map<String, Object> resultado = new HashMap<>();
        WebDriver driver = null;
        try {
            driver = setupDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seleniumTimeout));
            String sess = fazerLogin(driver, wait, urlBase);

            resultado.put("status", "Login realizado com sucesso");
            resultado.put("urlAtual", driver.getCurrentUrl());
            resultado.put("sess", sess);

        } catch (Exception e) {
            logger.error("Erro no /login: {}", e.getMessage());
            resultado.put("status", "Erro no login");
            resultado.put("erro", e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
                logger.info("Driver finalizado no /login");
            }
        }

        return gerarJson(resultado);
    }

    @GetMapping("/desligarAlarme")
    public String desligarAlarmePsu1(@RequestParam(defaultValue = "29") String cidade) {
        logger.info("=== /desligarAlarme chamado para cidade: {} ===", cidade);

        if (cidade == null || cidade.isEmpty()) {
            cidade = "29";
            logger.info("Cidade não informada, usando padrão: {}", cidade);
        }

        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        logger.info("URL que será acessada: {}", urlBase);

        Map<String, Object> resultado = new HashMap<>();
        WebDriver driver = null;
        try {
            driver = setupDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seleniumTimeout));
            fazerLogin(driver, wait, urlBase);

            // Sequência para desligar o alarme PSU1
            WebElement eventLogBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#event_button > a.eventLog.btn100x40_fault")));
            eventLogBtn.click();

            WebElement setupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#btnLogSetup > a#faultlogSet")));
            setupBtn.click();

            // Wait em vez de Thread.sleep(1500)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logCfgTable")));

            WebElement checkboxEnable = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(
                    "#logCfgTable > div.tableContent > div:nth-child(5) > div.col1.thickBrdrBoth > div.checkbox.wp[name='enable']"
            )));

            String classes = checkboxEnable.getAttribute("class");
            boolean estaMarcado = classes.contains("checked");

            if (estaMarcado) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", checkboxEnable);

                wait.until(ExpectedConditions.not(
                        ExpectedConditions.attributeContains(checkboxEnable, "class", "checked")
                ));

                resultado.put("alarmePsu1", "Desligado com sucesso");
            } else {
                resultado.put("alarmePsu1", "Já estava desligado");
            }
            resultado.put("status", "Sucesso");

        } catch (Exception e) {
            logger.error("Erro no /desligarAlarme: {}", e.getMessage());
            resultado.put("status", "Erro ao desligar alarme");
            resultado.put("erro", e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
                logger.info("Driver finalizado no /desligarAlarme");
            }
        }

        return gerarJson(resultado);
    }

    @GetMapping("/alarmess")
    public String buscarAlarmesAtivos(@RequestParam(defaultValue = "29") String cidade) {
        logger.info("=== /alarmess chamado para cidade: {} ===", cidade);

        if (cidade == null || cidade.isEmpty()) {
            cidade = "29";
            logger.info("Cidade não informada, usando padrão: {}", cidade);
        }

        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        logger.info("URL que será acessada: {}", urlBase);

        Map<String, Object> resultado = new HashMap<>();
        WebDriver driver = null;
        try {
            driver = setupDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seleniumTimeout));
            fazerLogin(driver, wait, urlBase);

            WebElement eventLogBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#event_button > a.eventLog.btn100x40_fault")));
            eventLogBtn.click();

            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("log_content")));

            List<WebElement> alarmesAtivos = driver.findElements(By.cssSelector("#log_content > div.c1[class*='ACT']"));

            List<Map<String, String>> listaAlarmes = new ArrayList<>();

            for (WebElement alarme : alarmesAtivos) {
                String classe = alarme.getAttribute("class");
                String tipo = "";
                if (classe.contains("WarningACT")) {
                    tipo = "Warning";
                } else if (classe.contains("FaultACT")) {
                    tipo = "Fault";
                } else {
                    continue;
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
            logger.error("Erro no /alarmess: {}", e.getMessage());
            resultado.put("status", "Erro ao buscar alarmes");
            resultado.put("erro", e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
                logger.info("Driver finalizado no /alarmess");
            }
        }

        return gerarJson(resultado);
    }

    @GetMapping("/alarmessSetupFaults")
    public String buscarAlarmeSetupEspecifico(@RequestParam(defaultValue = "29") String cidade) {
        logger.info("=== /alarmessSetupFaults chamado (focado no alarme específico) para cidade: {} ===", cidade);

        if (cidade == null || cidade.isEmpty()) {
            cidade = "29";
            logger.info("Cidade não informada, usando padrão: {}", cidade);
        }

        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        logger.info("URL que será acessada: {}", urlBase);

        Map<String, Object> resultado = new HashMap<>();
        WebDriver driver = null;
        try {
            driver = setupDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seleniumTimeout));
            fazerLogin(driver, wait, urlBase);

            WebElement eventLogBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#event_button > a.eventLog.btn100x40_fault")));
            eventLogBtn.click();

            WebElement setupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#btnLogSetup > a#faultlogSet")));
            setupBtn.click();

            // Wait em vez de Thread.sleep(5000)
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#logCfgTable > div.tableContent > div:nth-child(5)")
            ));

            WebElement linhaEspecifica = driver.findElement(
                    By.cssSelector("#logCfgTable > div.tableContent > div:nth-child(5)")
            );

            String style = linhaEspecifica.getAttribute("style");
            if (style != null && style.contains("display: none")) {
                resultado.put("status", "Linha específica está oculta");
                resultado.put("fault", Collections.emptyMap());
            } else {
                WebElement statusElem = linhaEspecifica.findElement(By.cssSelector("div.col3.thickBrdrRt > div[name='status']"));
                String classesLed = statusElem.getAttribute("class");

                WebElement msgElem = linhaEspecifica.findElement(By.cssSelector("div.col2[name='message']"));
                String mensagem = msgElem.getText();

                String categoria = linhaEspecifica.getAttribute("data-category");
                if (categoria == null) categoria = "";

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
            logger.error("Erro no /alarmessSetupFaults: {}", e.getMessage());
            resultado.put("status", "Erro ao buscar alarme específico");
            resultado.put("erro", e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
                logger.info("Driver finalizado no /alarmessSetupFaults");
            }
        }

        return gerarJson(resultado);
    }

    @GetMapping("/funcaoRemux")
    public String funcaoRemux(@RequestParam(defaultValue = "29") String cidade) {
        logger.info("=== /funcaoRemux chamado para cidade: {} ===", cidade);

        if (cidade == null || cidade.isEmpty()) {
            cidade = "29";
            logger.info("Cidade não informada, usando padrão: {}", cidade);
        }

        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        logger.info("URL que será acessada: {}", urlBase);

        Map<String, Object> resultado = new HashMap<>();
        WebDriver driver = null;
        try {
            driver = setupDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seleniumTimeout));
            fazerLogin(driver, wait, urlBase);

            // 1. Clicar em "Go To"
            WebElement goToBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a.btn60x22.link_btn[onclick*='modBtnClick(2)']")));
            goToBtn.click();
            // Wait em vez de Thread.sleep(1500)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#h_exciter")));

            // 2. Clicar em "Exciter"
            WebElement exciterDiv = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div#h_exciter")));
            exciterDiv.click();
            // Wait em vez de Thread.sleep(1500)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("estatus_modulator")));

            // 3. Clicar em "ISDB-T Modulator"
            WebElement modulatorLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("estatus_modulator")));
            modulatorLink.click();
            // Wait em vez de Thread.sleep(1500)
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[contains(@class,'btn120_default') and contains(text(),'IsdbT Config')]")));

            // 4. Clicar em "IsdbT Config"
            WebElement configLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@class,'btn120_default') and contains(text(),'IsdbT Config')]")));
            configLink.click();
            // Wait em vez de Thread.sleep(1500)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("isdbt_mode")));

            // 5. Alterar o select "Input Mode" para value "3"
            WebElement inputModeSelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("isdbt_mode")));
            Select select = new Select(inputModeSelect);
            select.selectByValue("3");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", inputModeSelect);
            // Wait em vez de Thread.sleep(1500)
            wait.until(ExpectedConditions.attributeToBe(inputModeSelect, "value", "3"));

            // 6. Alterar o select "Input Mode" para value "2"
            select.selectByValue("2");
            js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", inputModeSelect);
            // Wait em vez de Thread.sleep(1500)
            wait.until(ExpectedConditions.attributeToBe(inputModeSelect, "value", "2"));

            resultado.put("status", "Sequência executada com sucesso");
            resultado.put("inputModeAlterado", "Valor 3 (Async BTS) selecionado e depois voltou para 2 (Remux)");

        } catch (Exception e){logger.error("Erro no /funcaoRemux: {}", e.getMessage());
            resultado.put("status", "Erro ao executar sequência");
            resultado.put("erro", e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
                logger.info("Driver finalizado no /funcaoRemux");
            }
        }

        return gerarJson(resultado);
    }

    @GetMapping("/funcaoTaxa")
    public String funcaoTaxa(@RequestParam(defaultValue = "29") String cidade) {
        logger.info("=== /funcaoTaxa chamado para cidade: {} ===", cidade);

        if (cidade == null || cidade.isEmpty()) {
            cidade = "29";
            logger.info("Cidade não informada, usando padrão: {}", cidade);
        }

        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        logger.info("URL que será acessada: {}", urlBase);

        Map<String, Object> resultado = new HashMap<>();
        WebDriver driver = null;
        try {
            driver = setupDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seleniumTimeout));
            fazerLogin(driver, wait, urlBase);

            // 1. Clicar em "Go To"
            WebElement goToBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a.btn60x22.link_btn[onclick*='modBtnClick(2)']")));
            goToBtn.click();
            // Wait em vez de Thread.sleep(1500)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#h_exciter")));

            // 2. Clicar em "Exciter"
            WebElement exciterDiv = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div#h_exciter")));
            exciterDiv.click();
            // Wait em vez de Thread.sleep(1500)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("estatus_inputs")));

            // 3. Clicar em "Input"
            WebElement inputLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("estatus_inputs")));
            inputLink.click();
            // Wait em vez de Thread.sleep(1500)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("in_stat_inputs_br_1")));

            // 4. Pegar o valor da taxa de ASI e ETH
            WebElement ethDiv = driver.findElement(By.id("in_stat_inputs_br_1"));
            String ethValue = ethDiv.getText();

            WebElement asiDiv = driver.findElement(By.id("in_stat_inputs_br_3"));
            String asiValue = asiDiv.getText();

            resultado.put("status", "Sequência executada com sucesso");
            resultado.put("taxa_ETH", ethValue);
            resultado.put("taxa_ASI", asiValue);

        } catch (Exception e) {
            logger.error("Erro no /funcaoTaxa: {}", e.getMessage());
            resultado.put("status", "Erro ao executar sequência");
            resultado.put("erro", e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
                logger.info("Driver finalizado no /funcaoTaxa");
            }
        }

        return gerarJson(resultado);
    }

    @GetMapping("/funcaoPids")
    public String funcaoPids(@RequestParam(defaultValue = "29") String cidade) {
        logger.info("=== /funcaoPids chamado para cidade: {} ===", cidade);

        if (cidade == null || cidade.isEmpty()) {
            cidade = "29";
            logger.info("Cidade não informada, usando padrão: {}", cidade);
        }

        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        logger.info("URL que será acessada: {}", urlBase);

        Map<String, Object> resultado = new HashMap<>();
        WebDriver driver = null;
        try {
            driver = setupDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seleniumTimeout));
            fazerLogin(driver, wait, urlBase);

            // 1. Clicar em "Go To"
            WebElement goToBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a.btn60x22.link_btn[onclick*='modBtnClick(2)']")));
            goToBtn.click();
            // Wait em vez de Thread.sleep(1500)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#h_exciter")));

            // 2. Clicar em "Exciter"
            WebElement exciterDiv = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div#h_exciter")));
            exciterDiv.click();
            // Wait em vez de Thread.sleep(1500)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("estatus_inputs")));

            // 3. Clicar em "Input"
            WebElement inputLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("estatus_inputs")));
            inputLink.click();
            // Wait em vez de Thread.sleep(1500)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ts_stats_btn")));

            // 4. Clicar em "TS Stats"
            WebElement tsStatsBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("ts_stats_btn")));
            tsStatsBtn.click();
            // Wait em vez de Thread.sleep(2000)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("psi_info_tbl")));

            // 5. Aguardar o container dos PIDs estar presente
            WebElement psiInfoContainer = driver.findElement(By.id("psi_info_tbl"));

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
            logger.error("Erro no /funcaoPids: {}", e.getMessage());
            resultado.put("status", "Erro ao executar sequência");
            resultado.put("erro", e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
                logger.info("Driver finalizado no /funcaoPids");
            }
        }

        return gerarJson(resultado);
    }

    @GetMapping("/funcaoAlc")
    public String funcaoAlc(@RequestParam(defaultValue = "29") String cidade) {
        logger.info("=== /funcaoAlc chamado para cidade: {} ===", cidade);

        if (cidade == null || cidade.isEmpty()) {
            cidade = "29";
            logger.info("Cidade não informada, usando padrão: {}", cidade);
        }

        String urlBase = "http://172.17.10." + cidade + ":10251/login.html";
        logger.info("URL que será acessada: {}", urlBase);

        Map<String, Object> resultado = new HashMap<>();
        WebDriver driver = null;
        try {
            driver = setupDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seleniumTimeout));
            fazerLogin(driver, wait, urlBase);

            // 1. Clicar em "Go To"
            WebElement goToBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a.btn60x22.link_btn[onclick*='modBtnClick(2)']")));
            goToBtn.click();
            // Wait para o próximo elemento
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("smTri_warn")));

            // 2. Clicar em "AMP" (corrigido selector: assumindo classe; ajuste se for ID ou XPath)
            WebElement ampBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("smTri_warn")));
            ampBtn.click();
            // Wait em vez de sleep implícito
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pa_stat_alc_stat")));

            // 3. Pegar o estado do ALC
            WebElement alcElem = driver.findElement(By.id("pa_stat_alc_stat"));
            String alcStatus = alcElem.getText();

            resultado.put("status", "Checagem executada com sucesso");
            resultado.put("statusAlc", alcStatus);  // Corrigido: sem espaço extra

        } catch (Exception e) {
            logger.error("Erro no /funcaoAlc: {}", e.getMessage());
            resultado.put("status", "Erro ao executar Checagem");
            resultado.put("erro", e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
                logger.info("Driver finalizado no /funcaoAlc");
            }
        }

        return gerarJson(resultado);
    }

    // Método privado para setup do driver (reutilizável em todos os endpoints)
    private WebDriver setupDriver() {
        logger.info("Configurando WebDriver...");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments(chromeArgs.split(","));
        if (headless) {
            options.addArguments("--headless=new");  // Mais estável que --headless antigo
        }
        options.addArguments("--window-size=" + windowSize);
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");  // Anti-detecção
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        // Anti-detecção extra
        ((JavascriptExecutor) driver).executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        return driver;
    }

    // Método privado para login (reutilizável)
    private String fazerLogin(WebDriver driver, WebDriverWait wait, String urlBase) {
        logger.info("Iniciando login para URL: {}", urlBase);
        try {
            driver.get(urlBase);
            driver.manage().window().maximize();

            WebElement campoUsuario = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_user")));
            campoUsuario.clear();
            campoUsuario.sendKeys(username);

            WebElement campoSenha = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_pw")));
            campoSenha.clear();
            campoSenha.sendKeys(password);

            WebElement botaoLogin = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_loginBtn")));
            botaoLogin.click();

            wait.until(ExpectedConditions.urlContains("mainPage.html"));
            String urlAtual = driver.getCurrentUrl();
            String sess = extrairParametroSess(urlAtual);
            logger.info("Login realizado. Sess: {}", sess);
            return sess;
        } catch (Exception e) {
            logger.error("Erro no login: {}", e.getMessage());
            throw e;  // Propaga erro para o endpoint
        }
    }

    // Método privado para gerar JSON (reutilizável)
    private String gerarJson(Map<String, Object> resultado) {
        try {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(resultado);
        } catch (Exception e) {
            logger.error("Erro ao gerar JSON: {}", e.getMessage());
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
            logger.warn("Erro ao extrair sess: {}", e.getMessage());
        }
        return null;
    }
}

