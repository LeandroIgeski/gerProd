package view;

import dao.Conexao;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TelaReporteProducao extends JFrame {
    private JComboBox<String> cbRecurso;
    private JComboBox<String> cbOp;
    private JTextField txtQuantidade;
    private JLabel lblHorario;
    private JButton btnEnviar, btnFechar;

    private String tipoUsuario;  // <-- Armazena o tipo de usuário

    public TelaReporteProducao(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;

        setTitle("Reporte de Produção");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2));

        // Campo de seleção de recurso
        cbRecurso = new JComboBox<>(new String[]{
                "1001001 - Corte a Laser",
                "1002001 - Solda Robô 1",
                "1002002 - Solda Robô 2",
                "1003001 - Solda Manual",
                "1004001 - Pintura",
                "1005001 - Montagem"
        });

        // Campo de seleção de OP
        cbOp = new JComboBox<>();
        cbOp.addItem("Selecione uma OP");

        // Listener para atualizar OPs quando recurso mudar
        cbRecurso.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                carregarOp();
            }
        });

        // Inicializa com o primeiro recurso selecionado e carrega OPs
        cbRecurso.setSelectedIndex(0);
        carregarOp();

        txtQuantidade = new JTextField();

        lblHorario = new JLabel();
        atualizarHorario();

        btnEnviar = new JButton("Enviar");
        btnFechar = new JButton("Fechar");

        btnEnviar.addActionListener(e -> enviarReporte());
        btnFechar.addActionListener(e -> fecharTela());

        add(new JLabel("Selecione o Recurso:"));
        add(cbRecurso);
        add(new JLabel("Selecione a OP:"));
        add(cbOp);
        add(new JLabel("Quantidade:"));
        add(txtQuantidade);
        add(lblHorario);
        add(new JLabel()); // Espaço vazio
        add(btnEnviar);
        add(btnFechar);

        setVisible(true);
    }

    private void atualizarHorario() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        lblHorario.setText("Horário: " + sdf.format(new Date()));
    }

    private void carregarOp() {
        cbOp.removeAllItems();
        cbOp.addItem("Selecione uma OP");

        String recursoCodigo = cbRecurso.getSelectedItem().toString().split(" - ")[0];
        String sql = "SELECT op_numero FROM producao WHERE recurso_codigo = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, recursoCodigo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cbOp.addItem(rs.getString("op_numero"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar OPs.");
        }
    }

    private void enviarReporte() {
        String recursoCodigo = cbRecurso.getSelectedItem().toString().split(" - ")[0];
        String opNumero = (String) cbOp.getSelectedItem();
        String quantidadeStr = txtQuantidade.getText();
        int quantidade;

        if (opNumero == null || opNumero.equals("Selecione uma OP")) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma OP válida.");
            return;
        }

        try {
            quantidade = Integer.parseInt(quantidadeStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.");
            return;
        }

        String matriculaUsuario = "10804"; // TODO: pegar usuário logado dinamicamente

        Timestamp horario = new Timestamp(System.currentTimeMillis());

        String sql = "INSERT INTO reporte_producao (recurso_codigo, op_numero, quantidade, horario, matricula_usuario) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, recursoCodigo);
            stmt.setString(2, opNumero);
            stmt.setInt(3, quantidade);
            stmt.setTimestamp(4, horario);
            stmt.setString(5, matriculaUsuario);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Reporte de produção registrado com sucesso!");
            fecharTela();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao registrar reporte de produção.");
        }
    }

    private void fecharTela() {
        // Passa o tipoUsuario ao voltar para a TelaInicial
        new TelaInicial(tipoUsuario).setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        // Teste com tipoUsuario exemplo, por exemplo "Gerente"
        new TelaReporteProducao("Gerente");
    }
}
