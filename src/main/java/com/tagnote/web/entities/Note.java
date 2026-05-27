package com.tagnote.web.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    private String content;

    @Column(name = "created_at")
    private LocalDateTime created;

    @Column(name = "updated_at")
    private LocalDateTime updated;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();
}
