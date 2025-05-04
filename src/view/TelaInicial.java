package view;

import dao.Conexao;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class TelaInicial extends JFrame {
    private JButton btnInicioProducao, btnReporteProducao, btnStatusRecurso, btnRecursos,
            btnDisponibilidade, btnPerformance, btnGerarRelatorio, btnSair;

    private String tipoUsuario; // "Operador", "Gerente", "Diretor"
    private Connection connection;

    public TelaInicial(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;

        try {
            connection = Conexao.conectar();  // Conecta ao banco
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados:\n" + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        setTitle("Tela Inicial");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 1, 10, 10));  // 8 botões

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

        btnGerarRelatorio = new JButton("Gerar Relatório");
        btnGerarRelatorio.addActionListener(e -> abrirTelaRelatorio());

        btnSair = new JButton("Sair");
        btnSair.addActionListener(e -> {
            fecharConexao();
            System.exit(0);
        });

        add(btnInicioProducao);
        add(btnReporteProducao);
        add(btnStatusRecurso);
        add(btnRecursos);
        add(btnDisponibilidade);
        add(btnPerformance);

        if (tipoUsuario.equals("Gerente") || tipoUsuario.equals("Diretor")) {
            add(btnGerarRelatorio);
        }

        add(btnSair);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void abrirTelaInicioProducao() {
        new TelaInicioProducao(tipoUsuario).setVisible(true);
        dispose();
    }

    private void abrirTelaReporteProducao() {
        new TelaReporteProducao(tipoUsuario).setVisible(true);
        dispose();
    }

    private void abrirTelaStatusRecurso() {
        new TelaStatusRecurso(tipoUsuario).setVisible(true);
        dispose();
    }

    private void abrirTelaRecursos() {
        new TelaRecursos(tipoUsuario).setVisible(true);
        dispose();
    }

    private void abrirTelaDisponibilidade() {
        new TelaDisponibilidade(tipoUsuario).setVisible(true);
        dispose();
    }

    private void abrirTelaPerformance() {
        new TelaPerformance(tipoUsuario).setVisible(true);
        dispose();
    }

    private void abrirTelaRelatorio() {
        // Aqui passamos somente o tipoUsuario, pois TelaGerarRelatorio tem construtor com só uma String
        new TelaGerarRelatorio(tipoUsuario).setVisible(true);
        dispose();
    }

    private void fecharConexao() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // Exemplo: tipo de usuário "Gerente"
        SwingUtilities.invokeLater(() -> new TelaInicial("Gerente"));
    }
}
