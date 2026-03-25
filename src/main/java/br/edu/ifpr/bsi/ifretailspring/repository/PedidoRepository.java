package br.edu.ifpr.bsi.ifretailspring.repository;

import br.edu.ifpr.bsi.ifretailspring.domain.pedido.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório JPA para a entidade Pedido.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByStatus(boolean status);

    List<Pedido> findByDataDoPedidoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Pedido> findByClienteIdAndStatus(Long clienteId, boolean status);

    // ── JPQL ─────────────────────────────────────────────────────────────────

    /**
     * Busca pedidos pendentes (sem data de entrega definida) de um cliente.
     */
    @Query("SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId AND p.dataDeEntregaDoPedido IS NULL")
    List<Pedido> findPedidosPendentesPorCliente(@Param("clienteId") Long clienteId);

    // ── SQL nativo ────────────────────────────────────────────────────────────

    /**
     * Retorna o total de pedidos agrupado por status (true=ativo, false=cancelado).
     */
    @Query(value = "SELECT status, COUNT(*) FROM tb_pedidos GROUP BY status",
            nativeQuery = true)
    List<Object[]> countPedidosPorStatus();

    long countByClienteId(Long clienteId);
}
