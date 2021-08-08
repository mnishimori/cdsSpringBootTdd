package com.github.mnishimori;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LibraryApiApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void drawPicture() {
        String figura = "losango";
        Integer linhas = 9;
        Integer colunas = 0;
        Integer quantidadeSimbolos = 0;
        Integer i;
        Integer j;

        if (figura == "quadrado") {
            colunas = linhas * 2;
            for (i = 1; i <= linhas; i++) {
                for (j = 1; j <= colunas; j++) {
                    System.out.print("%");
                    quantidadeSimbolos++;
                }
                System.out.println("");
            }
        }

        if (figura == "triangulo") {
            colunas = linhas; // a mesma quantidade de linhas será a de colunas
            for (i = 1; i <= linhas; i++) {
                Integer imprimirNaColuna = colunas - i; // determina qual coluna deverá iniciar a impressão do símbolo
                for (j = 1; j <= colunas; j++) {
                    if (j > imprimirNaColuna) { // se a coluna atual é maior do que a coluna definida na instrução anterior ao for de colunas
                        System.out.print("%");
                        quantidadeSimbolos++;
                    } else {
                        System.out.print(" ");
                    }
                }
                System.out.println("");
            }
        }

        if (figura == "losango") {
            if (linhas % 2 != 0) { // o número de linhas do losango deve ser sempre ímpar
                Integer lin = (linhas / 2) + 1; // cálculo do número de linhas que será impresso na tela
                for (i = 1; i <= lin; i++) {
                    // imprime espaços na coluna toda na linha atual
                    for (j = 1; j <= lin - i; j++) {
                        System.out.print(" ");
                    }
                    // imprime % apenas na coluna correta
                    for (j = 1; j <= (2 * i) - 1; j++) {
                        System.out.print("%");
                        quantidadeSimbolos++;
                    }
                    System.out.println("");
                }
                for ( i = 1; i <= lin - 1; i++) {
                    // imprime espaços na coluna toda na linha atual
                    for (j = 1; j <= i; j++) {
                        System.out.print(" ");
                    }
                    // imprime % apenas na coluna correta
                    for (j = 1; j <= 2 * (lin-i)-1; j++){
                        System.out.print("%");
                        quantidadeSimbolos++;
                    }
                    System.out.println("");
                }
            }
        }

        System.out.println("Figura geométrica selecionada " + figura);
        System.out.println("Quantidade de linhas " + linhas);
        System.out.println("Quantidade de símbolos % " + quantidadeSimbolos);
    }
}
