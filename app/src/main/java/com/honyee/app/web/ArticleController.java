package com.honyee.app.web;

import com.honyee.app.config.limit.RateLimit;
import com.honyee.app.dto.ArticleDTO;
import com.honyee.app.dto.base.ChainDTO;
import com.honyee.app.dto.base.Insert;
import com.honyee.app.dto.base.MyPage;
import com.honyee.app.dto.base.Update;
import com.honyee.app.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Operation(summary = "文章列表")
    @GetMapping("/chain")
    public ChainDTO<ArticleDTO> findChain(@Schema(description = "链路ID") @RequestParam(required = false, defaultValue = "0") Long chainId, MyPage myPage) {
        return articleService.findChainDTO(chainId, myPage);
    }

    @Operation(summary = "文章详细")
    @GetMapping("/detail")
    public ArticleDTO findDetail(@Schema(description = "文章id") @RequestParam Long id) {
        return articleService.findDetailDTO(id);
    }

    @Operation(summary = "文章新增")
    @PostMapping("/create")
    @RateLimit(mode = RateLimit.LimitMode.LOCK, lockKey = "'article_create'")
    public ResponseEntity<Void> create(@Validated(Insert.class) @RequestBody ArticleDTO dto) {
        articleService.create(dto);
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "文章修改")
    @PostMapping("/modify")
    @RateLimit(mode = RateLimit.LimitMode.LOCK, lockKey = "#dto.id")
    public ResponseEntity<Void> modify(@Validated(Update.class) @RequestBody ArticleDTO dto) {
        articleService.modify(dto);
        return ResponseEntity.ok(null);
    }

}
