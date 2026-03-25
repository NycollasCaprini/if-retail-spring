package br.edu.ifpr.bsi.ifretailspring.repository;

import br.edu.ifpr.bsi.ifretailspring.domain.produto.Produto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

// @SpringBootTest sobe o contexto completo da aplicação (igual ao professor).
// @Transactional garante que cada teste rode em uma transação que é revertida
// ao final — o banco fica limpo entre os testes sem precisar de @BeforeEach/AfterEach.
@SpringBootTest
@Transactional
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    // ── Inserção simples ──────────────────────────────────────────────────────

    @Test
    public void testInsert() {
        Produto produto = new Produto();
        produto.setDescricao("Notebook Dell Inspiron");
        produto.setPrecoUnitario(3500.00);
        produto.setQuantidadeEmEstoque(10);
        produto.setStatus(true);
        produto = produtoRepository.save(produto);

        Produto produtoInserido = produtoRepository.findById(produto.getId()).get();

        Assertions.assertNotNull(produtoInserido, "O produto não foi inserido.");
        Assertions.assertEquals("Notebook Dell Inspiron", produtoInserido.getDescricao(),
                "A descrição do produto não confere.");
    }

    // ── Atualização ───────────────────────────────────────────────────────────

    @Test
    public void testUpdate() {
        Produto produto = new Produto();
        produto.setDescricao("Mouse sem fio");
        produto.setPrecoUnitario(80.00);
        produto.setQuantidadeEmEstoque(50);
        produto.setStatus(true);
        produto = produtoRepository.save(produto);

        produto.setPrecoUnitario(65.00);
        produto = produtoRepository.save(produto);

        Produto produtoAtualizado = produtoRepository.findById(produto.getId()).get();

        Assertions.assertEquals(65.00, produtoAtualizado.getPrecoUnitario(),
                "O preço do produto não foi atualizado.");
    }

    // ── Remoção ───────────────────────────────────────────────────────────────

    @Test
    public void testDelete() {
        Produto produto = new Produto();
        produto.setDescricao("Teclado Mecânico");
        produto.setPrecoUnitario(250.00);
        produto.setQuantidadeEmEstoque(15);
        produto.setStatus(true);
        produto = produtoRepository.save(produto);

        produtoRepository.delete(produto);

        Produto produtoDeletado = produtoRepository.findById(produto.getId()).orElse(null);
        Assertions.assertNull(produtoDeletado, "O produto ainda se encontra no banco de dados.");
    }

    // ── Listagem com teste de performance ─────────────────────────────────────

    @Test
    public void testListar() {
        // Insere 5 produtos para garantir que a lista não esteja vazia
        for (int i = 1; i <= 5; i++) {
            Produto p = new Produto();
            p.setDescricao("Produto " + i);
            p.setPrecoUnitario(i * 10.0);
            p.setQuantidadeEmEstoque(i * 5);
            p.setStatus(true);
            produtoRepository.save(p);
        }

        long inicio = System.currentTimeMillis();
        List<Produto> produtos = produtoRepository.findAll();
        long fim = System.currentTimeMillis();

        Assertions.assertFalse(produtos.isEmpty(), "A listagem não retornou resultados.");
        Assertions.assertTrue((fim - inicio) < 300, "A consulta demorou mais de 0,3 segundos.");
    }

    // ── Buscas automáticas (Derived Query Methods) ────────────────────────────

    @Test
    public void testFindByStatus() {
        Produto ativo = new Produto();
        ativo.setDescricao("Produto Ativo");
        ativo.setPrecoUnitario(100.00);
        ativo.setQuantidadeEmEstoque(10);
        ativo.setStatus(true);
        produtoRepository.save(ativo);

        Produto inativo = new Produto();
        inativo.setDescricao("Produto Inativo");
        inativo.setPrecoUnitario(100.00);
        inativo.setQuantidadeEmEstoque(0);
        inativo.setStatus(false);
        produtoRepository.save(inativo);

        List<Produto> ativos = produtoRepository.findByStatus(true);

        Assertions.assertFalse(ativos.isEmpty(), "Nenhum produto ativo encontrado.");
        ativos.forEach(p -> Assertions.assertTrue(p.isStatus(),
                "Produto inativo retornado na busca por ativos."));
    }

    @Test
    public void testFindByDescricaoContainingIgnoreCase() {
        Produto produto = new Produto();
        produto.setDescricao("Monitor Ultrawide");
        produto.setPrecoUnitario(1200.00);
        produto.setQuantidadeEmEstoque(5);
        produto.setStatus(true);
        produtoRepository.save(produto);

        List<Produto> resultado = produtoRepository.findByDescricaoContainingIgnoreCase("monitor");

        Assertions.assertFalse(resultado.isEmpty(),
                "Nenhum produto encontrado pela busca parcial de descrição.");
    }

    @Test
    public void testFindByPrecoUnitarioBetween() {
        Produto barato = new Produto();
        barato.setDescricao("Barato"); barato.setPrecoUnitario(50.00);
        barato.setQuantidadeEmEstoque(10); barato.setStatus(true);
        produtoRepository.save(barato);

        Produto medio = new Produto();
        medio.setDescricao("Médio"); medio.setPrecoUnitario(300.00);
        medio.setQuantidadeEmEstoque(10); medio.setStatus(true);
        produtoRepository.save(medio);

        Produto caro = new Produto();
        caro.setDescricao("Caro"); caro.setPrecoUnitario(2000.00);
        caro.setQuantidadeEmEstoque(10); caro.setStatus(true);
        produtoRepository.save(caro);

        List<Produto> resultado = produtoRepository.findByPrecoUnitarioBetween(100.00, 500.00);

        Assertions.assertFalse(resultado.isEmpty(), "Nenhum produto encontrado na faixa de preço.");
        resultado.forEach(p -> Assertions.assertTrue(
                p.getPrecoUnitario() >= 100.00 && p.getPrecoUnitario() <= 500.00,
                "Produto fora da faixa de preço retornado."));
    }

    // ── Query JPQL ────────────────────────────────────────────────────────────

    @Test
    public void testFindProdutosAtivosOrdenadosPorPreco() {
        Produto p1 = new Produto();
        p1.setDescricao("C"); p1.setPrecoUnitario(300.00);
        p1.setQuantidadeEmEstoque(1); p1.setStatus(true);
        produtoRepository.save(p1);

        Produto p2 = new Produto();
        p2.setDescricao("A"); p2.setPrecoUnitario(100.00);
        p2.setQuantidadeEmEstoque(1); p2.setStatus(true);
        produtoRepository.save(p2);

        // Este não deve aparecer (inativo)
        Produto p3 = new Produto();
        p3.setDescricao("B"); p3.setPrecoUnitario(200.00);
        p3.setQuantidadeEmEstoque(1); p3.setStatus(false);
        produtoRepository.save(p3);

        List<Produto> resultado = produtoRepository.findProdutosAtivosOrdenadosPorPreco();

        Assertions.assertFalse(resultado.isEmpty(), "A query JPQL não retornou resultados.");
        // Verifica que está ordenado por preço crescente
        for (int i = 0; i < resultado.size() - 1; i++) {
            Assertions.assertTrue(
                    resultado.get(i).getPrecoUnitario() <= resultado.get(i + 1).getPrecoUnitario(),
                    "Os produtos não estão ordenados por preço crescente.");
        }
    }

    @Test
    public void testFindProdutosSemEstoque() {
        Produto comEstoque = new Produto();
        comEstoque.setDescricao("Com estoque"); comEstoque.setPrecoUnitario(100.00);
        comEstoque.setQuantidadeEmEstoque(10); comEstoque.setStatus(true);
        produtoRepository.save(comEstoque);

        Produto semEstoque = new Produto();
        semEstoque.setDescricao("Sem estoque"); semEstoque.setPrecoUnitario(100.00);
        semEstoque.setQuantidadeEmEstoque(0); semEstoque.setStatus(true);
        produtoRepository.save(semEstoque);

        List<Produto> resultado = produtoRepository.findProdutosSemEstoque();

        Assertions.assertFalse(resultado.isEmpty(), "Nenhum produto sem estoque encontrado.");
        resultado.forEach(p -> Assertions.assertEquals(0, p.getQuantidadeEmEstoque(),
                "Produto com estoque retornado na busca de sem estoque."));
    }

    // ── Query SQL Nativo ──────────────────────────────────────────────────────

    @Test
    public void testAtualizarEstoqueNativo() {
        Produto produto = new Produto();
        produto.setDescricao("Webcam HD"); produto.setPrecoUnitario(300.00);
        produto.setQuantidadeEmEstoque(5); produto.setStatus(true);
        produto = produtoRepository.save(produto);

        // Executa a query SQL nativa de atualização de estoque
        int linhasAfetadas = produtoRepository.atualizarEstoque(produto.getId(), 30);

        Assertions.assertEquals(1, linhasAfetadas,
                "A query nativa deveria ter atualizado exatamente 1 linha.");
    }
}
