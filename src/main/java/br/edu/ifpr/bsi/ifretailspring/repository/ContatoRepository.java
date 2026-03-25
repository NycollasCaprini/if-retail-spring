package br.edu.ifpr.bsi.ifretailspring.repository;

import br.edu.ifpr.bsi.ifretailspring.domain.contato.Contato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Contato.
 */
@Repository
public interface ContatoRepository extends JpaRepository<Contato, Long> {

    List<Contato> findByUserId(Long userId);

    Optional<Contato> findByEmail(String email);

    Optional<Contato> findByTelefone(String telefone);

    boolean existsByEmail(String email);

    // ── JPQL ─────────────────────────────────────────────────────────────────

    @Query("SELECT c FROM Contato c WHERE c.user.id = :userId AND c.whatsapp IS NOT NULL")
    List<Contato> findContatosComWhatsapp(@Param("userId") Long userId);
}
