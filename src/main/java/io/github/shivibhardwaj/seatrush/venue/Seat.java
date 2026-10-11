package io.github.shivibhardwaj.seatrush.venue;

import io.github.shivibhardwaj.seatrush.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name= "seat")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
}
