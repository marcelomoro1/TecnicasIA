import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import busca.Heuristica;
import busca.BuscaLargura;
import busca.BuscaProfundidade;
import busca.Estado;
import busca.MostraStatusConsole;
import busca.Nodo;
import javax.swing.JOptionPane;

public class LabirintoObstaculos implements Estado, Heuristica {

    @Override
    public String getDescricao() {
        return "O jogo do labirinto é uma matriz NxM, onde são sorteadas duas peças:\n"
                + "peça que representa o portal de entrada no labirinto;\n"
                + "peça que representa o portal de saída no labirinto.\n"
                + "A Entrada é o portal em que um personagem qualquer inicia no labirinto e precisa se movimentar até a Saída.\n"
                + "O foco aqui é chegar na Saída pelo menor número de movimentos (células), mas não pode ser nas diagonais.";
    }

    final char matriz[][]; // preferir "immutable objects"
    int linhaEntrada1, colunaEntrada1; // Guarda a posição da primeira entrada (E1)
    int linhaEntrada2, colunaEntrada2; // Guarda a posição da segunda entrada (E2)
    int linhaSaida, colunaSaida; // Posição da saída (S)
    final String op; // operação que gerou o estado

    // Atenção... Matrizes precisam ser clonadas ao gerarmos novos estados
    char[][] clonar(char origem[][]) {
        char destino[][] = new char[origem.length][origem.length];
        for (int i = 0; i < origem.length; i++) {
            for (int j = 0; j < origem.length; j++) {
                destino[i][j] = origem[i][j];
            }
        }
        return destino;
    }

    /**
     * Construtor para o estado gerado na evolução/resolução do problema, recebe cada valor de atributo
     */
    public LabirintoObstaculos(char m[][], int linhaEntrada1, int colunaEntrada1, int linhaEntrada2, int colunaEntrada2, int linhaSaida, int colunaSaida, String o) {
        this.matriz = m;
        this.linhaEntrada1 = linhaEntrada1;
        this.colunaEntrada1 = colunaEntrada1;
        this.linhaEntrada2 = linhaEntrada2;
        this.colunaEntrada2 = colunaEntrada2;
        this.linhaSaida = linhaSaida;
        this.colunaSaida = colunaSaida;
        this.op = o;
    }

    /**
     * Construtor para o estado inicial
     */
    public LabirintoObstaculos(int dimensao, String o, int porcentagemObstaculos) {
        this.matriz = new char[dimensao][dimensao];
        this.op = o;

        int quantidadeObstaculos = (dimensao * dimensao) * porcentagemObstaculos / 100;
        System.out.println(quantidadeObstaculos);

        Random gerador = new Random();

        int entrada1 = gerador.nextInt(dimensao * dimensao); // Posição da primeira entrada
        int entrada2;
        do {
            entrada2 = gerador.nextInt(dimensao * dimensao); // Posição da segunda entrada
        } while (entrada1 == entrada2); // Garante que as entradas não são iguais

        int saida;
        do {
            saida = gerador.nextInt(dimensao * dimensao); // Posição da saída
        } while (entrada1 == saida || entrada2 == saida); // Garante que a saída não coincide com as entradas

        int contaPosicoes = 0;
        for (int i = 0; i < dimensao; i++) {
            for (int j = 0; j < dimensao; j++) {
                if (contaPosicoes == entrada1) {
                    this.matriz[i][j] = 'E';
                    this.linhaEntrada1 = i;
                    this.colunaEntrada1 = j;
                } else if (contaPosicoes == entrada2) {
                    this.matriz[i][j] = 'E';
                    this.linhaEntrada2 = i;
                    this.colunaEntrada2 = j;
                } else if (contaPosicoes == saida) {
                    this.matriz[i][j] = 'S';
                    this.linhaSaida = i;
                    this.colunaSaida = j;
                } else if (quantidadeObstaculos > 0 && gerador.nextInt(3) == 1) {
                    quantidadeObstaculos--;
                    this.matriz[i][j] = '@'; // Obstáculo
                } else {
                    this.matriz[i][j] = 'O'; // Espaço livre
                }
                contaPosicoes++;
            }
        }
    }

    /**
     * Verifica se o estado é meta (saída)
     */
    @Override
    public boolean ehMeta() {
        return (this.linhaEntrada1 == this.linhaSaida && this.colunaEntrada1 == this.colunaSaida) || 
               (this.linhaEntrada2 == this.linhaSaida && this.colunaEntrada2 == this.colunaSaida);
    }

    /**
     * Custo da ação
     */
    @Override
    public int custo() {
        return 1;
    }

    /**
     * Heurística (não implementada)
     */
    @Override
    public int h() {
        return 0;
    }

    /**
     * Gera uma lista de sucessores do nodo.
     */
    @Override
    public List<Estado> sucessores() {
        List<Estado> visitados = new LinkedList<Estado>(); // A lista de sucessores
        paraCima(visitados);
        paraBaixo(visitados);
        paraEsquerda(visitados);
        paraDireita(visitados);
        return visitados;
    }

    // Métodos para mover as entradas
    private void paraCima(List<Estado> visitados) {
        if (this.linhaEntrada1 > 0 && this.matriz[this.linhaEntrada1 - 1][this.colunaEntrada1] != '@') {
            char[][] mTemp = clonar(this.matriz);
            mTemp[this.linhaEntrada1][this.colunaEntrada1] = 'O';
            mTemp[this.linhaEntrada1 - 1][this.colunaEntrada1] = 'E';
            visitados.add(new LabirintoObstaculos(mTemp, this.linhaEntrada1 - 1, this.colunaEntrada1, this.linhaEntrada2, this.colunaEntrada2, this.linhaSaida, this.colunaSaida, "Movendo para cima"));
        }
    }

    private void paraBaixo(List<Estado> visitados) {
        if (this.linhaEntrada1 < this.matriz.length - 1 && this.matriz[this.linhaEntrada1 + 1][this.colunaEntrada1] != '@') {
            char[][] mTemp = clonar(this.matriz);
            mTemp[this.linhaEntrada1][this.colunaEntrada1] = 'O';
            mTemp[this.linhaEntrada1 + 1][this.colunaEntrada1] = 'E';
            visitados.add(new LabirintoObstaculos(mTemp, this.linhaEntrada1 + 1, this.colunaEntrada1, this.linhaEntrada2, this.colunaEntrada2, this.linhaSaida, this.colunaSaida, "Movendo para baixo"));
        }
    }

    private void paraEsquerda(List<Estado> visitados) {
        if (this.colunaEntrada1 > 0 && this.matriz[this.linhaEntrada1][this.colunaEntrada1 - 1] != '@') {
            char[][] mTemp = clonar(this.matriz);
            mTemp[this.linhaEntrada1][this.colunaEntrada1] = 'O';
            mTemp[this.linhaEntrada1][this.colunaEntrada1 - 1] = 'E';
            visitados.add(new LabirintoObstaculos(mTemp, this.linhaEntrada1, this.colunaEntrada1 - 1, this.linhaEntrada2, this.colunaEntrada2, this.linhaSaida, this.colunaSaida, "Movendo para esquerda"));
        }
    }

    private void paraDireita(List<Estado> visitados) {
        if (this.colunaEntrada1 < this.matriz.length - 1 && this.matriz[this.linhaEntrada1][this.colunaEntrada1 + 1] != '@') {
            char[][] mTemp = clonar(this.matriz);
            mTemp[this.linhaEntrada1][this.colunaEntrada1] = 'O';
            mTemp[this.linhaEntrada1][this.colunaEntrada1 + 1] = 'E';
            visitados.add(new LabirintoObstaculos(mTemp, this.linhaEntrada1, this.colunaEntrada1 + 1, this.linhaEntrada2, this.colunaEntrada2, this.linhaSaida, this.colunaSaida, "Movendo para direita"));
        }
    }

    /**
     * Verifica se um estado é igual a outro (usado para poda)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof LabirintoObstaculos) {
            LabirintoObstaculos e = (LabirintoObstaculos) o;
            for (int i = 0; i < e.matriz.length; i++) {
                for (int j = 0; j < e.matriz.length; j++) {
                    if (e.matriz[i][j] != this.matriz[i][j]) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Retorna o hashCode desse estado (usado para poda, conjunto de fechados)
     */
    @Override
    public int hashCode() {
        String estado = "";
        for (int i = 0; i < this.matriz.length; i++) {
            for (int j = 0; j < this.matriz.length; j++) {
                estado = estado + this.matriz[i][j];
            }
        }
        return estado.hashCode();
    }

    @Override
    public String toString() {
        StringBuffer resultado = new StringBuffer();
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz.length; j++) {
                resultado.append(this.matriz[i][j]);
                resultado.append("\t");
            }
            resultado.append("\n");
        }
        resultado.append("Posição Entrada 1: " + this.linhaEntrada1 + "," + this.colunaEntrada1 + "\n");
        resultado.append("Posição Entrada 2: " + this.linhaEntrada2 + "," + this.colunaEntrada2 + "\n");
        resultado.append("Posição Saída: " + this.linhaSaida + "," + this.colunaSaida + "\n");
        return "\n" + op + "\n" + resultado + "\n\n";
    }

    public static void main(String[] a) {
        LabirintoObstaculos estadoInicial = null;
        int dimensao;
        int porcentagemObstaculos;
        int qualMetodo;
        Nodo n;
        try {
            dimensao = Integer.parseInt(JOptionPane.showInputDialog(null,"Entre com a dimensão do Puzzle!"));
            porcentagemObstaculos = Integer.parseInt(JOptionPane.showInputDialog(null,"Porcentagem de obstáculos!"));
            qualMetodo = Integer.parseInt(JOptionPane.showInputDialog(null,"1 - Profundidade\n2 - Largura"));
            estadoInicial = new LabirintoObstaculos(dimensao, "estado inicial", porcentagemObstaculos);
            
            // Método de busca escolhido
            switch (qualMetodo) {
                case 1:
                    System.out.println("busca em PROFUNDIDADE");
                    n = new BuscaProfundidade(new MostraStatusConsole()).busca(estadoInicial);
                    break;
                case 2:
                    System.out.println("busca em LARGURA");
                    n = new BuscaLargura(new MostraStatusConsole()).busca(estadoInicial);
                    break;
                default:
                    n = null;
                    JOptionPane.showMessageDialog(null, "Método não implementado");
            }

            if (n == null) {
                System.out.println("sem solucao!");
                System.out.println(estadoInicial);
            } else {
                System.out.println("solucao:\n" + n.montaCaminho() + "\n\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        System.exit(0);
    }
}
