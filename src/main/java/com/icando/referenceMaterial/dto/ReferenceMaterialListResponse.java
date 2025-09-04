package com.icando.referenceMaterial.dto;

import com.icando.referenceMaterial.entity.ReferenceMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceMaterialListResponse {
    private String title;
    private String imageUrl;
    private String url;

    public static ReferenceMaterialListResponse of(ReferenceMaterial referenceMaterial) {
        return new ReferenceMaterialListResponse(
                referenceMaterial.getTitle(),
                referenceMaterial.getImageUrl(),
                referenceMaterial.getUrl()
        );
    }
}
