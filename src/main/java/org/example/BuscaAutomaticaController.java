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
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.transaction.annotation.Transactional;
import org.openqa.selenium.TimeoutException;

import java.util.Collections;
import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping("/") // Mapeia a raiz para este controller, ou ajuste conforme sua necessidade
public class BuscaAutomaticaController { // Renomeado de buscaAutomatica_orgn

    @Autowired
    private VarreduraRepository varreduraRepository;

    // Métodos auxiliares para mapeamento de cidades e IPs
    private String converterCidadeParaNumero(String nomeCidade) {
        Map<String, String> mapeamentoCidades = Map.ofEntries(
                Map.entry("PR - Cafeara", "1312"),
                Map.entry("PR - Siqueira Campos", "1432"),
                Map.entry("PR - Cambara", "1329"),
                Map.entry("PR - Campina da Lagoa", "410"),
                Map.entry("PR - Campo Bonito", "411"),
                Map.entry("PR - Capanema", "885"),
                Map.entry("PR - Boa Ventura de Sao Roque", "1339"),
                Map.entry("PR - Tibagi", "1403"),
                Map.entry("PR - Ariranha do Ivai", "1328"),
                Map.entry("PR - Arapoti", "1338"),
                Map.entry("PR - Cidade Gaucha", "419"),
                Map.entry("PR - Tres Barras do Parana", "1404"),
                Map.entry("PR - Ubirata", "1405"),
                Map.entry("PR - Andira", "406"),
                Map.entry("PR - Ampere", "405"),
                Map.entry("PR - Doutor Ulysses", "1299"),
                Map.entry("PR - Figueira", "1350"),
                Map.entry("PR - Formosa do Oeste", "1336"),
                Map.entry("PR - Guapirama", "1382"),
                Map.entry("PR - Guaraniacu", "1383"),
                Map.entry("PR - Imbau", "1351"),
                Map.entry("PR - Iretama", "1146"),
                Map.entry("PR - Ivai", "425"),
                Map.entry("PR - Joaquim Tavora", "1385"),
                Map.entry("PR - Nova Cantu", "1388"),
                Map.entry("PR - Paraiso do Norte", "1389"),
                Map.entry("PR - Pien", "2556"),
                Map.entry("PR - Pinhao", "438"),
                Map.entry("PR - Pinhalao", "437"),
                Map.entry("PR - Planalto", "439"),
                Map.entry("PR - Pranchita", "440"),
                Map.entry("PR - Querencia do Norte", "443"),
                Map.entry("PR - Rio Bonito do Iguacu", "448"),
                Map.entry("PR - Salto do Lontra", "451"),
                Map.entry("PR - Santo Inacio", "1398"),
                Map.entry("PR - Sao Joao do Caiua", "1399"),
                Map.entry("PR - Wenceslau Braz", "456"),
                Map.entry("PR - Laranjal", "1355"),
                Map.entry("PR - Santa Maria do Oeste", "453"),
                // Adicione outras cidades do seu HTML aqui, seguindo o padrão "UF - Nome da Cidade"
                Map.entry("SC - Rio Fortuna", "529"),
                Map.entry("BA - Igrapiuna", "397"),
                Map.entry("BA - Igapora", "994"),
                Map.entry("BA - Ibitiara", "389"),
                Map.entry("BA - Ibirapitanga", "1452"),
                Map.entry("BA - Gloria", "1464"),
                Map.entry("AL - Agua Branca", "298"),
                Map.entry("PE - Orobo", "288"),
                Map.entry("SC - Angelina", "500"),
                Map.entry("AL - Campo Alegre", "2802"),
                Map.entry("AL - Canapi", "263"),
                Map.entry("SC - Salete", "532"),
                Map.entry("BA - Jussari", "1131"),
                Map.entry("SC - Sao Martinho", "1289"),
                Map.entry("AL - Joaquim Gomes", "1278"),
                Map.entry("AL - Maravilha", "280"),
                Map.entry("BA - Elisio Medrado", "387"),
                Map.entry("RJ - Bom Jardim", "1134"),
                Map.entry("RJ - Bom Jesus do Itabapoana", "1984"),
                Map.entry("RJ - Casimiro de Abreu", "1982"),
                Map.entry("SC - Apiuna", "503"),
                Map.entry("SC - Vargem Bonita", "1284"),
                Map.entry("BA - Curaca", "376"),
                Map.entry("SC - Capinzal", "508"),
                Map.entry("SC - Caxambu do Sul", "1361"),
                Map.entry("SC - Coronel Freitas", "919"),
                Map.entry("SC - Vidal Ramos", "413"),
                Map.entry("SC - Vitor Meireles", "407"),
                Map.entry("AL - Sao Bras", "287"),
                Map.entry("RJ - Natividade", "2806"),
                Map.entry("AL - Sao Jose da Laje", "1502"),
                Map.entry("RS - Cacique Doble", "1410"),
                Map.entry("AL - Sao Jose da Tapera", "1273"),
                Map.entry("PE - Jurema", "275"),
                Map.entry("PE - Parnamirim", "1521"),
                Map.entry("PE - Passira", "291"),
                Map.entry("RS - Ibiaca", "476"),
                Map.entry("BA - Castro Alves", "352"),
                Map.entry("RS - Ibiraiaras", "477"),
                Map.entry("SC - Descanso", "509"),
                Map.entry("SC - Dona Emma", "1371"),
                Map.entry("BA - Capela do Alto Alegre", "358"),
                Map.entry("RS - Independencia", "478"),
                Map.entry("PE - Aguas Belas", "209"),
                Map.entry("RS - Manoel Viana", "481"),
                Map.entry("SE - Capela", "1493"),
                Map.entry("PE - Pombos", "1455"),
                Map.entry("BA - Maetinga", "353"),
                Map.entry("SC - Erval Velho", "510"),
                Map.entry("BA - Wagner", "377"),
                Map.entry("PE - Altinho", "1501"),
                Map.entry("PE - Amaraji", "1180"),
                Map.entry("RS - Porto Lucena", "692"),
                Map.entry("PE - Barra de Guabiraba", "2804"),
                Map.entry("RS - Progresso", "3652"),
                Map.entry("PE - Barreiros", "1293"),
                Map.entry("BA - Varzedo", "3092"),
                Map.entry("RS - Putinga", "1420"),
                Map.entry("RS - Roca Sales", "5026"),
                Map.entry("AL - Sao Sebastiao", "1316"),
                Map.entry("AL - Tanque d Arca", "N/A"), // N/A no HTML, manter assim
                Map.entry("PE - Jupi", "273"),
                Map.entry("AL - Teotonio Vilela", "1319"),
                Map.entry("RS - Santa Barbara do Sul", "491"),
                Map.entry("PE - Jucati", "2858"),
                Map.entry("PE - Belem do Sao Francisco", "1440"),
                Map.entry("PE - Brejao", "223"),
                Map.entry("RS - Sao Jose do Herval", "3692"),
                Map.entry("RS - Tapera", "10610"),
                Map.entry("BA - Ubaira", "366"),
                Map.entry("RS - Tupancireta", "1424"),
                Map.entry("PE - Joao Alfredo", "270"),
                Map.entry("SC - Ipira", "1357"),
                Map.entry("SC - Ipora do Oeste", "1327"),
                Map.entry("PE - Jatoba", "1143"),
                Map.entry("BA - Agua Fria", "359"),
                Map.entry("RS - Vila Flores", "1425"),
                Map.entry("PE - Cachoeirinha", "1165"),
                Map.entry("SC - Agrolandia", "1427"),
                Map.entry("PE - Calcado", "1977"),
                Map.entry("PE - Sanharo", "1477"),
                Map.entry("PE - Santa Maria do Cambuca", "N/A"), // N/A no HTML, manter assim
                Map.entry("PE - Santa Terezinha", "1480"),
                Map.entry("PE - Capoeiras", "239"),
                Map.entry("SC - Jaguaruna", "1978"),
                Map.entry("SE - Neopolis", "245"),
                Map.entry("PE - Condado", "1446"),
                Map.entry("PE - Cortes", "1363"),
                Map.entry("BA - Muquem de Sao Francisco", "333"),
                Map.entry("BA - Nilo Pecanha", "1250"),
                Map.entry("BA - Aracas", "357"),
                Map.entry("PE - Xexeu", "2864"),
                Map.entry("BA - Nova Redencao", "326"),
                Map.entry("PE - Itacuruba", "1522"),
                Map.entry("BA - Arataca", "371"),
                Map.entry("PE - Cumaru", "254"),
                Map.entry("SC - Nova Erechim", "523"),
                Map.entry("PE - Sao Joaquim do Monte", "1433"),
                Map.entry("SC - Papanduva", "1133"),
                Map.entry("SE - Riachao do Dantas", "246"),
                Map.entry("BA - Barrocas", "363"),
                Map.entry("SC - Peritiba", "402"),
                Map.entry("SC - Pinheiro Preto", "527"),
                Map.entry("BA - Pojuca", "1271"),
                Map.entry("SC - Piratuba", "1317"),
                Map.entry("BA - Ponto Novo", "391"),
                Map.entry("BA - Presidente Tancredo Neves", "393"),
                Map.entry("PE - Custodia", "256"),
                Map.entry("PE - Exu", "1511"),
                Map.entry("SC - Rio do Campo", "1305"),
                Map.entry("ES - Pinheiros", "549"),
                Map.entry("SC - Rio do Oeste", "2262"),
                Map.entry("BA - Belmonte", "336"),
                Map.entry("BA - Sao Jose da Vitoria", "2297"),
                Map.entry("PE - Gameleira", "1478"),
                Map.entry("PE - Gravata", "8099"),
                Map.entry("PE - Ibimirim", "1517"),
                Map.entry("PE - Sao Jose do Belmonte", "307"),
                Map.entry("PE - Sao Jose do Egito", "1529"),
                Map.entry("PE - Terezinha", "1491"),
                Map.entry("PE - Toritama", "1492"),
                Map.entry("PE - Trindade", "1495"),
                Map.entry("ES - Dores do Rio Preto", "1136"),
                Map.entry("PE - Tuparetama", "1498"),
                Map.entry("ES - Ecoporanga", "2742"),
                Map.entry("ES - Ibiracu", "1207"),
                Map.entry("BA - Serra Dourada", "310"),
                Map.entry("BA - Jitauna", "368"),
                Map.entry("BA - Itapitanga", "400"),
                Map.entry("BA - Itaju do Colonia", "384"),
                Map.entry("SC - Anchieta", "1428"),
                Map.entry("AL - Cajueiro", "1310"),
                Map.entry("AL - Igaci", "1505"),
                Map.entry("AL - Igreja Nova", "300"),
                Map.entry("AL - Jequia da Praia", "266"),
                Map.entry("SC - Benedito Novo", "1902"),
                Map.entry("BA - Crisopolis", "1451"),
                Map.entry("RS - Cambara do Sul", "539"),
                Map.entry("BA - Coaraci", "1449"),
                Map.entry("BA - Cipo", "1471"),
                Map.entry("PE - Panelas", "2809"),
                Map.entry("BA - Chorrocho", "322"),
                Map.entry("SE - Boquim", "1219"),
                Map.entry("SE - Brejo Grande", "243"),
                Map.entry("BA - Lapao", "1457"),
                Map.entry("PE - Pocao", "1075"),
                Map.entry("PE - Primavera", "1459"),
                Map.entry("PE - Quipapa", "1466"),
                Map.entry("BA - Malhada de Pedras", "341"),
                Map.entry("AL - Sao Miguel dos Milagres", "274"),
                Map.entry("PE - Betania", "221"),
                Map.entry("PE - Bonito", "1441"),
                Map.entry("RS - Sao Vicente do Sul", "10796"),
                Map.entry("SE - Japoata", "1258"),
                Map.entry("PE - Cabrobo", "1445"),
                Map.entry("PE - Carnaiba", "5318"),
                Map.entry("PE - Carnaubeira da Penha", "250"),
                Map.entry("BA - Mulungu do Morro", "342"),
                Map.entry("BA - Antonio Goncalves", "343"),
                Map.entry("PE - Itaiba", "542"),
                Map.entry("PE - Vertentes", "1474"),
                Map.entry("SE - Ribeiropolis", "1248"),
                Map.entry("ES - Anchieta", "1203"),
                Map.entry("ES - Apiaca", "547"),
                Map.entry("ES - Atilio Vivacqua", "1107"),
                Map.entry("SE - Umbauba", "1252"),
                Map.entry("BA - Cachoeira", "329"),
                Map.entry("ES - Piuma", "551"),
                Map.entry("BA - Sao Felix do Coribe", "1453"),
                Map.entry("PE - Ibirajuba", "1518"),
                Map.entry("PE - Sertania", "32"),
                Map.entry("BA - Tanquinho", "383"),
                Map.entry("PE - Tacaimbo", "315"),
                Map.entry("PE - Triunfo", "1298"),
                Map.entry("ES - Conceicao do Castelo", "1188")
        );

        return mapeamentoCidades.getOrDefault(nomeCidade, "0"); // Valor padrão caso não encontre
    }

    private String obterIpPorCidade(String nomeCidadeOuId) {
        String idCidade = nomeCidadeOuId;
        if (!nomeCidadeOuId.matches("\\d+")) { // Se não for um número, tenta converter
            idCidade = converterCidadeParaNumero(nomeCidadeOuId);
        }

        Map<String, String> ipMapeamento = Map.ofEntries(
                Map.entry("1312", "172.17.9.102"), // Cafeara PR
                Map.entry("1432", "172.17.10.160"), // Siqueira Campos PR
                Map.entry("1329", "172.17.9.122"), // Cambara PR
                Map.entry("410", "172.17.9.123"), // Campina da Lagoa PR
                Map.entry("411", "172.17.9.101"), // Campo Bonito PR
                Map.entry("885", "172.17.10.29"), // Capanema PR
                Map.entry("1339", "172.17.10.28"), // Boa Ventura de Sao Roque PR
                Map.entry("1403", "172.17.9.234"), // Tibagi PR
                Map.entry("1328", "172.17.9.108"), // Ariranha do Ivai PR
                Map.entry("1338", "172.17.9.237"), // Arapoti PR
                Map.entry("419", "172.17.9.93"), // Cidade Gaucha PR
                Map.entry("1404", "172.17.10.162"), // Tres Barras do Parana PR
                Map.entry("1405", "172.17.10.164"), // Ubirata PR
                Map.entry("406", "172.17.9.49"), // Andira PR
                Map.entry("405", "172.17.9.207"), // Ampere PR
                Map.entry("1299", "172.17.9.169"), // Doutor Ulysses PR
                Map.entry("1350", "172.17.9.94"), // Figueira PR
                Map.entry("1336", "172.17.9.100"), // Formosa do Oeste PR
                Map.entry("1382", "172.17.9.233"), // Guapirama PR
                Map.entry("1383", "172.17.10.150"), // Guaraniacu PR
                Map.entry("1351", "172.17.9.90"), // Imbau PR
                Map.entry("1146", "172.17.9.170"), // Iretama PR
                Map.entry("425", "172.17.9.171"), // Ivai PR
                Map.entry("1385", "172.17.9.173"), // Joaquim Tavora PR
                Map.entry("1388", "172.17.9.141"), // Nova Cantu PR
                Map.entry("1389", "172.17.9.109"), // Paraiso do Norte PR
                Map.entry("2556", "172.17.10.182"), // Pien PR
                Map.entry("438", "172.17.9.144"), // Pinhao PR
                Map.entry("437", "172.17.9.177"), // Pinhalao PR
                Map.entry("439", "172.17.9.188"), // Planalto PR
                Map.entry("440", "172.17.9.225"), // Pranchita PR
                Map.entry("443", "172.17.9.114"), // Querencia do Norte PR
                Map.entry("448", "172.17.10.59"), // Rio Bonito do Iguacu PR
                Map.entry("451", "172.17.9.92"), // Salto do Lontra PR
                Map.entry("1398", "172.17.10.105"), // Santo Inacio PR
                Map.entry("1399", "172.17.10.158"), // Sao Joao do Caiua PR
                Map.entry("456", "172.17.9.160"), // Wenceslau Braz PR
                Map.entry("1355", "172.17.9.174"), // Laranjal PR
                Map.entry("453", "172.17.9.96") // Santa Maria do Oeste PR
        );

        return ipMapeamento.getOrDefault(idCidade, "172.17.10.1"); // Valor padrão caso não encontre
    }
    private String processarParametroCidade(String cidade) {
        if (cidade == null || cidade.isEmpty()) {
            return "0";
        }
        // Se já é um número, retorna diretamente
        if (cidade.matches("\\d+")) {
            return cidade;
        }
        // Se é um nome de cidade, converte
        return converterCidadeParaNumero(cidade.trim());
    }

    @GetMapping("/urlsCidadesBusca")
    public String buscarCidades() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        List<String> cidades = Arrays.asList(
                "PR - Cafeara",
                "PR - Siqueira Campos",
                "PR - Cambara",
                "PR - Campina da Lagoa",
                "PR - Campo Bonito",
                "PR - Capanema",
                "PR - Boa Ventura de Sao Roque",
                "PR - Tibagi",
                "PR - Ariranha do Ivai",
                "PR - Arapoti",
                "PR - Cidade Gaucha",
                "PR - Tres Barras do Parana",
                "PR - Ubirata",
                "PR - Andira",
                "PR - Ampere",
                "PR - Doutor Ulysses",
                "PR - Figueira",
                "PR - Formosa do Oeste",
                "PR - Guapirama",
                "PR - Guaraniacu",
                "PR - Salto do Lontra",
                "PR - Wenceslau Braz",
                "PR - Laranjal",
                "PR - Nova Cantu",
                "PR - Pinhalao",
                "PR - Pranchita",
                "PR - Santo Inacio",
                "PR - Sao Joao do Caiua",
                "PR - Rio Bonito do Iguacu",
                "PR - Iretama",
                "PR - Imbau",
                "PR - Ivai",
                "PR - Joaquim Tavora",
                "PR - Paraiso do Norte",
                "PR - Pien",
                "PR - Pinhao",
                "PR - Planalto",
                "PR - Querencia do Norte",
                "PR - Santa Maria do Oeste",
                "AL - Agua Branca",
                "PE - Orobo",
                "SC - Angelina",
                "AL - Campo Alegre",
                "AL - Canapi",
                "SC - Salete",
                "RS - Casca",
                "BA - Jussari",
                "SC - Sao Martinho",
                "AL - Joaquim Gomes",
                "AL - Maravilha",
                "BA - Elisio Medrado",
                "RJ - Bom Jardim",
                "RJ - Bom Jesus do Itabapoana",
                "RJ - Casimiro de Abreu",
                "SC - Apiuna",
                "SC - Vargem Bonita",
                "SC - Capinzal",
                "SC - Caxambu do Sul",
                "SC - Coronel Freitas",
                "BA - Curaca",
                "SC - Rio Fortuna",
                "BA - Igrapiuna",
                "BA - Igapora",
                "BA - Ibitiara",
                "BA - Ibirapitanga",
                "BA - Gloria",
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
                "BA - Ubaira",
                "RS - Tupancireta",
                "PE - Joao Alfredo",
                "SC - Ipira",
                "SC - Ipora do Oeste",
                "PE - Jatoba",
                "BA - Agua Fria",
                "RS - Vila Flores",
                "PE - Cachoeirinha",
                "SC - Agrolandia",
                "PE - Calcado", "PE - Sanharo",
                "PE - Santa Maria do Cambuca",
                "PE - Santa Terezinha",
                "PE - Capoeiras",
                "SC - Jaguaruna",
                "SE - Neopolis",
                "PE - Condado",
                "PE - Cortes",
                "BA - Muquem de Sao Francisco",
                "BA - Nilo Pecanha",
                "BA - Aracas",
                "PE - Xexeu",
                "BA - Nova Redencao",
                "PE - Itacuruba",
                "BA - Arataca",
                "PE - Cumaru",
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
                "SC - Nova Erechim",
                "PE - Custodia",
                "PE - Exu",
                "SC - Rio do Campo",
                "ES - Pinheiros",
                "SC - Rio do Oeste",
                "BA - Belmonte",
                "BA - Sao Jose da Vitoria",
                "PE - Gameleira",
                "PE - Gravata",
                "PE - Sao Jose do Egito",
                "PE - Ibimirim",
                "PE - Sao Jose do Belmonte",
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
                "PE - Cabrobo",
                "PE - Carnaiba",
                "PE - Carnaubeira da Penha",
                "BA - Mulungu do Morro",
                "BA - Antonio Goncalves",
                "PE - Itaiba",
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
            botaoLogin.click();

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

    @GetMapping("/varredurageral")
    @Transactional
    public String varreduraGeral(@RequestParam(name = "cidade", required = false) String cidade) {
        System.out.println("=== /varredurageral ===");
        System.out.println("Parâmetro 'cidade' recebido: " + cidade);

        String cidadeProcessada = processarParametroCidade(cidade);
        System.out.println("Cidade processada para ID/Número: " + cidadeProcessada);

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        //options.addArguments("--headless"); // Descomente para rodar sem interface gráfica
        options.addArguments("--incognito");
        options.addArguments("--disable-cache");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60)); // Aumentado para 60 segundos

        Map<String, Object> resultadoGeral = new LinkedHashMap<>();

        // GERAL
        try {
            String ip = obterIpPorCidade(cidadeProcessada);
            String urlBase = "http://" + ip + ":10251/login.html";
            System.out.println("Tentando acessar URL do transmissor: " + urlBase);
            driver.get(urlBase);

            // --> Login
            WebElement campoUsuario = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_user")));
            System.out.println("Campo de usuário encontrado.");
            campoUsuario.clear();
            campoUsuario.sendKeys("factory");

            WebElement campoSenha = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_pw")));
            System.out.println("Campo de senha encontrado.");
            campoSenha.clear();
            campoSenha.sendKeys("f@ct0ry");

            WebElement botaoLogin = wait.until(ExpectedConditions.elementToBeClickable(By.id("hrs_login_loginBtn")));
            System.out.println("Botão de login encontrado.");
            botaoLogin.click();
            System.out.println("Botão de login clicado. Aguardando redirecionamento para mainPage.html...");

            wait.until(ExpectedConditions.urlContains("mainPage.html"));
            System.out.println("Redirecionado para mainPage.html com sucesso.");

            //  Navegação para Boards
            WebElement menuLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("firstMenu")));
            System.out.println("Elemento 'firstMenu' encontrado e clicável. Clicando...");
            menuLink.click();
            Thread.sleep(1000);
            System.out.println("Clicado em 'firstMenu'.");

            WebElement setupLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[normalize-space(text())='Setup']")));
            System.out.println("Link 'Setup' encontrado. Clicando...");
            setupLink.click();
            Thread.sleep(1500);
            System.out.println("Clicado em 'Setup'.");

            WebElement boardsLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, \"loadPage('boardpresence')\") and normalize-space(text())='Boards']")));
            System.out.println("Link 'Boards' encontrado. Clicando...");
            boardsLink.click();
            System.out.println("Clicado em 'Boards'. Aguardando carregamento da página...");

            // AGUARDA A PÁGINA CARREGAR COMPLETAMENTE
            Thread.sleep(5000);
            System.out.println("Página 'Boards' carregada.");

            // DEBUG: Verifica a estrutura da página
            System.out.println("=== DEBUG DA PÁGINA ===");
            System.out.println("URL atual: " + driver.getCurrentUrl());
            System.out.println("Título: " + driver.getTitle());

            // Tenta encontrar o container principal
            WebElement boardPresencePage = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("boardpresencePage")));
            System.out.println("boardpresencePage encontrado: " + (boardPresencePage != null));

            // Agora busca pelo caminho completo que você mencionou
            WebElement leftCol = null;
            try {
                leftCol = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("#boardpresencePage > div > div.canvas > div.leftCol")));
                System.out.println("leftCol encontrado por CSS: " + (leftCol != null));
            } catch (TimeoutException e) {
                System.out.println("leftCol não encontrado por CSS. Tentando por classe...");
            }


            // Se não encontrar pelo CSS, tenta por classe
            if (leftCol == null) {
                try {
                    leftCol = driver.findElement(By.className("leftCol"));
                    System.out.println("leftCol encontrado por classe.");
                } catch (Exception e) {
                    System.out.println("leftCol não encontrado por classe.");
                }
            }

            // Se ainda não encontrou, tenta buscar diretamente as contentRow
            List<WebElement> txRows;
            if (leftCol != null) {
                // Busca dentro do leftCol
                txRows = leftCol.findElements(By.className("contentRow"));
                System.out.println("Rows encontradas dentro do leftCol: " + txRows.size());
            } else {
                // Busca em toda a página
                txRows = driver.findElements(By.className("contentRow"));
                System.out.println("Rows encontradas em toda a página: " + txRows.size());
            }

            // Se ainda não encontrou, tenta por XPath
            if (txRows.isEmpty()) {
                txRows = driver.findElements(By.xpath("//div[contains(@class, 'contentRow')]"));
                System.out.println("Rows encontradas por XPath: " + txRows.size());
            }

            // DEBUG: Mostra o HTML encontrado
            if (!txRows.isEmpty()) {
                System.out.println("Primeira row encontrada: " + txRows.get(0).getAttribute("outerHTML"));
            } else {
                // Mostra o HTML da página para debug
                String pageSource = driver.getPageSource();
                System.out.println("Primeiros 1000 caracteres do HTML:");
                System.out.println(pageSource.substring(0, Math.min(1000, pageSource.length())));
            }

            // --> Checagem do Status dos TXs
            List<Map<String, String>> txList = new ArrayList<>();
            int numTxEnabled = 0;

            for (WebElement row : txRows) {
                try {
                    // Extrai o nome da TX (h2 dentro da contentRow)
                    String txName = "TX Desconhecida";
                    try {
                        WebElement txTitle = row.findElement(By.tagName("h2"));
                        txName = txTitle.getText().replace(":", "").trim();
                        System.out.println("Encontrado: " + txName);
                    } catch (Exception e) {
                        System.err.println("Erro ao extrair nome da TX: " + e.getMessage());
                        continue; // Pula esta row se não encontrar o h2
                    }

                    // Extrai o status do select
                    String status = "Unknown";
                    try {
                        WebElement selectElement = row.findElement(By.tagName("select"));
                        Select select = new Select(selectElement);
                        WebElement selectedOption = select.getFirstSelectedOption();
                        status = selectedOption.getText().trim();

                        // === CONTAGEM DE TXs HABILITADAS ===
                        if (status.equalsIgnoreCase("Enable")) {
                            numTxEnabled++;
                            System.out.println("✅ " + txName + " - HABILITADO (Total: " + numTxEnabled + ")");
                        } else {
                            System.out.println("❌ " + txName + " - DESABILITADO");
                        }

                    } catch (Exception e) {
                        System.err.println("Erro ao extrair status para " + txName + ": " + e.getMessage());
                    }

                    Map<String, String> txInfo = new HashMap<>();
                    txInfo.put("TX", txName);
                    txInfo.put("Status", status);
                    txList.add(txInfo);

                } catch (Exception rowEx) {
                    System.err.println("Erro ao processar row: " + rowEx.getMessage());
                }
            }

            resultadoGeral.put("BoardsStatus", txList);
            resultadoGeral.put("TotalTXsEnabled", numTxEnabled);  // ← ADICIONADO AO JSON

            System.out.println("=== RESUMO FINAL ===");
            System.out.println("TXs habilitadas: " + numTxEnabled + " de " + txList.size());

            // --> Alarmes: Common e TXs
            WebElement eventLogBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#event_button > a.eventLog.btn100x40_fault")));
            System.out.println("Botão 'Event Log' encontrado. Clicando...");
            eventLogBtn.click();
            System.out.println("Clicado em 'Event Log'.");

            WebElement setupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#btnLogSetup > a#faultlogSet")));
            System.out.println("Botão 'Setup' dentro de Event Log encontrado. Clicando...");
            setupBtn.click();
            System.out.println("Clicado em 'Setup' (faultlogSet).");

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.navAreaBtn")));
            System.out.println("Área de navegação de abas de alarme visível.");

            // 1) Buscar alarmes na aba Common
            System.out.println("Buscando alarmes na aba Common");
            WebElement commonBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class,'navAreaBtn') and text()='Common']")));
            commonBtn.click();
            Thread.sleep(2000);
            System.out.println("Clicado na aba 'Common'.");

            List<Map<String, String>> alarmesCommon = buscarAlarmesCommon(driver, wait);
            resultadoGeral.put("Common", alarmesCommon);
            System.out.println("Alarmes Common coletados: " + alarmesCommon.size());

            // 2) Buscar alarmes nas abas TX1 a TX8
            String[] abasTx = {"TX 1", "TX 2", "TX 3", "TX 4", "TX 5", "TX 6", "TX 7", "TX 8"};
            Map<String, List<Map<String, String>>> alarmesTxs = new LinkedHashMap<>();

            for (String aba : abasTx) {
                System.out.println("Buscando alarmes na aba " + aba);
                try {
                    WebElement abaBtn = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//div[contains(@class,'navAreaBtn') and text()='" + aba + "']")));
                    abaBtn.click();
                    Thread.sleep(5000);
                    System.out.println("Clicado na aba '" + aba + "'.");

                    List<Map<String, String>> alarmes = buscarAlarmesTx(driver, wait);
                    alarmesTxs.put(aba, alarmes);
                    System.out.println("Alarmes para " + aba + " coletados: " + alarmes.size());
                } catch (TimeoutException e) {
                    System.out.println("Aba " + aba + " não encontrada ou não clicável, adicionando lista vazia.");
                    alarmesTxs.put(aba, Collections.emptyList());
                } catch (Exception e) {
                    System.err.println("Erro ao processar aba " + aba + ": " + e.getMessage());
                    alarmesTxs.put(aba, Collections.emptyList());
                }
            }

            resultadoGeral.put("TXs", alarmesTxs);

            // --> Coleta das taxas de entrada, PIDs, Cooling, ALC e Alarmes ALC para todos os TXs
            Map<String, Map<String, Object>> dadosTxs = new LinkedHashMap<>();
            Map<String, String> alcStatusPorTx = new LinkedHashMap<>();

            String[] nomesAlarmesAlc = {
                    "Temperature (°C):", "RF Forward High:", "RF Forward Low (-1.5 dB):",
                    "RF Forward Low (-3 dB):", "RF Forward Low (-7 dB):", "RF Reflected High:"
            };

            for (int i = 1; i <= numTxEnabled; i++) {
                String txKey = "TX " + i;
                System.out.println("🚀 PROCESSANDO " + txKey + " (" + i + " de " + numTxEnabled + ")");

                Map<String, Object> dadosTx = new LinkedHashMap<>();

                Map<String, String> taxasEntrada = new HashMap<>();
                taxasEntrada.put("taxa_ETH", "");
                taxasEntrada.put("taxa_ASI", "");
                dadosTx.put("TaxasEntrada", taxasEntrada);

                Map<String, Object> resultadoPids = new LinkedHashMap<>();
                resultadoPids.put("tsId", "");
                resultadoPids.put("status", "");
                resultadoPids.put("programas", new ArrayList<>());
                dadosTx.put("PIDs", resultadoPids);

                String coolingStatus = "";
                String alcStatus = "";
                Map<String, Map<String, Object>> alarmesAlcTx = new LinkedHashMap<>();

                try {
                    // Navega para Home
                    WebElement homeBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("firstMenu")));
                    homeBtn.click();
                    Thread.sleep(1500);
                    System.out.println("Navegado para Home para processar " + txKey);

                    // Clica no botão Go To do TX i
                    WebElement goToBtn = wait.until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("a.btn60x22.link_btn[onclick*='modBtnClick(" + i + ")']")));
                    goToBtn.click();
                    Thread.sleep(1500);
                    System.out.println("Clicado em 'Go To' para " + txKey);

                    // Navega para Exciter -> Input (para taxas e PIDs)
                    WebElement exciterDiv = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div#h_exciter")));
                    exciterDiv.click();
                    Thread.sleep(1500);
                    System.out.println("Clicado em 'Exciter' para " + txKey);

                    WebElement inputLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("estatus_inputs")));
                    inputLink.click();
                    Thread.sleep(1500);
                    System.out.println("Clicado em 'Input' para " + txKey);

                    // Coleta taxas ETH e ASI
                    try {
                        WebElement ethDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("in_stat_inputs_br_1")));
                        String ethValue = ethDiv.getText();
                        Thread.sleep(2000);
                        taxasEntrada.put("taxa_ETH", ethValue);
                        System.out.println(txKey + " - Taxa ETH: " + ethValue);
                    } catch (TimeoutException e) {
                        System.out.println(txKey + " - Taxa ETH não encontrada.");
                    }

                    try {
                        WebElement asiDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("in_stat_inputs_br_3")));
                        String asiValue = asiDiv.getText();
                        Thread.sleep(2000);
                        taxasEntrada.put("taxa_ASI", asiValue);
                        System.out.println(txKey + " - Taxa ASI: " + asiValue);
                    } catch (TimeoutException e) {
                        System.out.println(txKey + " - Taxa ASI não encontrada.");
                    }

                    // Coleta PIDs
                    try {
                        WebElement tsStatsBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("ts_stats_btn")));
                        tsStatsBtn.click();
                        Thread.sleep(2000);
                        System.out.println("Clicado em 'TS Stats' para " + txKey);

                        WebElement psiInfoContainer = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("psi_info_tbl")));
                        System.out.println("Container PSI Info encontrado para " + txKey);

                        List<Map<String, String>> listaProgramas = new ArrayList<>();

                        try {
                            WebElement tsIdElem = psiInfoContainer.findElement(By.cssSelector("div.rowA div.psiData"));
                            String tsId = tsIdElem.getText();
                            Thread.sleep(2000);
                            resultadoPids.put("tsId", tsId);
                            System.out.println(txKey + " - TS ID: " + tsId);
                        } catch (Exception e) {
                            resultadoPids.put("tsId", "");
                            System.out.println(txKey + " - TS ID não encontrado.");
                        }

                        List<WebElement> programas = psiInfoContainer.findElements(By.cssSelector("div.rowB"));
                        System.out.println(txKey + " - Programas encontrados: " + programas.size());

                        for (WebElement programa : programas) {
                            Map<String, String> programaInfo = new HashMap<>();
                            try {
                                WebElement programaData = programa.findElement(By.cssSelector("div.psiData"));
                                String programaTexto = programaData.getText();
                                programaInfo.put("programa", programaTexto);

                                List<String> pidsPrograma = new ArrayList<>();
                                WebElement proximoElemento = programa;

                                while (true) {
                                    try {
                                        proximoElemento = proximoElemento.findElement(By.xpath("following-sibling::div[1]"));
                                        if (proximoElemento.getAttribute("class").contains("rowC")) {
                                            WebElement pidData = proximoElemento.findElement(By.cssSelector("div.psiData"));
                                            String pidTexto = pidData.getText();

                                            String icone = "";
                                            try {
                                                WebElement iconeElem = proximoElemento.findElement(By.cssSelector("div.icon"));
                                                icone = iconeElem.getAttribute("class").replace("icon", "").trim();
                                            } catch (Exception ignored) {
                                            }

                                            if (!icone.isEmpty()) {
                                                pidTexto += " (" + icone + ")";
                                            }

                                            pidsPrograma.add(pidTexto);
                                        } else {
                                            break;
                                        }
                                    } catch (Exception e) {
                                        break;
                                    }
                                }

                                programaInfo.put("pids", String.join(", ", pidsPrograma));
                                listaProgramas.add(programaInfo);
                            } catch (Exception e) {
                                System.err.println("Erro ao processar programa em " + txKey + ": " + e.getMessage());
                            }
                        }

                        resultadoPids.put("status", "Sequência executada com sucesso");
                        resultadoPids.put("programas", listaProgramas);

                    } catch (Exception e) {
                        System.err.println("Erro ao coletar PIDs do " + txKey + ": " + e.toString());
                    }

                    // Coleta cooling
                    try {
                        WebElement coolingHeader = wait.until(ExpectedConditions.elementToBeClickable(
                                By.xpath("//h3[contains(@class,'ui-accordion-header') and contains(text(),'Cooling')]")));
                        coolingHeader.click();
                        Thread.sleep(1500);
                        System.out.println("Clicado em 'Cooling' para " + txKey);

                        WebElement coolingPanel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ui-id-4")));
                        coolingStatus = coolingPanel.getText();
                        System.out.println(txKey + " - Cooling Status: " + coolingStatus);
                    } catch (Exception e) {
                        System.err.println("Erro ao coletar Cooling do " + txKey + ": " + e.toString());
                        coolingStatus = "";
                    }

                    // Coleta ALC Status + Alarmes ALC via Amp
                    System.out.println("Coletando ALC Status + Alarmes ALC para " + txKey + " (via Amp)");
                    boolean ampClicado = false;
                    WebElement ampElement = null;

                    try {
                        homeBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("firstMenu")));
                        homeBtn.click();
                        Thread.sleep(1000);
                        goToBtn = wait.until(ExpectedConditions.elementToBeClickable(
                                By.cssSelector("a.btn60x22.link_btn[onclick*='modBtnClick(" + i + ")']")));
                        goToBtn.click();
                        Thread.sleep(2000);

                        System.out.println("Tentando clicar em Amp (#h_ipa_link) para " + txKey);
                        try {
                            ampElement = wait.until(ExpectedConditions.elementToBeClickable(By.id("h_ipa_link")));
                            System.out.println("Sucesso: Encontrou #h_ipa_link para " + txKey);
                            ampClicado = true;
                        } catch (TimeoutException e1) {
                            try {
                                ampElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@id='h_ipa_link']")));
                                System.out.println("Sucesso: Encontrou via XPath para " + txKey);
                                ampClicado = true;
                            } catch (TimeoutException e2) {
                                try {
                                    ampElement = wait.until(ExpectedConditions.elementToBeClickable(By.id("h_ipa")));
                                    System.out.println("Sucesso: Fallback div#h_ipa para " + txKey);
                                    ampClicado = true;
                                } catch (TimeoutException e3) {
                                    System.out.println("Falhou todos - Amp não encontrado em " + txKey);
                                }
                            }
                        }

                        if (ampClicado && ampElement != null) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", ampElement);
                            Thread.sleep(1000);
                            try {
                                ampElement.click();
                            } catch (Exception e) {
                                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ampElement);
                            }
                            System.out.println("Clique executado em Amp para " + txKey);

                            new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                                    ExpectedConditions.or(
                                            ExpectedConditions.presenceOfElementLocated(By.id("pa_stat_alc_stat")),
                                            ExpectedConditions.presenceOfElementLocated(By.id("pa_stat_alrm_readings")),
                                            webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
                                    )
                            );
                            Thread.sleep(3000);
                            System.out.println("Amp carregado para " + txKey);

                            // Extrai ALC Status
                            try {
                                WebElement alcElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pa_stat_alc_stat")));
                                alcStatus = alcElement.getText().trim();
                                if (alcStatus.isEmpty()) alcStatus = "N/A";
                                else alcStatus = interpretarAlcStatus(alcStatus);
                                System.out.println(txKey + " - ALC: " + alcStatus);
                            } catch (TimeoutException e) {
                                alcStatus = "Não encontrado após Amp";
                                System.out.println(txKey + " - ALC Status não encontrado após Amp.");
                            } catch (Exception e) {
                                alcStatus = "Erro extração: " + e.getMessage();
                                System.err.println(txKey + " - Erro ao extrair ALC Status: " + e.getMessage());
                            }

                            // Extrai Alarmes ALC
                            try {
                                WebElement alarmContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pa_stat_alrm_readings")));
                                List<WebElement> alarmeRows = alarmContainer.findElements(By.cssSelector("div.contentRowA"));

                                if (alarmeRows.size() < 6) {
                                    System.out.println("Aviso: Menos de 6 alarmes encontrados em " + txKey + " (" + alarmeRows.size() + ")");
                                }

                                for (int j = 0; j < Math.min(alarmeRows.size(), nomesAlarmesAlc.length); j++) {
                                    WebElement row = alarmeRows.get(j);
                                    String nomeAlarme = nomesAlarmesAlc[j];

                                    try {
                                        WebElement ledElem = row.findElement(By.cssSelector("div[style*='float:right']"));
                                        String ledClass = ledElem.getAttribute("class");
                                        String ledStatus = interpretarLedAmpClass(ledClass);

                                        Map<String, Object> alarmeInfo = new HashMap<>();
                                        alarmeInfo.put("status", ledStatus);

                                        if (j == 0) {
                                            WebElement valueElem = row.findElement(By.cssSelector("div.table_colRight"));
                                            String value = valueElem.getText().trim();
                                            if (value.isEmpty()) value = "N/A";
                                            alarmeInfo.put("value", value);
                                            System.out.println(txKey + " - " + nomeAlarme + ": Status=" + ledStatus + ", Value=" + value);
                                        } else {
                                            System.out.println(txKey + " - " + nomeAlarme + ": Status=" + ledStatus);
                                        }

                                        alarmesAlcTx.put(nomeAlarme, alarmeInfo);

                                    } catch (Exception e) {
                                        Map<String, Object> alarmeInfo = new HashMap<>();
                                        alarmeInfo.put("status", "Erro extração");
                                        if (j == 0) {
                                            alarmeInfo.put("value", "N/A");
                                        }
                                        alarmesAlcTx.put(nomeAlarme, alarmeInfo);
                                        System.err.println("Erro ao extrair alarme " + (j + 1) + " em " + txKey + ": " + e.getMessage());
                                    }
                                }

                                if (alarmesAlcTx.size() < nomesAlarmesAlc.length) {
                                    for (int j = alarmesAlcTx.size(); j < nomesAlarmesAlc.length; j++) {
                                        String nomeAlarme = nomesAlarmesAlc[j];
                                        Map<String, Object> alarmeInfo = new HashMap<>();
                                        alarmeInfo.put("status", "N/A");
                                        if (j == 0) {
                                            alarmeInfo.put("value", "N/A");
                                        }
                                        alarmesAlcTx.put(nomeAlarme, alarmeInfo);
                                    }
                                }

                            } catch (TimeoutException e) {
                                System.err.println("Timeout no container #pa_stat_alrm_readings para " + txKey + ": " + e.getMessage());
                                for (int j = 0; j < nomesAlarmesAlc.length; j++) {
                                    Map<String, Object> alarmeInfo = new HashMap<>();
                                    alarmeInfo.put("status", "N/A");
                                    if (j == 0) {
                                        alarmeInfo.put("value", "N/A");
                                    }
                                    alarmesAlcTx.put(nomesAlarmesAlc[j], alarmeInfo);
                                }
                            } catch (Exception e) {
                                System.err.println("Erro geral na extração de alarmes ALC para " + txKey + ": " + e.getMessage());
                                for (int j = 0; j < nomesAlarmesAlc.length; j++) {
                                    Map<String, Object> alarmeInfo = new HashMap<>();
                                    alarmeInfo.put("status", "Erro extração geral");
                                    if (j == 0) {
                                        alarmeInfo.put("value", "N/A");
                                    }
                                    alarmesAlcTx.put(nomesAlarmesAlc[j], alarmeInfo);
                                }
                            }

                        } else {
                            System.out.println("Amp não clicado para " + txKey + " - Alarmes ALC indisponíveis");
                            for (int j = 0; j < nomesAlarmesAlc.length; j++) {
                                Map<String, Object> alarmeInfo = new HashMap<>();
                                alarmeInfo.put("status", "Amp não encontrado");
                                if (j == 0) {
                                    alarmeInfo.put("value", "N/A");
                                }
                                alarmesAlcTx.put(nomesAlarmesAlc[j], alarmeInfo);
                            }
                            alcStatus = "Amp não encontrado";
                        }

                    } catch (Exception e) {
                        System.err.println("Erro geral no ALC + Alarmes ALC para " + txKey + ": " + e.getMessage());
                        alcStatus = "Erro geral";
                        for (int j = 0; j < nomesAlarmesAlc.length; j++) {
                            Map<String, Object> alarmeInfo = new HashMap<>();
                            alarmeInfo.put("status", "Erro geral");
                            if (j == 0) {
                                alarmeInfo.put("value", "N/A");
                            }
                            alarmesAlcTx.put(nomesAlarmesAlc[j], alarmeInfo);
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Erro ao coletar dados do " + txKey + ": " + e.toString());
                    alcStatus = "Erro geral no TX";
                    for (int j = 0; j < nomesAlarmesAlc.length; j++) {
                        Map<String, Object> alarmeInfo = new HashMap<>();
                        alarmeInfo.put("status", "Erro geral no TX");
                        if (j == 0) {
                            alarmeInfo.put("value", "N/A");
                        }
                        alarmesAlcTx.put(nomesAlarmesAlc[j], alarmeInfo);
                    }
                }

                dadosTx.put("CoolingStatus", coolingStatus);
                dadosTx.put("alcStatus", alcStatus);
                dadosTx.put("alarmesAlc", alarmesAlcTx);

                dadosTxs.put(txKey, dadosTx);
                alcStatusPorTx.put("TX" + i, alcStatus);
                System.out.println("✅ FINALIZADO " + txKey);
            }

            // === ADICIONA DADOS VAZIOS PARA TXs NÃO PROCESSADAS (OPCIONAL) ===
            for (int i = numTxEnabled + 1; i <= 8; i++) {
                String txKey = "TX " + i;
                dadosTxs.put(txKey, criarDadosTxVazios(txKey));
                alcStatusPorTx.put("TX" + i, "Não processada");
                System.out.println("⚪ DADOS VAZIOS ADICIONADOS PARA " + txKey);
            }

            resultadoGeral.put("DadosTXs", dadosTxs);
            resultadoGeral.put("alcStatusPorTx", alcStatusPorTx);

            // --> ALARMES DO MODULADOR
            System.out.println("=== INICIANDO COLETA DE ALARMES DO MODULADOR ===");
            Map<String, Map<String, String>> alarmesModulatorPorTx = coletarAlarmesModulator(driver, wait, numTxEnabled);
            resultadoGeral.put("AlarmesModulator", alarmesModulatorPorTx);
            System.out.println("✅ COLETA DE ALARMES DO MODULADOR CONCLUÍDA");

            // --> Coleta ID dos equipamentos
            Map<String, Object> idEquipamentoData = coletarIdEquipamento(driver, wait);
            resultadoGeral.putAll(idEquipamentoData);

            resultadoGeral.put("status", "Consulta executada com sucesso");

            // <<< SALVAMENTO NO BANCO DE DADOS >>>
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String jsonString = mapper.writeValueAsString(resultadoGeral);

            // --> Salva o JSON completo na tabela varredura
            try {
                System.out.println("=== TENTANDO SALVAR NO BD ===");
                System.out.println("Cidade: " + cidade);
                System.out.println("JSON length: " + jsonString.length());

                Varredura varredura = new Varredura(cidade, "Sucesso", jsonString);
                System.out.println("Objeto Varredura criado: " + varredura);

                Varredura salva = varreduraRepository.save(varredura);
                System.out.println("✅ SALVO NO BD! ID: " + salva.getId());

            } catch (Exception e) {
                System.err.println("❌ ERRO AO SALVAR NO BD: " + e.getMessage());
                e.printStackTrace();
            }

            return jsonString;

        } catch (Exception e) {
            System.err.println("Erro na varredura geral: " + e.toString());
            e.printStackTrace();
            resultadoGeral.put("status", "Erro na varredura geral");
            resultadoGeral.put("erro", e.toString());

            // TENTATIVA DE SALVAMENTO NO BD
            try {
                ObjectMapper mapperErro = new ObjectMapper();
                mapperErro.enable(SerializationFeature.INDENT_OUTPUT);
                String jsonErro = mapperErro.writeValueAsString(resultadoGeral);
                Varredura varreduraErro = new Varredura(cidade, "Erro", jsonErro);
                varreduraRepository.save(varreduraErro);
            } catch (Exception ignored) {
            }

            // EXIBIÇÃO DE POSSIVEIS ERROS
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                return mapper.writeValueAsString(resultadoGeral);
            } catch (Exception ex) {
                return "{\"erro\":\"Falha ao gerar JSON\"}";
            }
            // FINAL
        } finally {
            driver.quit();
            System.out.println("Driver finalizado no /varredurageral");
        }
    }

    // métodos necessários para varreduraGeral()

    private Map<String, Object> criarDadosTxVazios(String txKey) {
        Map<String, Object> dadosVazios = new LinkedHashMap<>();

        Map<String, String> taxasEntrada = new HashMap<>();
        taxasEntrada.put("taxa_ETH", "Não processada");
        taxasEntrada.put("taxa_ASI", "Não processada");
        dadosVazios.put("TaxasEntrada", taxasEntrada);

        Map<String, Object> resultadoPids = new LinkedHashMap<>();
        resultadoPids.put("tsId", "Não processada");
        resultadoPids.put("status", "Não processada");
        resultadoPids.put("programas", new ArrayList<>());
        dadosVazios.put("PIDs", resultadoPids);

        dadosVazios.put("CoolingStatus", "Não processada");
        dadosVazios.put("alcStatus", "Não processada");

        Map<String, Map<String, Object>> alarmesAlcVazios = new LinkedHashMap<>();
        String[] nomesAlarmesAlc = {
                "Temperature (°C):", "RF Forward High:", "RF Forward Low (-1.5 dB):",
                "RF Forward Low (-3 dB):", "RF Forward Low (-7 dB):", "RF Reflected High:"
        };

        for (String alarme : nomesAlarmesAlc) {
            Map<String, Object> alarmeInfo = new HashMap<>();
            alarmeInfo.put("status", "Não processada");
            if (alarme.equals("Temperature (°C):")) {
                alarmeInfo.put("value", "N/A");
            }
            alarmesAlcVazios.put(alarme, alarmeInfo);
        }

        dadosVazios.put("alarmesAlc", alarmesAlcVazios);

        return dadosVazios;
    }

    private Map<String, Object> coletarIdEquipamento(WebDriver driver, WebDriverWait wait) throws InterruptedException {
        Map<String, Object> resultado = new HashMap<>();

        WebElement menuLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("firstMenu")));
        menuLink.click();
        Thread.sleep(1000);
        System.out.println("Navegado para Home para coletar ID do equipamento.");

        WebElement setupLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space(text())='Setup']")));
        setupLink.click();
        Thread.sleep(2000);
        System.out.println("Clicado em 'Setup' para coletar ID do equipamento.");

        WebElement versionLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, \"loadPage('hardwareversion')\") and normalize-space(text())='Version']")));
        versionLink.click();
        Thread.sleep(2000);
        System.out.println("Clicado em 'Version' para coletar ID do equipamento.");

        WebElement serialNumberDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("hw_ver_sys_sn_read")));
        String serialNumber = serialNumberDiv.getText();
        resultado.put("SerialNumber", serialNumber);
        System.out.println("Serial Number coletado: " + serialNumber);

        WebElement transmittersBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("hw_ver_transmitters_box_body")));
        List<WebElement> txRows = transmittersBox.findElements(By.xpath(".//div[contains(@class,'contentRowA')]"));

        List<Map<String, String>> txList = new ArrayList<>();

        for (WebElement row : txRows) {
            List<WebElement> cols = row.findElements(By.xpath(".//div[contains(@class,'table_label')]"));
            String txName = cols.size() > 0 ? cols.get(0).getText().trim() : "";
            String txSerial = cols.size() > 2 ? cols.get(2).getText().trim() : "";

            Map<String, String> txInfo = new HashMap<>();
            txInfo.put("TX", txName);
            txInfo.put("Serial", txSerial);

            txList.add(txInfo);
            System.out.println("Transmitter ID coletado: " + txName + " - " + txSerial);
        }

        resultado.put("Transmitters", txList);

        return resultado;
    }
/*
    private Map<String, Map<String, String>> coletarAlarmesModulator(WebDriver driver, WebDriverWait wait, int numTxEnabled) throws InterruptedException {
        Map<String, Map<String, String>> modulatorStatusPorTx = new LinkedHashMap<>();

        // Loop para coletar alarmes analógicos de cada TX habilitada
        for (int i = 1; i <= numTxEnabled; i++) {
            String txKey = "TX" + i;
            System.out.println("Coletando alarmes do Modulator para " + txKey);

            Map<String, String> alarmesTx = new HashMap<>();
            String statusLocal = "Sucesso";

            try {
                // 1. Navega de volta para Home (reset para próximo TX)
                WebElement homeBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("firstMenu")));
                homeBtn.click();
                Thread.sleep(1500);
                System.out.println("Navegado para Home para Modulator de " + txKey);

                // 2. Clicar em "Go To" para o TX i (painel principal)
                WebElement goToBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a.btn60x22.link_btn[onclick*='modBtnClick(" + i + ")']")));
                goToBtn.click();
                Thread.sleep(1500);
                System.out.println("Clicado em 'Go To' para Modulator de " + txKey);

                // 3. Clicar em "Exciter"
                WebElement exciterDiv = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div#h_exciter")));
                exciterDiv.click();
                Thread.sleep(1500);
                System.out.println("Clicado em 'Exciter' para Modulator de " + txKey);

                // 4. Clicar em "Modulator" (acessa o painel com analog_alarms)
                WebElement modulatorLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("estatus_modulator")));
                modulatorLink.click();
                Thread.sleep(2000);
                System.out.println("Clicado em 'Modulator' para " + txKey);

                // 5. Aguarda o container #analog_alarms carregar e extrai os 4 alarmes
                WebElement analogAlarmsContainer = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("analog_alarms")));
                System.out.println("Container 'isdbt_alrms' encontrado para " + txKey);

                // Alarme 1: TS Presence
                try {
                    WebElement IIPBox = analogAlarmsContainer.findElement(By.id("isdbt_alrm_ts_presence_box"));
                    WebElement TSPresenceLed = analogAlarmsContainer.findElement(By.id("isdbt_alrm_ts_presence"));
                    String TSPresenceClass = TSPresenceLed.getAttribute("class");
                    String TSPresenceStatus = interpretarLedModClass(TSPresenceClass);
                    alarmesTx.put("TS Presence", TSPresenceStatus);
                    System.out.println(txKey + " - TS Presence: " + TSPresenceStatus);
                } catch (Exception e) {
                    alarmesTx.put("TS Presence", "Erro: " + e.getMessage());
                    statusLocal = "Aviso: TS Presence não encontrado";
                    System.err.println(txKey + " - Erro ao coletar TS Presence: " + e.getMessage());
                }

                // Alarme 2: Rate Overflow:
                try {
                    WebElement IIPBox = analogAlarmsContainer.findElement(By.id("isdbt_alrm_rate_overflow_box"));
                    WebElement RateOverflowLed = analogAlarmsContainer.findElement(By.id("isdbt_alrm_rate_overflow"));
                    String RateOverflowClass = RateOverflowLed.getAttribute("class");
                    String RateOverflowStatus = interpretarLedModClass(RateOverflowClass);
                    alarmesTx.put("Rate Overflow", RateOverflowStatus);
                    System.out.println(txKey + " - Rate Overflow: " + RateOverflowStatus);
                } catch (Exception e) {
                    alarmesTx.put("Rate Overflow", "Erro: " + e.getMessage());
                    if (statusLocal.equals("Sucesso")) statusLocal = "Aviso: Rate Overflow não encontrado";
                    System.err.println(txKey + " - Erro ao coletar Rate Overflow: " + e.getMessage());
                }

                // Alarme 3: IIP
                try {
                    WebElement IIPBox = analogAlarmsContainer.findElement(By.id("isdbt_alrm_iip_box"));
                    WebElement IIPLed = analogAlarmsContainer.findElement(By.id("isdbt_alrm_iip"));
                    String IIPClass = IIPLed.getAttribute("class");
                    String IIPStatus = interpretarLedModClass(IIPClass);
                    alarmesTx.put("IIP", IIPStatus);
                    System.out.println(txKey + " - IIP: " + IIPStatus);
                } catch (Exception e) {
                    alarmesTx.put("IIP", "Erro: " + e.getMessage());
                    if (statusLocal.equals("Sucesso")) statusLocal = "Aviso: IIP não encontrado";
                    System.err.println(txKey + " - Erro ao coletar IIP: " + e.getMessage());
                }

                // Alarme 4: Remux Tx Muting
                try {
                    WebElement IIPBox = analogAlarmsContainer.findElement(By.id("isdbt_alrm_remux_tx_mute_box"));
                    WebElement RemuxTxMutingLed = analogAlarmsContainer.findElement(By.id("isdbt_alrm_remux_tx_mute"));
                    String RemuxTxMutingClass = RemuxTxMutingLed.getAttribute("class");
                    String RemuxTxMutingStatus = interpretarLedModClass(RemuxTxMutingClass);
                    alarmesTx.put("Remux Tx Muting", RemuxTxMutingStatus);
                    System.out.println(txKey + " - Remux Tx Muting: " + RemuxTxMutingStatus);
                } catch (Exception e) {
                    alarmesTx.put("Remux Tx Muting", "Erro: " + e.getMessage());
                    if (statusLocal.equals("Sucesso")) statusLocal = "Aviso: Remux Tx Muting não encontrado";
                    System.err.println(txKey + " - Erro ao coletar Remux Tx Muting: " + e.getMessage());
                }

                // Se todos vazios/erro, aviso geral
                if (alarmesTx.values().stream().allMatch(v -> v.startsWith("Erro") || v.isEmpty())) {
                    statusLocal = "Aviso: Nenhum alarme analógico encontrado";
                }

            } catch (TimeoutException e) {
                System.err.println("Timeout ao coletar dados do Modulator para " + txKey + ": " + e.getMessage());
                statusLocal = "Timeout - Painel Modulator não carregou";
                alarmesTx.put("TS Presence", "Timeout");
                alarmesTx.put("Rate Overflow", "Timeout");
                alarmesTx.put("IIP", "Timeout");
                alarmesTx.put("Remux Tx Muting", "Timeout");
            } catch (Exception e) {
                System.err.println("Erro ao coletar dados do Modulator para " + txKey + ": " + e.toString());
                statusLocal = "Erro: " + e.getMessage();
                alarmesTx.put("TS Presence", "Erro geral");
                alarmesTx.put("Rate Overflow", "Erro geral");
                alarmesTx.put("IIP", "Erro geral");
                alarmesTx.put("Remux Tx Muting", "Erro geral");
            }

            // Adiciona status para debug (opcional)
            alarmesTx.put("status", statusLocal);
            modulatorStatusPorTx.put(txKey, alarmesTx);

            System.out.println(txKey + " - Status Modulator: " + statusLocal);

            // Pequeno delay entre TXs
            Thread.sleep(1000);
        }

        // Adiciona dados vazios para TXs não processadas (se necessário)
        for (int i = numTxEnabled + 1; i <= 8; i++) {
            String txKey = "TX" + i;
            Map<String, String> alarmesVazios = new HashMap<>();
            alarmesVazios.put("Video Lock", "TX não habilitada");
            alarmesVazios.put("Video White Clipping", "TX não habilitada");
            alarmesVazios.put("Audio Saturation", "TX não habilitada");
            alarmesVazios.put("status", "TX não habilitada");
            modulatorStatusPorTx.put(txKey, alarmesVazios);
        }

        return modulatorStatusPorTx;
    }
*/
private Map<String, Map<String, String>> coletarAlarmesModulator(WebDriver driver, WebDriverWait wait, int numTxEnabled) {
    Map<String, Map<String, String>> modulatorStatusPorTx = new LinkedHashMap<>();
    for (int i = 1; i <= numTxEnabled; i++) {
        String txKey = "TX" + i;
        System.out.println("Coletando alarmes do Modulator para " + txKey);
        Map<String, String> alarmesTx = new HashMap<>();
        String statusLocal = "Sucesso";
        try {
            WebElement homeBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("firstMenu")));
            homeBtn.click();
            wait.until(ExpectedConditions.urlContains("mainPage.html"));
            System.out.println("Navegado para Home para Modulator de " + txKey);
            WebElement goToBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.btn60x22.link_btn[onclick*='modBtnClick(" + i + ")']")));
            goToBtn.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("h_exciter")));
            System.out.println("Clicado em 'Go To' para Modulator de " + txKey);
            WebElement exciterDiv = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div#h_exciter")));
            exciterDiv.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("estatus_modulator")));
            System.out.println("Clicado em 'Exciter' para Modulator de " + txKey);
            WebElement modulatorLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("estatus_modulator")));
            modulatorLink.click();
            WebElement analogAlarmsContainer = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("analog_alarms")));
            System.out.println("Container 'analog_alarms' encontrado para " + txKey);

            // Alarme 1: TS Presence
            try {
                WebElement TSPresenceLed = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("isdbt_alrm_ts_presence")));
                String TSPresenceClass = TSPresenceLed.getAttribute("class");
                String TSPresenceStatus = interpretarLedModClass(TSPresenceClass);
                alarmesTx.put("TS Presence", TSPresenceStatus);
                System.out.println(txKey + " - TS Presence: " + TSPresenceStatus);
            } catch (org.openqa.selenium.NoSuchElementException e) {
                alarmesTx.put("TS Presence", "Erro: Elemento não encontrado - " + e.getMessage());
                statusLocal = "Aviso: TS Presence não encontrado";
                System.err.println(txKey + " - Erro ao coletar TS Presence: " + e.getMessage());
            } catch (Exception e) {
                alarmesTx.put("TS Presence", "Erro: " + e.getMessage());
                statusLocal = "Aviso: Erro em TS Presence";
                System.err.println(txKey + " - Erro ao coletar TS Presence: " + e.getMessage());
            }

            // Alarme 2: Rate Overflow
            try {
                WebElement RateOverflowLed = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("isdbt_alrm_rate_overflow")));
                String RateOverflowClass = RateOverflowLed.getAttribute("class");
                String RateOverflowStatus = interpretarLedModClass(RateOverflowClass);
                alarmesTx.put("Rate Overflow", RateOverflowStatus);
                System.out.println(txKey + " - Rate Overflow: " + RateOverflowStatus);
            } catch (org.openqa.selenium.NoSuchElementException e) {
                alarmesTx.put("Rate Overflow", "Erro: Elemento não encontrado - " + e.getMessage());
                statusLocal = "Aviso: Rate Overflow não encontrado";
                System.err.println(txKey + " - Erro ao coletar Rate Overflow: " + e.getMessage());
            } catch (Exception e) {
                alarmesTx.put("Rate Overflow", "Erro: " + e.getMessage());
                statusLocal = "Aviso: Erro em Rate Overflow";
                System.err.println(txKey + " - Erro ao coletar Rate Overflow: " + e.getMessage());
            }

            // Alarme 3: IIP
            try {
                WebElement IIPLed = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("isdbt_alrm_iip")));
                String IIPClass = IIPLed.getAttribute("class");
                String IIPStatus = interpretarLedModClass(IIPClass);
                alarmesTx.put("IIP", IIPStatus);
                System.out.println(txKey + " - IIP: " + IIPStatus);
            } catch (org.openqa.selenium.NoSuchElementException e) {
                alarmesTx.put("IIP", "Erro: Elemento não encontrado - " + e.getMessage());
                statusLocal = "Aviso: IIP não encontrado";
                System.err.println(txKey + " - Erro ao coletar IIP: " + e.getMessage());
            } catch (Exception e) {
                alarmesTx.put("IIP", "Erro: " + e.getMessage());
                statusLocal = "Aviso: Erro em IIP";
                System.err.println(txKey + " - Erro ao coletar IIP: " + e.getMessage());
            }

            // Alarme 4: Remux Tx Muting
            try {
                WebElement RemuxTxMutingLed = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("isdbt_alrm_remux_tx_mute")));
                String RemuxTxMutingClass = RemuxTxMutingLed.getAttribute("class");
                String RemuxTxMutingStatus = interpretarLedModClass(RemuxTxMutingClass);
                alarmesTx.put("Remux Tx Muting", RemuxTxMutingStatus);
                System.out.println(txKey + " - Remux Tx Muting: " + RemuxTxMutingStatus);
            } catch (org.openqa.selenium.NoSuchElementException e) {
                alarmesTx.put("Remux Tx Muting", "Erro: Elemento não encontrado - " + e.getMessage());
                statusLocal = "Aviso: Remux Tx Muting não encontrado";
                System.err.println(txKey + " - Erro ao coletar Remux Tx Muting: " + e.getMessage());
            } catch (Exception e) {
                alarmesTx.put("Remux Tx Muting", "Erro: " + e.getMessage());
                statusLocal = "Aviso: Erro em Remux Tx Muting";
                System.err.println(txKey + " - Erro ao coletar Remux Tx Muting: " + e.getMessage());
            }

            if (alarmesTx.values().stream().allMatch(v -> v.startsWith("Erro") || v.isEmpty())) {
                statusLocal = "Aviso: Nenhum alarme analógico encontrado";
            }
        } catch (TimeoutException e) {
            System.err.println("Timeout ao coletar dados do Modulator para " + txKey + ": " + e.getMessage());
            statusLocal = "Timeout";
            // Adicione valores de erro
        } catch (Exception e) {
            System.err.println("Erro ao coletar dados do Modulator para " + txKey + ": " + e.getMessage());
            statusLocal = "Erro geral";
        }
        alarmesTx.put("status", statusLocal);
        modulatorStatusPorTx.put(txKey, alarmesTx);
        System.out.println(txKey + " - Status Modulator: " + statusLocal);
    }
    for (int i = numTxEnabled + 1; i <= 8; i++) {
        String txKey = "TX" + i;
        Map<String, String> alarmesVazios = new HashMap<>();
        alarmesVazios.put("TS Presence", "TX não habilitada");
        alarmesVazios.put("Rate Overflow", "TX não habilitada");
        alarmesVazios.put("IIP", "TX não habilitada");
        alarmesVazios.put("Remux Tx Muting", "TX não habilitada");
        alarmesVazios.put("status", "TX não habilitada");
        modulatorStatusPorTx.put(txKey, alarmesVazios);
    }
    return modulatorStatusPorTx;
}

    private List<Map<String, String>> buscarAlarmesCommon(WebDriver driver, WebDriverWait wait) {
        List<Map<String, String>> faults = new ArrayList<>();

        try {
            String[] seletoresStatusLed = {
                    "#logCfgTable > div.tableContent > div:nth-child(3) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(4) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(5) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(6) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(7) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(13) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(14) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(15) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(16) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(17) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(23) > div.col3.thickBrdrRt > div[name='status']"
            };

            for (String seletorLed : seletoresStatusLed) {
                try {
                    WebElement statusElem = driver.findElement(By.cssSelector(seletorLed));
                    String classesLed = statusElem.getAttribute("class");

                    String statusInterpretado;
                    if (classesLed.contains("led16x16_fault")) {
                        statusInterpretado = "ERRO";
                    } else if (classesLed.contains("led led16x16_warn")) {
                        statusInterpretado = "ALARME";
                    } else if (classesLed.contains("led16x16_disabled")) {
                        statusInterpretado = "DESABILITADO";
                    } else if (classesLed.contains("led16x16_default")) {
                        statusInterpretado = "NORMAL";
                    } else {
                        statusInterpretado = "DESCONHECIDO";
                    }

                    int linhaIndex = extrairNumeroNthChild(seletorLed);
                    if (linhaIndex == -1) {
                        System.out.println("Não foi possível extrair o número do nth-child do seletor: " + seletorLed);
                        continue;
                    }

                    String seletorLinha = "#logCfgTable > div.tableContent > div:nth-child(" + linhaIndex + ")";
                    WebElement linha = driver.findElement(By.cssSelector(seletorLinha));

                    WebElement nomeAlarmeElem = linha.findElement(By.cssSelector("div.col2[name='message']"));
                    String nomeAlarme = nomeAlarmeElem.getText();

                    Map<String, String> fault = new HashMap<>();
                    fault.put("nome", nomeAlarme);
                    fault.put("statusLed", classesLed);
                    fault.put("statusInterpretado", statusInterpretado);

                    faults.add(fault);

                } catch (Exception e) {
                    System.err.println("Erro ao processar alarme na aba Common: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro geral ao buscar alarmes na aba Common: " + e.toString());
        }

        return faults;
    }

    private List<Map<String, String>> buscarAlarmesTx(WebDriver driver, WebDriverWait wait) {
        List<Map<String, String>> faults = new ArrayList<>();

        try {
            String[] seletoresStatusLed = {
                    "#logCfgTable > div.tableContent > div:nth-child(1) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(2) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(3) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(4) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(5) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(6) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(7) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(8) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(9) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(10) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(11) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(12) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(13) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(14) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(15) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(16) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(17) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(18) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(19) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(20) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(21) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(22) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(23) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(24) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(25) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(26) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(27) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(28) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(29) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(31) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(45) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(46) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(47) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(137) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(138) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(139) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(140) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(141) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(142) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(149) > div.col3.thickBrdrRt > div[name='status']",
                    "#logCfgTable > div.tableContent > div:nth-child(150) > div.col3.thickBrdrRt > div[name='status']"
            };

            for (String seletorLed : seletoresStatusLed) {
                try {
                    WebElement statusElem = driver.findElement(By.cssSelector(seletorLed));
                    String classesLed = statusElem.getAttribute("class");

                    String statusInterpretado;
                    if (classesLed.contains("led16x16_fault")) {
                        statusInterpretado = "ERRO";
                    } else if (classesLed.contains("led led16x16_warn")) {
                        statusInterpretado = "ALARME";
                    } else if (classesLed.contains("led16x16_disabled")) {
                        statusInterpretado = "DESABILITADO";
                    } else if (classesLed.contains("led16x16_default")) {
                        statusInterpretado = "NORMAL";
                    } else {
                        statusInterpretado = "DESCONHECIDO";
                    }

                    int linhaIndex = extrairNumeroNthChild(seletorLed);
                    if (linhaIndex == -1) {
                        System.out.println("Não foi possível extrair o número do nth-child do seletor: " + seletorLed);
                        continue;
                    }

                    String seletorLinha = "#logCfgTable > div.tableContent > div:nth-child(" + linhaIndex + ")";
                    WebElement linha = driver.findElement(By.cssSelector(seletorLinha));

                    WebElement nomeAlarmeElem = linha.findElement(By.cssSelector("div.col2[name='message']"));
                    String nomeAlarme = nomeAlarmeElem.getText();

                    Map<String, String> fault = new HashMap<>();
                    fault.put("nome", nomeAlarme);
                    fault.put("statusLed", classesLed);
                    fault.put("statusInterpretado", statusInterpretado);

                    faults.add(fault);

                } catch (Exception e) {
                    System.err.println("Erro ao processar alarme na aba TX: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro geral ao buscar alarmes na aba TX: " + e.toString());
        }

        return faults;
    }

    private int extrairNumeroNthChild(String seletor) {
        String prefix = "nth-child(";
        int start = seletor.indexOf(prefix);
        if (start == -1) return -1;
        int end = seletor.indexOf(")", start);
        if (end == -1) return -1;
        String numStr = seletor.substring(start + prefix.length(), end);
        try {
            return Integer.parseInt(numStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String interpretarAlcStatus(String statusRaw) {
        if (statusRaw == null || statusRaw.isEmpty()) return "N/A";
        if (statusRaw.toLowerCase().contains("enabled") || statusRaw.toLowerCase().contains("on"))
            return "Enabled (OK)";
        if (statusRaw.toLowerCase().contains("disabled") || statusRaw.toLowerCase().contains("off"))
            return "Disabled (Off)";
        return statusRaw;
    }

    private String interpretarLedAmpClass(String classe) {
        if (classe == null || classe.isEmpty()) return "Unknown";
        if (classe.contains("led16x16_fault")) return "Fault";
        if (classe.contains("led16x16_warn")) return "Warning";
        if (classe.contains("led16x16_disabled")) return "Disabled";
        if (classe.contains("led16x16_default")) return "Normal";
        return "Unknown (" + classe + ")";
    }

    private String interpretarLedModClass(String classe) {
        if (classe == null || classe.isEmpty()) return "Unknown";
        if (classe.contains("led16x16_fault")) return "Fault";  // Erro crítico
        if (classe.contains("led16x16_warn")) return "Warning";  // Aviso
        if (classe.contains("led16x16_disabled")) return "Disabled";  // Desabilitado
        if (classe.contains("led16x16_default")) return "Normal";  // Normal
        return "Unknown (" + classe + ")";  // Outros, com classe raw para debug
    }
}