package br.com.pdvloja.controller;

import br.com.pdvloja.dao.ProdutoDAO;
import br.com.pdvloja.model.Produto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProdutoController implements Initializable {

    @FXML
    private TableView<Produto> tabelaProdutos;
    @FXML
    private TableColumn<Produto, Integer> colunaId;
    @FXML
    private TableColumn<Produto, String> colunaNome;
    @FXML
    private TableColumn<Produto, Double> colunaPreco;

    @FXML
    private TextField campoNome;
    @FXML
    private TextField campoPreco;

    @FXML
    private Button botaoSalvar;
    @FXML
    private Button botaoExcluir;
    @FXML
    private Button botaoLimpar;

    private ProdutoDAO produtoDAO;
    private ObservableList<Produto> produtos;
    private Produto produtoSelecionado;

    /**
     * O método initialize é chamado automaticamente pelo JavaFX
     * depois que o arquivo FXML é carregado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.produtoDAO = new ProdutoDAO();
        configurarTabela();
        carregarProdutos();

        // Adiciona um "ouvinte" para quando um item da tabela for selecionado
        tabelaProdutos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarProduto(newValue)
        );
    }

    private void configurarTabela() {
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
    }

    private void carregarProdutos() {
        List<Produto> listaDoBanco = produtoDAO.listarTodos();
        produtos = FXCollections.observableArrayList(listaDoBanco);
        tabelaProdutos.setItems(produtos);
    }

    private void selecionarProduto(Produto produto) {
        this.produtoSelecionado = produto;
        if (produto != null) {
            campoNome.setText(produto.getNome());
            campoPreco.setText(String.format("%.2f", produto.getPreco()));
        } else {
            handleLimpar();
        }
    }

    @FXML
    private void handleSalvar() {
        String nome = campoNome.getText();
        String precoStr = campoPreco.getText().replace(",", ".");
        double preco;

        if (nome.isEmpty() || precoStr.isEmpty()) {
            // Adicionar um alerta para o usuário aqui depois
            System.out.println("Nome e preço são obrigatórios.");
            return;
        }

        preco = Double.parseDouble(precoStr);

        if (produtoSelecionado == null) {
            // Inserir novo produto
            Produto novoProduto = new Produto(nome, preco);
            produtoDAO.inserir(novoProduto);
        } else {
            // Atualizar produto existente
            produtoSelecionado.setNome(nome);
            produtoSelecionado.setPreco(preco);
            produtoDAO.atualizar(produtoSelecionado);
        }

        carregarProdutos(); // Recarrega a tabela para mostrar as mudanças
        handleLimpar();
    }

    @FXML
    private void handleExcluir() {
        if (produtoSelecionado != null) {
            produtoDAO.deletar(produtoSelecionado.getId());
            carregarProdutos();
            handleLimpar();
        } else {
            System.out.println("Nenhum produto selecionado para excluir.");
        }
    }

    @FXML
    private void handleLimpar() {
        campoNome.clear();
        campoPreco.clear();
        produtoSelecionado = null;
        tabelaProdutos.getSelectionModel().clearSelection();
    }
}