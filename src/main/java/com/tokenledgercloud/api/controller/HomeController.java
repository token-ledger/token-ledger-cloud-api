package com.tokenledgercloud.api.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tokenledgercloud.api.dto.MemberResponse;
import com.tokenledgercloud.api.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final MemberService memberService;

	@GetMapping("/")
	public String home(Authentication authentication, Model model) {
		MemberResponse me = memberService.getCurrentMember(authentication);
		model.addAttribute("memberName", me.name());
		return "home";
	}
}
