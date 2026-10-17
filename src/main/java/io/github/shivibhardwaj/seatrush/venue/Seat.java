package io.github.shivibhardwaj.seatrush.venue;

import io.github.shivibhardwaj.seatrush.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name= "seat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat extends BaseEntity {

    @Column(nullable = false)
    private String seatNo;

    @Column(nullable = false)
    private String rowLabel;

    @Column(nullable = false)
    private String section;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name= "venue_id", nullable = false)
    private Venue venue;

    public Seat(String seatNo, String rowLabel, String section, Venue venue) {
        this.seatNo = Objects.requireNonNull(seatNo, "seatNo");
        this.rowLabel = Objects.requireNonNull(rowLabel, "rowLabel");
        this.section = Objects.requireNonNull(section, "section");
        this.venue = Objects.requireNonNull(venue, "venue");
    }
}
