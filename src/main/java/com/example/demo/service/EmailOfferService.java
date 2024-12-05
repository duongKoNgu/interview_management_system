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
public class EmailOfferService {

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

    public String sendTestEmail(Integer id) {

        Optional<Offer> offer = offerRepository.findById(id);

        String emailCandidate = offer.get().getCandidate().getEmail();
        String candidateName =offer.get().getCandidate().getName();
        String job = offer.get().getJob().getTitle();
        String interview = offer.get().getInterview().getScheduleTitle();
        String salary = offer.get().getSalary();
        String department = offer.get().getDepartment();
        String from = String.valueOf(offer.get().getContractFrom());
        String to = String.valueOf(offer.get().getContractTo());
        String contractType = offer.get().getContractType();
        String level = offer.get().getLevel();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(emailCandidate);
        message.setSubject("Dear "+candidateName+";");

        StringBuilder messageBody = new StringBuilder();
        messageBody.append("Dear " + candidateName + ";\n\n");
        messageBody.append("Candidate Name: " + candidateName + ";\n");
        messageBody.append("Job Name: " + job + ";\n");
        messageBody.append("Interview Name: " + interview + ";\n");
        messageBody.append("Status: Approved;\n");
        messageBody.append("Department: " + department +";\n");
        messageBody.append("Contract Period from: " + from +";\n");
        messageBody.append("Contract Period to: " + to +";\n");
        messageBody.append("Contract type: " + contractType +";\n");
        messageBody.append("Level: " + level +";\n");
        messageBody.append("Salary: " + salary +" VND;\n \n");

        messageBody.append("Do you Accepted or Declined this offer?;\n");

        message.setText(messageBody.toString());

        emailSender.send(message);
        return "Email sent successfully to " + candidateName +".";
    }
}