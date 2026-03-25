package br.edu.ifpr.bsi.ifretailspring.domain;

import br.edu.ifpr.bsi.ifretailspring.domain.contato.Contato;
import br.edu.ifpr.bsi.ifretailspring.domain.endereco.Endereco;
import br.edu.ifpr.bsi.ifretailspring.domain.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade-raiz da hierarquia de usuários.
 *
 * @Inheritance(JOINED) → cada subclasse tem sua própria tabela;
 *   a PK da subclasse é também FK para tb_users (estratégia mais normalizada).
 * @NoArgsConstructor / @AllArgsConstructor → construtores exigidos pelo JPA
 *   (sem argumentos) e convenientes para testes.
 * @SuperBuilder → permite usar o padrão builder em classes com herança.
 * @EqualsAndHashCode(callSuper = true) → inclui o id herdado no equals/hashCode.
 * @ToString(callSuper = true) → inclui campos da superclasse no toString.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "tb_users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User extends GenericDomain {

    private String name;

    @Column(unique = true, nullable = false, length = 14)
    private String cpf;

    @Column(nullable = false)
    private String password; // deve armazenar hash (BCrypt, etc.)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType tipo;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    // @Builder.Default necessário para que o builder Lombok inicialize a lista
    private List<Endereco> enderecoList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Contato> contatoList = new ArrayList<>();
}
