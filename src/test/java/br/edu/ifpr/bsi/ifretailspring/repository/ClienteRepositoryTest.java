package br.edu.ifpr.bsi.ifretailspring.repository;

import br.edu.ifpr.bsi.ifretailspring.domain.carrinho.Carrinho;
import br.edu.ifpr.bsi.ifretailspring.domain.cliente.Cliente;
import br.edu.ifpr.bsi.ifretailspring.domain.contato.Contato;
import br.edu.ifpr.bsi.ifretailspring.domain.endereco.Endereco;
import br.edu.ifpr.bsi.ifretailspring.domain.enums.UserType;
import br.edu.ifpr.bsi.ifretailspring.domain.pedido.Pedido;
import br.edu.ifpr.bsi.ifretailspring.domain.produto.Produto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Cliente herda de User (herança JOINED) e possui relacionamentos com
// Carrinho (OneToOne), Pedido (OneToMany) e Produto/favoritos (ManyToMany).
// O @Transactional reverte todas as inserções ao fim de cada teste.
@SpringBootTest
@Transactional
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    // ── Inserção simples ──────────────────────────────────────────────────────

    @Test
    public void testInsert() {
        Cliente cliente = new Cliente();
        cliente.setName("João da Silva");
        cliente.setCpf("123.456.789-00");
        cliente.setPassword("hash");
        cliente.setTipo(UserType.CLIENTE);

        Carrinho carrinho = new Carrinho();
        cliente.setCarrinho(carrinho);

        cliente = clienteRepository.save(cliente);

        Cliente clienteInserido = clienteRepository.findById(cliente.getId()).get();

        Assertions.assertNotNull(clienteInserido, "O cliente não foi inserido.");
        Assertions.assertNotNull(clienteInserido.getCarrinho(),
                "O carrinho do cliente não foi criado junto (cascade ALL).");
    }

    // ── Atualização ───────────────────────────────────────────────────────────

    @Test
    public void testUpdate() {
        Cliente cliente = new Cliente();
        cliente.setName("Maria Antunes");
        cliente.setCpf("987.654.321-00");
        cliente.setPassword("hash");
        cliente.setTipo(UserType.CLIENTE);
        cliente = clienteRepository.save(cliente);

        cliente.setName("Maria Antunes Silva");
        cliente = clienteRepository.save(cliente);

        Cliente clienteAtualizado = clienteRepository.findById(cliente.getId()).get();

        Assertions.assertEquals("Maria Antunes Silva", clienteAtualizado.getName(),
                "O nome do cliente não foi atualizado.");
    }

    // ── Remoção ───────────────────────────────────────────────────────────────

    @Test
    public void testDelete() {
        Cliente cliente = new Cliente();
        cliente.setName("Pedro Oliveira");
        cliente.setCpf("111.111.111-11");
        cliente.setPassword("hash");
        cliente.setTipo(UserType.CLIENTE);
        cliente = clienteRepository.save(cliente);

        clienteRepository.delete(cliente);

        Cliente clienteDeletado = clienteRepository.findById(cliente.getId()).orElse(null);
        Assertions.assertNull(clienteDeletado, "O cliente ainda se encontra no banco de dados.");
    }

    // ── Listagem com teste de performance ─────────────────────────────────────

    @Test
    public void testListar() {
        Cliente cliente = new Cliente();
        cliente.setName("Carlos Souza");
        cliente.setCpf("222.222.222-22");
        cliente.setPassword("hash");
        cliente.setTipo(UserType.CLIENTE);
        clienteRepository.save(cliente);

        long inicio = System.currentTimeMillis();
        List<Cliente> clientes = clienteRepository.findAll();
        long fim = System.currentTimeMillis();

        Assertions.assertFalse(clientes.isEmpty(), "A listagem não retornou resultados.");
        Assertions.assertTrue((fim - inicio) < 300, "A consulta demorou mais de 0,3 segundos.");
    }

    // ── Buscas automáticas (Derived Query Methods) ────────────────────────────

    @Test
    public void testFindByCpf() {
        Cliente cliente = new Cliente();
        cliente.setName("Ana Pereira");
        cliente.setCpf("333.333.333-33");
        cliente.setPassword("hash");
        cliente.setTipo(UserType.CLIENTE);
        clienteRepository.save(cliente);

        Cliente encontrado = clienteRepository.findByCpf("333.333.333-33").orElse(null);

        Assertions.assertNotNull(encontrado, "Cliente não encontrado pelo CPF.");
        Assertions.assertEquals("Ana Pereira", encontrado.getName(), "O nome do cliente não confere.");
    }

    @Test
    public void testExistsByCpf() {
        Cliente cliente = new Cliente();
        cliente.setName("Existência Teste");
        cliente.setCpf("444.444.444-44");
        cliente.setPassword("hash");
        cliente.setTipo(UserType.CLIENTE);
        clienteRepository.save(cliente);

        Assertions.assertTrue(clienteRepository.existsByCpf("444.444.444-44"),
                "O CPF deveria existir.");
        Assertions.assertFalse(clienteRepository.existsByCpf("000.000.000-00"),
                "O CPF não deveria existir.");
    }

    // ── Inserção com relacionamentos ──────────────────────────────────────────

    @Test
    public void testInserirComContatos() {
        Cliente cliente = new Cliente();
        cliente.setName("Leticia Laumann");
        cliente.setCpf("555.666.777-88");
        cliente.setPassword("hash");
        cliente.setTipo(UserType.CLIENTE);

        List<Contato> contatos = new ArrayList<>();

        Contato contato1 = new Contato();
        contato1.setEmail("leticia@gmail.com");
        contato1.setTelefone("+55(46)99999-9999");
        contato1.setWhatsapp("+55(46)99999-9999");
        contato1.setUser(cliente);
        contatos.add(contato1);

        Contato contato2 = new Contato();
        contato2.setEmail("leticia2@hotmail.com");
        contato2.setTelefone("+55(46)88888-8888");
        contato2.setWhatsapp("+55(46)88888-8888");
        contato2.setUser(cliente);
        contatos.add(contato2);

        cliente.setContatoList(contatos);
        cliente = clienteRepository.save(cliente);

        Cliente clienteInserido = clienteRepository.findById(cliente.getId()).get();

        Assertions.assertNotNull(clienteInserido.getContatoList(),
                "A lista de contatos está nula.");
        Assertions.assertFalse(clienteInserido.getContatoList().isEmpty(),
                "Os contatos do cliente não foram inseridos corretamente.");
    }

    @Test
    public void testInserirComEndereco() {
        Cliente cliente = new Cliente();
        cliente.setName("Gabriela Fogaça");
        cliente.setCpf("666.777.888-99");
        cliente.setPassword("hash");
        cliente.setTipo(UserType.CLIENTE);

        Endereco endereco = new Endereco();
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setCep("85555-000");
        endereco.setCidade("Palmas");
        endereco.setEstado("PR");
        endereco.setPais("Brasil");
        endereco.setUser(cliente);

        List<Endereco> enderecos = new ArrayList<>();
        enderecos.add(endereco);
        cliente.setEnderecoList(enderecos);

        cliente = clienteRepository.save(cliente);

        Cliente clienteInserido = clienteRepository.findById(cliente.getId()).get();

        Assertions.assertFalse(clienteInserido.getEnderecoList().isEmpty(),
                "O endereço do cliente não foi inserido corretamente.");
        Assertions.assertEquals("Palmas",
                clienteInserido.getEnderecoList().get(0).getCidade(),
                "A cidade do endereço não confere.");
    }

    @Test
    public void testInserirComFavoritos() {
        Produto produto1 = new Produto();
        produto1.setDescricao("Notebook"); produto1.setPrecoUnitario(3000.00);
        produto1.setQuantidadeEmEstoque(5); produto1.setStatus(true);
        produto1 = produtoRepository.save(produto1);

        Produto produto2 = new Produto();
        produto2.setDescricao("Fone de Ouvido"); produto2.setPrecoUnitario(200.00);
        produto2.setQuantidadeEmEstoque(20); produto2.setStatus(true);
        produto2 = produtoRepository.save(produto2);

        Cliente cliente = new Cliente();
        cliente.setName("Bruno Favoritos");
        cliente.setCpf("777.888.999-00");
        cliente.setPassword("hash");
        cliente.setTipo(UserType.CLIENTE);
        cliente.getFavoritos().add(produto1);
        cliente.getFavoritos().add(produto2);
        cliente = clienteRepository.save(cliente);

        Cliente clienteInserido = clienteRepository.findById(cliente.getId()).get();

        Assertions.assertEquals(2, clienteInserido.getFavoritos().size(),
                "Os produtos favoritos não foram associados corretamente (ManyToMany).");
    }

    @Test
    public void testInserirComPedido() {
        Cliente cliente = new Cliente();
        cliente.setName("Roberta Pedido");
        cliente.setCpf("888.999.000-11");
        cliente.setPassword("hash");
        cliente.setTipo(UserType.CLIENTE);
        cliente = clienteRepository.save(cliente);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataDoPedido(LocalDateTime.now());
        pedido.setStatus(true);
        cliente.getPedidoList().add(pedido);
        cliente = clienteRepository.save(cliente);

        Cliente clienteComPedido = clienteRepository.findByIdWithPedidos(cliente.getId()).get();

        Assertions.assertFalse(clienteComPedido.getPedidoList().isEmpty(),
                "O pedido do cliente não foi inserido corretamente.");
    }

    // ── Query JPQL ────────────────────────────────────────────────────────────

    @Test
    public void testGetAllByNomeLike() {
        Cliente cliente = new Cliente();
        cliente.setName("Eduardo Alvarenga");
        cliente.setCpf("100.200.300-40");
        cliente.setPassword("hash");
        cliente.setTipo(UserType.CLIENTE);
        clienteRepository.save(cliente);

        // Usando findByName como busca exata disponível no repositório
        Cliente encontrado = clienteRepository.findByName("Eduardo Alvarenga").orElse(null);

        Assertions.assertNotNull(encontrado, "Nenhum cliente encontrado com o nome especificado.");
    }

    // ── Query SQL Nativo ──────────────────────────────────────────────────────

    @Test
    public void testCountPedidosPorCliente() {
        Cliente cliente = new Cliente();
        cliente.setName("Luiz Contagem");
        cliente.setCpf("500.600.700-80");
        cliente.setPassword("hash");
        cliente.setTipo(UserType.CLIENTE);
        cliente = clienteRepository.save(cliente);

        Pedido p1 = new Pedido();
        p1.setCliente(cliente); p1.setDataDoPedido(LocalDateTime.now()); p1.setStatus(true);
        Pedido p2 = new Pedido();
        p2.setCliente(cliente); p2.setDataDoPedido(LocalDateTime.now()); p2.setStatus(true);
        cliente.getPedidoList().add(p1);
        cliente.getPedidoList().add(p2);
        clienteRepository.save(cliente);

        List<Object[]> resultado = clienteRepository.countPedidosPorCliente();

        Assertions.assertFalse(resultado.isEmpty(),
                "A query SQL nativa de contagem de pedidos não retornou resultados.");
    }
}
