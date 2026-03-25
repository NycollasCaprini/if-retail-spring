package br.edu.ifpr.bsi.ifretailspring.domain.contato;

import br.edu.ifpr.bsi.ifretailspring.domain.GenericDomain;
import br.edu.ifpr.bsi.ifretailspring.domain.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade de contato associada a um User (telefone, e-mail, WhatsApp).
 *
 * Relacionamento ManyToOne com User: vários contatos podem pertencer
 * ao mesmo usuário. A FK 'user_id' é gerada pela anotação @JoinColumn.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_contatos")
public class Contato extends GenericDomain {

    private String telefone;

    @Column(unique = true)
    private String email;

    private String whatsapp;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
