package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.DisposableBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.time.Duration;

@Configuration
public class SeleniumConfig implements DisposableBean {

    private WebDriver driver;  // Mantido para cleanup futuro, se precisar

    // COMENTADO: WebDriver global removido (evita crash no boot)
    /*
    @Bean
    @Scope("singleton")
    public WebDriver webDriver() {
        // ... código anterior ...
    }
    */

    // COMENTADO/REMOVIDO: webDriverWait depende do WebDriver bean (não precisamos global)
    // Spring não encontra WebDriver para injetar, causando o erro atual
    /*
    @Bean
    public WebDriverWait webDriverWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(30));
    }
    */

    // Bean para ObjectMapper (mantido: útil globalmente para JSON indentado)
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