package br.com.pdvloja.dao;

import br.com.pdvloja.model.Produto;

import java.util.List;

public class ProdutoDAOTest {
    public static void main(String[] args) {
        ProdutoDAO dao = new ProdutoDAO();

        // Inserir produto
        Produto p1 = new Produto("Bolo de Chocolate", 35.00);
        dao.inserir(p1);
        System.out.println("Produto inserido!");

        // Listar todos
        List<Produto> produtos = dao.listarTodos();
        produtos.forEach(System.out::println);

        // Atualizar
        if (!produtos.isEmpty()) {
            Produto prod = produtos.get(0);
            prod.setPreco(40.00);
            dao.atualizar(prod);
            System.out.println("Produto atualizado!");
        }

        // Deletar
        if (!produtos.isEmpty()) {
            dao.deletar(produtos.get(0).getId());
            System.out.println("Produto deletado!");
        }
    }
}
