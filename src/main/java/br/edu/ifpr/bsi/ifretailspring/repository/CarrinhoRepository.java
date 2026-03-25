package br.edu.ifpr.bsi.ifretailspring.repository;

import br.edu.ifpr.bsi.ifretailspring.domain.carrinho.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório JPA para a entidade Carrinho.
 */
@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {

    /** Busca o carrinho de um cliente pelo ID do cliente. */
    Optional<Carrinho> findByClienteId(Long clienteId);

    /**
     * Carrega o carrinho com todos os itens em uma única query.
     * Evita N+1 ao exibir o carrinho de um cliente.
     */
    @Query("SELECT c FROM Carrinho c LEFT JOIN FETCH c.itens WHERE c.id = :id")
    Optional<Carrinho> findByIdWithItens(@Param("id") Long id);

    // ── SQL nativo ────────────────────────────────────────────────────────────

    /**
     * Conta o total de itens no carrinho de um cliente.
     */
    @Query(value = """
            SELECT COALESCE(SUM(ic.quantidade), 0)
            FROM tb_itens_carrinho ic
            INNER JOIN tb_carrinho c ON ic.carrinho_id = c.id
            WHERE c.id = :carrinhoId
            """, nativeQuery = true)
    int countTotalItens(@Param("carrinhoId") Long carrinhoId);
}
