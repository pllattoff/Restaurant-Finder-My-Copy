package org.restaurantfinder.backend.controller;

import org.junit.jupiter.api.Test;
import org.restaurantfinder.backend.model.Route;
import org.restaurantfinder.backend.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RouteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteService routeService;

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/routes")
                        .param("startLat", "52.3809821")
                        .param("startLon", "9.7450007")
                        .param("destinationLat", "52.3812597")
                        .param("destinationLon", "9.7447447")
                        .param("mode", "walk"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void shouldReturnRouteWhenAuthenticated() throws Exception {
        Route expectedRoute = new Route(
                39,
                36.2,
                List.of(
                        List.of(9.7450007, 52.3809821),
                        List.of(9.7447447, 52.3812597)
                )
        );

        when(routeService.getRoute(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyString()))
                .thenReturn(expectedRoute);

        mockMvc.perform(get("/api/routes")
                        .param("startLat", "52.3809821")
                        .param("startLon", "9.7450007")
                        .param("destinationLat", "52.3812597")
                        .param("destinationLon", "9.7447447")
                        .param("mode", "walk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distance").value(39))
                .andExpect(jsonPath("$.duration").value(36.2));
    }
}
