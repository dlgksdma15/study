package com.suntime.study.controller;

import com.suntime.study.dto.MemberDTO;
import com.suntime.study.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/register")
    public String saveForm(Model model) {
        model.addAttribute("memberDTO", new MemberDTO()); // MemberDTO 객체를 모델에 추가
        return "register";
    }

    @PostMapping("/register")
    public String save(@Valid @ModelAttribute MemberDTO memberDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        memberService.save(memberDTO);
        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 이메일 인증을 해주세요.");
        return "redirect:/";
    }

    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmailDuplicate(@RequestParam String email) {
        return memberService.isEmailDuplicate(email);
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String email, @RequestParam String token, Model model) {
        boolean isVerified = memberService.verifyEmail(email, token);
        if (isVerified) {
            model.addAttribute("message", "이메일 인증이 완료되었습니다.");
        } else {
            model.addAttribute("message", "이메일 인증에 실패했습니다.");
        }
        return "verificationResult";
    }

    @PostMapping("/index")
    public String login(@ModelAttribute MemberDTO memberDTO, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        MemberDTO loginResult = memberService.login(memberDTO);
        if (loginResult != null) {
            HttpSession session = request.getSession();
            session.setAttribute("loginMember", loginResult);
            return "redirect:/timer";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "아이디 또는 비밀번호가 일치하지 않습니다.\n 이메일 인증을 확인하십시오.");
            return "redirect:/";
        }
    }



}
