package com.laulem.vectopath.referential.api.infra.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderGroupPermissionIdEmbeddable implements Serializable {

    @Column(name = "folder_id")
    private UUID folderId;

    @Column(name = "group_id")
    private UUID groupId;
}
