package br.com.pdvloja.controller;

import br.com.pdvloja.dao.ProdutoDAO;
import br.com.pdvloja.model.ItemVenda;
import br.com.pdvloja.model.Produto;
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
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // --- Componentes da Seção de Adicionar Produto ---
    @FXML private ComboBox<Produto> produtosComboBox;
    @FXML private TextField quantidadeField;
    @FXML private Button adicionarButton;
    @FXML private Button gerenciarProdutosButton;

    // --- Componentes da Tabela de Itens da Venda ---
    @FXML private TableView<ItemVenda> vendaTableView;
    @FXML private TableColumn<ItemVenda, String> colunaProduto;
    @FXML private TableColumn<ItemVenda, Integer> colunaQuantidade;
    @FXML private TableColumn<ItemVenda, Double> colunaPrecoUnitario;
    @FXML private TableColumn<ItemVenda, Double> colunaSubtotal;

    // --- Componentes da Seção de Totalização ---
    @FXML private Label totalLabel;
    @FXML private Button finalizarVendaButton;

    private ProdutoDAO produtoDAO;
    private ObservableList<Produto> listaDeProdutos;
    private ObservableList<ItemVenda> itensDaVenda;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializa os DAOs e as listas
        this.produtoDAO = new ProdutoDAO();
        this.itensDaVenda = FXCollections.observableArrayList();

        // Configura as colunas da tabela
        configurarTabela();

        // Carrega os produtos do banco de dados para o ComboBox
        carregarProdutos();

        // Associa a lista de itens da venda com a tabela
        vendaTableView.setItems(itensDaVenda);
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
    private void handleFinalizarVenda() {

        System.out.println("Botão 'Finalizar Venda' clicado.");
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
}