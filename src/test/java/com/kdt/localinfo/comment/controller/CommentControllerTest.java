package com.kdt.localinfo.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.localinfo.comment.dto.CommentSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Sql(scripts = {"classpath:data.sql"})
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("댓글 생성")
    void saveTest() throws Exception {
        CommentSaveRequest commentSaveRequest = new CommentSaveRequest(1L, "댓글 생성해주세요.");
        mockMvc.perform(RestDocumentationRequestBuilders.post("/posts/{post-id}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentSaveRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.contents", is("댓글 생성해주세요.")))
                .andExpect(jsonPath("$.data.nickName", is("0kwon")))
                .andExpect(jsonPath("$.data.region", is("행신동")))
                .andExpect(jsonPath("$.data.depth", is(0)))
                .andDo(print());
    }

}