package br.edu.ifpr.bsi.ifretailspring.domain.produto;

import br.edu.ifpr.bsi.ifretailspring.domain.GenericDomain;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade de produto do catálogo.
 *
 * @Builder → habilita o padrão builder para criação facilitada em testes.
 * O campo 'quantidadeEmEstoque' foi renomeado para seguir o padrão camelCase
 * do Java (anteriormente 'QuantidadeEmEstoque' com maiúscula inicial era inválido).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_produtos")
public class Produto extends GenericDomain {

    @Column(nullable = false)
    private String descricao;

    @Column(name = "quantidade_em_estoque")
    private int quantidadeEmEstoque;

    @Column(name = "preco_unitario", nullable = false)
    private double precoUnitario;

    @Builder.Default
    private boolean status = true;
}
