package br.edu.ifpr.bsi.ifretailspring.repository;

import br.edu.ifpr.bsi.ifretailspring.domain.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Admin.
 *
 * JpaRepository<Admin, Long> fornece automaticamente:
 *   save(), findById(), findAll(), delete(), count(), existsById(), etc.
 *
 * Métodos derivados (Derived Query Methods): o Spring Data JPA interpreta
 * o nome do método e gera o SQL correspondente em tempo de execução.
 *
 * @Query com JPQL: linguagem orientada a objetos (usa nomes de classes/campos
 *   Java, não nomes de tabelas/colunas SQL).
 * @Query com nativeQuery = true: SQL puro, útil para queries complexas ou
 *   específicas do banco.
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // ── Busca automática por campo único ──────────────────────────────────────

    /** Busca admin pelo CPF (herdado de User). */
    Optional<Admin> findByCpf(String cpf);

    /** Busca admin pelo setor. */
    List<Admin> findBySetor(String setor);

    /** Busca admin pelo cargo. */
    List<Admin> findByCargo(String cargo);

    /** Busca admins ativos/inativos. */
    List<Admin> findByStatus(boolean status);

    /** Busca admin pela matrícula. */
    Optional<Admin> findByMatricula(String matricula);

    // ── Query JPQL ────────────────────────────────────────────────────────────

    /**
     * Retorna admins ativos de um determinado setor.
     * JPQL referencia o nome da classe Java (Admin) e seus atributos.
     */
    @Query("SELECT a FROM Admin a WHERE a.status = true AND a.setor = :setor")
    List<Admin> findAdminsAtivosPorSetor(@Param("setor") String setor);

    // ── Query SQL nativo ──────────────────────────────────────────────────────

    /**
     * Lista matrículas de todos os admins ativos via SQL nativo.
     * nativeQuery = true → executa diretamente no banco (PostgreSQL).
     */
    @Query(value = "SELECT a.matricula FROM tb_admins a WHERE a.status = true",
            nativeQuery = true)
    List<String> findMatriculasAdminsAtivos();

    // ── Contagem e existência ─────────────────────────────────────────────────

    boolean existsByMatricula(String matricula);

    long countByStatus(boolean status);
}
