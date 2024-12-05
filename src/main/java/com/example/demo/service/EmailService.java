package com.example.demo.service;

import com.example.demo.database.entity.Offer;
import com.example.demo.database.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InterviewRepository interviewRepository;
    @Autowired
    private InterviewerInterviewRepository interviewerInterviewRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendTestEmail(Integer id) {

        Optional<Offer> offer = offerRepository.findById(id);

        String emailCandidate = offer.get().getCandidate().getEmail();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(emailCandidate);
        message.setSubject("Test Email");
        message.setText("Duong Testing");
        emailSender.send(message);
    }
}