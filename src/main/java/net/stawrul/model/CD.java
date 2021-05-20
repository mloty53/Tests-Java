package net.stawrul.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.UUID;

/**
 * Klasa encyjna reprezentująca towar w sklepie (książkę).
 */
@Entity
@EqualsAndHashCode(of = "id")
@NamedQueries(value = {
        @NamedQuery(name = CD.FIND_ALL, query = "SELECT c FROM CD c")
})
public class CD {
    public static final String FIND_ALL = "CD.FIND_ALL";

    @Getter
    @Id
    UUID id = UUID.randomUUID();

    @Getter
    @Setter
    String title;

    @Getter
    @Setter
    Integer amount;

    @Getter
    @Setter
    String band;

    @Getter
    @Setter
    Integer year_of_production;

    @Getter
    @Setter
    String genre;

    @Getter
    @Setter
    Double price;

    public double getPrice() {
        return this.price;
    }
}
