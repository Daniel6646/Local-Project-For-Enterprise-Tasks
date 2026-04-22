package com.debuglab.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.debuglab.dto.OrderDTO;
import com.debuglab.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
//@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

	@Autowired 
    public OrderService service;

	
	// swagger opening link
	// http://localhost:8080/swagger-ui.html
	 //  access h2 database db below link
	 //  http://localhost:8080/h2-console
	
    @PostMapping("/saveOrders")
    public OrderDTO create(@RequestBody @Valid OrderDTO dto) throws Exception {
        return service.create(dto);
    }

    @GetMapping("/getOrderById")
    public OrderDTO get(@RequestParam Long id) {
        return service.get(id);
    }

    @GetMapping("/orderPagination")
    public org.springframework.data.domain.Page<OrderDTO> list(
        @RequestParam String status,
        @RequestParam int page,
        @RequestParam int size) {
        System.out.println("Controller HIT → " + status + " " + page + " " + size);

        return service.list(status, page, size);
    }

    @DeleteMapping("/id")
    public void delete(@RequestParam Long id) {
        service.delete(id);
    }

    @PostMapping("/id/upload")
    public String upload(
        @RequestParam Long id,
        @RequestParam MultipartFile file) {

        return service.uploadFile(id, file);
    }

    @GetMapping("/id/download")
    public ResponseEntity<byte[]> download(@RequestParam Long id) {

        return ResponseEntity.ok(service.downloadFile(id));
    }
    
    @GetMapping("/getAllOrders")
    public List<OrderDTO> getAllOrders() throws Exception {
        return service.getAll();
    }
    
//    @GetMapping("/exportFileDownload")
//    public ResponseEntity<InputStreamResource> downloadOrders() throws IOException {
//
//        ByteArrayInputStream file = service.exportDownloadOrders();
//
//        return ResponseEntity.ok()
//                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=orders.csv")
//                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
//                .body(new InputStreamResource(file));
//    }
//

    @GetMapping("/export-excelFileDownload")
    public ResponseEntity<InputStreamResource> downloadExcel() throws Exception {

        ByteArrayInputStream file = service.exportDownloadToExcel();

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=orders.xlsx")
                .contentType(
                    org.springframework.http.MediaType.parseMediaType(
                      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(file));
    }

    
	@PostMapping("/excelBulk-upload")
	public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file) throws Exception {

		try {
			
			System.out.println("INSIDE EXCELBULK UPLOAD CONTROLLER");
			int count = service.uploadExcelAndSaveExcelData(file);

			return ResponseEntity.ok("Uploaded records from excel ile: " + count);

		} catch (Exception e) {

			e.printStackTrace(); 
			System.out.println("EXCEPTIONNN::  "+e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error uploading file: " + e.getMessage());
		}

	} 
	
    @PostMapping("/sendEmail")
    public String sendEmail(  @RequestParam String to, @RequestParam String subject, @RequestParam String body) {

    	System.out.println("inside sendEmail line 132");
    	service.sendEmail(to, subject, body);
    	System.out.println("inside sendEmail line 134");

        return "Email sent successfully";
    }
    @PostMapping("/writedynamicContentIntoFile")
    public String writedynamicContentIntoFile(@RequestParam String content) {
        try {

        
       	service.writeDataToFile(content);
           
        } 
        catch (Exception e) {
            return "Error writing file";
        }
        
        return"Data successfully written into text file by daniel";
    }
 
    @PostMapping("/writestaticDataIntoFile")
    public String writestaticDataIntoFile() {

    	String result = "";
    	try {

        
        result = service.writestaticDataIntoFile();
           
        } 
        catch (Exception e) {
            return "Error writing file";
        }
        
        return result;
    }
 
    
    @GetMapping("/readFileContents")
    public String readFile() throws IOException {

        String content = new String(Files.readAllBytes(Paths.get("logs.txt")));

        return content;
    }

    @PostMapping("/uploadFileAndSaveFileContentInAnotherFile")
    public String uploadFileAndSaveFileContentInAnotherFile(@RequestParam("file") MultipartFile file) throws IOException {

    	//send or upload file should read contents and save into another file
    	
        String path = "uploads/" + file.getOriginalFilename();

        Files.copy(file.getInputStream(), Paths.get(path));

        return "File uploaded successfully";
    }

    @GetMapping("/writeDataFromListToExcelCsvFile")
    public String writeDataFromListToExcelCsvFile() throws IOException {

        List<String> data = Arrays.asList(
                "Id,Name,Salary",
                "1,John,50000",
                "2,Mary,60000"
        );

        Files.write(Paths.get("report.csv"), data);

        return "CSV file created";
    }

    @GetMapping("/readExcelCsvFileContents")
    public List<String> readExcelCsvFileContents(@RequestParam("path") String path) throws IOException {

    	
    	//body then form=data
    	//key = path same as req param
    	//value = C:\Users\Admin\Desktop\Code tools\SpringBootDebugProject File or excel creation through code/EmployeeCsvReport.csv
    	System.out.println("Inside readExcelCsvFileContents() ");
        return Files.readAllLines(Paths.get(path));
    }

    
    @PostMapping("/readExcelCsvFileContentsAfterUploadingAFile")
    public List<String> readExcelCsvFileContentsAfterUploadingAFile(@RequestParam("file") MultipartFile file) throws IOException {
    	System.out.println("inside readExcelCsvFileContentsAfterUploadingAFile()");
        List<String> lines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()));

        String line;

        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        return lines;
    }
    
    
    @PostMapping("/createFileAndWriteDbDataIntoIt")
    public String createAndWriteFile() {

        try {
        	System.out.println("INSIDE createAndWriteFile()*********");	
            // Example data (could come from DB)
            String data = "Id,Name,Salary\n"
                        + "1,John,50000\n"
                        + "2,Mary,60000\n"
                        + "3,Danny,10000\n"
                        + "4,Ryomen Sukuna,60000\n"
                        + "5,Itadori Yuji,50000\n"
                        + "6,Mahito,60000\n"
                        + "7,Alexander,50000\n"
                        + "8,Gojo Satoru,60000\n";

            // File location
            String folderPath = "C:\\Users\\Admin\\Desktop\\Code tools\\SpringBootDebugProject File or excel creation through code\\";
            String fileName = "employee_report.txt";

            Path filePath = Paths.get(folderPath + fileName);

            // Create directories if not exist
            Files.createDirectories(filePath.getParent());

            // Create file and write data
            Files.write(
                    filePath,
                    data.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            return "File created and data written successfully";

        }
        catch (IOException e) {
            return "Error creating file";
        }
    }

    
    
}