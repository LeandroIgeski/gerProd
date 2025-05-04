package view;

import dao.Conexao;
import model.Recurso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelaDisponibilidade extends JFrame {
    private JComboBox<String> cbRecursos;
    private JTable tabelaStatus;
    private DefaultTableModel tableModel;
    private JButton btnFechar;

    private List<Recurso> recursos;

    private String tipoUsuario;  // ADICIONADO

    // Construtor alterado para receber tipoUsuario
    public TelaDisponibilidade(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;

        setTitle("Disponibilidade dos Recursos");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Painel superior para escolher recurso
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTopo.add(new JLabel("Selecione o Recurso:"));

        cbRecursos = new JComboBox<>();
        painelTopo.add(cbRecursos);

        add(painelTopo, BorderLayout.NORTH);

        // Tabela para mostrar os status
        String[] colunas = {"Status", "Início", "Fim"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabela somente leitura
            }
        };
        tabelaStatus = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabelaStatus);
        add(scrollPane, BorderLayout.CENTER);

        // Botão fechar
        btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> fecharTela());
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelRodape.add(btnFechar);
        add(painelRodape, BorderLayout.SOUTH);

        carregarRecursos();

        // Quando o recurso for selecionado, atualiza a tabela
        cbRecursos.addActionListener(e -> {
            if (cbRecursos.getSelectedIndex() >= 0) {
                Recurso recursoSelecionado = recursos.get(cbRecursos.getSelectedIndex());
                carregarStatusDoRecurso(recursoSelecionado);
            }
        });

        setVisible(true);
    }

    private void carregarRecursos() {
        recursos = new ArrayList<>();
        cbRecursos.removeAllItems();

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM recursos")) {

            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String nome = rs.getString("nome");
                Recurso recurso = new Recurso(codigo, nome);
                recursos.add(recurso);
                cbRecursos.addItem(codigo + " - " + nome);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar recursos.");
            e.printStackTrace();
        }
    }

    private void carregarStatusDoRecurso(Recurso recurso) {
        // Limpa a tabela
        tableModel.setRowCount(0);

        String sql = "SELECT status, horario_inicio, horario_fim FROM disponibilidade WHERE recurso_codigo = ? ORDER BY horario_inicio";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, recurso.getCodigo());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    Timestamp inicio = rs.getTimestamp("horario_inicio");
                    Timestamp fim = rs.getTimestamp("horario_fim");

                    String fimStr = (fim != null) ? fim.toString() : "Em andamento";

                    tableModel.addRow(new Object[]{status, inicio.toString(), fimStr});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar status do recurso.");
            e.printStackTrace();
        }
    }

    private void fecharTela() {
        dispose();
        new TelaInicial(tipoUsuario).setVisible(true);
    }
}
