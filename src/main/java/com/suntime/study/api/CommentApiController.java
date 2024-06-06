package com.suntime.study.api;

import com.suntime.study.dto.CommentDto;
import com.suntime.study.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentApiController {
    @Autowired
    private CommentService commentService;
    // 1. 댓글 조회
    @GetMapping("/api/board/{boardId}/comments")
    public ResponseEntity<List<CommentDto>> comments(@PathVariable Long boardId){
        // 서비스에 위임
        List<CommentDto> dtos = commentService.comments(boardId);
        // 결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }
    // 2. 댓글 생성
    @PostMapping("/api/board/{boardId}/comments") // 댓글 생성 요청 접수
    public ResponseEntity<CommentDto> create(@PathVariable Long boardId,
                                             @RequestBody CommentDto dto){
        // 서비스에 위임
        CommentDto createdDto = commentService.create(boardId, dto);
        // 결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(createdDto);
    }
    // 3. 댓글 수정
    @PatchMapping("/api/comments/{id}")
    public ResponseEntity<CommentDto> update(@PathVariable Long id,
                                             @RequestBody CommentDto dto){
        // 서비스에 위임
        CommentDto updatedDto = commentService.update(id,dto);
        // 결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }
    // 4. 댓글 삭제
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<CommentDto> delete(@PathVariable Long id){
        // 서비스에 위임
        CommentDto deletedDto = commentService.delete(id);
        // 결과 응답
        return null; // 리턴값
    }
}
