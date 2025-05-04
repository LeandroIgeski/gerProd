import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TelaInicioProducao extends JFrame {
    private JComboBox<String> cbRecurso;
    private JTextField txtNumeroOP;
    private JLabel lblHorario;
    private JButton btnEnviar, btnFechar;
    private Timer timer;

    public TelaInicioProducao() {
        setTitle("Início de Produção");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2));

        // Campo de seleção de recurso
        cbRecurso = new JComboBox<>(new String[]{
                "1001001 - Corte a Laser",
                "1002001 - Solda Robô 1",
                "1002002 - Solda Robô 2",
                "1003001 - Solda Manual",
                "1004001 - Pintura",
                "1005001 - Montagem"
        });

        // Campo de número da OP
        txtNumeroOP = new JTextField();

        // Label do horário
        lblHorario = new JLabel();
        atualizarHorario(); // Atualiza o horário inicial

        // Atualização automática do horário a cada segundo
        timer = new Timer(1000, e -> atualizarHorario());
        timer.start();

        // Botões
        btnEnviar = new JButton("Enviar");
        btnFechar = new JButton("Fechar");

        btnEnviar.addActionListener(e -> enviarProducao());
        btnFechar.addActionListener(e -> fecharTela());

        // Adicionar componentes à tela
        add(new JLabel("Selecione o Recurso:"));
        add(cbRecurso);
        add(new JLabel("Número da OP:"));
        add(txtNumeroOP);
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

    private void enviarProducao() {
        String recursoCodigo = cbRecurso.getSelectedItem().toString().split(" - ")[0]; // Ex: "1001001"
        String numeroOP = txtNumeroOP.getText();
        Timestamp horarioAtual = new Timestamp(System.currentTimeMillis());

        // Substitua "10804" pelo código da matrícula do usuário logado
        String matriculaUsuario = "10804"; // Aqui você deve pegar o usuário logado, por exemplo, da tela de login

        String sql = "INSERT INTO producao (recurso_codigo, op_numero, horario_inicio, matricula) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar(); // Conexão automática com try-with-resources
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Definir os parâmetros do PreparedStatement
            stmt.setString(1, recursoCodigo);
            stmt.setString(2, numeroOP);
            stmt.setTimestamp(3, horarioAtual);
            stmt.setString(4, matriculaUsuario); // Substitua por usuário logado real

            // Executar a inserção no banco de dados
            stmt.executeUpdate();

            // Feedback ao usuário
            JOptionPane.showMessageDialog(this, "Início de produção registrado com sucesso!");
            fecharTela(); // Fecha a janela de produção e volta à tela inicial
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao registrar início de produção.");
        }
    }

    private void fecharTela() {
        dispose(); // Fecha a janela de produção
        new TelaInicial(); // Retorna para a Tela Inicial
    }

    public static void main(String[] args) {
        new TelaInicioProducao();
    }
}
