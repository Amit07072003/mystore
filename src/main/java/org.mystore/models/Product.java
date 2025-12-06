package org.mystore.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Long price;
    private int stockQuantity;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    // ✅ Prevent infinite loop with Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonBackReference   // ✅ IMPORTANT
    private Category category;

    // ✅ Prevent infinite loop with OrderItem
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore          // ✅ IMPORTANT
    private List<OrderItem> orderItems;

    // ✅ Prevent infinite loop with User
    @ManyToOne
    @JoinColumn(name = "seller_id")
    @JsonIgnore          // ✅ IMPORTANT
    private User seller;
}
