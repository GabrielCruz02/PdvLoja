package br.com.pdvloja.controller;

import br.com.pdvloja.dao.CaixaDAO;
import br.com.pdvloja.dao.ItemVendaDAO;
import br.com.pdvloja.dao.ProdutoDAO;
import br.com.pdvloja.dao.VendaDAO;
import br.com.pdvloja.model.Caixa;
import br.com.pdvloja.model.ItemVenda;
import br.com.pdvloja.model.Produto;
import br.com.pdvloja.model.Venda;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // --- Componentes da Seção de Adicionar Produto ---
    @FXML private ComboBox<Produto> produtosComboBox;
    @FXML private TextField quantidadeField;
    @FXML private Button adicionarButton;
    @FXML private Button gerenciarProdutosButton;

    // --- Componentes do botão de finalizar venda ---
    @FXML private ComboBox<String> formaPagamentoComboBox;
    @FXML private TextField valorPagoField;
    @FXML private Label trocoLabel;

    // --- Componentes da Tabela de Itens da Venda ---
    @FXML private TableView<ItemVenda> vendaTableView;
    @FXML private TableColumn<ItemVenda, String> colunaProduto;
    @FXML private TableColumn<ItemVenda, Integer> colunaQuantidade;
    @FXML private TableColumn<ItemVenda, Double> colunaPrecoUnitario;
    @FXML private TableColumn<ItemVenda, Double> colunaSubtotal;

    // --- Componentes da Seção de Totalização ---
    @FXML private Label totalLabel;
    @FXML private Button finalizarVendaButton;

    @FXML private Button removerItemButton;

    @FXML private Button fecharCaixaButton;

    private ProdutoDAO produtoDAO;
    private VendaDAO vendaDAO;
    private ItemVendaDAO itemVendaDAO;
    private CaixaDAO caixaDAO;

    private ObservableList<Produto> listaDeProdutos;
    private ObservableList<ItemVenda> itensDaVenda;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Inicializa TODOS os DAOs para que não fiquem nulos
        this.produtoDAO = new ProdutoDAO();
        this.vendaDAO = new VendaDAO();
        this.itemVendaDAO = new ItemVendaDAO();
        this.caixaDAO = new CaixaDAO();

        this.itensDaVenda = FXCollections.observableArrayList();

        // Configura as colunas da tabela
        configurarTabela();

        // Carrega os produtos do banco de dados para o ComboBox
        carregarProdutos();

        // Associa a lista de itens da venda com a tabela
        vendaTableView.setItems(itensDaVenda);

        formaPagamentoComboBox.setItems(FXCollections.observableArrayList("Dinheiro", "Cartão de Crédito", "Pix"));
        formaPagamentoComboBox.setValue("Dinheiro");

        configurarListeners();
    }

    private void configurarListeners() {
        // Listener para a FORMA DE PAGAMENTO
        formaPagamentoComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean isDinheiro = "Dinheiro".equals(newValue);
            valorPagoField.setVisible(isDinheiro);
            trocoLabel.setVisible(isDinheiro);

            if (!isDinheiro) {
                valorPagoField.clear();
                trocoLabel.setText("R$ 0,00");
            }
        });

        // Listener para o VALOR PAGO (calcula o troco em tempo real)
        valorPagoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if ("Dinheiro".equals(formaPagamentoComboBox.getValue())) {
                try {
                    double valorPago = 0;
                    if (!newValue.isEmpty()) {
                        valorPago = Double.parseDouble(newValue.replace(",", "."));
                    }

                    double totalVenda = Double.parseDouble(totalLabel.getText().replace("R$ ", "").replace(",", "."));
                    double troco = valorPago - totalVenda;

                    if (troco < 0) {
                        trocoLabel.setText("R$ 0,00");
                    } else {
                        trocoLabel.setText(String.format("R$ %.2f", troco));
                    }

                } catch (NumberFormatException e) {
                    trocoLabel.setText("R$ 0,00");
                }
            }
        });

        // Dispara o listener uma vez para configurar o estado inicial da tela
        formaPagamentoComboBox.getSelectionModel().selectFirst();
    }

    private void configurarTabela() {

        colunaProduto.setCellValueFactory(cellData -> cellData.getValue().getProduto().nomeProperty());

        colunaQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colunaPrecoUnitario.setCellValueFactory(new PropertyValueFactory<>("precoUnitario"));
        colunaSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    private void carregarProdutos() {
        // Busca os produtos do banco
        listaDeProdutos = FXCollections.observableArrayList(produtoDAO.listarTodos());
        // Associa a lista ao ComboBox
        produtosComboBox.setItems(listaDeProdutos);
    }

    // Em MainController.java

    @FXML
    private void handleAdicionarProduto() {
        // Pega os dados da tela
        Produto produtoSelecionado = produtosComboBox.getSelectionModel().getSelectedItem();
        String quantidadeTexto = quantidadeField.getText();

        // Validação dos inputs
        if (produtoSelecionado == null) {
            System.out.println("Erro: Nenhum produto selecionado.");
            // Futuramente, trocaremos isso por um alerta visual
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeTexto);
            if (quantidade <= 0) {
                System.out.println("Erro: A quantidade deve ser positiva.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Erro: Quantidade inválida.");
            return;
        }

        // Cria um novo ItemVenda
        ItemVenda novoItem = new ItemVenda();
        novoItem.setProduto(produtoSelecionado);
        novoItem.setQuantidade(quantidade);
        novoItem.setPrecoUnitario(produtoSelecionado.getPreco());
        novoItem.setSubtotal(produtoSelecionado.getPreco() * quantidade);

        // Adiciona o item à lista que está ligada à tabela
        itensDaVenda.add(novoItem);

        // Atualiza o valor total da venda
        atualizarTotalVenda();

        // Limpa os campos para a próxima inserção
        produtosComboBox.getSelectionModel().clearSelection();
        quantidadeField.setText("1");
    }

    // Método auxiliar para calcular e exibir o total da venda.
    private void atualizarTotalVenda() {
        double total = 0.0;
        for (ItemVenda item : itensDaVenda) {
            total += item.getSubtotal();
        }
        totalLabel.setText(String.format("R$ %.2f", total));
    }

    @FXML
    private void handleRemoverItem() {
        // 1. Pega o item que está selecionado na tabela
        ItemVenda itemSelecionado = vendaTableView.getSelectionModel().getSelectedItem();

        // 2. Verifica se algo foi realmente selecionado
        if (itemSelecionado == null) {
            mostrarAlerta("Atenção", "Por favor, selecione um item na tabela para remover.");
            return;
        }

        // 3. Remove o item da lista
        // Como a tabela está ligada (bound) a esta lista,
        // o item removido da lista desaparecerá automaticamente da tabela.
        itensDaVenda.remove(itemSelecionado);

        // 4. Atualiza o valor total da venda
        atualizarTotalVenda();
    }

    @FXML
    private void handleFinalizarVenda() {
        if (itensDaVenda.isEmpty()) {
            mostrarAlerta("Erro", "Não é possível finalizar uma venda vazia.");
            return;
        }
        Caixa caixaAberto = caixaDAO.buscarCaixaAberto();
        if (caixaAberto == null) {
            // ... (código de erro do caixa existente)
            return;
        }

        Venda novaVenda = new Venda();
        novaVenda.setDataHora(java.time.LocalDateTime.now());

        // Pega os valores da tela
        double total = Double.parseDouble(totalLabel.getText().replace("R$ ", "").replace(",", "."));
        double valorPago = 0;
        double troco = 0;

        String formaPagamento = formaPagamentoComboBox.getValue();

        if ("Dinheiro".equals(formaPagamento) && !valorPagoField.getText().isEmpty()) {
            valorPago = Double.parseDouble(valorPagoField.getText().replace(",", "."));
            troco = Double.parseDouble(trocoLabel.getText().replace("R$ ", "").replace(",", "."));
        }

        // Define os valores no objeto Venda
        novaVenda.setValorTotal(total);
        novaVenda.setFormaPagamento(formaPagamento);
        novaVenda.setValorPago(valorPago);
        novaVenda.setTroco(troco);

        try {
            int vendaId = vendaDAO.inserir(novaVenda, caixaAberto.getId());
            for (ItemVenda item : itensDaVenda) {
                item.setVendaId(vendaId);
                itemVendaDAO.inserir(item);
            }
            mostrarAlerta("Sucesso", "Venda finalizada com sucesso!");
            limparVendaAtual();
        } catch (RuntimeException e) {
            mostrarAlerta("Erro de Banco de Dados", "Ocorreu um erro ao salvar a venda.");
            e.printStackTrace();
        }
    }

    // Método auxiliar para limpar a tela
    private void limparVendaAtual() {
        itensDaVenda.clear();
        totalLabel.setText("R$ 0,00");
        produtosComboBox.getSelectionModel().clearSelection();
        quantidadeField.setText("1");

        // Limpeza dos novos campos
        formaPagamentoComboBox.setValue("Dinheiro");
        valorPagoField.clear();
        trocoLabel.setText("R$ 0,00");
    }

    // Método auxiliar para mostrar alertas ao usuário
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    private void handleGerenciarProdutos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/pdvloja/fxml/ProdutoView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Gerenciamento de Produtos");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            // Recarrega os produtos caso algum tenha sido alterado
            carregarProdutos();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFecharCaixa() {
        System.out.println("Botão 'Fechar Caixa' clicado. Buscando dados para o relatório...");

        // Pega o caixa que está aberto atualmente
        Caixa caixaAberto = caixaDAO.buscarCaixaAberto();
        if (caixaAberto == null) {
            mostrarAlerta("Erro", "Não há nenhum caixa aberto para fechar.");
            return;
        }

        // Usa o novo método para buscar todas as vendas desse caixa
        List<Venda> vendasDoDia = vendaDAO.listarPorCaixaId(caixaAberto.getId());

        if (vendasDoDia.isEmpty()) {
            mostrarAlerta("Atenção", "Nenhuma venda registrada neste caixa.");
            // Aqui poderíamos perguntar se quer fechar mesmo assim. Por enquanto, só avisamos.
            return;
        }

        // Imprime as vendas no console para teste
        System.out.println("--- VENDAS DO DIA ---");
        for (Venda venda : vendasDoDia) {
            System.out.println("ID: " + venda.getId() + ", Valor: " + venda.getValorTotal() + ", Pagamento: " + venda.getFormaPagamento());
        }
        System.out.println("--------------------");
    }
}