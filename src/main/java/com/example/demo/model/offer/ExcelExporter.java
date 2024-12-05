package com.example.demo.model.offer;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
public class ExcelExporter  {
    public static byte[] exportData(List<OfferListToExcel> toExcels) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Offer List");
            // Tạo header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"No.", "Candidate ID", "Candidate Name", "Approved By", "Contract Type",
                    "Position", "Level", "Department", "Recruiter Owner", "Interviewer",
                    "Contract Start From", "Contract To", "Basic Salary", "Interview Notes", "Notes"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            // Điền dữ liệu
            int rowNum = 1;
            for (OfferListToExcel toExcel : toExcels) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(toExcel.getNo());
                row.createCell(1).setCellValue(toExcel.getCandidateId());
                row.createCell(2).setCellValue(toExcel.getCandidateName());
                row.createCell(3).setCellValue(toExcel.getApprovedBy());
                row.createCell(4).setCellValue(toExcel.getContractType());
                row.createCell(5).setCellValue(toExcel.getPosition());
                row.createCell(6).setCellValue(toExcel.getLevel());
                row.createCell(7).setCellValue(toExcel.getDepartment());
                row.createCell(8).setCellValue(toExcel.getRecruiterOwner());
                row.createCell(9).setCellValue(toExcel.getInterviewer());
                row.createCell(10).setCellValue(String.valueOf(toExcel.getContractFrom()));
                row.createCell(11).setCellValue(String.valueOf(toExcel.getContractTo()));
                row.createCell(12).setCellValue(String.valueOf(toExcel.getSalary()));
                row.createCell(13).setCellValue(toExcel.getInterviewNote());
                row.createCell(14).setCellValue(toExcel.getNote());
            }
            workbook.write(out);
            return out.toByteArray();
        }
    }
}