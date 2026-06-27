package com.pinmoa.core.place.entity;

import com.pinmoa.core.space.entity.Space;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "saved_places",
    uniqueConstraints = @UniqueConstraint(columnNames = {"space_id", "place_id", "saved_by"}))
@Getter
@NoArgsConstructor
public class SavedPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "saved_by", nullable = false)
    private Long savedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public SavedPlace(Space space, Place place, Long savedBy) {
        this.space = space;
        this.place = place;
        this.savedBy = savedBy;
    }
}
