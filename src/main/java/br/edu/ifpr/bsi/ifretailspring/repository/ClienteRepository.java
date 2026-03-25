package br.edu.ifpr.bsi.ifretailspring.repository;

import br.edu.ifpr.bsi.ifretailspring.domain.cliente.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Cliente.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(String cpf);

    Optional<Cliente> findByName(String name);

    // ── JPQL com JOIN FETCH ───────────────────────────────────────────────────

    /**
     * Carrega cliente com seus pedidos em uma única query (evita N+1).
     * JOIN FETCH força o carregamento EAGER apenas nesta consulta específica,
     * mantendo o mapeamento LAZY no restante da aplicação.
     */
    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.pedidoList WHERE c.id = :id")
    Optional<Cliente> findByIdWithPedidos(@Param("id") Long id);

    /**
     * Carrega cliente com seus produtos favoritos.
     */
    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.favoritos WHERE c.id = :id")
    Optional<Cliente> findByIdWithFavoritos(@Param("id") Long id);

    // ── SQL nativo ────────────────────────────────────────────────────────────

    /**
     * Conta quantos pedidos cada cliente tem (relatório agregado).
     * Retorna lista de Object[]: [cliente_id, total_pedidos].
     */
    @Query(value = """
            SELECT c.user_id, COUNT(p.id) AS total_pedidos
            FROM tb_clientes c
            LEFT JOIN tb_pedidos p ON p.cliente_id = c.user_id
            GROUP BY c.user_id
            ORDER BY total_pedidos DESC
            """, nativeQuery = true)
    List<Object[]> countPedidosPorCliente();

    boolean existsByCpf(String cpf);
}
