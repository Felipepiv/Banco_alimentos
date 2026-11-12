package ui;

import model.CategoriaAlimento;
import model.Doacao;
import model.Lote;
import service.DoacaoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class DoacaoFrame extends JFrame {
    private final DoacaoService service = new DoacaoService();

    // Campos do Formulário de Doações
    private final JTextField txtId = new JTextField(15);
    private final JTextField txtDoador = new JTextField(15);
    private final JComboBox<CategoriaAlimento> cbCategoria = new JComboBox<>(CategoriaAlimento.values());
    private final JTextField txtValidade = new JTextField(15);
    private final JTextField txtQuantidade = new JTextField(15);
    private final JTextField txtUnidade = new JTextField(15);

    // Tabela de Doações
    private final DefaultTableModel doacoesModel = new DefaultTableModel(new String[]{"ID Doação", "Doador", "Categoria", "Validade", "Qtd", "Unidade"}, 0);
    private final JTable tabelaDoacoes = new JTable(doacoesModel);

    // Tabela de Lotes (Substituindo a antiga aba de testes fictícios)
    private final DefaultTableModel lotesModel = new DefaultTableModel(new String[]{"ID Lote", "Qtd Disponível", "Unidade Medida", "Validade", "Status/Alerta"}, 0);
    private final JTable tabelaLotes = new JTable(lotesModel);

    // Campos para baixa manual em Lotes
    private final JTextField txtLoteIdBaixa = new JTextField(10);
    private final JTextField txtQtdBaixa = new JTextField(5);

    public DoacaoFrame() {
        super("Banco de Alimentos - Gestão de Doações e Lotes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 680);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // -------------------------------------------------------------
        // ABA 1: Cadastro de Doações
        // -------------------------------------------------------------
        JPanel panelCadastro = new JPanel(new BorderLayout(10, 10));
        panelCadastro.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Cadastrar Doação (Gera Lote Automático)"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 0
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        panelForm.add(new JLabel("ID da Doação:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        panelForm.add(txtId, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.0;
        panelForm.add(new JLabel("Doador:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0;
        panelForm.add(txtDoador, gbc);

        // Linha 1
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        panelForm.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        panelForm.add(cbCategoria, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.0;
        panelForm.add(new JLabel("Validade (AAAA-MM-DD):"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 1.0;
        panelForm.add(txtValidade, gbc);

        // Linha 2
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        panelForm.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        panelForm.add(txtQuantidade, gbc);

        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.0;
        panelForm.add(new JLabel("Unidade Medida:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; gbc.weightx = 1.0;
        panelForm.add(txtUnidade, gbc);

        // Botões CRUD
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnCadastrar = new JButton("Cadastrar Doação");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");

        panelBotoes.add(btnCadastrar);
        panelBotoes.add(btnAtualizar);
        panelBotoes.add(btnExcluir);
        panelBotoes.add(btnLimpar);

        JPanel panelTopo = new JPanel(new BorderLayout());
        panelTopo.add(panelForm, BorderLayout.CENTER);
        panelTopo.add(panelBotoes, BorderLayout.SOUTH);

        panelCadastro.add(panelTopo, BorderLayout.NORTH);
        panelCadastro.add(new JScrollPane(tabelaDoacoes), BorderLayout.CENTER);

        // -------------------------------------------------------------
        // ABA 2: Produtos e Lotes (Controle Real de Estoque)
        // -------------------------------------------------------------
        JPanel panelLotes = new JPanel(new BorderLayout(10, 10));
        panelLotes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelBaixa = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelBaixa.setBorder(BorderFactory.createTitledBorder("Retirada / Distribuição de Itens do Lote"));

        panelBaixa.add(new JLabel("ID do Lote:"));
        panelBaixa.add(txtLoteIdBaixa);
        panelBaixa.add(new JLabel("Qtd a Retirar:"));
        panelBaixa.add(txtQtdBaixa);

        JButton btnRetirar = new JButton("Confirmar Retirada");
        panelBaixa.add(btnRetirar);

        panelLotes.add(panelBaixa, BorderLayout.NORTH);
        panelLotes.add(new JScrollPane(tabelaLotes), BorderLayout.CENTER);

        // Adiciona as abas no TabbedPane
        tabbedPane.addTab("Cadastro de Doações", panelCadastro);
        tabbedPane.addTab("Produtos e Lotes Gerados", panelLotes);

        add(tabbedPane);

        // Eventos
        btnCadastrar.addActionListener(e -> cadastrarDoacao());
        btnAtualizar.addActionListener(e -> atualizarDoacao());
        btnExcluir.addActionListener(e -> excluirDoacao());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnRetirar.addActionListener(e -> efetuarBaixaLote());

        // Evento ao clicar em uma linha da tabela de Lotes para preencher o campo de baixa automaticamente
        tabelaLotes.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tabelaLotes.getSelectedRow();
            if (selectedRow >= 0) {
                txtLoteIdBaixa.setText(lotesModel.getValueAt(selectedRow, 0).toString());
            }
        });

        txtUnidade.setText("kg");
        txtValidade.setText(LocalDate.now().plusDays(30).toString());

        // Carrega o que já está gravado no banco
        try {
            atualizarTabelas();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao conectar ao banco de dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cadastrarDoacao() {
        try {
            String id = txtId.getText().trim();
            String doador = txtDoador.getText().trim();
            CategoriaAlimento categoria = (CategoriaAlimento) cbCategoria.getSelectedItem();
            LocalDate validade = LocalDate.parse(txtValidade.getText().trim());
            int qtd = Integer.parseInt(txtQuantidade.getText().trim());
            String unidade = txtUnidade.getText().trim();

            Doacao d = new Doacao(id, doador, categoria, validade, qtd, unidade);
            service.cadastrarDoacao(d);

            atualizarTabelas();
            limparFormulario();
            JOptionPane.showMessageDialog(this, "Doação cadastrada com sucesso! Lote LOTE-" + id + " gerado no estoque.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso / Erro de Validação", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void atualizarDoacao() {
        try {
            String id = txtId.getText().trim();
            String doador = txtDoador.getText().trim();
            CategoriaAlimento categoria = (CategoriaAlimento) cbCategoria.getSelectedItem();
            LocalDate validade = LocalDate.parse(txtValidade.getText().trim());
            int qtd = Integer.parseInt(txtQuantidade.getText().trim());
            String unidade = txtUnidade.getText().trim();

            Doacao d = new Doacao(id, doador, categoria, validade, qtd, unidade);
            service.atualizar(d);

            atualizarTabelas();
            limparFormulario();
            JOptionPane.showMessageDialog(this, "Doação e Lote atualizados com sucesso!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao Atualizar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirDoacao() {
        try {
            String id = txtId.getText().trim();
            service.excluir(id);
            atualizarTabelas();
            limparFormulario();
            JOptionPane.showMessageDialog(this, "Doação e Lote excluídos com sucesso!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao Excluir", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void efetuarBaixaLote() {
        try {
            String idLote = txtLoteIdBaixa.getText().trim();
            int qtd = Integer.parseInt(txtQtdBaixa.getText().trim());

            service.retirarDoLote(idLote, qtd);

            atualizarTabelas();
            txtQtdBaixa.setText("");
            JOptionPane.showMessageDialog(this, "Retirada de " + qtd + " itens do lote " + idLote + " realizada com sucesso!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro na Retirada / Regra de Negócio", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarTabelas() {
        // Atualiza Tabela de Doações
        doacoesModel.setRowCount(0);
        for (Doacao d : service.listar()) {
            doacoesModel.addRow(new Object[]{
                    d.getId(), d.getDoador(), d.getCategoria(), d.getData_validade(), d.getQuantidade(), d.getUnidade_medida()
            });
        }

        // Atualiza Tabela de Lotes Reais
        lotesModel.setRowCount(0);
        for (Lote l : service.listarLotes()) {
            String status = "OK (Válido)";
            if (!l.verificarValidade()) {
                status = "⚠️ VENCIDO";
            } else if (l.gerarAlertaVencimento()) {
                status = "⚡ Vence em breve";
            }

            lotesModel.addRow(new Object[]{
                    l.getId(), l.getQuantidade(), l.getUnidadeMedida(), l.getDataValidade(), status
            });
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtDoador.setText("");
        txtQuantidade.setText("");
        txtValidade.setText(LocalDate.now().plusDays(30).toString());
        txtUnidade.setText("kg");
    }
}
