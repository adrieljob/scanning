package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// Adicione @ComponentScan se seus controladores e serviços não estiverem no mesmo pacote ou subpacotes de App.java
// Como todos os seus arquivos estão em 'org.example', o @SpringBootApplication já faz o scan automaticamente.
// Mas se você tiver problemas de injeção de dependência, pode descomentar e ajustar.
@ComponentScan(basePackages = {"org.example"})
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}