package br.edu.ifpr.bsi.ifretailspring.repository;

import br.edu.ifpr.bsi.ifretailspring.domain.carrinho.ItemCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade ItemCarrinho.
 */
@Repository
public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {

    List<ItemCarrinho> findByCarrinhoId(Long carrinhoId);

    Optional<ItemCarrinho> findByCarrinhoIdAndProdutoId(Long carrinhoId, Long produtoId);

    void deleteByCarrinhoId(Long carrinhoId);

    // ── JPQL ─────────────────────────────────────────────────────────────────

    /**
     * Calcula o valor total de um carrinho (soma: precoUnitario * quantidade).
     */
    @Query("SELECT SUM(i.precoUnitario * i.quantidade) FROM ItemCarrinho i WHERE i.carrinho.id = :carrinhoId")
    Double calcularTotalCarrinho(@Param("carrinhoId") Long carrinhoId);
}
