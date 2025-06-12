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
import javafx.collections.transformation.FilteredList;
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
    @FXML private TextField pesquisaProdutoField;
    @FXML private ListView<Produto> produtosListView;
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

    private ObservableList<Produto> listaCompletaDeProdutos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Inicializa os DAOs e as listas
        this.produtoDAO = new ProdutoDAO();
        this.vendaDAO = new VendaDAO();
        this.itemVendaDAO = new ItemVendaDAO();
        this.caixaDAO = new CaixaDAO();
        this.itensDaVenda = FXCollections.observableArrayList();

        // Configura as colunas da tabela
        configurarTabela();
        vendaTableView.setItems(itensDaVenda);

        // --- LÓGICA DE PESQUISA ATUALIZADA ---
        atualizarListaDeProdutos();

        // Cria uma FilteredList envolvendo a lista principal
        FilteredList<Produto> produtosFiltrados = new FilteredList<>(listaCompletaDeProdutos, p -> true);

        // Adiciona um "ouvinte" ao campo de pesquisa.
        pesquisaProdutoField.textProperty().addListener((observable, oldValue, newValue) -> {
            produtosFiltrados.setPredicate(produto -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String textoPesquisa = newValue.toLowerCase();
                if (produto.getNome().toLowerCase().contains(textoPesquisa)) {
                    return true;
                }
                return false;
            });
        });

        // Conecta a lista filtrada ao nosso ListView na tela
        produtosListView.setItems(produtosFiltrados);
        configurarListeners();

        ObservableList<String> metodosDePagamento = FXCollections.observableArrayList(
          "Dinheiro",
          "Cartão",
          "Pix"
        );

        formaPagamentoComboBox.setItems(metodosDePagamento);

        formaPagamentoComboBox.setValue("Dinheiro");

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

    // Em MainController.java

    @FXML
    private void handleAdicionarProduto() {
        Produto produtoSelecionado = produtosListView.getSelectionModel().getSelectedItem();
        String quantidadeTexto = quantidadeField.getText();

        // Validação com feedback visual para o usuário
        if (produtoSelecionado == null) {
            mostrarAlerta("Atenção", "Por favor, selecione um produto da lista.");
            return;
        }
        int quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeTexto);
            if (quantidade <= 0) {
                mostrarAlerta("Erro", "A quantidade deve ser um número positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "A quantidade informada é inválida.");
            return;
        }

        // --- LÓGICA REFINADA PARA JUNTAR ITENS ---
        boolean produtoJaExisteNoCarrinho = false;
        // Percorre os itens que já estão no carrinho
        for (ItemVenda itemExistente : itensDaVenda) {
            // Se o ID do produto no carrinho for igual ao ID do produto selecionado...
            if (itemExistente.getProduto().getId() == produtoSelecionado.getId()) {
                // ...apenas atualiza a quantidade e o subtotal do item que já existe.
                int novaQuantidade = itemExistente.getQuantidade() + quantidade;
                itemExistente.setQuantidade(novaQuantidade);
                itemExistente.setSubtotal(itemExistente.getPrecoUnitario() * novaQuantidade);
                produtoJaExisteNoCarrinho = true;
                break; // Sai do loop, pois já encontrou e atualizou
            }
        }

        // Se o produto não foi encontrado no loop, adiciona como um novo item
        if (!produtoJaExisteNoCarrinho) {
            ItemVenda novoItem = new ItemVenda();
            novoItem.setProduto(produtoSelecionado);
            novoItem.setQuantidade(quantidade);
            novoItem.setPrecoUnitario(produtoSelecionado.getPreco());
            novoItem.setSubtotal(produtoSelecionado.getPreco() * quantidade);
            itensDaVenda.add(novoItem);
        }

        // Atualiza o total e limpa a seleção para a próxima adição
        atualizarTotalVenda();
        vendaTableView.refresh(); // Força a atualização visual da tabela caso uma linha seja alterada
        limparCamposDeAdicao();
    }

    private void atualizarListaDeProdutos() {
        // Garante que a lista principal não seja nula
        if (listaCompletaDeProdutos == null) {
            listaCompletaDeProdutos = FXCollections.observableArrayList();
        }
        // Limpa a lista atual para não duplicar itens
        listaCompletaDeProdutos.clear();
        // Busca a lista atualizada do banco e a adiciona na nossa lista
        listaCompletaDeProdutos.addAll(produtoDAO.listarTodos());
        System.out.println("Lista de produtos atualizada com " + listaCompletaDeProdutos.size() + " itens.");
    }

    /**
     * Método auxiliar para limpar os campos de pesquisa e quantidade após adicionar um item.
     */
    private void limparCamposDeAdicao() {
        pesquisaProdutoField.clear();
        produtosListView.getSelectionModel().clearSelection();
        quantidadeField.setText("1");
        pesquisaProdutoField.requestFocus(); // Devolve o foco para o campo de pesquisa
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

    private void limparVendaAtual() {
        // Limpa a tabela de itens da venda
        itensDaVenda.clear();
        totalLabel.setText("R$ 0,00");

        // Limpa os campos de pagamento e troco
        formaPagamentoComboBox.setValue("Dinheiro");
        valorPagoField.clear();
        trocoLabel.setText("R$ 0,00");

        // Chama o outro método auxiliar para limpar a parte de seleção de produtos
        limparCamposDeAdicao();
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

            // Após fechar a janela, chama o método para atualizar a lista
            atualizarListaDeProdutos();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFecharCaixa() {
        Caixa caixaAberto = caixaDAO.buscarCaixaAberto();
        if (caixaAberto == null) {
            mostrarAlerta("Erro", "Não há nenhum caixa aberto para fechar.");
            return;
        }
        List<Venda> vendasDoDia = vendaDAO.listarPorCaixaId(caixaAberto.getId());

        // --- Lógica de Cálculo dos Totais ---
        double totalDinheiro = 0;
        double totalCartao = 0;
        double totalPix = 0;

        for (Venda venda : vendasDoDia) {
            switch (venda.getFormaPagamento()) {
                case "Dinheiro":
                    totalDinheiro += venda.getValorTotal();
                    break;
                case "Cartão":
                    totalCartao += venda.getValorTotal();
                    break;
                case "Pix":
                    totalPix += venda.getValorTotal();
                    break;
            }
        }

        // --- Lógica para Abrir a Janela de Relatório ---
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/pdvloja/fxml/FecharCaixaView.fxml"));
            Scene scene = new Scene(loader.load());


            FecharCaixaController controller = loader.getController();
            // Chama o método initData para passar os dados para a nova tela
            controller.initData(caixaAberto, totalDinheiro, totalCartao, totalPix);

            Stage stage = new Stage();
            stage.setTitle("Fechamento de Caixa");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}