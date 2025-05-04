import javax.swing.*; // Importa as classes necessárias do pacote Swing
import java.awt.*;     // Importa as classes necessárias para o layout
import java.awt.event.*; // Para usar ActionListener
import java.sql.*;        // Para interação com o banco de dados
import java.text.SimpleDateFormat; // Para formatação do horário
import java.util.Date;    // Para manipulação de datas

public class TelaInicial extends JFrame {
    private JButton btnInicioProducao, btnReporteProducao, btnStatusRecurso, btnRecursos, btnDisponibilidade, btnPerformance, btnSair;

    public TelaInicial() {
        // Configuração da janela
        setTitle("Tela Inicial");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 1, 10, 10));  // 7 botões, um para cada função

        // Botões para as funcionalidades
        btnInicioProducao = new JButton("Início de Produção");
        btnInicioProducao.addActionListener(e -> abrirTelaInicioProducao());

        btnReporteProducao = new JButton("Reporte de Produção");
        btnReporteProducao.addActionListener(e -> abrirTelaReporteProducao());

        btnStatusRecurso = new JButton("Status de Recurso");
        btnStatusRecurso.addActionListener(e -> abrirTelaStatusRecurso());

        btnRecursos = new JButton("Recursos");
        btnRecursos.addActionListener(e -> abrirTelaRecursos());

        btnDisponibilidade = new JButton("Disponibilidade");
        btnDisponibilidade.addActionListener(e -> abrirTelaDisponibilidade());

        btnPerformance = new JButton("Performance");
        btnPerformance.addActionListener(e -> abrirTelaPerformance());

        btnSair = new JButton("Sair");
        btnSair.addActionListener(e -> System.exit(0));

        // Adicionar os botões à janela
        add(btnInicioProducao);
        add(btnReporteProducao);
        add(btnStatusRecurso);
        add(btnRecursos);
        add(btnDisponibilidade);
        add(btnPerformance);
        add(btnSair);

        // Exibir a janela
        setVisible(true);
    }

    private void abrirTelaInicioProducao() {
        new TelaInicioProducao();
        dispose(); // Fecha a tela atual (Tela Inicial)
    }

    private void abrirTelaReporteProducao() {
        new TelaReporteProducao();
        dispose(); // Fecha a tela atual (Tela Inicial)
    }

    private void abrirTelaStatusRecurso() {
        new TelaStatusRecurso(); // Abre a tela de Status de Recurso
        dispose(); // Fecha a tela atual (Tela Inicial)
    }

    private void abrirTelaRecursos() {
        JOptionPane.showMessageDialog(this, "Tela de Recursos ainda não implementada.");
    }

    private void abrirTelaDisponibilidade() {
        JOptionPane.showMessageDialog(this, "Tela de Disponibilidade ainda não implementada.");
    }

    private void abrirTelaPerformance() {
        JOptionPane.showMessageDialog(this, "Tela de Performance ainda não implementada.");
    }

    public static void main(String[] args) {
        new TelaInicial();
    }
}
