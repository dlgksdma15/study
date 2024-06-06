package com.suntime.study.controller;

import com.suntime.study.entity.TimerEntity;
import com.suntime.study.dto.MemberDTO;
import com.suntime.study.dto.TimerDTO;
import com.suntime.study.service.TimerService;

import jakarta.servlet.http.HttpSession;

import org.checkerframework.checker.units.qual.Time;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TimerController {
    private final TimerService timerService;

    @PostMapping("/timer/subject")
    public String createSubject(@RequestParam("email") String email, @RequestParam("subject") String subject) {
        TimerDTO timerDTO = new TimerDTO();
        timerDTO.setEmail(email);
        timerDTO.setSubject(subject);
        timerService.save(timerDTO);
        return "redirect:/timer";
    }

    @GetMapping("/timer")
    public String subAllList(Model model, HttpSession session) {
        // 세션 확인
        if (session.getAttribute("loginMember") == null) {
            // 세션이 없는 경우에는 로그인 페이지로 리다이렉트
            return "index";
        }
        //세션이 있는경우 세션 안에서 이메일 확인해서 리스트 불러옴
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        String userEmail = loginMember.getMemberEmail();
        List<TimerEntity> filteredList = timerService.subAllByEmail(userEmail);
        model.addAttribute("list", filteredList);
        return "timer";
    }

    @PostMapping("/timer/del/{id}")
    public String delDataById(@PathVariable Long id) {
        timerService.delDataById(id);
        return "redirect:/timer"; // timer 페이지로 리다이렉트
    }

    @PostMapping("/save-time/{id}")
    public ResponseEntity<String> updateTimer(@PathVariable Long id, @RequestBody TimerDTO timerUpdateDTO) {
        try {
            timerService.updateTimer(id, timerUpdateDTO);
            return ResponseEntity.ok("Timer updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update timer");
        }
    }

    @GetMapping("/get-time/{id}")
    public ResponseEntity<TimerDTO> getTime(@PathVariable Long id) {
        TimerDTO timerDTO = timerService.getTime(id);
        return ResponseEntity.ok(timerDTO);
    }

    @GetMapping("/total")
    public String getTotalTime(Model model, HttpSession session) {
        if (session.getAttribute("loginMember") == null) {
            // 세션이 없는 경우에는 로그인 페이지로 리다이렉트
            return "index";
        }
        //세션이 있는경우 세션 안에서 이메일 확인해서 리스트 불러옴
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        String userEmail = loginMember.getMemberEmail();
        List<TimerEntity> filteredList = timerService.subAllByEmail(userEmail);
        model.addAttribute("list", filteredList);
    
        // 총 시간을 계산할 변수
        int totalHours = 0;
        int totalMinutes = 0;
        int totalSeconds = 0;
    
        // 각 TimerEntity의 시간을 합산
        for (TimerEntity timer : filteredList) {
            totalHours += timer.getHours();
            totalMinutes += timer.getMinutes();
            totalSeconds += timer.getSeconds();
        }
    
        // 시간을 합산한 후, 초가 60을 넘으면 분으로 넘기고, 분이 60을 넘으면 시간으로 넘김
        totalMinutes += totalSeconds / 60;
        totalSeconds %= 60;
        totalHours += totalMinutes / 60;
        totalMinutes %= 60;
    
        // 모델에 총 시간을 추가
        model.addAttribute("totalHours", totalHours);
        model.addAttribute("totalMinutes", totalMinutes);
        model.addAttribute("totalSeconds", totalSeconds);
    
        return "total";
    }
}
