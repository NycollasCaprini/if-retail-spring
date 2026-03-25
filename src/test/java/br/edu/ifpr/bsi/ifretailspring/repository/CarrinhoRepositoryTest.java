package br.edu.ifpr.bsi.ifretailspring.repository;

import br.edu.ifpr.bsi.ifretailspring.domain.carrinho.Carrinho;
import br.edu.ifpr.bsi.ifretailspring.domain.carrinho.ItemCarrinho;
import br.edu.ifpr.bsi.ifretailspring.domain.cliente.Cliente;
import br.edu.ifpr.bsi.ifretailspring.domain.enums.UserType;
import br.edu.ifpr.bsi.ifretailspring.domain.produto.Produto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Transactional
class CarrinhoRepositoryTest {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    private Cliente criarCliente(String nome, String cpf) {
        Carrinho carrinho = new Carrinho();
        Cliente c = new Cliente();
        c.setName(nome); c.setCpf(cpf);
        c.setPassword("hash"); c.setTipo(UserType.CLIENTE);
        c.setCarrinho(carrinho);
        return clienteRepository.save(c);
    }

    private Produto criarProduto(String descricao, double preco) {
        Produto p = new Produto();
        p.setDescricao(descricao); p.setPrecoUnitario(preco);
        p.setQuantidadeEmEstoque(20); p.setStatus(true);
        return produtoRepository.save(p);
    }

    // ── Inserção simples ──────────────────────────────────────────────────────

    @Test
    public void testInsert() {
        Carrinho carrinho = new Carrinho();
        carrinho = carrinhoRepository.save(carrinho);

        Carrinho carrinhoInserido = carrinhoRepository.findById(carrinho.getId()).get();

        Assertions.assertNotNull(carrinhoInserido, "O carrinho não foi inserido.");
    }

    // ── Inserção com itens relacionados ───────────────────────────────────────

    @Test
    public void testInserirComItens() {
        Produto produto1 = criarProduto("Teclado", 150.00);
        Produto produto2 = criarProduto("Mouse", 80.00);

        Carrinho carrinho = new Carrinho();
        carrinho = carrinhoRepository.save(carrinho);

        ItemCarrinho item1 = new ItemCarrinho();
        item1.setCarrinho(carrinho); item1.setProduto(produto1);
        item1.setQuantidade(2); item1.setPrecoUnitario(150.00);

        ItemCarrinho item2 = new ItemCarrinho();
        item2.setCarrinho(carrinho); item2.setProduto(produto2);
        item2.setQuantidade(1); item2.setPrecoUnitario(80.00);

        carrinho.getItens().add(item1);
        carrinho.getItens().add(item2);
        carrinhoRepository.save(carrinho);

        Carrinho carrinhoComItens = carrinhoRepository.findByIdWithItens(carrinho.getId()).get();

        Assertions.assertEquals(2, carrinhoComItens.getItens().size(),
                "Os itens do carrinho não foram inseridos corretamente (cascade ALL).");
    }

    // ── Atualização ───────────────────────────────────────────────────────────

    @Test
    public void testUpdate() {
        Produto produto = criarProduto("Webcam", 250.00);
        Carrinho carrinho = new Carrinho();
        carrinho = carrinhoRepository.save(carrinho);

        ItemCarrinho item = new ItemCarrinho();
        item.setCarrinho(carrinho); item.setProduto(produto);
        item.setQuantidade(1); item.setPrecoUnitario(250.00);
        carrinho.getItens().add(item);
        carrinhoRepository.save(carrinho);

        // Atualiza a quantidade do item
        item.setQuantidade(3);
        itemCarrinhoRepository.save(item);

        List<ItemCarrinho> itens = itemCarrinhoRepository.findByCarrinhoId(carrinho.getId());

        Assertions.assertEquals(3, itens.get(0).getQuantidade(),
                "A quantidade do item do carrinho não foi atualizada.");
    }

    // ── Remoção ───────────────────────────────────────────────────────────────

    @Test
    public void testDelete() {
        Carrinho carrinho = new Carrinho();
        carrinho = carrinhoRepository.save(carrinho);
        Long id = carrinho.getId();

        carrinhoRepository.delete(carrinho);

        Carrinho carrinhoRemovido = carrinhoRepository.findById(id).orElse(null);
        Assertions.assertNull(carrinhoRemovido, "O carrinho ainda se encontra no banco de dados.");
    }

    @Test
    public void testRemoverItemViaOrphanRemoval() {
        Produto produto = criarProduto("Monitor", 1200.00);
        Carrinho carrinho = new Carrinho();
        carrinho = carrinhoRepository.save(carrinho);

        ItemCarrinho item = new ItemCarrinho();
        item.setCarrinho(carrinho); item.setProduto(produto);
        item.setQuantidade(1); item.setPrecoUnitario(1200.00);
        carrinho.getItens().add(item);
        carrinhoRepository.save(carrinho);

        // Remove o item da lista — o orphanRemoval remove do banco automaticamente
        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho);

        List<ItemCarrinho> itens = itemCarrinhoRepository.findByCarrinhoId(carrinho.getId());
        Assertions.assertTrue(itens.isEmpty(),
                "O item deveria ter sido removido pelo orphanRemoval.");
    }

    // ── Listagem ──────────────────────────────────────────────────────────────

    @Test
    public void testListar() {
        carrinhoRepository.save(new Carrinho());
        carrinhoRepository.save(new Carrinho());

        long inicio = System.currentTimeMillis();
        var carrinhos = carrinhoRepository.findAll();
        long fim = System.currentTimeMillis();

        Assertions.assertFalse(carrinhos.isEmpty(), "A listagem não retornou resultados.");
        Assertions.assertTrue((fim - inicio) < 300, "A consulta demorou mais de 0,3 segundos.");
    }

    // ── Buscas automáticas ────────────────────────────────────────────────────

    @Test
    public void testFindByClienteId() {
        Cliente cliente = criarCliente("Busca Carrinho", "123.123.123-12");

        Carrinho resultado = carrinhoRepository.findByClienteId(cliente.getId()).orElse(null);

        Assertions.assertNotNull(resultado,
                "O carrinho do cliente não foi encontrado pelo ID do cliente.");
    }

    // ── Query JPQL ────────────────────────────────────────────────────────────

    @Test
    public void testFindByIdWithItens() {
        Produto produto = criarProduto("SSD", 400.00);
        Carrinho carrinho = new Carrinho();
        carrinho = carrinhoRepository.save(carrinho);

        ItemCarrinho item = new ItemCarrinho();
        item.setCarrinho(carrinho); item.setProduto(produto);
        item.setQuantidade(2); item.setPrecoUnitario(400.00);
        carrinho.getItens().add(item);
        carrinhoRepository.save(carrinho);

        Carrinho carrinhoComItens = carrinhoRepository.findByIdWithItens(carrinho.getId()).get();

        Assertions.assertNotNull(carrinhoComItens, "O carrinho não foi encontrado via JPQL com JOIN FETCH.");
        Assertions.assertFalse(carrinhoComItens.getItens().isEmpty(),
                "Os itens não foram carregados pelo JOIN FETCH.");
    }

    // ── Query SQL Nativo ──────────────────────────────────────────────────────

    @Test
    public void testCountTotalItens() {
        Produto produto = criarProduto("Headset", 300.00);
        Carrinho carrinho = new Carrinho();
        carrinho = carrinhoRepository.save(carrinho);

        ItemCarrinho item = new ItemCarrinho();
        item.setCarrinho(carrinho); item.setProduto(produto);
        item.setQuantidade(4); item.setPrecoUnitario(300.00);
        carrinho.getItens().add(item);
        carrinhoRepository.save(carrinho);

        int total = carrinhoRepository.countTotalItens(carrinho.getId());

        Assertions.assertEquals(4, total,
                "A query SQL nativa de contagem de itens não retornou o valor correto.");
    }
}
