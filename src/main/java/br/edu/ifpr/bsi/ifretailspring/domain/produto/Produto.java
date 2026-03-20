package br.edu.ifpr.bsi.ifretailspring.domain.produto;

import br.edu.ifpr.bsi.ifretailspring.domain.GenericDomain;
import br.edu.ifpr.bsi.ifretailspring.domain.carrinho.Carrinho;
import br.edu.ifpr.bsi.ifretailspring.domain.cliente.Cliente;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name="tb_produtos")
public class Produto extends GenericDomain {
    private String descricao;
    private int QuantidadeEmEstoque;
    private double precoUnitario;
    private boolean status;

    @ManyToOne
    @JoinColumn(name="cliente_id")
    private Cliente cliente;

    @ManyToMany
    @JoinTable(

    )
    private List<Carrinho> carrinho;

}
