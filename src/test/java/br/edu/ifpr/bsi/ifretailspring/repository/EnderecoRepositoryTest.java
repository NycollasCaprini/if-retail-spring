package br.edu.ifpr.bsi.ifretailspring.repository;

import br.edu.ifpr.bsi.ifretailspring.domain.cliente.Cliente;
import br.edu.ifpr.bsi.ifretailspring.domain.endereco.Endereco;
import br.edu.ifpr.bsi.ifretailspring.domain.enums.UserType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Transactional
class EnderecoRepositoryTest {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente criarCliente(String nome, String cpf) {
        Cliente c = new Cliente();
        c.setName(nome); c.setCpf(cpf);
        c.setPassword("hash"); c.setTipo(UserType.CLIENTE);
        return clienteRepository.save(c);
    }

    private Endereco novoEndereco(Cliente user, String rua, String cidade, String estado, String cep) {
        Endereco e = new Endereco();
        e.setUser(user);
        e.setRua(rua); e.setNumero("100");
        e.setBairro("Centro"); e.setCidade(cidade);
        e.setEstado(estado); e.setCep(cep);
        e.setPais("Brasil");
        return e;
    }

    // ── Inserção simples ──────────────────────────────────────────────────────

    @Test
    public void testInsert() {
        Cliente cliente = criarCliente("Endereco Insert", "010.010.010-10");

        Endereco endereco = novoEndereco(cliente, "Rua das Acácias", "Palmas", "PR", "85555-000");
        endereco = enderecoRepository.save(endereco);

        Endereco enderecoInserido = enderecoRepository.findById(endereco.getId()).get();

        Assertions.assertNotNull(enderecoInserido, "O endereço não foi inserido.");
        Assertions.assertEquals("Palmas", enderecoInserido.getCidade(), "A cidade não confere.");
    }

    // ── Inserção com usuário relacionado ──────────────────────────────────────

    @Test
    public void testInserirComUsuario() {
        Cliente cliente = criarCliente("Relação Endereco", "020.020.020-20");

        Endereco endereco = novoEndereco(cliente, "Av. Brasil", "Cascavel", "PR", "85800-000");
        endereco = enderecoRepository.save(endereco);

        Endereco enderecoInserido = enderecoRepository.findById(endereco.getId()).get();

        Assertions.assertNotNull(enderecoInserido.getUser(),
                "O usuário associado ao endereço está nulo.");
        Assertions.assertEquals(cliente.getId(), enderecoInserido.getUser().getId(),
                "O usuário associado ao endereço não confere.");
    }

    // ── Atualização ───────────────────────────────────────────────────────────

    @Test
    public void testUpdate() {
        Cliente cliente = criarCliente("Update Endereco", "030.030.030-30");

        Endereco endereco = novoEndereco(cliente, "Rua Antiga", "Curitiba", "PR", "80000-000");
        endereco = enderecoRepository.save(endereco);

        endereco.setRua("Rua Nova");
        endereco = enderecoRepository.save(endereco);

        Endereco enderecoAtualizado = enderecoRepository.findById(endereco.getId()).get();

        Assertions.assertEquals("Rua Nova", enderecoAtualizado.getRua(),
                "A rua do endereço não foi atualizada.");
    }

    // ── Remoção ───────────────────────────────────────────────────────────────

    @Test
    public void testDelete() {
        Cliente cliente = criarCliente("Delete Endereco", "040.040.040-40");

        Endereco endereco = novoEndereco(cliente, "Rua Deletar", "Maringá", "PR", "87000-000");
        endereco = enderecoRepository.save(endereco);

        enderecoRepository.delete(endereco);

        Endereco enderecoDeletado = enderecoRepository.findById(endereco.getId()).orElse(null);
        Assertions.assertNull(enderecoDeletado, "O endereço ainda se encontra no banco de dados.");
    }

    // ── Listagem ──────────────────────────────────────────────────────────────

    @Test
    public void testListar() {
        Cliente cliente = criarCliente("Listar Endereco", "050.050.050-50");

        enderecoRepository.save(novoEndereco(cliente, "Rua A", "Palmas", "PR", "85555-001"));
        enderecoRepository.save(novoEndereco(cliente, "Rua B", "Palmas", "PR", "85555-002"));

        long inicio = System.currentTimeMillis();
        List<Endereco> enderecos = enderecoRepository.findAll();
        long fim = System.currentTimeMillis();

        Assertions.assertFalse(enderecos.isEmpty(), "A listagem não retornou resultados.");
        Assertions.assertTrue((fim - inicio) < 300, "A consulta demorou mais de 0,3 segundos.");
    }

    // ── Buscas automáticas ────────────────────────────────────────────────────

    @Test
    public void testFindByUserId() {
        Cliente cliente = criarCliente("Find User End", "060.060.060-60");

        enderecoRepository.save(novoEndereco(cliente, "Rua Teste", "Toledo", "PR", "85900-000"));

        List<Endereco> resultado = enderecoRepository.findByUserId(cliente.getId());

        Assertions.assertFalse(resultado.isEmpty(),
                "Nenhum endereço encontrado para o usuário informado.");
    }

    @Test
    public void testFindByCidade() {
        Cliente cliente = criarCliente("Find Cidade", "070.070.070-70");

        enderecoRepository.save(novoEndereco(cliente, "Rua PR1", "Francisco Beltrão", "PR", "85601-000"));
        enderecoRepository.save(novoEndereco(cliente, "Rua PR2", "Pato Branco", "PR", "85501-000"));

        List<Endereco> resultado = enderecoRepository.findByCidade("Francisco Beltrão");

        Assertions.assertFalse(resultado.isEmpty(), "Nenhum endereço encontrado pela cidade.");
        resultado.forEach(e -> Assertions.assertEquals("Francisco Beltrão", e.getCidade(),
                "Endereço de cidade diferente retornado."));
    }

    @Test
    public void testFindByCep() {
        Cliente cliente = criarCliente("Find CEP", "080.080.080-80");

        enderecoRepository.save(novoEndereco(cliente, "Rua do CEP", "Palmas", "PR", "85555-999"));

        List<Endereco> resultado = enderecoRepository.findByCep("85555-999");

        Assertions.assertFalse(resultado.isEmpty(), "Nenhum endereço encontrado pelo CEP.");
    }

    // ── Query JPQL ────────────────────────────────────────────────────────────

    @Test
    public void testFindByUserIdAndCidade() {
        Cliente cliente = criarCliente("JPQL Endereco", "090.090.090-90");

        enderecoRepository.save(novoEndereco(cliente, "Rua A", "Dois Vizinhos", "PR", "85660-000"));
        enderecoRepository.save(novoEndereco(cliente, "Rua B", "Curitiba", "PR", "80010-000"));

        List<Endereco> resultado = enderecoRepository.findByUserIdAndCidade(
                cliente.getId(), "Dois Vizinhos");

        Assertions.assertFalse(resultado.isEmpty(),
                "Nenhum endereço encontrado para o usuário na cidade especificada.");
        resultado.forEach(e -> Assertions.assertEquals("Dois Vizinhos", e.getCidade(),
                "Endereço de cidade diferente retornado na busca JPQL."));
    }

    // ── Query SQL Nativo ──────────────────────────────────────────────────────

    @Test
    public void testFindAllCidades() {
        Cliente c1 = criarCliente("Cidades 1", "100.100.100-00");
        Cliente c2 = criarCliente("Cidades 2", "100.100.100-01");

        enderecoRepository.save(novoEndereco(c1, "Rua X", "Guarapuava", "PR", "85010-000"));
        enderecoRepository.save(novoEndereco(c2, "Rua Y", "Londrina", "PR", "86010-000"));

        List<String> cidades = enderecoRepository.findAllCidades();

        Assertions.assertFalse(cidades.isEmpty(),
                "A query SQL nativa de listagem de cidades não retornou resultados.");
        Assertions.assertTrue(cidades.contains("Guarapuava"),
                "A cidade Guarapuava deveria estar na lista.");
        Assertions.assertTrue(cidades.contains("Londrina"),
                "A cidade Londrina deveria estar na lista.");
    }
}
