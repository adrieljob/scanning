package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement; // ADICIONADO: Import para WebElement
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.DisposableBean; // Para o cleanup

import com.fasterxml.jackson.databind.ObjectMapper; // ADICIONADO: Import para ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature; // ADICIONADO: Import para SerializationFeature

import java.time.Duration;

@Configuration
public class SeleniumConfig implements DisposableBean {

    private WebDriver driver;

    @Bean
    @Scope("singleton") // Garante que apenas uma instância do driver seja criada
    public WebDriver webDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Rodar sem interface gráfica
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-debugging-port=9222"); // Porta para depuração remota
        options.addArguments("--disable-web-security"); // Cuidado: relaxa a segurança
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36");

        this.driver = new ChromeDriver(options);
        System.out.println("Selenium: WebDriver inicializado.");

        // Realizar o login uma única vez aqui
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            driver.get("https://pdb.nemesys.cloud/dashboards/");
            driver.manage().window().maximize();

            WebElement campoUsuario = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#nemesys\\.username")
            ));
            WebElement campoSenha = driver.findElement(By.cssSelector("#nemesys\\.password"));
            WebElement botaoLogin = driver.findElement(By.cssSelector(
                    "body > div > div.relative.h-screen.w-screen.overflow-hidden > div > div > form > div.card-actions.justify-center"
            ));
            campoUsuario.sendKeys("foccus.ead");
            campoSenha.sendKeys("foccus123");
            botaoLogin.click();

            wait.until(ExpectedConditions.urlContains("dashboards")); // Espera o redirecionamento após o login
            System.out.println("Selenium: Login no Nemesys dashboard realizado com sucesso.");

        } catch (Exception e) {
            System.err.println("Selenium: Erro durante o login inicial: " + e.getMessage());
            // Dependendo da gravidade, você pode querer relançar a exceção ou sair
            // throw new RuntimeException("Falha ao inicializar o driver Selenium com login", e);
        }

        return driver;
    }

    @Bean
    public WebDriverWait webDriverWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    @Override
    public void destroy() {
        if (driver != null) {
            driver.quit();
            System.out.println("Selenium: WebDriver encerrado.");
        }
    }
}