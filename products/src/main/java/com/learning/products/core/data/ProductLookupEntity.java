package com.learning.products.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="productLookup")
@AllArgsConstructor
@NoArgsConstructor
public class ProductLookupEntity {

    @Id
    private String productId;
    @Column(unique = true)
    private String title;
}
