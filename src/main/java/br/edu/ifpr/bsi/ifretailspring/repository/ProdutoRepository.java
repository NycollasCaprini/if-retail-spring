package br.edu.ifpr.bsi.ifretailspring.repository;

import br.edu.ifpr.bsi.ifretailspring.domain.produto.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repositório JPA para a entidade Produto.
 *
 * @Modifying + @Transactional: necessários para queries de UPDATE/DELETE
 * via @Query — o Spring Data não deduz que é uma operação de escrita
 * a partir do JPQL sozinho.
 */
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByStatus(boolean status);

    List<Produto> findByDescricaoContainingIgnoreCase(String descricao);

    List<Produto> findByPrecoUnitarioBetween(double min, double max);

    List<Produto> findByQuantidadeEmEstoqueGreaterThan(int quantidade);

    List<Produto> findByQuantidadeEmEstoqueLessThanEqual(int quantidade);

    // ── JPQL ─────────────────────────────────────────────────────────────────

    /** Retorna produtos ativos ordenados por preço crescente. */
    @Query("SELECT p FROM Produto p WHERE p.status = true ORDER BY p.precoUnitario ASC")
    List<Produto> findProdutosAtivosOrdenadosPorPreco();

    /** Retorna produtos com estoque zerado. */
    @Query("SELECT p FROM Produto p WHERE p.quantidadeEmEstoque = 0")
    List<Produto> findProdutosSemEstoque();

    // ── SQL nativo ────────────────────────────────────────────────────────────

    /**
     * Atualiza o estoque de um produto diretamente via SQL nativo.
     * Útil quando a lógica de negócio exige uma operação atômica no banco.
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE tb_produtos SET quantidade_em_estoque = :qtd WHERE id = :id",
            nativeQuery = true)
    int atualizarEstoque(@Param("id") Long id, @Param("qtd") int qtd);

    boolean existsByDescricao(String descricao);
}
