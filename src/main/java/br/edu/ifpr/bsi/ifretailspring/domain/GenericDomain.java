package br.edu.ifpr.bsi.ifretailspring.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Superclasse abstrata para todas as entidades do domínio.
 *
 * @MappedSuperclass → diz ao JPA que esta classe NÃO é uma tabela,
 *   mas que seus campos mapeados devem ser herdados pelas subclasses.
 * @Data (Lombok) → gera getters, setters, equals, hashCode e toString.
 * @EqualsAndHashCode(callSuper = false) → impede que Lombok encadeie
 *   o equals/hashCode da hierarquia de forma incorreta com proxies JPA.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
public abstract class GenericDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // IDENTITY é preferido ao AUTO com PostgreSQL: usa SERIAL/SEQUENCE nativo
    // e evita a tabela hibernate_sequence gerada pelo AUTO.
    private Long id;
}
