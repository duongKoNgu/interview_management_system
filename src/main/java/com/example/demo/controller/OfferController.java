package com.example.demo.controller;

import com.example.demo.model.offer.*;
import com.example.demo.service.EmailOfferService;
import com.example.demo.service.OfferService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@CrossOrigin("*")

@RequestMapping("/offer")

public class OfferController {


    @Autowired
    private OfferService offerService;

    @Autowired
    private EmailOfferService emailOfferService;

    @PostMapping("/send-email")
    public String sendEmail(@RequestParam("id") String id) {
        try {

            return emailOfferService.sendTestEmail(Integer.valueOf(id));
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }

    @GetMapping("/userLogin")
    public UserLogin getUserLogin(@RequestParam("username") String username,
                                  @RequestParam("password") String password){
        return offerService.getUserLogin(username,password);
    }

    @PostMapping("/editStatus")
    public void editStatus(@RequestParam("status") String status,
                           @RequestParam("id") String id){
        offerService.editStatus(status, Integer.valueOf(id));
    }

    @GetMapping("/exportExcel")
    public ResponseEntity<byte[]> exportExcel(@RequestParam("from")  LocalDateTime from,
                                              @RequestParam("to")  LocalDateTime to) {
        try {
            byte[] excelBytes = offerService.getOfferToExcel(from, to);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "Offer_list.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format", ex);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred", ex);
        }
    }


    @GetMapping("/getInterviewNotes")
    public String getInterviewNote(@RequestParam("candidateName") String candidateName,
                                   @RequestParam("interviewName") String interviewName) {
        return offerService.getInterviewNote(candidateName,interviewName);
    }


    @PostMapping("/postSearch")
    public List<OfferGetEntity> searchPostRequest(@RequestBody String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);

        String name = "";
        String department = "";
        String status = "";
        JsonNode dataNode = rootNode.get("data");
        if (dataNode.isArray()) {
            name = dataNode.get(0).asText();
            department = dataNode.get(1).asText();
            status = dataNode.get(2).asText();
        }
        return  offerService.getBySearch(name,department,status);
    }


    @GetMapping("/getdefault")
    public List<List<String>> getDefault(){

        return offerService.getOfferListDefaultEntity();
    }



    @GetMapping("/view")
    public OfferGetViewDto getViewOffer(@RequestParam("id") String id){
        System.out.println(id);

        return offerService.getOfferView(Integer.valueOf(id));
    }


    @PostMapping("/create")
    public ResponseEntity<String> createOffer(@RequestParam("username") String username,
                                              @RequestBody List<String> inputValue,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (!username.equals(userDetails.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access");
            }
            OfferDto offerDto = new OfferDto();
            setOfferCreateDto(offerDto, inputValue);
            String result = offerService.createOffer(offerDto, username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating offer: " + e.getMessage());
        }
    }
    private void setOfferCreateDto(OfferDto offerDto, List<String> inputValueCreate) {
        offerDto.setCandidate(inputValueCreate.get(0));
        offerDto.setPosition(inputValueCreate.get(1));
        offerDto.setApprover(inputValueCreate.get(2));
        offerDto.setInterviewInfo(inputValueCreate.get(3));
        offerDto.setContractFrom(LocalDate.parse(inputValueCreate.get(4)));
        offerDto.setContractTo(LocalDate.parse(inputValueCreate.get(5)));
        offerDto.setInterviewNotes(inputValueCreate.get(6));
        offerDto.setContractType(inputValueCreate.get(7));
        offerDto.setLevel(inputValueCreate.get(8));
        offerDto.setDepartment(inputValueCreate.get(9));
        offerDto.setRecruiter(inputValueCreate.get(10));
        offerDto.setDueDate(LocalDate.parse(inputValueCreate.get(11)));
        offerDto.setSalaryBasic(inputValueCreate.get(12));
        offerDto.setNotes(inputValueCreate.get(13));
    }


    @PostMapping("/edit")
    public ResponseEntity<String> updateOffer(
            @RequestParam("username") String username,
            @RequestParam("id") Integer idEdit,
            @RequestBody List<String> inputValue,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (!username.equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access");
        }

        OfferDto offerDto = new OfferDto();
        setOfferCreateDto(offerDto, inputValue);

        String result = offerService.updateOfferById(idEdit, offerDto, username);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllOffers")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<OfferGetEntity> getAllOffers() {
        return offerService.findAllOffer();
    }

}