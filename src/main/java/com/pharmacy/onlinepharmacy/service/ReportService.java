package com.pharmacy.onlinepharmacy.service;

import com.pharmacy.onlinepharmacy.dto.SalesReportResponse;
import com.pharmacy.onlinepharmacy.dto.TopProductResponse;
import com.pharmacy.onlinepharmacy.repository.OrderItemRepository;
import com.pharmacy.onlinepharmacy.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public SalesReportResponse getSalesReport(LocalDateTime from, LocalDateTime to) {
        var revenue = orderRepository.sumRevenueBetweenDates(from, to);
        var count = orderRepository.countOrdersBetweenDates(from, to);
        return new SalesReportResponse(revenue, count);
    }

    public List<TopProductResponse> getTopProducts(int limit) {
        List<Object[]> results = orderItemRepository.findTopProducts();

        // Превращаем результат из БД в DTO и ограничиваем количество (limit)
        return results.stream()
                .limit(limit)
                .map(row -> new TopProductResponse((String) row[0], ((Number) row[1]).longValue()))
                .collect(Collectors.toList());
    }
}