package br.edu.ifpr.bsi.ifretailspring.repository;

import br.edu.ifpr.bsi.ifretailspring.domain.endereco.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório JPA para a entidade Endereco.
 */
@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    List<Endereco> findByUserId(Long userId);

    List<Endereco> findByCidade(String cidade);

    List<Endereco> findByEstado(String estado);

    List<Endereco> findByCep(String cep);

    // ── JPQL ─────────────────────────────────────────────────────────────────

    @Query("SELECT e FROM Endereco e WHERE e.user.id = :userId AND e.cidade = :cidade")
    List<Endereco> findByUserIdAndCidade(@Param("userId") Long userId, @Param("cidade") String cidade);

    // ── SQL nativo ────────────────────────────────────────────────────────────

    @Query(value = "SELECT DISTINCT cidade FROM tb_enderecos ORDER BY cidade", nativeQuery = true)
    List<String> findAllCidades();
}
