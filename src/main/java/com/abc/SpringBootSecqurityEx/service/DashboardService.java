package com.abc.SpringBootSecqurityEx.service;

import com.abc.SpringBootSecqurityEx.dtos.DashboardDTO;
import com.abc.SpringBootSecqurityEx.dtos.ProductDTO;
import com.abc.SpringBootSecqurityEx.entity.ProductEntity;
import com.abc.SpringBootSecqurityEx.repository.ProductRepository;
import com.abc.SpringBootSecqurityEx.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public DashboardService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }
    public DashboardDTO roleBasedDashboard(Authentication authentication) {

        String role = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .findFirst()
                .orElse("USER");

        return switch (role) {

            case "ADMIN" ->
                    adminDashboard(authentication);

            case "MODERATOR" ->
                    moderatorDashboard(authentication);

            case "PREMIUM_USER" ->
                    premiumDashboard(authentication);

            case "EMPLOYEE" ->
                    userDashboard(authentication);

            case "USER" ->
                    userDashboard(authentication);

            default ->
                    userDashboard(authentication);
        };
    }

    public DashboardDTO userDashboard(Authentication authentication) {
        List<ProductEntity> products = productRepository.findAllByActiveTrueOrderByNameAsc();
        return dashboard("user", authentication,
                Map.of(
                        "activeProducts", productRepository.countByActiveTrue(),
                        "premiumProducts", productRepository.countByPremiumTrueAndActiveTrue()
                ),
                products.stream().limit(3).toList());
    }

    public DashboardDTO moderatorDashboard(Authentication authentication) {
        return dashboard("moderator", authentication,
                Map.of(
                        "totalProducts", productRepository.count(),
                        "activeProducts", productRepository.countByActiveTrue(),
                        "inactiveProducts", productRepository.countByActiveFalse(),
                        "lowStockProducts", productRepository.countByStockLessThan(5)
                ),
                productRepository.findAllByActiveTrueOrderByNameAsc().stream().limit(5).toList());
    }

    public DashboardDTO adminDashboard(Authentication authentication) {
        long totalUsers = userRepository.count();
        long enabledUsers = userRepository.countByEnabledTrue();
        return dashboard("admin", authentication,
                Map.of(
                        "totalUsers", totalUsers,
                        "enabledUsers", enabledUsers,
                        "disabledUsers", totalUsers - enabledUsers,
                        "totalProducts", productRepository.count(),
                        "activeProducts", productRepository.countByActiveTrue(),
                        "inactiveProducts", productRepository.countByActiveFalse()
                ),
                productRepository.findAllByActiveTrueOrderByNameAsc().stream().limit(5).toList());
    }

    public DashboardDTO premiumDashboard(Authentication authentication) {
        List<ProductEntity> premiumProducts = productRepository.findAllByPremiumTrueAndActiveTrueOrderByNameAsc();
        return dashboard("premium", authentication,
                Map.of("premiumProducts", (long) premiumProducts.size()),
                premiumProducts.stream().limit(5).toList());
    }

    private DashboardDTO dashboard(String dashboardName,
                                   Authentication authentication,
                                   Map<String, Long> metrics,
                                   List<ProductEntity> products) {
        DashboardDTO dto = new DashboardDTO();
        dto.setDashboard(dashboardName);
        dto.setUsername(authentication.getName());
        dto.setRoles(authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList());
        dto.setMetrics(metrics);
        dto.setProducts(products.stream().map(this::toDto).toList());
        return dto;
    }

    private ProductDTO toDto(ProductEntity product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setCategory(product.getCategory());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setStock(product.getStock());
        dto.setActive(product.getActive());
        dto.setPremium(product.getPremium());
        dto.setImageUrl(product.getImageUrl());
        dto.setCreatedAt(product.getCreatedAt());
        return dto;
    }
}
