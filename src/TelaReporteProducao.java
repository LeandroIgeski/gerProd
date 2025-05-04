import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TelaReporteProducao extends JFrame {
    private JComboBox<String> cbRecurso;
    private JComboBox<String> cbOp;
    private JTextField txtQuantidade;
    private JLabel lblHorario;
    private JButton btnEnviar, btnFechar;

    public TelaReporteProducao() {
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
        cbOp.addItem("Selecione um recurso primeiro");

        // Adicionando um listener para atualizar as OPs quando o recurso for alterado
        cbRecurso.addItemListener(e -> carregarOp());

        // Campo de quantidade
        txtQuantidade = new JTextField();

        // Label do horário
        lblHorario = new JLabel();
        atualizarHorario(); // Atualiza o horário inicial

        // Botões
        btnEnviar = new JButton("Enviar");
        btnFechar = new JButton("Fechar");

        btnEnviar.addActionListener(e -> enviarReporte());
        btnFechar.addActionListener(e -> fecharTela());

        // Adicionar componentes à tela
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
        // Limpar opções anteriores
        cbOp.removeAllItems();
        cbOp.addItem("Selecione uma OP");

        // Buscar OPs para o recurso selecionado
        String recursoCodigo = cbRecurso.getSelectedItem().toString().split(" - ")[0]; // Ex: "1001001"
        String sql = "SELECT op_numero FROM producao WHERE recurso_codigo = ?"; // Ajustado para buscar todas as OPs associadas ao recurso

        try (Connection conn = Conexao.conectar(); // Conexão com banco de dados
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
        String recursoCodigo = cbRecurso.getSelectedItem().toString().split(" - ")[0]; // Ex: "1001001"
        String opNumero = cbOp.getSelectedItem().toString();
        String quantidadeStr = txtQuantidade.getText();
        int quantidade = 0;

        // Validar quantidade
        try {
            quantidade = Integer.parseInt(quantidadeStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.");
            return;
        }

        // Pegar a matrícula do usuário logado
        String matriculaUsuario = "10804"; // Aqui você deve pegar o usuário logado

        // Definir o horário atual
        Timestamp horario = new Timestamp(System.currentTimeMillis());

        // SQL para inserir dados na tabela reporte_producao
        String sql = "INSERT INTO reporte_producao (recurso_codigo, op_numero, quantidade, horario, matricula_usuario) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar(); // Conexão com banco de dados
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, recursoCodigo);
            stmt.setString(2, opNumero);
            stmt.setInt(3, quantidade);
            stmt.setTimestamp(4, horario);
            stmt.setString(5, matriculaUsuario); // Substitua pela matrícula do usuário logado

            // Executar a inserção no banco de dados
            stmt.executeUpdate();

            // Feedback ao usuário
            JOptionPane.showMessageDialog(this, "Reporte de produção registrado com sucesso!");
            fecharTela(); // Fechar a tela e voltar para a Tela Inicial
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao registrar reporte de produção.");
        }
    }

    private void fecharTela() {
        // Fechar a tela de reporte de produção e abrir a TelaInicial
        new TelaInicial();  // Cria a instância da TelaInicial
        dispose();  // Fecha a tela de reporte de produção atual
    }

    public static void main(String[] args) {
        new TelaReporteProducao();
    }
}
