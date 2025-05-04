package view;

import dao.Conexao;
import model.Recurso;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelaRecursos extends JFrame {
    private JList<String> listaRecursos;
    private DefaultListModel<String> listModel;
    private JLabel lblStatusAtual, lblHorario;
    private JButton btnFechar;
    private List<Recurso> recursos;

    private String tipoUsuario;  // ADICIONADO

    // Construtor alterado para receber tipoUsuario
    public TelaRecursos(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;

        setTitle("Recursos Cadastrados");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Painel da lista
        listModel = new DefaultListModel<>();
        listaRecursos = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(listaRecursos);

        // Painel inferior para exibir status
        JPanel painelStatus = new JPanel(new GridLayout(3, 1, 5, 5));
        lblStatusAtual = new JLabel("Status atual: ");
        lblHorario = new JLabel("Horário: ");
        painelStatus.add(lblStatusAtual);
        painelStatus.add(lblHorario);

        // Botão fechar
        btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> fecharTela());
        painelStatus.add(btnFechar);

        add(scrollPane, BorderLayout.CENTER);
        add(painelStatus, BorderLayout.SOUTH);

        // Carregar recursos do banco
        carregarRecursos();

        // Evento de clique na lista
        listaRecursos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int index = listaRecursos.getSelectedIndex();
                if (index >= 0) {
                    Recurso recursoSelecionado = recursos.get(index);
                    mostrarStatusAtual(recursoSelecionado);
                }
            }
        });

        setVisible(true);
    }

    private void carregarRecursos() {
        recursos = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM recursos")) {

            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String nome = rs.getString("nome");
                Recurso recurso = new Recurso(codigo, nome);
                recursos.add(recurso);
                listModel.addElement(codigo + " - " + nome);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar recursos.");
            e.printStackTrace();
        }
    }

    private void mostrarStatusAtual(Recurso recurso) {
        // Consulta usando a tabela única "disponibilidade" e filtrando pelo código do recurso
        String sql = "SELECT status, horario_inicio FROM disponibilidade WHERE recurso_codigo = ? ORDER BY horario_inicio DESC LIMIT 1";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, recurso.getCodigo());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                Timestamp horario = rs.getTimestamp("horario_inicio");

                lblStatusAtual.setText("Status atual: " + status);
                lblHorario.setText("Horário: " + horario.toString());
            } else {
                lblStatusAtual.setText("Status atual: Nenhum registro encontrado.");
                lblHorario.setText("Horário: -");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar status do recurso.");
            e.printStackTrace();
        }
    }

    private void fecharTela() {
        dispose();
        new TelaInicial(tipoUsuario).setVisible(true);
    }
}
