package com.anubhav.techblog.Techblogging.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/infra")
public class InfraController {

	@GetMapping("/warmup")
	public String warmup() {
		return "OK";
	}
}
