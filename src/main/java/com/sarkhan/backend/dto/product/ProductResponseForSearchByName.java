package com.sarkhan.backend.dto.product;

import java.util.List;

public record ProductResponseForSearchByName(String name,
                                             List<ProductResponseForGroupOfProduct> products) {
}
