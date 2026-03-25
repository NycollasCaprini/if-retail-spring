package br.edu.ifpr.bsi.ifretailspring.domain.pedido;

import br.edu.ifpr.bsi.ifretailspring.domain.GenericDomain;
import br.edu.ifpr.bsi.ifretailspring.domain.cliente.Cliente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade de pedido realizado por um Cliente.
 *
 * Alteração: DataDoPedido e dataDeEntregaDoPedido agora usam LocalDateTime
 * (em vez de java.util.Date), pois LocalDateTime é preferido no Java moderno
 * e tem suporte nativo no Hibernate sem necessidade de @Temporal.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "cliente")
@ToString(callSuper = true, exclude = "cliente")
@Entity
@Table(name = "tb_pedidos")
public class Pedido extends GenericDomain {

    @Column(nullable = false)
    private LocalDateTime dataDoPedido;

    private LocalDateTime dataDeEntregaDoPedido;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Builder.Default
    private boolean status = true;
}
