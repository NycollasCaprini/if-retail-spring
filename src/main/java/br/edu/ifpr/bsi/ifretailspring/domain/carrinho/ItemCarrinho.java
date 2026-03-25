package br.edu.ifpr.bsi.ifretailspring.domain.carrinho;

import br.edu.ifpr.bsi.ifretailspring.domain.GenericDomain;
import br.edu.ifpr.bsi.ifretailspring.domain.produto.Produto;
import jakarta.persistence.*;
import lombok.*;

/**
 * Representa um item (produto + quantidade + preço snapshot) dentro de um Carrinho.
 *
 * precoUnitario é um "snapshot" do preço no momento da adição ao carrinho,
 * desacoplado do preço atual do Produto — boa prática para histórico.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "carrinho")
@ToString(callSuper = true, exclude = "carrinho")
@Entity
@Table(name = "tb_itens_carrinho")
public class ItemCarrinho extends GenericDomain {

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "carrinho_id", nullable = false)
    private Carrinho carrinho;

    @Column(nullable = false)
    private int quantidade;

    @Column(name = "preco_unitario", nullable = false)
    private double precoUnitario;
}
