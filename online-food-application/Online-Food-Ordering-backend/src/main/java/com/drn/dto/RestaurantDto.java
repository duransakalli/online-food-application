package com.drn.dto;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Embeddable
public class RestaurantDto {

    Long id;

    private String title;

    @Column(length = 1000)
    private List<String> images;

    private String description;
}
