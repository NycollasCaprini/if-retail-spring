package br.edu.ifpr.bsi.ifretailspring.domain.cliente;

import br.edu.ifpr.bsi.ifretailspring.domain.User;
import br.edu.ifpr.bsi.ifretailspring.domain.carrinho.Carrinho;
import br.edu.ifpr.bsi.ifretailspring.domain.pedido.Pedido;
import br.edu.ifpr.bsi.ifretailspring.domain.produto.Produto;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Subclasse de User que representa o cliente da loja.
 *
 * @OneToOne(cascade = ALL) no carrinho: o carrinho é criado e destruído
 * com o cliente (ciclo de vida acoplado).
 *
 * @ManyToMany (favoritos): tabela de junção tb_clientes_favoritos permite
 * que um cliente favorite vários produtos e um produto apareça nos favoritos
 * de vários clientes.
 *
 * @SuperBuilder → necessário pois User já usa @SuperBuilder.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"pedidoList", "favoritos"})
@ToString(callSuper = true, exclude = {"pedidoList", "favoritos", "carrinho"})
@Entity
@Table(name = "tb_clientes")
@PrimaryKeyJoinColumn(name = "user_id")
public class Cliente extends User {

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "carrinho_id")
    private Carrinho carrinho;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pedido> pedidoList = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "tb_clientes_favoritos",
            joinColumns = @JoinColumn(name = "cliente_id"),
            inverseJoinColumns = @JoinColumn(name = "produto_id")
    )
    @Builder.Default
    private List<Produto> favoritos = new ArrayList<>();
}
