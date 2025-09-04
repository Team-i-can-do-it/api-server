package com.icando.referenceMaterial.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceMaterialAiResponse {
    private String title;
    private String description;
    private String imageUrl;
    private String url;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
