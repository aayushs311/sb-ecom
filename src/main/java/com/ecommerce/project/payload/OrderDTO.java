package com.ecommerce.project.payload;

import com.ecommerce.project.models.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String email;
    private List<OrderItemDTO> orderItems;
    private ProductDTO product;
    private Payment payment;
    private Double totalAmount;
    private String orderStatus;
    private Long addressId;
}
