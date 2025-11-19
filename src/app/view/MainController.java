package app.view;

import app.dao.ProdutoDAO;
import app.model.*;
import app.service.MonitorService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class MainController {

    // Tabela e Colunas
    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, String> colMarca;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TableColumn<Produto, Integer> colEstoque;

    // NOVA COLUNA
    @FXML private TableColumn<Produto, String> colSpec;

    // Campos
    @FXML private ComboBox<String> cbTipo;
    @FXML private TextField txtNome, txtMarca, txtPreco, txtQtd;
    @FXML private HBox boxEspecificos;
    @FXML private TextField txtSpecInt, txtSpecDouble, txtSpecTexto;

    // Avisos e Dashboard
    @FXML private ListView<String> listaAlertas;
    @FXML private Label lblBalanco;

    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private MonitorService monitorService = new MonitorService();

    @FXML
    public void initialize() {
        // 1. Configuração Básica das Colunas
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        // Formatação de Preço (R$)
        colPreco.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : String.format("R$ %.2f", item));
            }
        });

        // 2. CONFIGURAÇÃO DA COLUNA DE ESPECIFICAÇÃO (Lógica Visual)
        colSpec.setCellValueFactory(cellData -> {
            Produto p = cellData.getValue();
            String detalhe = "-"; // Padrão

            // Verifica qual o tipo e formata o texto para a tabela
            if (p instanceof Mouse) detalhe = ((Mouse) p).getDpi() + " DPI";
            else if (p instanceof Monitor) detalhe = ((Monitor) p).getPolegadas() + "\" pol";
            else if (p instanceof Armazenamento) detalhe = ((Armazenamento) p).getCapacidadeGB() + " GB";
            else if (p instanceof Roteador) detalhe = ((Roteador) p).getVelocidadeMbps() + " Mbps";
            else if (p instanceof Teclado) detalhe = ((Teclado) p).getTipoSwitch();
            else if (p instanceof Microfone) detalhe = ((Microfone) p).getTipo();
            else if (p instanceof Camera) detalhe = ((Camera) p).getResolucao();
            else if (p instanceof Fone) detalhe = ((Fone) p).getConexao();
            else if (p instanceof Impressora) detalhe = ((Impressora) p).getTipoImpressao();
            else if (p instanceof Controle) detalhe = ((Controle) p).getCompatibilidade();

            return new SimpleStringProperty(detalhe);
        });

        // 3. Configuração do ComboBox
        cbTipo.setItems(FXCollections.observableArrayList(
                "Mouse", "Monitor", "Teclado", "Armazenamento",
                "Roteador", "Microfone", "Camera", "Fone",
                "Impressora", "Controle", "Outros"
        ));

        cbTipo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            atualizarCamposEspecificos(newVal);
        });

        cbTipo.getSelectionModel().select("Outros");
        carregarTabela();
        atualizarDashboard();
    }

    private void atualizarCamposEspecificos(String tipo) {
        esconderTodosCampos();
        if (tipo == null) return;

        switch (tipo) {
            case "Mouse": mostrarCampo(txtSpecInt, "DPI (ex: 1600)"); break;
            case "Monitor": mostrarCampo(txtSpecDouble, "Polegadas (ex: 24.5)"); break;
            case "Armazenamento": mostrarCampo(txtSpecInt, "Capacidade GB (ex: 500)"); break;
            case "Roteador": mostrarCampo(txtSpecInt, "Velocidade Mbps (ex: 300)"); break;
            case "Teclado": mostrarCampo(txtSpecTexto, "Tipo de Tecla (ex: Mecânico)"); break;
            case "Microfone": mostrarCampo(txtSpecTexto, "Tipo (ex: Condensador)"); break;
            case "Camera": mostrarCampo(txtSpecTexto, "Resolução (ex: 4K)"); break;
            case "Fone": mostrarCampo(txtSpecTexto, "Conexão (ex: Bluetooth)"); break;
            case "Impressora": mostrarCampo(txtSpecTexto, "Tipo Impressão (ex: Laser)"); break;
            case "Controle": mostrarCampo(txtSpecTexto, "Compatibilidade (ex: PC/Xbox)"); break;
        }
    }

    private void esconderTodosCampos() {
        txtSpecInt.setVisible(false); txtSpecInt.setManaged(false);
        txtSpecDouble.setVisible(false); txtSpecDouble.setManaged(false);
        txtSpecTexto.setVisible(false); txtSpecTexto.setManaged(false);
    }

    private void mostrarCampo(TextField campo, String dica) {
        campo.setVisible(true);
        campo.setManaged(true);
        campo.setPromptText(dica);
        campo.clear();
    }

    // --- SALVAR ---
    @FXML
    public void handleSalvarProduto() {
        try {
            String tipo = cbTipo.getValue();
            String nome = txtNome.getText();
            String marca = txtMarca.getText();
            // Correção para aceitar vírgula no preço
            double preco = Double.parseDouble(txtPreco.getText().replace(",", "."));
            int qtd = Integer.parseInt(txtQtd.getText());

            Produto p;

            switch (tipo) {
                case "Mouse": p = new Mouse(0, nome, marca, preco, qtd, Integer.parseInt(txtSpecInt.getText())); break;
                case "Monitor": p = new Monitor(0, nome, marca, preco, qtd, Double.parseDouble(txtSpecDouble.getText())); break;
                case "Teclado": p = new Teclado(0, nome, marca, preco, qtd, txtSpecTexto.getText()); break;
                case "Armazenamento": p = new Armazenamento(0, nome, marca, preco, qtd, Integer.parseInt(txtSpecInt.getText())); break;
                case "Roteador": p = new Roteador(0, nome, marca, preco, qtd, Integer.parseInt(txtSpecInt.getText())); break;
                case "Microfone": p = new Microfone(0, nome, marca, preco, qtd, txtSpecTexto.getText()); break;
                case "Camera": p = new Camera(0, nome, marca, preco, qtd, txtSpecTexto.getText()); break;
                case "Fone": p = new Fone(0, nome, marca, preco, qtd, txtSpecTexto.getText()); break;
                case "Impressora": p = new Impressora(0, nome, marca, preco, qtd, txtSpecTexto.getText()); break;
                case "Controle": p = new Controle(0, nome, marca, preco, qtd, txtSpecTexto.getText()); break;
                default: p = new Produto(0, nome, "Geral", marca, preco, qtd, 5); break;
            }

            produtoDAO.salvar(p);
            carregarTabela();
            limparCampos();
            atualizarDashboard();

        } catch (NumberFormatException e) {
            mostrarErro("Erro de Formato", "Verifique os números (Preço, Qtd, Specs).");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- AÇÕES DE ESTOQUE ---
    @FXML
    public void handleReporEstoque() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;
        try {
            int qtd = Integer.parseInt(txtQtd.getText());
            selecionado.setEstoque(selecionado.getEstoque() + qtd);
            produtoDAO.atualizar(selecionado);
            tabelaProdutos.refresh();
            atualizarDashboard();
        } catch (NumberFormatException e) { mostrarErro("Erro", "Digite a quantidade para repor."); }
    }

    @FXML
    public void handleBaixarEstoque() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;
        try {
            int qtd = Integer.parseInt(txtQtd.getText());
            if (selecionado.getEstoque() < qtd) {
                mostrarErro("Estoque Insuficiente", "Não há itens suficientes.");
                return;
            }
            selecionado.setEstoque(selecionado.getEstoque() - qtd);
            produtoDAO.atualizar(selecionado);
            tabelaProdutos.refresh();
            atualizarDashboard();
        } catch (NumberFormatException e) { mostrarErro("Erro", "Digite a quantidade para baixar."); }
    }

    @FXML
    public void handleRemoverProduto() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;
        produtoDAO.deletar(selecionado.getId());
        tabelaProdutos.getItems().remove(selecionado);
        atualizarDashboard();
    }

    // --- UTILS ---
    private void carregarTabela() {
        tabelaProdutos.setItems(FXCollections.observableArrayList(produtoDAO.listarTodos()));
    }

    @FXML
    public void atualizarDashboard() {
        // Atualiza os alertas na Aba 2
        listaAlertas.setItems(FXCollections.observableArrayList(monitorService.verificarAlertas()));

        // Calcula valor total do estoque para a Aba 3
        double total = tabelaProdutos.getItems().stream().mapToDouble(p -> p.getPreco() * p.getEstoque()).sum();
        lblBalanco.setText(String.format("R$ %.2f", total));
    }

    private void limparCampos() {
        txtNome.clear(); txtMarca.clear(); txtPreco.clear(); txtQtd.clear();
        txtSpecInt.clear(); txtSpecDouble.clear(); txtSpecTexto.clear();
    }

    private void mostrarErro(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}