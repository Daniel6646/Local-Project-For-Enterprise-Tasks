package com.debuglab.mapper;

import org.springframework.stereotype.Component;

import com.debuglab.dto.OrderDTO;
import com.debuglab.entity.OrderEntity;

@Component

public class OrderMapper {

    public OrderEntity toEntity(OrderDTO dto) {
        OrderEntity e = new OrderEntity();
        e.setCustomerName(dto.getCustomerName());
        e.setAmount(dto.getAmount());
        return e;
    }

    public OrderDTO toDto(OrderEntity e) {
        OrderDTO d = new OrderDTO();
        d.setCustomerName(e.getCustomerName());
        d.setAmount(e.getAmount());
        return d;
    }
}

