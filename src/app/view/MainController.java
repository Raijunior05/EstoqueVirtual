package app.view;

import app.dao.ProdutoDAO;
import app.model.*;
import app.service.FinanceiroService;
import app.service.MonitorService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import java.time.LocalDate;

public class MainController {

    // Tabela e Colunas de Produtos
    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, String> colMarca;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TableColumn<Produto, Integer> colEstoque;
    @FXML private TableColumn<Produto, String> colSpec;

    // CAMPO DE PESQUISA
    @FXML private TextField txtPesquisa;

    // LISTA MESTRE (Guarda os dados originais para o filtro funcionar)
    private ObservableList<Produto> masterData = FXCollections.observableArrayList();

    // Campos de Cadastro
    @FXML private ComboBox<String> cbTipo;
    @FXML private TextField txtNome, txtMarca, txtQtd;
    @FXML private TextField txtPrecoCusto, txtMargem, txtPrecoVenda;
    @FXML private HBox boxEspecificos;
    @FXML private TextField txtSpecInt, txtSpecDouble, txtSpecTexto;
    @FXML private TextField txtEstoqueMin;
    @FXML private DatePicker dpDataCadastro;

    // Avisos e Dashboard
    @FXML private DatePicker dpDataOperacao; // Data para Vender/Repor
    @FXML private ListView<String> listaAlertas;
    @FXML private Label lblBalanco;

    // --- FINANCEIRO & ORÇAMENTO ---
    @FXML private Label lblTotalEntradas, lblTotalSaidas, lblSaldoFinanceiro;
    @FXML private ComboBox<String> cbTipoTransacao;
    @FXML private TextField txtDescTransacao, txtValorTransacao;
    @FXML private DatePicker dpDataFinanceiro;// data para lançamento manual
    @FXML private TableView<Transacao> tabelaFinanceira;
    @FXML private TableColumn<Transacao, String> colFinData;
    @FXML private TableColumn<Transacao, String> colFinTipo;
    @FXML private TableColumn<Transacao, String> colFinDesc;
    @FXML private TableColumn<Transacao, Double> colFinValor;

    // --- META FINANCEIRA
    @FXML private TextField txtMetaInput;
    @FXML private Label lblFaltaMeta, lblPorcentagemMeta;
    @FXML private ProgressBar barraMeta;

    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private MonitorService monitorService = new MonitorService();
    private FinanceiroService financeiroService = new FinanceiroService();

    @FXML
    public void initialize() {
        // Inicializa as datas com o dia de hoje
        dpDataCadastro.setValue(LocalDate.now());
        dpDataOperacao.setValue(LocalDate.now());
        dpDataFinanceiro.setValue(LocalDate.now());

        // 1. Configuração Básica das Colunas de Produto
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        colPreco.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : String.format("R$ %.2f", item));
            }
        });

        // 2. Configuração da Coluna de Especificação
        colSpec.setCellValueFactory(cellData -> {
            Produto p = cellData.getValue();
            String detalhe = "-";
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

        // CONFIGURAÇÃO DA PESQUISA (FILTRO)

        // Carrega dados do banco para a memória (masterData)
        masterData.addAll(produtoDAO.listarTodos());

        // Cria a lista filtrada envolvendo a masterData
        FilteredList<Produto> filteredData = new FilteredList<>(masterData, p -> true);

        // Adiciona o "ouvinte" no campo de texto
        if (txtPesquisa != null) {
            txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(produto -> {
                    // Se o filtro estiver vazio, mostra tudo
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    // Regras do filtro (Nome, Marca, Categoria)
                    if (produto.getNome().toLowerCase().contains(lowerCaseFilter)) return true;
                    if (produto.getMarca().toLowerCase().contains(lowerCaseFilter)) return true;
                    if (produto.getCategoria().toLowerCase().contains(lowerCaseFilter)) return true;

                    return false; // Não achou
                });
            });
        }

        // Cria a lista ordenada (para permitir clicar no cabeçalho da tabela)
        SortedList<Produto> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tabelaProdutos.comparatorProperty());

        // Joga a lista final na tabela
        tabelaProdutos.setItems(sortedData);

        // ---------------------------------------------

        // Configuração do ComboBox
        cbTipo.setItems(FXCollections.observableArrayList(
                "Mouse", "Monitor", "Teclado", "Armazenamento",
                "Roteador", "Microfone", "Camera", "Fone",
                "Impressora", "Controle", "Outros"
        ));
        cbTipo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> atualizarCamposEspecificos(newVal));
        cbTipo.getSelectionModel().select("Outros");

        // --- LÓGICA DE CÁLCULO AUTOMÁTICO DE PREÇO ---
        // Quando digitar no custo ou margem, calcula a venda sozinho
        if (txtPrecoCusto != null && txtMargem != null) {
            txtPrecoCusto.setOnKeyReleased(e -> calcularPrecoVenda());
            txtMargem.setOnKeyReleased(e -> calcularPrecoVenda());
        }

        // --- FINANCEIRO ---
        cbTipoTransacao.setItems(FXCollections.observableArrayList("RECEITA", "DESPESA"));

        colFinDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colFinValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        // Formatação condicional (Verde/Vermelho)
        colFinValor.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(String.format("R$ %.2f", item));
                    Transacao t = getTableView().getItems().get(getIndex());
                    if (t instanceof app.model.Despesa) setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    else setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                }
            }
        });

        colFinTipo.setCellValueFactory(cellData -> {
            String tipo = (cellData.getValue() instanceof app.model.Receita) ? "ENTRADA" : "SAÍDA";
            return new SimpleStringProperty(tipo);
        });

        configurarFinanceiro();
        atualizarDashboard();

        // Carrega a meta inicial no campo
        double metaAtual = financeiroService.getMeta();
        if (txtMetaInput != null) {
            txtMetaInput.setText(String.valueOf(metaAtual));
        }
    }

    // funcao auxiliar para calcular o preço
    private void calcularPrecoVenda() {
        try {
            String custoStr = txtPrecoCusto.getText().replace(",", ".");
            String margemStr = txtMargem.getText().replace(",", ".");

            if (!custoStr.isEmpty() && !margemStr.isEmpty()) {
                double custo = Double.parseDouble(custoStr);
                double margem = Double.parseDouble(margemStr);

                // Fórmula: Custo + (Lucro)
                double vendaSugerida = custo + (custo * (margem / 100));

                txtPrecoVenda.setText(String.format("%.2f", vendaSugerida).replace(",", "."));
            }
        } catch (NumberFormatException ex) {
            // Ignora letras
        }
    }

    private void configurarFinanceiro() {
        cbTipoTransacao.setItems(FXCollections.observableArrayList("RECEITA", "DESPESA"));
        colFinData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colFinDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colFinValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        colFinValor.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(String.format("R$ %.2f", item));
                    Transacao t = getTableView().getItems().get(getIndex());
                    if (t instanceof app.model.Despesa) setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    else setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                }
            }
        });

        colFinTipo.setCellValueFactory(cellData -> {
            String tipo = (cellData.getValue() instanceof app.model.Receita) ? "ENTRADA" : "SAÍDA";
            return new SimpleStringProperty(tipo);
        });
    }

    // --- MÉTODOS VISUAIS AUXILIARES ---
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
        campo.setVisible(true); campo.setManaged(true); campo.setPromptText(dica); campo.clear();
    }

    // --- AÇÕES DE PRODUTO ---
    @FXML
    public void handleSalvarProduto() {
        try {
            String tipo = cbTipo.getValue();
            String nome = txtNome.getText();
            String marca = txtMarca.getText();
            int qtd = Integer.parseInt(txtQtd.getText());
            double custo = Double.parseDouble(txtPrecoCusto.getText().replace(",", "."));
            double venda = Double.parseDouble(txtPrecoVenda.getText().replace(",", "."));
            int estMin = Integer.parseInt(txtEstoqueMin.getText());
            String dataCad = dpDataCadastro.getValue().toString(); // Formato YYYY-MM-DD

            Produto p;
            switch (tipo) {
                case "Mouse": p = new Mouse(0, nome, marca, custo, venda, qtd, estMin, dataCad, Integer.parseInt(txtSpecInt.getText())); break;
                case "Monitor": p = new Monitor(0, nome, marca, custo, venda, qtd, estMin, dataCad, Double.parseDouble(txtSpecDouble.getText())); break;
                case "Teclado": p = new Teclado(0, nome, marca, custo, venda, qtd, estMin, dataCad, txtSpecTexto.getText()); break;
                case "Armazenamento": p = new Armazenamento(0, nome, marca, custo, venda, qtd, estMin, dataCad, Integer.parseInt(txtSpecInt.getText())); break;
                case "Roteador": p = new Roteador(0, nome, marca, custo, venda, qtd, estMin, dataCad, Integer.parseInt(txtSpecInt.getText())); break;
                case "Microfone": p = new Microfone(0, nome, marca, custo, venda, qtd, estMin, dataCad, txtSpecTexto.getText()); break;
                case "Camera": p = new Camera(0, nome, marca, custo, venda, qtd, estMin, dataCad, txtSpecTexto.getText()); break;
                case "Fone": p = new Fone(0, nome, marca, custo, venda, qtd, estMin, dataCad, txtSpecTexto.getText()); break;
                case "Impressora": p = new Impressora(0, nome, marca, custo, venda, qtd, estMin, dataCad, txtSpecTexto.getText()); break;
                case "Controle": p = new Controle(0, nome, marca, custo, venda, qtd, estMin, dataCad, txtSpecTexto.getText()); break;
                default: p = new Produto(0, nome, "Geral", marca, custo, venda, qtd, estMin, dataCad); break;
            }
            produtoDAO.salvar(p);

            //Gera a Despesa Financeira Automática
            double custoTotalInvestimento = custo * qtd;

            if (custoTotalInvestimento > 0) {
                financeiroService.registrarDespesa("Compra Inicial: " + nome + " (" + qtd + "x)", custoTotalInvestimento, dataCad);
            }

            carregarTabela();
            atualizarFinanceiro();
            atualizarDashboard();

            limparCampos();
            mostrarSucesso("Sucesso", "Produto cadastrado e despesa de R$" + custoTotalInvestimento + " registrada!");

        } catch (Exception e) {
            mostrarErro("Erro", "Verifique os dados preenchidos.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleReporEstoque() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarErro("Seleção", "Selecione um produto na tabela para repor.");
            return;
        }

        try {
            int qtd = Integer.parseInt(txtQtd.getText());

            if (qtd <= 0) {
                mostrarErro("Quantidade Inválida", "A quantidade deve ser maior que zero.");
                return;
            }

            String dataOp = dpDataOperacao.getValue().toString();

            // 1. Atualiza o Estoque no Banco
            selecionado.setEstoque(selecionado.getEstoque() + qtd);
            produtoDAO.atualizar(selecionado);

            // Gera Despesa baseada no CUSTO REAL ---
            double custoTotal = selecionado.getPrecoCusto() * qtd;

            if (custoTotal > 0) {
                financeiroService.registrarDespesa("Reposição: " + selecionado.getNome() + " (" + qtd + "x)", custoTotal, dataOp);
            }

            //Atualiza as telas
            tabelaProdutos.refresh(); // Atualiza número na tabela
            atualizarDashboard();     // Atualiza alertas e balanço
            atualizarFinanceiro();    // Atualiza o saldo e extrato

            mostrarSucesso("Estoque Atualizado",
                    "Foram adicionadas " + qtd + " unidades.\n" +
                            "Despesa de R$ " + String.format("%.2f", custoTotal) + " registrada no caixa.");

        } catch (NumberFormatException e) {
            mostrarErro("Erro", "Digite uma quantidade válida no campo 'Qtd'.");
        }
    }

    @FXML
    public void handleBaixarEstoque() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            System.out.println("DEBUG: Nenhum produto selecionado.");
            return;
        }

        try {
            // Pega o valor digitado
            int qtdDigitada = Integer.parseInt(txtQtd.getText());

            // Pega o estoque real do objeto
            int estoqueAtual = selecionado.getEstoque();

            // Imprime no console para vermos a verdade
            System.out.println("--- DEBUG VENDA ---");
            System.out.println("Produto: " + selecionado.getNome());
            System.out.println("Estoque no Objeto: " + estoqueAtual);
            System.out.println("Qtd Digitada: " + qtdDigitada);
            System.out.println("Condição (Estoque < Qtd): " + (estoqueAtual < qtdDigitada));
            System.out.println("-------------------");

            if (estoqueAtual < qtdDigitada) {
                mostrarErro("Estoque Insuficiente",
                        "Você quer vender " + qtdDigitada + ", mas o sistema consta apenas " + estoqueAtual + " no estoque.");
                return;
            }

            String dataOp = dpDataOperacao.getValue().toString();

            selecionado.setEstoque(estoqueAtual - qtdDigitada);
            selecionado.setQtdVendida(selecionado.getQtdVendida() + qtdDigitada);
            selecionado.setValorTotalVendido(selecionado.getValorTotalVendido() + (selecionado.getPreco() * qtdDigitada));
            produtoDAO.atualizar(selecionado);

            // Receita baseada no PREÇO DE VENDA
            double valorVenda = selecionado.getPreco() * qtdDigitada;
            financeiroService.registrarReceita("Venda: " + selecionado.getNome(), valorVenda, dataOp);

            tabelaProdutos.refresh();
            atualizarDashboard();
            atualizarFinanceiro();
            mostrarSucesso("Venda", "Venda registrada! Receita: R$ " + valorVenda);

        } catch (NumberFormatException e) {
            mostrarErro("Erro", "Digite uma quantidade válida (número inteiro).");
        }
    }

    @FXML
    public void handleRemoverProduto() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            produtoDAO.deletar(selecionado.getId());
            masterData.remove(selecionado);;
            atualizarDashboard();
        }
    }

    // --- AÇÕES FINANCEIRAS ---
    @FXML
    public void handleSalvarMeta() {
        try {
            double novaMeta = Double.parseDouble(txtMetaInput.getText().replace(",", "."));
            financeiroService.salvarMeta(novaMeta);

            atualizarFinanceiro();
            mostrarSucesso("Meta Definida", "Nova meta de R$ " + novaMeta + " configurada!");

        } catch (NumberFormatException e) {
            mostrarErro("Valor Inválido", "Digite um número válido para a meta.");
        }
    }

    @FXML
    public void handleSalvarTransacao() {
        try {
            String tipo = cbTipoTransacao.getValue();
            String desc = txtDescTransacao.getText();
            double valor = Double.parseDouble(txtValorTransacao.getText().replace(",", "."));
            String data = dpDataFinanceiro.getValue().toString();

            if (tipo == null || desc.isEmpty()) {
                mostrarErro("Erro", "Preencha tipo e descrição.");
                return;
            }
            if (tipo.equals("RECEITA")) financeiroService.registrarReceita(desc, valor, data);
            else financeiroService.registrarDespesa(desc, valor, data);

            txtDescTransacao.clear();
            txtValorTransacao.clear();
            atualizarFinanceiro();
        } catch (Exception e) { mostrarErro("Erro", "Valor inválido."); }
    }

    // --- UTILS & ATUALIZAÇÕES ---

    private void carregarTabela() {
        masterData.clear();
        masterData.addAll(produtoDAO.listarTodos());
    }

    @FXML
    public void atualizarDashboard() {
        listaAlertas.setItems(FXCollections.observableArrayList(monitorService.verificarAlertas()));
        double total = tabelaProdutos.getItems().stream().mapToDouble(p -> p.getPrecoCusto() * p.getEstoque()).sum();
        lblBalanco.setText(String.format("R$ %.2f", total));
    }

    @FXML
    public void atualizarFinanceiro() {
        tabelaFinanceira.setItems(FXCollections.observableArrayList(financeiroService.getHistorico()));

        lblTotalEntradas.setText(String.format("R$ %.2f", financeiroService.calcularTotalEntradas()));
        lblTotalSaidas.setText(String.format("R$ %.2f", financeiroService.calcularTotalSaidas()));

        double saldo = financeiroService.calcularSaldo();
        lblSaldoFinanceiro.setText(String.format("R$ %.2f", saldo));
        if (saldo >= 0) lblSaldoFinanceiro.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: green;");
        else lblSaldoFinanceiro.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: red;");

        // Atualiza Barra de Progresso da Meta
        double meta = financeiroService.getMeta();
        double falta = financeiroService.calcularFaltaParaMeta();
        double progresso = financeiroService.calcularProgressoMeta();

        if (falta > 0) {
            lblFaltaMeta.setText(String.format("R$ %.2f", falta));
            lblFaltaMeta.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e67e22;");
        } else {
            lblFaltaMeta.setText("META BATIDA! 🎉");
            lblFaltaMeta.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        }

        barraMeta.setProgress(progresso);
        lblPorcentagemMeta.setText(String.format("%.1f%% Concluído (Meta: R$ %.2f)", progresso * 100, meta));

        if (progresso >= 1.0) barraMeta.setStyle("-fx-accent: #f1c40f;");
        else barraMeta.setStyle("-fx-accent: #27ae60;");
    }

    private void limparCampos() {
        txtNome.clear(); txtMarca.clear(); txtQtd.clear();
        txtPrecoCusto.clear(); txtMargem.clear(); txtPrecoVenda.clear();
        txtSpecInt.clear(); txtSpecDouble.clear(); txtSpecTexto.clear();
    }

    private void mostrarErro(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo); alert.setHeaderText(null); alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarSucesso(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo); alert.setHeaderText(null); alert.setContentText(msg);
        alert.showAndWait();
    }
}