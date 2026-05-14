package com.ecommerce;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addToCartAndReadSummary() throws Exception {
        mockMvc.perform(post("/api/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "userId", 1,
                    "productId", 1,
                    "quantity", 1
                ))))
            .andDo(result -> System.out.println("ADD STATUS=" + result.getResponse().getStatus()))
            .andDo(result -> System.out.println("ADD BODY=" + result.getResponse().getContentAsString()))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/cart/1"))
            .andDo(result -> System.out.println("GET STATUS=" + result.getResponse().getStatus()))
            .andDo(result -> System.out.println("GET BODY=" + result.getResponse().getContentAsString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items").isArray());
    }
}
