package com.abc.SpringBootSecqurityEx.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class DashboardDTO {
    private String dashboard;
    private String username;
    private List<String> roles;
    private Map<String, Long> metrics;
    private List<ProductDTO> products;
    private Map<String, List<DashboardChartDataDTO>> charts;
}
