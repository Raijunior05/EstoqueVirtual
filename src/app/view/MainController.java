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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.util.List;

public class MainController {

    // Tabela e Colunas de Produtos
    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, String> colMarca;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TableColumn<Produto, Integer> colEstoque;
    @FXML private TableColumn<Produto, String> colSpec;

    @FXML private BarChart<String, Number> graficoEstoque;

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

    @FXML private BarChart<String, Number> graficoPareto;

    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private MonitorService monitorService = new MonitorService();
    private FinanceiroService financeiroService = new FinanceiroService();

    @FXML
    public void initialize() {
        // Inicializa as datas com o dia de hoje
        if (dpDataCadastro != null) dpDataCadastro.setValue(LocalDate.now());
        if (dpDataOperacao != null) dpDataOperacao.setValue(LocalDate.now());
        if (dpDataFinanceiro != null) dpDataFinanceiro.setValue(LocalDate.now());

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
        masterData.addAll(produtoDAO.listarTodos());
        FilteredList<Produto> filteredData = new FilteredList<>(masterData, p -> true);

        if (txtPesquisa != null) {
            txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(produto -> {
                    if (newValue == null || newValue.isEmpty()) return true;
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (produto.getNome().toLowerCase().contains(lowerCaseFilter)) return true;
                    if (produto.getMarca().toLowerCase().contains(lowerCaseFilter)) return true;
                    if (produto.getCategoria().toLowerCase().contains(lowerCaseFilter)) return true;
                    return false;
                });
            });
        }

        SortedList<Produto> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tabelaProdutos.comparatorProperty());
        tabelaProdutos.setItems(sortedData);

        // Configuração do ComboBox
        cbTipo.setItems(FXCollections.observableArrayList(
                "Mouse", "Monitor", "Teclado", "Armazenamento",
                "Roteador", "Microfone", "Camera", "Fone",
                "Impressora", "Controle", "Outros"
        ));
        cbTipo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> atualizarCamposEspecificos(newVal));
        cbTipo.getSelectionModel().select("Outros");

        // Lógica de Preço Automático
        if (txtPrecoCusto != null && txtMargem != null) {
            txtPrecoCusto.setOnKeyReleased(e -> calcularPrecoVenda());
            txtMargem.setOnKeyReleased(e -> calcularPrecoVenda());
        }

        // FINANCEIRO
        cbTipoTransacao.setItems(FXCollections.observableArrayList("RECEITA", "DESPESA"));
        configurarFinanceiro();
        atualizarDashboard();
        atualizarGraficoAnalise();
        atualizarGraficoPareto();

        double metaAtual = financeiroService.getMeta();
        if (txtMetaInput != null) {
            txtMetaInput.setText(String.valueOf(metaAtual));
        }
    }

    // =================================================================================
    // GRÁFICOS COM RÓTULOS (VALORES EM CIMA DAS BARRAS)
    // =================================================================================

    private void atualizarGraficoPareto() {
        graficoPareto.getData().clear();
        graficoPareto.setAnimated(false); // Desliga animação para os rótulos não "dançarem"

        graficoPareto.getYAxis().setLabel("Valor Total Vendido (R$)");
        graficoPareto.setTitle("Ranking de Faturamento por Produto");
        graficoPareto.setStyle("CHART_COLOR_1: #3498db;");

        ProdutoDAO dao = new ProdutoDAO();
        List<Produto> produtos = dao.listarTodos();

        produtos.sort((p1, p2) -> Double.compare(
                p2.getQtdVendida() * p2.getPreco(),
                p1.getQtdVendida() * p1.getPreco()
        ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Receita Gerada");

        for (Produto p : produtos) {
            double totalReais = p.getQtdVendida() * p.getPreco();
            if (totalReais > 0) {
                series.getData().add(new XYChart.Data<>(p.getNome(), totalReais));
            }
        }

        graficoPareto.getData().add(series);

        // Adiciona os números em cima das barras (Formatado como Dinheiro)
        adicionarRotulos(series, true);
    }

    private void atualizarGraficoAnalise() {
        graficoEstoque.getData().clear();
        graficoEstoque.setAnimated(false);

        XYChart.Series<String, Number> serieAtual = new XYChart.Series<>();
        serieAtual.setName("Estoque Atual");

        XYChart.Series<String, Number> serieMinimo = new XYChart.Series<>();
        serieMinimo.setName("Mínimo Necessário");

        for (Produto p : masterData) {
            serieAtual.getData().add(new XYChart.Data<>(p.getNome(), p.getEstoque()));
            serieMinimo.getData().add(new XYChart.Data<>(p.getNome(), p.getEstoqueMinimo()));
        }

        graficoEstoque.getData().addAll(serieAtual, serieMinimo);

        // Adiciona os números em cima das barras (Formatado como Inteiro Simples)
        adicionarRotulos(serieAtual, false);
        adicionarRotulos(serieMinimo, false);
    }

    /**
     * Método Mágico: Coloca o texto com o valor em cima de cada barra do gráfico.
     */
    private void adicionarRotulos(XYChart.Series<String, Number> series, boolean isDinheiro) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    mostrarValorNaBarra(data, isDinheiro);
                }
            });
            // Caso o nó já exista (se não for animado)
            if (data.getNode() != null) {
                mostrarValorNaBarra(data, isDinheiro);
            }
        }
    }

    private void mostrarValorNaBarra(XYChart.Data<String, Number> data, boolean isDinheiro) {
        Node node = data.getNode();
        if (node.getParent() == null) return;

        String textoValor;
        if (isDinheiro) {
            textoValor = String.format("R$ %.0f", data.getYValue().doubleValue());
        } else {
            textoValor = String.valueOf(data.getYValue().intValue());
        }

        Label label = new Label(textoValor);
        label.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Adiciona o label dentro do mesmo grupo da barra, mas tenta posicionar em cima
        // Nota: O posicionamento exato em JavaFX Charts é complexo sem libs externas,
        // mas colocar o texto no Tooltip e um Label simples ajuda.

        Tooltip t = new Tooltip(data.getXValue() + "\nValor: " + textoValor);
        Tooltip.install(node, t);

        // Exibe o valor dentro da barra (no topo)
        if (node instanceof javafx.scene.layout.Region) {
            // StackPane é usado internamente pelo BarChart, podemos adicionar o Label nele se convertermos
            // Mas a forma mais segura sem quebrar o layout padrão é usar o Tooltip (acima)
            // ou tentar adicionar um nó de texto se o parent permitir.
            // Para simplificar e não causar erro de layout, vamos usar um truque:
            // Adicionar o texto como "display" no topo do nó é difícil sem acesso ao layout absolute do gráfico.

            // ALTERNATIVA SIMPLES QUE FUNCIONA:
            // Colocamos o valor DENTRO da própria barra (node).
        }

        // -----------------------------------------------------------------------
        // TRUQUE DO LABEL NO TOPO DA BARRA:
        // O JavaFX BarChart não suporta nativamente labels "flutuando" acima.
        // A melhor abordagem nativa é usar displayLabelForData se estendesse a classe.
        // Como estamos num Controller, vamos usar um truque visual:
        // Vamos adicionar um Text SOBRE a barra.
        // -----------------------------------------------------------------------

        // Nota: Devido à complexidade de coordenadas X/Y relativas no JavaFX,
        // a implementação mais estável para "iniciantes" é usar Tooltip (já adicionado acima).
        // Se quiser ver o número SEM passar o mouse, a melhor opção é:
        // Habilitar a legenda do eixo Y ou usar uma biblioteca como TilesFX.

        // ENTRETANTO, vou tentar uma abordagem que adiciona um Text no centro do Node:
        if (data.getNode() instanceof javafx.scene.layout.StackPane) {
            javafx.scene.layout.StackPane bar = (javafx.scene.layout.StackPane) data.getNode();
            Text text = new Text(textoValor);
            text.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-effect: dropshadow(one-pass-box, black, 2, 0.0, 0, 1);");

            // Coloca o texto no topo da barra (dentro dela)
            bar.getChildren().add(text);
            javafx.scene.layout.StackPane.setAlignment(text, Pos.TOP_CENTER);
        }
    }

    // =================================================================================

    // funcao auxiliar para calcular o preço
    private void calcularPrecoVenda() {
        try {
            String custoStr = txtPrecoCusto.getText().replace(",", ".");
            String margemStr = txtMargem.getText().replace(",", ".");

            if (!custoStr.isEmpty() && !margemStr.isEmpty()) {
                double custo = Double.parseDouble(custoStr);
                double margem = Double.parseDouble(margemStr);
                double vendaSugerida = custo + (custo * (margem / 100));
                txtPrecoVenda.setText(String.format("%.2f", vendaSugerida).replace(",", "."));
            }
        } catch (NumberFormatException ex) {}
    }

    private void configurarFinanceiro() {
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
            String dataCad = dpDataCadastro.getValue().toString();

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
        if (selecionado == null) { mostrarErro("Seleção", "Selecione um produto na tabela para repor."); return; }

        try {
            int qtd = Integer.parseInt(txtQtd.getText());
            if (qtd <= 0) { mostrarErro("Quantidade Inválida", "A quantidade deve ser maior que zero."); return; }
            String dataOp = dpDataOperacao.getValue().toString();

            selecionado.setEstoque(selecionado.getEstoque() + qtd);
            produtoDAO.atualizar(selecionado);

            double custoTotal = selecionado.getPrecoCusto() * qtd;
            if (custoTotal > 0) {
                financeiroService.registrarDespesa("Reposição: " + selecionado.getNome() + " (" + qtd + "x)", custoTotal, dataOp);
            }

            tabelaProdutos.refresh();
            atualizarDashboard();
            atualizarFinanceiro();
            mostrarSucesso("Estoque Atualizado", "Adicionadas " + qtd + " unidades.\nDespesa de R$ " + String.format("%.2f", custoTotal));

        } catch (NumberFormatException e) { mostrarErro("Erro", "Digite uma quantidade válida no campo 'Qtd'."); }
    }

    @FXML
    public void handleBaixarEstoque() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        try {
            int qtdDigitada = Integer.parseInt(txtQtd.getText());
            int estoqueAtual = selecionado.getEstoque();

            if (estoqueAtual < qtdDigitada) {
                mostrarErro("Estoque Insuficiente", "Você quer vender " + qtdDigitada + ", mas só tem " + estoqueAtual + ".");
                return;
            }

            String dataOp = dpDataOperacao.getValue().toString();
            selecionado.setEstoque(estoqueAtual - qtdDigitada);
            selecionado.setQtdVendida(selecionado.getQtdVendida() + qtdDigitada);
            selecionado.setValorTotalVendido(selecionado.getValorTotalVendido() + (selecionado.getPreco() * qtdDigitada));
            produtoDAO.atualizar(selecionado);

            double valorVenda = selecionado.getPreco() * qtdDigitada;
            financeiroService.registrarReceita("Venda: " + selecionado.getNome(), valorVenda, dataOp);

            tabelaProdutos.refresh();
            atualizarDashboard();
            atualizarFinanceiro();
            mostrarSucesso("Venda", "Venda registrada! Receita: R$ " + valorVenda);

        } catch (NumberFormatException e) { mostrarErro("Erro", "Digite uma quantidade válida."); }
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

    @FXML
    public void handleSalvarMeta() {
        try {
            double novaMeta = Double.parseDouble(txtMetaInput.getText().replace(",", "."));
            financeiroService.salvarMeta(novaMeta);
            atualizarFinanceiro();
            mostrarSucesso("Meta Definida", "Nova meta de R$ " + novaMeta + " configurada!");
        } catch (NumberFormatException e) { mostrarErro("Valor Inválido", "Digite um número válido para a meta."); }
    }

    @FXML
    public void handleSalvarTransacao() {
        try {
            String tipo = cbTipoTransacao.getValue();
            String desc = txtDescTransacao.getText();
            double valor = Double.parseDouble(txtValorTransacao.getText().replace(",", "."));
            String data = dpDataFinanceiro.getValue().toString();

            if (tipo == null || desc.isEmpty()) { mostrarErro("Erro", "Preencha tipo e descrição."); return; }
            if (tipo.equals("RECEITA")) financeiroService.registrarReceita(desc, valor, data);
            else financeiroService.registrarDespesa(desc, valor, data);

            txtDescTransacao.clear(); txtValorTransacao.clear();
            atualizarFinanceiro();
        } catch (Exception e) { mostrarErro("Erro", "Valor inválido."); }
    }

    private void carregarTabela() {
        masterData.clear();
        masterData.addAll(produtoDAO.listarTodos());
    }

    @FXML
    public void atualizarDashboard() {
        listaAlertas.setItems(FXCollections.observableArrayList(monitorService.verificarAlertas()));
        double total = tabelaProdutos.getItems().stream().mapToDouble(p -> p.getPrecoCusto() * p.getEstoque()).sum();
        lblBalanco.setText(String.format("R$ %.2f", total));
        atualizarGraficoAnalise();
        atualizarGraficoPareto();
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
        if (progresso >= 1.0) barraMeta.setStyle("-fx-accent: #f1c40f;"); else barraMeta.setStyle("-fx-accent: #27ae60;");
    }

    private void limparCampos() {
        txtNome.clear(); txtMarca.clear(); txtQtd.clear();
        txtPrecoCusto.clear(); txtMargem.clear(); txtPrecoVenda.clear();
        txtSpecInt.clear(); txtSpecDouble.clear(); txtSpecTexto.clear();
    }

    private void mostrarErro(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR); alert.setTitle(titulo); alert.setHeaderText(null); alert.setContentText(msg); alert.showAndWait();
    }

    private void mostrarSucesso(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); alert.setTitle(titulo); alert.setHeaderText(null); alert.setContentText(msg); alert.showAndWait();
    }
}