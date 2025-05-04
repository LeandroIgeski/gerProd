package view;

import dao.Conexao;
import model.Recurso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelaPerformance extends JFrame {
    private String tipoUsuario;

    private JComboBox<String> cbRecursos;
    private DefaultTableModel tableModel;
    private JTable tabelaPerformance;
    private JButton btnFechar;
    private List<Recurso> recursos;

    public TelaPerformance() {
        this("default");
    }

    public TelaPerformance(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;

        setTitle("Performance dos Recursos - Usuário: " + tipoUsuario);
        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Painel topo para seleção do recurso
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTopo.add(new JLabel("Selecione o Recurso:"));

        cbRecursos = new JComboBox<>();
        painelTopo.add(cbRecursos);

        add(painelTopo, BorderLayout.NORTH);

        // Configurar tabela
        String[] colunas = {"OP", "Código da Peça", "Quantidade", "Horário"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabela só leitura
            }
        };
        tabelaPerformance = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabelaPerformance);
        add(scrollPane, BorderLayout.CENTER);

        // Botão fechar
        btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> fecharTela());
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelRodape.add(btnFechar);
        add(painelRodape, BorderLayout.SOUTH);

        // Inicializa lista e carrega recursos
        recursos = new ArrayList<>();
        carregarRecursos();

        // Atualizar tabela quando selecionar recurso
        cbRecursos.addActionListener(e -> {
            int idx = cbRecursos.getSelectedIndex();
            if (idx >= 0 && idx < recursos.size()) {
                Recurso recursoSelecionado = recursos.get(idx);
                carregarPerformanceDoRecurso(recursoSelecionado);
            }
        });

        // Selecionar primeiro recurso automaticamente, se houver
        if (!recursos.isEmpty()) {
            cbRecursos.setSelectedIndex(0);
            carregarPerformanceDoRecurso(recursos.get(0));
        }

        setVisible(true);
    }

    private void fecharTela() {
        dispose();
        new TelaInicial(tipoUsuario).setVisible(true); // VOLTAR para TelaInicial
    }

    private void carregarRecursos() {
        recursos.clear();
        cbRecursos.removeAllItems();

        try (Connection conn = Conexao.conectar()) {
            String sql = "SELECT codigo, nome FROM recursos";  // tabela correta e colunas certas
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String nome = rs.getString("nome");
                Recurso r = new Recurso(codigo, nome);
                recursos.add(r);
                cbRecursos.addItem(codigo + " - " + nome);  // Mostrar código e nome para o usuário
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar recursos: " + e.getMessage());
        }
    }

    private void carregarPerformanceDoRecurso(Recurso recurso) {
        tableModel.setRowCount(0);

        try (Connection conn = Conexao.conectar()) {
            // Ajuste para buscar na tabela correta, reporte_producao
            String sql = "SELECT op_numero AS op, quantidade, horario, recurso_codigo " +
                    "FROM reporte_producao WHERE recurso_codigo = ? ORDER BY horario DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, recurso.getCodigo());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String op = rs.getString("op");
                // A tabela reporte_producao não tem codigo_peca, então coloque um placeholder
                String codigoPeca = "-";
                int quantidade = rs.getInt("quantidade");
                String horario = rs.getString("horario");

                Object[] linha = {op, codigoPeca, quantidade, horario};
                tableModel.addRow(linha);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar performance: " + e.getMessage());
        }
    }
}
