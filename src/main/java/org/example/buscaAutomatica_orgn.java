/*
package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.*;

@RestController
public class buscaAutomatica_orgn {

    @GetMapping("/urlsCidadesBusca")
    public String buscarCidades() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        List<String> cidades = Arrays.asList(
                "PR - Cafeara",
                "PR - Siqueira Campos",
                "SC - Rio Fortuna",
                "BA - Igrapiuna",
                "BA - Igapora",
                "BA - Ibitiara",
                "BA - Ibirapitanga",
                "PR - Cambara",
                "PR - Campina da Lagoa",
                "BA - Gloria",
                "PR - Campo Bonito",
                "PR - Capanema",
                "AL - Agua Branca",
                "PE - Orobo",
                "PR - Boa Ventura de Sao Roque",
                "PR - Tibagi",
                "SC - Angelina",
                "AL - Campo Alegre",
                "PR - Ariranha do Ivai",
                "PR - Arapoti",
                "AL - Canapi",
                "SC - Salete",
                "PR - Cidade Gaucha",
                "BA - Jussari",
                "SC - Sao Martinho",
                "AL - Joaquim Gomes",
                "AL - Maravilha",
                "PR - Tres Barras do Parana",
                "RS - Casca",
                "PR - Ubirata",
                "BA - Elisio Medrado",
                "RJ - Bom Jardim",
                "PR - Andira",
                "PR - Ampere",
                "RJ - Bom Jesus do Itabapoana",
                "RJ - Casimiro de Abreu",
                "SC - Apiuna",
                "SC - Vargem Bonita",
                "PR - Doutor Ulysses",
                "PR - Figueira",
                "BA - Curaca",
                "PR - Formosa do Oeste",
                "SC - Capinzal",
                "SC - Caxambu do Sul",
                "SC - Coronel Freitas",
                "PR - Guapirama",
                "PR - Guaraniacu",
                "SC - Vidal Ramos",
                "SC - Vitor Meireles",
                "AL - Sao Bras",
                "RJ - Natividade",
                "AL - Sao Jose da Laje",
                "RS - Cacique Doble",
                "AL - Sao Jose da Tapera",
                "PE - Jurema",
                "PE - Parnamirim",
                "PE - Passira",
                "RS - Ibiaca",
                "BA - Castro Alves",
                "RS - Ibiraiaras",
                "SC - Descanso",
                "SC - Dona Emma",
                "BA - Capela do Alto Alegre",
                "RS - Independencia",
                "PE - Aguas Belas",
                "RS - Manoel Viana",
                "SE - Capela",
                "PE - Pombos",
                "BA - Maetinga",
                "SC - Erval Velho",
                "BA - Wagner",
                "PE - Altinho",
                "PE - Amaraji",
                "RS - Porto Lucena",
                "PE - Barra de Guabiraba",
                "RS - Progresso",
                "PE - Barreiros",
                "BA - Varzedo",
                "RS - Putinga",
                "RS - Roca Sales",
                "PR - Imbau",
                "AL - Sao Sebastiao",
                "AL - Tanque d Arca",
                "PE - Jupi",
                "AL - Teotonio Vilela",
                "RS - Santa Barbara do Sul",
                "PE - Jucati",
                "PE - Belem do Sao Francisco",
                "PE - Brejao",
                "RS - Sao Jose do Herval",
                "RS - Tapera",
                "PR - Iretama",
                "BA - Ubaira",
                "RS - Tupancireta",
                "PR - Ivai",
                "PE - Joao Alfredo",
                "PR - Joaquim Tavora",
                "SC - Ipira",
                "SC - Ipora do Oeste",
                "PE - Jatoba",
                "BA - Agua Fria",
                "RS - Vila Flores",
                "PE - Cachoeirinha",
                "SC - Agrolandia",
                "PE - Calcado",
                "PE - Sanharo",
                "PE - Santa Maria do Cambuca",
                "PE - Santa Terezinha",
                "PE - Capoeiras",
                "SC - Jaguaruna",
                "SE - Neopolis",
                "PE - Condado",
                "PE - Cortes",
                "PR - Paraiso do Norte",
                "PR - Pien",
                "BA - Muquem de Sao Francisco",
                "BA - Nilo Pecanha",
                "BA - Aracas",
                "PE - Xexeu",
                "BA - Nova Redencao",
                "PE - Itacuruba",
                "BA - Arataca",
                "PE - Cumaru",
                "PR - Pinhao",
                "SC - Nova Erechim",
                "PR - Planalto",
                "PE - Sao Joaquim do Monte",
                "SC - Papanduva",
                "SE - Riachao do Dantas",
                "BA - Barrocas",
                "SC - Peritiba",
                "SC - Pinheiro Preto",
                "BA - Pojuca",
                "SC - Piratuba",
                "BA - Ponto Novo",
                "BA - Presidente Tancredo Neves",
                "PR - Querencia do Norte",
                "PE - Custodia",
                "PE - Exu",
                "SC - Rio do Campo",
                "ES - Pinheiros",
                "SC - Rio do Oeste",
                "BA - Belmonte",
                "BA - Sao Jose da Vitoria",
                "PE - Gameleira",
                "PE - Gravata",
                "PR - Rio Bonito do Iguacu",
                "PE - Ibimirim",
                "PE - Sao Jose do Belmonte",
                "PR - Salto do Lontra",
                "PE - Sao Jose do Egito",
                "PR - Santo Inacio",
                "PR - Sao Joao do Caiua",
                "PE - Terezinha",
                "PE - Toritama",
                "PE - Trindade",
                "ES - Dores do Rio Preto",
                "PE - Tuparetama",
                "ES - Ecoporanga",
                "ES - Ibiracu",
                "BA - Serra Dourada",
                "BA - Jitauna",
                "BA - Itapitanga",
                "BA - Itaju do Colonia",
                "SC - Anchieta",
                "AL - Cajueiro",
                "AL - Igaci",
                "AL - Igreja Nova",
                "AL - Jequia da Praia",
                "PR - Wenceslau Braz",
                "SC - Benedito Novo",
                "BA - Crisopolis",
                "RS - Cambara do Sul",
                "BA - Coaraci",
                "BA - Cipo",
                "PE - Panelas",
                "BA - Chorrocho",
                "SE - Boquim",
                "SE - Brejo Grande",
                "BA - Lapao",
                "PE - Pocao",
                "PE - Primavera",
                "PE - Quipapa",
                "BA - Malhada de Pedras",
                "AL - Sao Miguel dos Milagres",
                "PE - Betania",
                "PE - Bonito",
                "RS - Sao Vicente do Sul",
                "SE - Japoata",
                "PR - Laranjal",
                "PE - Cabrobo",
                "PE - Carnaiba",
                "PE - Carnaubeira da Penha",
                "PR - Nova Cantu",
                "PR - Pinhalao",
                "BA - Mulungu do Morro",
                "BA - Antonio Goncalves",
                "PE - Itaiba",
                "PR - Pranchita",
                "PE - Vertentes",
                "SE - Ribeiropolis",
                "ES - Anchieta",
                "ES - Apiaca",
                "ES - Atilio Vivacqua",
                "SE - Umbauba",
                "BA - Cachoeira",
                "ES - Piuma",
                "BA - Sao Felix do Coribe",
                "PE - Ibirajuba",
                "PE - Sertania",
                "BA - Tanquinho",
                "PE - Tacaimbo",
                "PE - Triunfo",
                "ES - Conceicao do Castelo"
        );

        List<Map<String, String>> resultados = new ArrayList<>();

        try {
            driver.get("https://pdb.nemesys.cloud/dashboards/");
            driver.manage().window().maximize();

            // --- LOGIN ---
            WebElement campoUsuario = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#nemesys\\.username")
            ));
            WebElement campoSenha = driver.findElement(By.cssSelector("#nemesys\\.password"));
            WebElement botaoLogin = driver.findElement(By.cssSelector(
                    "body > div > div.relative.h-screen.w-screen.overflow-hidden > div > div > form > div.card-actions.justify-center"
            ));
            campoUsuario.sendKeys("foccus.ead");
            campoSenha.sendKeys("foccus123");
            botaoLogin.cl   ick();

            // Espera a página inicial carregar
            Thread.sleep(5000);

            for (String cidade : cidades) {
                Map<String, String> dado = new HashMap<>();
                dado.put("cidade", cidade);

                try {
                    // Espera a página carregar o input
                    Thread.sleep(2000);

                    // Espera longa para o input estar clicável
                    WebDriverWait waitLong = new WebDriverWait(driver, Duration.ofSeconds(20));
                    WebElement barraBusca = waitLong.until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("body > div:nth-child(1) > div.relative.h-screen.w-screen.overflow-hidden > div > div.flex.flex-col.relative.h-screen.w-full.overflow-visble > div.flex.w-full.h-full.overflow-hidden > div.h-full.w-full.overflow-auto.bg-base-200\\/25 > div > form > div.flex.space-x-2.items-end.flex-grow.w-full > div > div > div:nth-child(1) > input")
                    ));

                    // Digita a cidade e dá ENTER
                    barraBusca.click();
                    barraBusca.clear();
                    barraBusca.sendKeys(cidade);
                    barraBusca.sendKeys(Keys.ENTER);

                    // Aguarda o primeiro resultado aparecer
                    WebElement primeiroResultado = waitLong.until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("table.table tbody tr:first-child td a")
                    ));

                    String href = primeiroResultado.getAttribute("href");
                    dado.put("urlCompleta", href);

                } catch (Exception e) {
                    dado.put("urlCompleta", "N/A");
                }

                resultados.add(dado);

                Thread.sleep(1000); // pausa entre buscas
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

// Retorna JSON bonito
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(resultados);
        } catch (Exception e) {
            return "[{\"erro\":\"Falha ao gerar JSON\"}]";
        }

    }
}
*/