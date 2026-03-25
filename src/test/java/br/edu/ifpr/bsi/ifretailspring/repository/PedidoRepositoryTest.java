package br.edu.ifpr.bsi.ifretailspring.repository;

import br.edu.ifpr.bsi.ifretailspring.domain.cliente.Cliente;
import br.edu.ifpr.bsi.ifretailspring.domain.enums.UserType;
import br.edu.ifpr.bsi.ifretailspring.domain.pedido.Pedido;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // Método auxiliar para criar e persistir um cliente rapidamente
    private Cliente criarCliente(String nome, String cpf) {
        Cliente c = new Cliente();
        c.setName(nome);
        c.setCpf(cpf);
        c.setPassword("hash");
        c.setTipo(UserType.CLIENTE);
        return clienteRepository.save(c);
    }

    // ── Inserção simples ──────────────────────────────────────────────────────

    @Test
    public void testInsert() {
        Cliente cliente = criarCliente("Joana Dark", "100.100.100-10");

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataDoPedido(LocalDateTime.now());
        pedido.setStatus(true);
        pedido = pedidoRepository.save(pedido);

        Pedido pedidoInserido = pedidoRepository.findById(pedido.getId()).get();

        Assertions.assertNotNull(pedidoInserido, "O pedido não foi inserido.");
        Assertions.assertNotNull(pedidoInserido.getCliente(), "O cliente do pedido está nulo.");
    }

    // ── Inserção com cliente relacionado ──────────────────────────────────────

    @Test
    public void testInserirComCliente() {
        Cliente cliente = criarCliente("Marcos Vinicius", "200.200.200-20");

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataDoPedido(LocalDateTime.now());
        pedido.setDataDeEntregaDoPedido(LocalDateTime.now().plusDays(5));
        pedido.setStatus(true);
        pedido = pedidoRepository.save(pedido);

        Pedido pedidoInserido = pedidoRepository.findById(pedido.getId()).get();

        Assertions.assertEquals(cliente.getId(), pedidoInserido.getCliente().getId(),
                "O cliente associado ao pedido não confere.");
        Assertions.assertNotNull(pedidoInserido.getDataDeEntregaDoPedido(),
                "A data de entrega do pedido está nula.");
    }

    // ── Atualização ───────────────────────────────────────────────────────────

    @Test
    public void testUpdate() {
        Cliente cliente = criarCliente("Silvia Torres", "300.300.300-30");

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataDoPedido(LocalDateTime.now());
        pedido.setStatus(true);
        pedido = pedidoRepository.save(pedido);

        // Simula cancelamento do pedido
        pedido.setStatus(false);
        pedido = pedidoRepository.save(pedido);

        Pedido pedidoAtualizado = pedidoRepository.findById(pedido.getId()).get();

        Assertions.assertFalse(pedidoAtualizado.isStatus(),
                "O status do pedido não foi atualizado para cancelado.");
    }

    // ── Remoção ───────────────────────────────────────────────────────────────

    @Test
    public void testDelete() {
        Cliente cliente = criarCliente("Ricardo Gomes", "400.400.400-40");

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataDoPedido(LocalDateTime.now());
        pedido.setStatus(true);
        pedido = pedidoRepository.save(pedido);

        pedidoRepository.delete(pedido);

        Pedido pedidoDeletado = pedidoRepository.findById(pedido.getId()).orElse(null);
        Assertions.assertNull(pedidoDeletado, "O pedido ainda se encontra no banco de dados.");
    }

    // ── Listagem com teste de performance ─────────────────────────────────────

    @Test
    public void testListar() {
        Cliente cliente = criarCliente("Listar Teste", "500.500.500-50");

        for (int i = 0; i < 3; i++) {
            Pedido p = new Pedido();
            p.setCliente(cliente);
            p.setDataDoPedido(LocalDateTime.now().minusDays(i));
            p.setStatus(true);
            pedidoRepository.save(p);
        }

        long inicio = System.currentTimeMillis();
        List<Pedido> pedidos = pedidoRepository.findAll();
        long fim = System.currentTimeMillis();

        Assertions.assertFalse(pedidos.isEmpty(), "A listagem não retornou resultados.");
        Assertions.assertTrue((fim - inicio) < 300, "A consulta demorou mais de 0,3 segundos.");
    }

    // ── Buscas automáticas ────────────────────────────────────────────────────

    @Test
    public void testFindByClienteId() {
        Cliente cliente = criarCliente("Busca Por ID", "600.600.600-60");

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataDoPedido(LocalDateTime.now());
        pedido.setStatus(true);
        pedidoRepository.save(pedido);

        List<Pedido> resultado = pedidoRepository.findByClienteId(cliente.getId());

        Assertions.assertFalse(resultado.isEmpty(),
                "Nenhum pedido encontrado para o cliente informado.");
    }

    @Test
    public void testFindByStatus() {
        Cliente cliente = criarCliente("Status Teste", "700.700.700-70");

        Pedido ativo = new Pedido();
        ativo.setCliente(cliente); ativo.setDataDoPedido(LocalDateTime.now()); ativo.setStatus(true);
        pedidoRepository.save(ativo);

        Pedido cancelado = new Pedido();
        cancelado.setCliente(cliente); cancelado.setDataDoPedido(LocalDateTime.now()); cancelado.setStatus(false);
        pedidoRepository.save(cancelado);

        List<Pedido> ativos = pedidoRepository.findByStatus(true);
        List<Pedido> cancelados = pedidoRepository.findByStatus(false);

        Assertions.assertFalse(ativos.isEmpty(), "Nenhum pedido ativo encontrado.");
        Assertions.assertFalse(cancelados.isEmpty(), "Nenhum pedido cancelado encontrado.");
    }

    @Test
    public void testCountByClienteId() {
        Cliente cliente = criarCliente("Count Teste", "800.800.800-80");

        for (int i = 0; i < 3; i++) {
            Pedido p = new Pedido();
            p.setCliente(cliente);
            p.setDataDoPedido(LocalDateTime.now());
            p.setStatus(true);
            pedidoRepository.save(p);
        }

        long total = pedidoRepository.countByClienteId(cliente.getId());

        Assertions.assertEquals(3, total, "A contagem de pedidos por cliente não confere.");
    }

    // ── Query JPQL ────────────────────────────────────────────────────────────

    @Test
    public void testFindPedidosPendentesPorCliente() {
        Cliente cliente = criarCliente("Pendente Teste", "900.900.900-90");

        // Pedido sem data de entrega = pendente
        Pedido pendente = new Pedido();
        pendente.setCliente(cliente);
        pendente.setDataDoPedido(LocalDateTime.now());
        pendente.setStatus(true);
        // dataDeEntregaDoPedido não definida = null
        pedidoRepository.save(pendente);

        // Pedido com data de entrega = entregue
        Pedido entregue = new Pedido();
        entregue.setCliente(cliente);
        entregue.setDataDoPedido(LocalDateTime.now().minusDays(3));
        entregue.setDataDeEntregaDoPedido(LocalDateTime.now().minusDays(1));
        entregue.setStatus(true);
        pedidoRepository.save(entregue);

        List<Pedido> pendentes = pedidoRepository.findPedidosPendentesPorCliente(cliente.getId());

        Assertions.assertFalse(pendentes.isEmpty(),
                "Nenhum pedido pendente encontrado para o cliente.");
        pendentes.forEach(p -> Assertions.assertNull(p.getDataDeEntregaDoPedido(),
                "Pedido com data de entrega retornado como pendente."));
    }

    // ── Query SQL Nativo ──────────────────────────────────────────────────────

    @Test
    public void testCountPedidosPorStatus() {
        Cliente cliente = criarCliente("Nativo Count", "010.010.010-01");

        Pedido p1 = new Pedido();
        p1.setCliente(cliente); p1.setDataDoPedido(LocalDateTime.now()); p1.setStatus(true);
        pedidoRepository.save(p1);

        Pedido p2 = new Pedido();
        p2.setCliente(cliente); p2.setDataDoPedido(LocalDateTime.now()); p2.setStatus(false);
        pedidoRepository.save(p2);

        List<Object[]> resultado = pedidoRepository.countPedidosPorStatus();

        Assertions.assertFalse(resultado.isEmpty(),
                "A query SQL nativa de contagem por status não retornou resultados.");
    }
}
