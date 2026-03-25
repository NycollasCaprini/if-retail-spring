package br.edu.ifpr.bsi.ifretailspring.domain.endereco;

import br.edu.ifpr.bsi.ifretailspring.domain.GenericDomain;
import br.edu.ifpr.bsi.ifretailspring.domain.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade de endereço associada a um User.
 * Permite múltiplos endereços por usuário (residencial, entrega, etc.).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_enderecos")
public class Endereco extends GenericDomain {

    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;

    @Column(length = 9)
    private String cep;

    private String pais;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
