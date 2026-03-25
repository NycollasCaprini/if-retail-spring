package br.edu.ifpr.bsi.ifretailspring.domain.carrinho;

import br.edu.ifpr.bsi.ifretailspring.domain.GenericDomain;
import br.edu.ifpr.bsi.ifretailspring.domain.cliente.Cliente;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Carrinho de compras, associada a um único Cliente (OneToOne).
 *
 * orphanRemoval = true: ao remover um ItemCarrinho da lista, ele é deletado
 * automaticamente do banco (evita itens "órfãos").
 *
 * @ToString(exclude = ...) e @EqualsAndHashCode(exclude = ...) evitam
 * StackOverflowError por referência cíclica entre Carrinho ↔ Cliente ↔ Carrinho.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "cliente")
@ToString(callSuper = true, exclude = "cliente")
@Entity
@Table(name = "tb_carrinho")
public class Carrinho extends GenericDomain {

    @OneToOne(mappedBy = "carrinho")
    private Cliente cliente;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemCarrinho> itens = new ArrayList<>();
}
