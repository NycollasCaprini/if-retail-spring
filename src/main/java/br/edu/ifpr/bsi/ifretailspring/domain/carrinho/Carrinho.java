package br.edu.ifpr.bsi.ifretailspring.domain.carrinho;

import br.edu.ifpr.bsi.ifretailspring.domain.GenericDomain;
import br.edu.ifpr.bsi.ifretailspring.domain.cliente.Cliente;
import br.edu.ifpr.bsi.ifretailspring.domain.produto.Produto;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name="tb_carrinho")
public class Carrinho extends GenericDomain {
    private List<Produto> produtoList;
    private double valorTotal;


    @OneToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

}
