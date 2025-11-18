package app.view;

import app.dao.ProdutoDAO;
import app.model.Produto;
import app.service.MonitorService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController {

    // Widgets Estoque
    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, String> colMarca;
    @FXML private TableColumn<Produto, Integer> colEstoque;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TextField txtNome, txtMarca, txtPreco, txtQtd;

    // Widgets Dashboard
    @FXML private ListView<String> listaAlertas;
    @FXML private Label lblBalanco;

    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private MonitorService monitorService = new MonitorService();

    @FXML
    public void initialize() {
        // Configura colunas da tabela
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));

        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        // (Opcional) Formatação para aparecer com R$ e duas casas decimais
        colPreco.setCellFactory(tc -> new TableCell<Produto, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("R$ %.2f", item));
                }
            }
        });

        carregarTabela();
    }

    private void carregarTabela() {
        tabelaProdutos.setItems(FXCollections.observableArrayList(produtoDAO.listarTodos()));
    }

    @FXML
    public void handleSalvarProduto() {
        try {
            String nome = txtNome.getText();
            String marca = txtMarca.getText();
            double preco = Double.parseDouble(txtPreco.getText());
            int qtd = Integer.parseInt(txtQtd.getText());

            // Cria produto com estoque minimo padrão de 5
            Produto p = new Produto(0, nome, "Geral", marca, preco, qtd, 5);
            produtoDAO.salvar(p);

            carregarTabela();
            limparCampos();
        } catch (Exception e) {
            System.out.println("Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    public void atualizarDashboard() {
        // 1. Atualiza Alertas (LLL)
        listaAlertas.setItems(FXCollections.observableArrayList(monitorService.verificarAlertas()));

        // 2. Atualiza Financeiro (Simulação simples para exemplo)
        lblBalanco.setText("Saldo Atual: Implementar lógica com TransacaoDAO");
    }

    private void limparCampos() {
        txtNome.clear(); txtMarca.clear(); txtPreco.clear(); txtQtd.clear();
    }
    @FXML
    public void handleRemoverProduto() { // Pode manter o nome ou mudar para handleDarBaixa
        // 1. Pega o produto selecionado
        Produto produto = tabelaProdutos.getSelectionModel().getSelectedItem();

        if (produto == null) {
            System.out.println("Selecione um produto na tabela!");
            return;
        }

        try {
            // 2. Pega a quantidade que o usuário digitou na caixinha de texto
            int qtdBaixa = Integer.parseInt(txtQtd.getText());

            // 3. Verifica se tem estoque suficiente
            if (qtdBaixa > produto.getEstoque()) {
                System.out.println("Erro: Você tentou remover mais do que tem no estoque!");
                return; // Para aqui
            }

            // 4. Calcula o novo estoque
            int novoEstoque = produto.getEstoque() - qtdBaixa;
            produto.setEstoque(novoEstoque);

            // 5. Atualiza no Banco de Dados
            produtoDAO.atualizar(produto);

            // 6. Atualiza a tabela visualmente
            tabelaProdutos.refresh();
            System.out.println("Estoque atualizado! Restam: " + novoEstoque);

            // (Opcional) Limpa o campo de quantidade
            txtQtd.clear();

        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um número válido na caixa 'Qtd'");
        }
    }
    @FXML
    public void handleReporEstoque() {
        // 1. Pega o produto selecionado
        Produto produto = tabelaProdutos.getSelectionModel().getSelectedItem();

        if (produto == null) {
            System.out.println("Selecione um produto para repor o estoque!");
            return;
        }

        try {
            // 2. Pega a quantidade digitada
            int qtdEntrada = Integer.parseInt(txtQtd.getText());

            if (qtdEntrada <= 0) {
                System.out.println("Digite um valor positivo para repor.");
                return;
            }

            // 3. SOMA ao estoque atual
            int novoEstoque = produto.getEstoque() + qtdEntrada;
            produto.setEstoque(novoEstoque);

            // 4. Salva no banco e atualiza a tela
            produtoDAO.atualizar(produto);
            tabelaProdutos.refresh();

            System.out.println("Estoque reposto! Total agora: " + novoEstoque);
            txtQtd.clear(); // Limpa o campo para a próxima operação

        } catch (NumberFormatException e) {
            System.out.println("Digite um número válido na caixa 'Qtd'");
        }
    }
}