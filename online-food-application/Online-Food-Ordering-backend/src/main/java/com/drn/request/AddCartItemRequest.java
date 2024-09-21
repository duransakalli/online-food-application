package com.drn.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddCartItemRequest {

    private Long foodId;
    private int quantity;
    private List<String> ingredients;

}
