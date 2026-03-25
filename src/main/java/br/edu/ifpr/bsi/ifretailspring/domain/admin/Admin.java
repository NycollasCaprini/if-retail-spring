package br.edu.ifpr.bsi.ifretailspring.domain.admin;

import br.edu.ifpr.bsi.ifretailspring.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * Subclasse de User para administradores do sistema.
 *
 * @PrimaryKeyJoinColumn → define o nome da FK/PK na tabela tb_admins
 *   que referencia tb_users (obrigatório na estratégia JOINED).
 * @SuperBuilder → necessário em subclasses que usam @SuperBuilder na superclasse.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "tb_admins")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {

    @Column(unique = true)
    private String matricula;

    private String setor;
    private String cargo;

    private LocalDate dataAdmissao;

    @Column(nullable = false)
    @Builder.Default
    private boolean status = true;
}
