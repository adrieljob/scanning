package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DashboardController {

    private static final Map<String, String> SESS_MAP = Map.of(
            "29", "1439296833",
            "108", "1424738505",
            "123", "9876543210",
            "456", "1234567890"
    );

    @GetMapping("/dashboard/{cidadeId}")
    public ResponseEntity<String> extrairDadosDashboard(@PathVariable String cidadeId) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        Map<String, Object> resultado = new HashMap<>();

        try {
            String sess = SESS_MAP.get(cidadeId);
            if (sess == null) {
                driver.quit();
                resultado.put("status", "Erro: Cidade não encontrada ou 'sess' não mapeado.");
                resultado.put("erro", "cidadeId '" + cidadeId + "' não possui um 'sess' correspondente no mapa.");
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapper.writeValueAsString(resultado));
            }

            String url = String.format("http://172.17.10.%s:10251/mainPage.html?sess=%s", cidadeId, sess);
            driver.get(url);
            driver.manage().window().maximize();

            // TODO: Extrair dados reais da página aqui
            WebElement elemento = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id_do_elemento")));
            String dado = elemento.getText();

            resultado.put("dadoExtraido", dado);
            resultado.put("status", "Dados extraídos com sucesso para cidade " + cidadeId);

        } catch (Exception e) {
            resultado.put("status", "Erro ao extrair dados para cidade " + cidadeId);
            resultado.put("erro", e.toString());
        } finally {
            driver.quit();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return ResponseEntity.ok(mapper.writeValueAsString(resultado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"erro\":\"Falha ao gerar JSON\"}");
        }
    }

    // Novo endpoint para desligar o alarme PSUs Main Left 24V Not Present
    @PostMapping("/dashboard/{cidadeId}/alarme/psu1/desligar")
    public ResponseEntity<String> desligarAlarmePsu1(@PathVariable String cidadeId) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        Map<String, Object> resultado = new HashMap<>();

        try {
            String sess = SESS_MAP.get(cidadeId);
            if (sess == null) {
                driver.quit();
                resultado.put("status", "Erro: Cidade não encontrada ou 'sess' não mapeado.");
                resultado.put("erro", "cidadeId '" + cidadeId + "' não possui um 'sess' correspondente no mapa.");
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapper.writeValueAsString(resultado));
            }

            String url = String.format("http://172.17.10.%s:10251/mainPage.html?sess=%s", cidadeId, sess);
            driver.get(url);
            driver.manage().window().maximize();

            // Espera o container do alarme estar visível
            WebElement alarmeDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.logSetRow[data-category='Psu1']")
            ));

            WebElement checkboxEnable = alarmeDiv.findElement(By.cssSelector("div[name='enable']"));

            String classes = checkboxEnable.getAttribute("class");
            boolean estaMarcado = classes.contains("checked");

            if (estaMarcado) {
                checkboxEnable.click();

                // Espera até que a classe 'checked' seja removida
                wait.until(ExpectedConditions.not(
                        ExpectedConditions.attributeContains(checkboxEnable, "class", "checked")
                ));
            }

            resultado.put("status", "Alarme PSUs Main Left 24V desligado com sucesso.");
            resultado.put("desligado", estaMarcado);

        } catch (Exception e) {
            resultado.put("status", "Erro ao desligar o alarme para cidade " + cidadeId);
            resultado.put("erro", e.toString());
        } finally {
            driver.quit();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return ResponseEntity.ok(mapper.writeValueAsString(resultado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"erro\":\"Falha ao gerar JSON\"}");
        }
    }
}
