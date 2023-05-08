package com.honyee.app.web;

import com.honyee.app.dto.QuestionnaireCreateDTO;
import com.honyee.app.model.Questionnaire;
import com.honyee.app.service.QuestionnaireService;
import com.honyee.app.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/questionnaire")
public class QuestionnaireController {

    @Autowired
    private QuestionnaireService questionnaireService;

    @PostMapping("create")
    public void create(@Valid @RequestBody QuestionnaireCreateDTO dto) {
        questionnaireService.create(dto, dto.getPhoneNumber());
    }
}
