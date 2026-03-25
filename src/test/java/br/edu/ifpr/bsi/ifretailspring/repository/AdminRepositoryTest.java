package br.edu.ifpr.bsi.ifretailspring.repository;

import br.edu.ifpr.bsi.ifretailspring.domain.admin.Admin;
import br.edu.ifpr.bsi.ifretailspring.domain.enums.UserType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

// Admin herda de User (herança JOINED): o JPA insere uma linha em tb_users
// e uma linha em tb_admins com a mesma PK — os testes cobrem esse comportamento.
@SpringBootTest
@Transactional
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    // ── Inserção simples ──────────────────────────────────────────────────────

    @Test
    public void testInsert() {
        Admin admin = new Admin();
        admin.setName("Carlos Alberto");
        admin.setCpf("111.222.333-44");
        admin.setPassword("hash_senha");
        admin.setTipo(UserType.ADMIN);
        admin.setMatricula("MAT001");
        admin.setSetor("TI");
        admin.setCargo("Desenvolvedor");
        admin.setDataAdmissao(LocalDate.of(2022, 3, 10));
        admin.setStatus(true);
        admin = adminRepository.save(admin);

        Admin adminInserido = adminRepository.findById(admin.getId()).get();

        Assertions.assertNotNull(adminInserido, "O admin não foi inserido.");
        Assertions.assertEquals("MAT001", adminInserido.getMatricula(),
                "A matrícula do admin não confere.");
    }

    // ── Atualização ───────────────────────────────────────────────────────────

    @Test
    public void testUpdate() {
        Admin admin = new Admin();
        admin.setName("Fernanda Lima");
        admin.setCpf("222.333.444-55");
        admin.setPassword("hash");
        admin.setTipo(UserType.ADMIN);
        admin.setMatricula("MAT002");
        admin.setSetor("RH");
        admin.setCargo("Recrutadora");
        admin.setStatus(true);
        admin = adminRepository.save(admin);

        admin.setCargo("Gerente de RH");
        admin = adminRepository.save(admin);

        Admin adminAtualizado = adminRepository.findById(admin.getId()).get();

        Assertions.assertEquals("Gerente de RH", adminAtualizado.getCargo(),
                "O cargo do admin não foi atualizado.");
    }

    // ── Remoção ───────────────────────────────────────────────────────────────

    @Test
    public void testDelete() {
        Admin admin = new Admin();
        admin.setName("Rodrigo Melo");
        admin.setCpf("333.444.555-66");
        admin.setPassword("hash");
        admin.setTipo(UserType.ADMIN);
        admin.setMatricula("MAT003");
        admin.setSetor("Financeiro");
        admin.setCargo("Contador");
        admin.setStatus(true);
        admin = adminRepository.save(admin);

        adminRepository.delete(admin);

        Admin adminDeletado = adminRepository.findById(admin.getId()).orElse(null);
        Assertions.assertNull(adminDeletado, "O admin ainda se encontra no banco de dados.");
    }

    // ── Listagem com teste de performance ─────────────────────────────────────

    @Test
    public void testListar() {
        for (int i = 1; i <= 3; i++) {
            Admin a = new Admin();
            a.setName("Admin " + i);
            a.setCpf("00" + i + ".000.000-0" + i);
            a.setPassword("hash");
            a.setTipo(UserType.ADMIN);
            a.setMatricula("MAT10" + i);
            a.setSetor("TI");
            a.setCargo("Dev");
            a.setStatus(true);
            adminRepository.save(a);
        }

        long inicio = System.currentTimeMillis();
        List<Admin> admins = adminRepository.findAll();
        long fim = System.currentTimeMillis();

        Assertions.assertFalse(admins.isEmpty(), "A listagem não retornou resultados.");
        Assertions.assertTrue((fim - inicio) < 300, "A consulta demorou mais de 0,3 segundos.");
    }

    // ── Buscas automáticas (Derived Query Methods) ────────────────────────────

    @Test
    public void testFindByMatricula() {
        Admin admin = new Admin();
        admin.setName("Paulo Salave'a");
        admin.setCpf("444.555.666-77");
        admin.setPassword("hash");
        admin.setTipo(UserType.ADMIN);
        admin.setMatricula("MAT999");
        admin.setSetor("Vendas");
        admin.setCargo("Supervisor");
        admin.setStatus(true);
        adminRepository.save(admin);

        Admin encontrado = adminRepository.findByMatricula("MAT999").orElse(null);

        Assertions.assertNotNull(encontrado, "Admin não encontrado pela matrícula.");
        Assertions.assertEquals("Vendas", encontrado.getSetor(), "O setor do admin não confere.");
    }

    @Test
    public void testFindBySetor() {
        for (int i = 1; i <= 3; i++) {
            Admin a = new Admin();
            a.setName("TI Dev " + i);
            a.setCpf("55" + i + ".000.000-0" + i);
            a.setPassword("hash");
            a.setTipo(UserType.ADMIN);
            a.setMatricula("TI00" + i);
            a.setSetor("TI");
            a.setCargo("Dev");
            a.setStatus(true);
            adminRepository.save(a);
        }

        List<Admin> resultado = adminRepository.findBySetor("TI");

        Assertions.assertFalse(resultado.isEmpty(), "Nenhum admin encontrado no setor TI.");
    }

    @Test
    public void testExistsByMatricula() {
        Admin admin = new Admin();
        admin.setName("Tester");
        admin.setCpf("000.111.222-33");
        admin.setPassword("hash");
        admin.setTipo(UserType.ADMIN);
        admin.setMatricula("MAT_EXISTE");
        admin.setSetor("QA");
        admin.setCargo("Tester");
        admin.setStatus(true);
        adminRepository.save(admin);

        Assertions.assertTrue(adminRepository.existsByMatricula("MAT_EXISTE"),
                "A matrícula deveria existir.");
        Assertions.assertFalse(adminRepository.existsByMatricula("MAT_INEXISTENTE"),
                "A matrícula não deveria existir.");
    }

    // ── Query JPQL ────────────────────────────────────────────────────────────

    @Test
    public void testGetAdminsAtivosPorSetor() {
        Admin ativo = new Admin();
        ativo.setName("Ativo TI");
        ativo.setCpf("600.000.000-01");
        ativo.setPassword("hash");
        ativo.setTipo(UserType.ADMIN);
        ativo.setMatricula("AT001");
        ativo.setSetor("TI");
        ativo.setCargo("Dev");
        ativo.setStatus(true);
        adminRepository.save(ativo);

        Admin inativo = new Admin();
        inativo.setName("Inativo TI");
        inativo.setCpf("600.000.000-02");
        inativo.setPassword("hash");
        inativo.setTipo(UserType.ADMIN);
        inativo.setMatricula("AT002");
        inativo.setSetor("TI");
        inativo.setCargo("Dev");
        inativo.setStatus(false); // inativo — não deve aparecer
        adminRepository.save(inativo);

        List<Admin> resultado = adminRepository.findAdminsAtivosPorSetor("TI");

        Assertions.assertFalse(resultado.isEmpty(), "Nenhum admin ativo encontrado no setor TI.");
        resultado.forEach(a -> Assertions.assertTrue(a.isStatus(),
                "Admin inativo retornado na busca por ativos."));
    }

    // ── Query SQL Nativo ──────────────────────────────────────────────────────

    @Test
    public void testGetMatriculasAdminsAtivos() {
        Admin admin = new Admin();
        admin.setName("Nativo Teste");
        admin.setCpf("700.000.000-01");
        admin.setPassword("hash");
        admin.setTipo(UserType.ADMIN);
        admin.setMatricula("NAT001");
        admin.setSetor("Ops");
        admin.setCargo("Analista");
        admin.setStatus(true);
        adminRepository.save(admin);

        List<String> matriculas = adminRepository.findMatriculasAdminsAtivos();

        Assertions.assertFalse(matriculas.isEmpty(),
                "A query SQL nativa não retornou matrículas de admins ativos.");
        Assertions.assertTrue(matriculas.contains("NAT001"),
                "A matrícula NAT001 deveria estar na lista.");
    }
}
