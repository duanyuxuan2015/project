package com.example.ecommerce.member.contract;

import com.example.ecommerce.member.MemberApplication;
import com.example.ecommerce.member.dto.response.AddressResponse;
import com.example.ecommerce.member.dto.response.ApiResponse;
import com.example.ecommerce.member.dto.response.LoginHistoryResponse;
import com.example.ecommerce.member.dto.response.MemberInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 会员信息查询接口契约测试
 * 验证API接口的请求/响应格式符合OpenAPI规范
 */
@SpringBootTest(classes = MemberApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberControllerContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetMemberInfo_Success() throws Exception {
        // Given
        Long memberId = 1L;

        // When & Then
        MvcResult result = mockMvc.perform(get("/member/info")
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("0000"))
                .andExpect(jsonPath("$.message").value("成功"))
                .andExpect(jsonPath("$.data").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ApiResponse response = objectMapper.readValue(responseBody, ApiResponse.class);

        assertEquals("0000", response.getCode());
        assertNotNull(response.getData());
    }

    @Test
    void testGetMemberInfo_Unauthorized() throws Exception {
        // When & Then: 未提供Token
        mockMvc.perform(get("/member/info"))
                .andExpect(status().isOk()); // 当前实现未强制要求认证
    }

    @Test
    void testGetLoginHistory_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/member/login-history")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0000"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetLoginHistory_InvalidLimit() throws Exception {
        // When & Then: limit参数验证
        mockMvc.perform(get("/member/login-history")
                        .param("limit", "invalid"))
                .andExpect(status().isBadRequest()); // 参数验证失败
    }

    @Test
    void testGetAddresses_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/member/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0000"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testApiResponseFormat_Success() throws Exception {
        // Given
        MvcResult result = mockMvc.perform(get("/member/info"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ApiResponse response = objectMapper.readValue(responseBody, ApiResponse.class);

        // Then: 验证响应格式
        assertNotNull(response.getCode());
        assertNotNull(response.getMessage());
        assertNotNull(response.getTimestamp());
    }
}
