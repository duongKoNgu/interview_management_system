package com.example.demo.service.impl;

import com.example.demo.customError.CustomExceptions.*;
import com.example.demo.database.entity.Candidate;
import com.example.demo.database.entity.Job;
import com.example.demo.database.entity.User;
import com.example.demo.database.repository.CandidateRepository;
import com.example.demo.database.repository.JobRepository;
import com.example.demo.database.repository.UserRepository;
import com.example.demo.model.candidate.CandidateDetailDto;
import com.example.demo.model.candidate.CandidatePostDto;
import com.example.demo.model.candidate.CandidateViewDto;
import com.example.demo.service.CandidateService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CandidateServiceImpl implements CandidateService {
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${spring.file.upload-dir}")
    private String uploadDir;

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern emailPattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        return emailPattern.matcher(email).find();
    }

    public void validateFields(CandidatePostDto request, BindingResult result) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            result.rejectValue("name", "error.candidatePostDto", "Full name is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            result.rejectValue("email", "error.candidatePostDto", "Email is required");
        } else if (!isValidEmail(request.getEmail())) {
            result.rejectValue("email", "error.candidatePostDto", "Email is not valid format");
        }
        if (request.getGender() == null || request.getGender().trim().isEmpty()) {
            result.rejectValue("gender", "error.candidatePostDto", "Gender is required");
        }
        if (request.getPosition() == null || request.getPosition().trim().isEmpty()) {
            result.rejectValue("position", "error.candidatePostDto", "Position is required");
        }
        if (request.getSkills() == null || request.getSkills().isEmpty()) {
            result.rejectValue("skills", "error.candidatePostDto", "Skills are required");
        }
        if (request.getRecruiter() == null || request.getRecruiter().trim().isEmpty()) {
            result.rejectValue("recruiter", "error.candidatePostDto", "Recruiter is required");
        }
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            result.rejectValue("status", "error.candidatePostDto", "Status is required");
        }
        if (request.getHighestLevel() == null || request.getHighestLevel().trim().isEmpty()) {
            result.rejectValue("highestLevel", "error.candidatePostDto", "Highest level is required");
        }
        if (request.getDob() != null && !request.getDob().isBefore(LocalDate.now())) {
            result.rejectValue("dob", "error.candidatePostDto", "Date of Birth must be in the past");
        }
        if (request.getPhone() != null && !request.getPhone().matches("^[0-9]*$")) {
            result.rejectValue("phone", "error.candidatePostDto", "Phone must be a number");
        }
        if (request.getYearOfExperience() != null && request.getYearOfExperience() < 0) {
            result.rejectValue("yearOfExperience", "error.candidatePostDto", "Year of experience must be greater than 0");
        }
    }

    @Override
    public void createCandidate(CandidatePostDto request, BindingResult result) {
        validateFields(request, result);

        boolean hasErrors = false;

        Optional<Candidate> candidateOptional = candidateRepository.findByEmail(request.getEmail());
        if (candidateOptional.isPresent()) {
            result.rejectValue("email", "error.candidatePostDto", "Email already exists");
            hasErrors = true;
        }

        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            Optional<Candidate> candidateOptionalPhone = candidateRepository.findByPhone(request.getPhone());
            if (candidateOptionalPhone.isPresent()) {
                result.rejectValue("phone", "error.candidatePostDto", "Phone already exists");
                hasErrors = true;
            }
        }

        User recruiter = userRepository.findByUsername(request.getRecruiter());
        if (recruiter == null) {
            result.rejectValue("recruiter", "error.candidatePostDto", "Recruiter not found");
            hasErrors = true;
        }

        if (result.hasErrors() || hasErrors) {
            return;
        }

        // Tạo mới candidate
        Candidate createCandidate = new Candidate();
        createCandidate.setName(request.getName());
        createCandidate.setDob(request.getDob());
        createCandidate.setEmail(request.getEmail());
        createCandidate.setPhone(request.getPhone());
        createCandidate.setGender(request.getGender());
        createCandidate.setAddress(request.getAddress());

        MultipartFile cvFile = request.getCv();
        String fileName = cvFile != null ? cvFile.getOriginalFilename() : null;

        if (cvFile != null && fileName != null && !fileName.isEmpty()) {
            try {
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }
                File uploadFile = new File(uploadDir, fileName);
                cvFile.transferTo(uploadFile);
            } catch (Exception e) {
                result.rejectValue("cv", "error.candidatePostDto", "Could not save file");
                return;
            }
        }
        createCandidate.setCv(fileName);

        createCandidate.setPosition(request.getPosition());
        createCandidate.setSkills(request.getSkills());
        createCandidate.setStatus(request.getStatus());
        createCandidate.setYearOfExperience(request.getYearOfExperience());
        createCandidate.setHighestLevel(request.getHighestLevel());
        createCandidate.setRecruiter(recruiter);
        createCandidate.setNotes(request.getNotes());
        createCandidate.setCreatedAt(LocalDateTime.now());
        candidateRepository.save(createCandidate);
    }

    @Override
    public CandidateDetailDto findById(Integer id) {
        Candidate candidate = candidateRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Candidate not found"));
        CandidateDetailDto dto = new CandidateDetailDto();
        dto.setId(candidate.getCandidateId());
        dto.setName(candidate.getName());
        dto.setDob(candidate.getDob());
        dto.setPhone(candidate.getPhone());
        dto.setEmail(candidate.getEmail());
        dto.setAddress(candidate.getAddress());
        dto.setGender(candidate.getGender());
        dto.setCv(candidate.getCv());
        dto.setPosition(candidate.getPosition());
        dto.setSkills(candidate.getSkills());
        dto.setRecruiter(candidate.getRecruiter().getUsername());
        dto.setStatus(candidate.getStatus());
        dto.setYearOfExperience(candidate.getYearOfExperience());
        dto.setHighestLevel(candidate.getHighestLevel());
        dto.setNotes(candidate.getNotes());
        return dto;
    }

    @Override
    public CandidatePostDto findCandidateById(Integer id) {
        Optional<Candidate> candidateOptional = candidateRepository.findById(id);

        if (candidateOptional.isEmpty()) {
            throw new RuntimeException("Candidate not found");
        }

        Candidate candidate = candidateOptional.get();
        CandidatePostDto candidatePostDto = new CandidatePostDto();
        candidatePostDto.setId(candidate.getCandidateId());
        candidatePostDto.setName(candidate.getName());
        candidatePostDto.setDob(candidate.getDob());
        candidatePostDto.setPhone(candidate.getPhone());
        candidatePostDto.setEmail(candidate.getEmail());
        candidatePostDto.setAddress(candidate.getAddress());
        candidatePostDto.setGender(candidate.getGender());
        candidatePostDto.setPosition(candidate.getPosition());
        candidatePostDto.setSkills(candidate.getSkills());
        candidatePostDto.setRecruiter(candidate.getRecruiter().getUsername());
        candidatePostDto.setStatus(candidate.getStatus());
        candidatePostDto.setYearOfExperience(candidate.getYearOfExperience());
        candidatePostDto.setHighestLevel(candidate.getHighestLevel());
        candidatePostDto.setNotes(candidate.getNotes());

        String cvFileName = candidate.getCv();
        if (cvFileName != null && !cvFileName.isEmpty()) {
            String filePath = uploadDir + File.separator + cvFileName;

            try {
                File file = new File(filePath);
                FileInputStream input = new FileInputStream(file);
                MultipartFile cvFile = new MockMultipartFile("cv", file.getName(), "application/octet-stream", IOUtils.toByteArray(input));
                candidatePostDto.setCv(cvFile);
            } catch (Exception e) {
                throw new RuntimeException("Could not create MultipartFile for CV");
            }
        }

        return candidatePostDto;
    }

    public void updateCandidate(CandidatePostDto request, BindingResult result) {
        Optional<Candidate> candidateOptional = candidateRepository.findById(request.getId());

        if (candidateOptional.isPresent()) {
            Candidate existingCandidate = candidateOptional.get();

            if (request.getName() == null || request.getName().trim().isEmpty()) {
                result.rejectValue("name", "error.candidatePostDto", "Full name is required");
            } else {
                existingCandidate.setName(request.getName());
            }

            if (request.getDob() != null) {
                if (request.getDob().isAfter(LocalDate.now())) {
                    result.rejectValue("dob", "error.candidatePostDto", "Date of Birth must be in the past");
                } else {
                    existingCandidate.setDob(request.getDob());
                }
            }

            if (request.getPhone() != null && !request.getPhone().isEmpty() && !request.getPhone().equals(existingCandidate.getPhone())) {
                if (!request.getPhone().matches("^[0-9]*$")) {
                    result.rejectValue("phone", "error.candidatePostDto", "Phone must be a number");
                } else {
                    Optional<Candidate> candidateOptionalPhone = candidateRepository.findByPhone(request.getPhone());
                    if (candidateOptionalPhone.isPresent() && !candidateOptionalPhone.get().getCandidateId().equals(existingCandidate.getCandidateId())) {
                        result.rejectValue("phone", "error.candidatePostDto", "Phone already exists");
                    } else {
                        existingCandidate.setPhone(request.getPhone());
                    }
                }
            }

            existingCandidate.setGender(request.getGender());
            existingCandidate.setAddress(request.getAddress());

            if (!isValidEmail(request.getEmail())) {
                result.rejectValue("email", "error.candidatePostDto", "Email is not in a valid format");
            } else if (!request.getEmail().equals(existingCandidate.getEmail())) {
                Optional<Candidate> candidateOptionalEmail = candidateRepository.findByEmail(request.getEmail());
                if (candidateOptionalEmail.isPresent() && !candidateOptionalEmail.get().getCandidateId().equals(existingCandidate.getCandidateId())) {
                    result.rejectValue("email", "error.candidatePostDto", "Email already exists");
                } else {
                    existingCandidate.setEmail(request.getEmail());
                }
            }

            MultipartFile cvFile = request.getCv();
            if (cvFile != null && !cvFile.isEmpty()) {
                try {
                    String uploadDir = "C:\\Users\\MyPC\\Desktop\\SAVE_FILE";
                    File uploadDirFile = new File(uploadDir);
                    if (!uploadDirFile.exists()) {
                        uploadDirFile.mkdirs();
                    }

                    String fileName = cvFile.getOriginalFilename();
                    String newFileName = fileName;
                    File uploadFile = new File(uploadDir, newFileName);
                    int counter = 1;
                    while (uploadFile.exists()) {
                        String name = fileName.substring(0, fileName.lastIndexOf('.'));
                        String extension = fileName.substring(fileName.lastIndexOf('.'));
                        newFileName = name + "_" + counter + extension;
                        uploadFile = new File(uploadDir, newFileName);
                        counter++;
                    }

                    cvFile.transferTo(uploadFile);
                    existingCandidate.setCv(newFileName);
                } catch (Exception e) {
                    result.rejectValue("cv", "error.candidatePostDto", "Failed to save file");
                }
            }

            existingCandidate.setPosition(request.getPosition());
            existingCandidate.setSkills(request.getSkills());

            User recruiter = userRepository.findByUsername(request.getRecruiter());
            if (recruiter == null) {
                result.rejectValue("recruiter", "error.candidatePostDto", "Recruiter not found");
            } else {
                existingCandidate.setRecruiter(recruiter);
            }

            if (request.getYearOfExperience() != null && request.getYearOfExperience() < 0) {
                result.rejectValue("yearOfExperience", "error.candidatePostDto", "Year of experience must be greater than 0");
            } else {
                existingCandidate.setYearOfExperience(request.getYearOfExperience());
            }

            existingCandidate.setHighestLevel(request.getHighestLevel());
            existingCandidate.setNotes(request.getNotes());

            existingCandidate.setUpdatedAt(LocalDateTime.now());

            if (!result.hasErrors()) {
                candidateRepository.save(existingCandidate);
            }
        } else {
            result.reject("error.candidatePostDto", "Candidate not found");
        }
    }

    @Override
    public Page<CandidateViewDto> getAllCandidates(Pageable pageable) {
        Page<Candidate> candidatePage = candidateRepository.findAll(pageable);
        List<CandidateViewDto> candidateViewList = candidatePage.stream().map(candidate -> {
            CandidateViewDto candidateViewDto = new CandidateViewDto();
            candidateViewDto.setId(candidate.getCandidateId());
            candidateViewDto.setName(candidate.getName());
            candidateViewDto.setEmail(candidate.getEmail());
            candidateViewDto.setPhone(candidate.getPhone());
            candidateViewDto.setPosition(candidate.getPosition());
            candidateViewDto.setRecruiterUsername(candidate.getRecruiter().getUsername());
            candidateViewDto.setStatus(candidate.getStatus());
            return candidateViewDto;
        }).collect(Collectors.toList());

        return new PageImpl<>(candidateViewList, pageable, candidatePage.getTotalElements());
    }

    @Override
    public Page<CandidateViewDto> getAllByInterviewer(Pageable pageable, String username) {
        Page<Candidate> candidatePage = candidateRepository.findAllByInterviewerUsername(pageable, username);
        List<CandidateViewDto> candidateViewList = candidatePage.stream().map(candidate -> {
            CandidateViewDto candidateViewDto = new CandidateViewDto();
            candidateViewDto.setId(candidate.getCandidateId());
            candidateViewDto.setName(candidate.getName());
            candidateViewDto.setEmail(candidate.getEmail());
            candidateViewDto.setPhone(candidate.getPhone());
            candidateViewDto.setPosition(candidate.getPosition());
            candidateViewDto.setRecruiterUsername(candidate.getRecruiter().getUsername());
            candidateViewDto.setStatus(candidate.getStatus());
            return candidateViewDto;
        }).collect(Collectors.toList());

        return new PageImpl<>(candidateViewList, pageable, candidatePage.getTotalElements());
    }

    @Override
    public Page<CandidateViewDto> getAllByRecruiter(Pageable pageable, String username) {
        Page<Candidate> candidatePage = candidateRepository.findAllByRecruiter(pageable, username);
        List<CandidateViewDto> candidateViewList = candidatePage.stream().map(candidate -> {
            CandidateViewDto candidateViewDto = new CandidateViewDto();
            candidateViewDto.setId(candidate.getCandidateId());
            candidateViewDto.setName(candidate.getName());
            candidateViewDto.setEmail(candidate.getEmail());
            candidateViewDto.setPhone(candidate.getPhone());
            candidateViewDto.setPosition(candidate.getPosition());
            candidateViewDto.setRecruiterUsername(candidate.getRecruiter().getUsername());
            candidateViewDto.setStatus(candidate.getStatus());
            return candidateViewDto;
        }).collect(Collectors.toList());

        return new PageImpl<>(candidateViewList, pageable, candidatePage.getTotalElements());
    }

    @Override
    public Page<CandidateViewDto> searchCandidates(String keyword, String status, Pageable pageable) {
        Page<Candidate> candidatePage = candidateRepository.searchByKeywordAndStatus(keyword, status, pageable);
        List<CandidateViewDto> candidateViewList = candidatePage.stream().map(this::convertToDto).collect(Collectors.toList());

        return new PageImpl<>(candidateViewList, pageable, candidatePage.getTotalElements());
    }

    @Override
    public Page<CandidateViewDto> searchCandidatesByRecruiter(String keyword, String status,
                                                              String recruiter, Pageable pageable) {
        Page<Candidate> candidatePage = candidateRepository.searchByKeywordAndStatusAndRecruiter(keyword, status, recruiter, pageable);
        List<CandidateViewDto> candidateViewList = candidatePage.stream().map(this::convertToDto).collect(Collectors.toList());

        return new PageImpl<>(candidateViewList, pageable, candidatePage.getTotalElements());
    }

    @Override
    public Page<CandidateViewDto> searchCandidatesByInterviewer(String keyword, String status,
                                                                String interviewer, Pageable pageable) {
        Page<Candidate> candidatePage = candidateRepository.searchByKeywordAndStatusAndInterviewer(keyword, status, interviewer, pageable);
        List<CandidateViewDto> candidateViewList = candidatePage.stream().map(this::convertToDto).collect(Collectors.toList());

        return new PageImpl<>(candidateViewList, pageable, candidatePage.getTotalElements());
    }

    private CandidateViewDto convertToDto(Candidate candidate) {
        CandidateViewDto candidateViewDto = new CandidateViewDto();
        candidateViewDto.setId(candidate.getCandidateId());
        candidateViewDto.setName(candidate.getName());
        candidateViewDto.setEmail(candidate.getEmail());
        candidateViewDto.setPhone(candidate.getPhone());
        candidateViewDto.setPosition(candidate.getPosition());
        candidateViewDto.setRecruiterUsername(candidate.getRecruiter().getUsername());
        candidateViewDto.setStatus(candidate.getStatus());
        return candidateViewDto;
    }

    @Override
    public void deleteCandidate(String email) {
        Optional<Candidate> candidateOptional = candidateRepository.findByEmail(email);
        if (candidateOptional.isPresent()) {
            Candidate candidate = candidateOptional.get();
            if (candidate.getStatus().equals("Open")) {
                candidateRepository.delete(candidateOptional.get());
            } else {
                throw new RuntimeException("Candidate cannot delete because status is not Open");
            }
        } else {
            throw new RuntimeException("Candidate not found");
        }
    }

    @Override
    public void banCandidate(Integer id) {
        Optional<Candidate> candidateOptional = candidateRepository.findById(id);
        if (candidateOptional.isPresent()) {
            Candidate candidate = candidateOptional.get();
            if (!candidate.getStatus().equals("Banned")) {
                candidate.setStatus("Banned");
                candidateRepository.save(candidate);
            } else {
                throw new RuntimeException("Candidate already banned");
            }
        } else {
            throw new RuntimeException("Candidate not found");
        }
    }

    @Override
    public boolean isCandidateInterviewedBy(Integer candidateId, String username) {
        return candidateRepository.existsByIdAndInterviewer(candidateId, username);
    }

    @Override
    public boolean isCandidateRecruitedBy(Integer candidateId, String username) {
        return candidateRepository.existsByIdAndRecruiter(candidateId, username);
    }

    @Override
    public List<User> getAllRecruiters() {
        return userRepository.findAllRecruiters();
    }

    @Override
    public int getTotalPages(int pageSize) {
        return (int) Math.ceil((double) candidateRepository.count() / pageSize);
    }
}
