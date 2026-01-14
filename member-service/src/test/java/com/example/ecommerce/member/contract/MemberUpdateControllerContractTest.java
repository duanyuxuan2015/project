package com.example.ecommerce.member.contract;

import com.example.ecommerce.member.MemberApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 会员信息修改接口契约测试
 * 验证修改接口的请求/响应格式符合OpenAPI规范
 */
@SpringBootTest(classes = MemberApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberUpdateControllerContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUpdateMemberInfo_Success() throws Exception {
        // Given
        String requestBody = """
            {
                "nickname": "新昵称",
                "gender": 1,
                "birthday": "1990-01-01"
            }
            """;

        // When & Then
        mockMvc.perform(put("/member/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0000"))
                .andExpect(jsonPath("$.message").value("成功"));
    }

    @Test
    void testUpdateMemberInfo_InvalidGender() throws Exception {
        // Given: 性别值超出范围
        String requestBody = """
            {
                "gender": 5
            }
            """;

        // When & Then
        mockMvc.perform(put("/member/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateMemberInfo_InvalidNickname() throws Exception {
        // Given: 昵称太短
        String requestBody = """
            {
                "nickname": "a"
            }
            """;

        // When & Then
        mockMvc.perform(put("/member/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateMemberInfo_PartialUpdate() throws Exception {
        // Given: 只更新昵称
        String requestBody = """
            {
                "nickname": "部分更新"
            }
            """;

        // When & Then
        mockMvc.perform(put("/member/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0000"));
    }

    @Test
    void testApiResponse_ErrorFormat() throws Exception {
        // Given: 错误的请求
        String requestBody = """
            {
                "gender": 10
            }
            """;

        // When & Then
        mockMvc.perform(put("/member/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
