package com.anubhav.techblog.Techblogging.service;

public interface EmailService {

	void sendPasswordResetEmail(String to, String resetLink);
}
