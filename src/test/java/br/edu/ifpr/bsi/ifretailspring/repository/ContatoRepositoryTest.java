package br.edu.ifpr.bsi.ifretailspring.repository;

import br.edu.ifpr.bsi.ifretailspring.domain.contato.Contato;
import br.edu.ifpr.bsi.ifretailspring.domain.cliente.Cliente;
import br.edu.ifpr.bsi.ifretailspring.domain.enums.UserType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Transactional
class ContatoRepositoryTest {

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente criarCliente(String nome, String cpf) {
        Cliente c = new Cliente();
        c.setName(nome); c.setCpf(cpf);
        c.setPassword("hash"); c.setTipo(UserType.CLIENTE);
        return clienteRepository.save(c);
    }

    // ── Inserção simples ──────────────────────────────────────────────────────

    @Test
    public void testInsert() {
        Cliente cliente = criarCliente("Contato Teste", "010.020.030-40");

        Contato contato = new Contato();
        contato.setUser(cliente);
        contato.setTelefone("(46) 3025-1234");
        contato.setEmail("contato@teste.com");
        contato.setWhatsapp("(46) 99999-1234");
        contato = contatoRepository.save(contato);

        Contato contatoInserido = contatoRepository.findById(contato.getId()).get();

        Assertions.assertNotNull(contatoInserido, "O contato não foi inserido.");
        Assertions.assertEquals("contato@teste.com", contatoInserido.getEmail(),
                "O e-mail do contato não confere.");
    }

    // ── Inserção com usuário relacionado ──────────────────────────────────────

    @Test
    public void testInserirComUsuario() {
        Cliente cliente = criarCliente("Relação Contato", "050.060.070-80");

        Contato contato = new Contato();
        contato.setUser(cliente);
        contato.setTelefone("(46) 3333-4444");
        contato.setEmail("relacao@teste.com");
        contato = contatoRepository.save(contato);

        Contato contatoInserido = contatoRepository.findById(contato.getId()).get();

        Assertions.assertNotNull(contatoInserido.getUser(),
                "O usuário associado ao contato está nulo.");
        Assertions.assertEquals(cliente.getId(), contatoInserido.getUser().getId(),
                "O usuário associado ao contato não confere.");
    }

    // ── Atualização ───────────────────────────────────────────────────────────

    @Test
    public void testUpdate() {
        Cliente cliente = criarCliente("Update Contato", "090.080.070-60");

        Contato contato = new Contato();
        contato.setUser(cliente);
        contato.setTelefone("(46) 1111-2222");
        contato.setEmail("antes@email.com");
        contato = contatoRepository.save(contato);

        contato.setEmail("depois@email.com");
        contato = contatoRepository.save(contato);

        Contato contatoAtualizado = contatoRepository.findById(contato.getId()).get();

        Assertions.assertEquals("depois@email.com", contatoAtualizado.getEmail(),
                "O e-mail do contato não foi atualizado.");
    }

    // ── Remoção ───────────────────────────────────────────────────────────────

    @Test
    public void testDelete() {
        Cliente cliente = criarCliente("Delete Contato", "110.110.110-11");

        Contato contato = new Contato();
        contato.setUser(cliente);
        contato.setTelefone("(46) 9999-8888");
        contato.setEmail("deletar@email.com");
        contato = contatoRepository.save(contato);

        contatoRepository.delete(contato);

        Contato contatoDeletado = contatoRepository.findById(contato.getId()).orElse(null);
        Assertions.assertNull(contatoDeletado, "O contato ainda se encontra no banco de dados.");
    }

    // ── Listagem ──────────────────────────────────────────────────────────────

    @Test
    public void testListar() {
        Cliente cliente = criarCliente("Listar Contato", "220.220.220-22");

        for (int i = 1; i <= 3; i++) {
            Contato c = new Contato();
            c.setUser(cliente);
            c.setTelefone("(46) 9999-000" + i);
            c.setEmail("email" + i + "@teste.com");
            contatoRepository.save(c);
        }

        long inicio = System.currentTimeMillis();
        List<Contato> contatos = contatoRepository.findAll();
        long fim = System.currentTimeMillis();

        Assertions.assertFalse(contatos.isEmpty(), "A listagem não retornou resultados.");
        Assertions.assertTrue((fim - inicio) < 300, "A consulta demorou mais de 0,3 segundos.");
    }

    // ── Buscas automáticas ────────────────────────────────────────────────────

    @Test
    public void testFindByUserId() {
        Cliente cliente = criarCliente("Find User Contato", "330.330.330-33");

        Contato contato = new Contato();
        contato.setUser(cliente);
        contato.setTelefone("(46) 7777-6666");
        contato.setEmail("user@find.com");
        contatoRepository.save(contato);

        List<Contato> resultado = contatoRepository.findByUserId(cliente.getId());

        Assertions.assertFalse(resultado.isEmpty(),
                "Nenhum contato encontrado para o usuário informado.");
    }

    @Test
    public void testFindByEmail() {
        Cliente cliente = criarCliente("Email Find", "440.440.440-44");

        Contato contato = new Contato();
        contato.setUser(cliente);
        contato.setTelefone("(46) 5555-4444");
        contato.setEmail("unico@email.com");
        contatoRepository.save(contato);

        Contato resultado = contatoRepository.findByEmail("unico@email.com").orElse(null);

        Assertions.assertNotNull(resultado, "Contato não encontrado pelo e-mail.");
    }

    @Test
    public void testExistsByEmail() {
        Cliente cliente = criarCliente("Exists Email", "550.550.550-55");

        Contato contato = new Contato();
        contato.setUser(cliente);
        contato.setEmail("existe@email.com");
        contato.setTelefone("(46) 3333-2222");
        contatoRepository.save(contato);

        Assertions.assertTrue(contatoRepository.existsByEmail("existe@email.com"),
                "O e-mail deveria existir no banco.");
        Assertions.assertFalse(contatoRepository.existsByEmail("nao@existe.com"),
                "O e-mail não deveria existir no banco.");
    }

    // ── Query JPQL ────────────────────────────────────────────────────────────

    @Test
    public void testFindContatosComWhatsapp() {
        Cliente cliente = criarCliente("WhatsApp Teste", "660.660.660-66");

        Contato comWhats = new Contato();
        comWhats.setUser(cliente);
        comWhats.setTelefone("(46) 9900-0011");
        comWhats.setEmail("whats@email.com");
        comWhats.setWhatsapp("(46) 99900-0011");
        contatoRepository.save(comWhats);

        Contato semWhats = new Contato();
        semWhats.setUser(cliente);
        semWhats.setTelefone("(46) 3300-0011");
        semWhats.setEmail("semwhats@email.com");
        // whatsapp = null
        contatoRepository.save(semWhats);

        List<Contato> resultado = contatoRepository.findContatosComWhatsapp(cliente.getId());

        Assertions.assertFalse(resultado.isEmpty(),
                "Nenhum contato com WhatsApp encontrado.");
        resultado.forEach(c -> Assertions.assertNotNull(c.getWhatsapp(),
                "Contato sem WhatsApp retornado na busca específica."));
    }
}
