package com.laulem.vectopath.referential.api.infra.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "files", schema = "third_party_ref",
        uniqueConstraints = @UniqueConstraint(columnNames = {"folder_id", "name"}, name = "uq_file_folder_name"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "folder_id", nullable = false, columnDefinition = "UUID")
    private UUID folderId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 2048, unique = true)
    private String path;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", insertable = false, updatable = false, nullable = false)
    private FolderEntity folder;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "file")
    private Set<FileGroupPermissionEntity> permissions;
}
