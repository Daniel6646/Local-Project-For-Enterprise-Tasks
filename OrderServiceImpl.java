package com.debuglab.serviceImpl;

import java.io.ByteArrayInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.query.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.emitter.EmitterException;

import com.debuglab.Exception.FileStorageException;
import com.debuglab.Exception.ResourceNotFoundException;
import com.debuglab.controller.OrderController;
import com.debuglab.dto.OrderDTO;
import com.debuglab.entity.OrderEntity;
import com.debuglab.mapper.OrderMapper;
import com.debuglab.repository.OrderRepository;
import com.debuglab.service.OrderService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

   @Autowired
   public OrderRepository repo;
   
   @Autowired
   public OrderMapper mapper;
   
   @Autowired 
   	private JavaMailSender javaMailSender;
   
// swagger opening link
// http://localhost:8080/swagger-ui.html
//  access h2 database db below link
//  http://localhost:8080/h2-console
   
   private static final Logger logger =
	        LoggerFactory.getLogger(OrderController.class);
	
    public OrderDTO create(OrderDTO dto) throws Exception {

    	if (dto == null) {
    		
    		throw new Exception("Order dto sent from request is null");
    	}
    	
    	
        OrderEntity e = mapper.toEntity(dto);

        e.setStatus("NEW");
        e.setCreatedAt(LocalDateTime.now());

        // BUG — wrong amount transform
        e.setAmount(dto.getAmount() * 100);

        return mapper.toDto(repo.save(e));
    }

    public OrderDTO get(Long id) {

        OrderEntity e = repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return mapper.toDto(e);
    }

    public org.springframework.data.domain.Page<OrderDTO> list(String status, int page, int size) {

        // BUG — size zero crash
        Pageable p = PageRequest.of(page, size);

        return repo.findByStatus(status, p)
                   .map(mapper::toDto);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public String uploadFile(Long id, MultipartFile file) {

        OrderEntity e = repo.findById(id).orElseThrow();

        String path = "uploads/" + file.getOriginalFilename();

        try {
            file.transferTo(new File(path));
        } catch (Exception ex) {
            throw new FileStorageException("Upload failed");
        }

        e.setFilePath(path);

        return path;
    }

    public byte[] downloadFile(Long id) {

        OrderEntity e = repo.findById(id).orElseThrow();

        try {
            return Files.readAllBytes(Path.of(e.getFilePath()));
        } catch (Exception ex) {
            throw new FileStorageException("Download failed");
        }
    }

    public List<OrderDTO> getAllSorted(String field) {
    	
        return repo.findAll(Sort.by(field))
                   .stream()
                   .map(mapper::toDto)
                   .toList();
    }

	@Override
	public List<OrderDTO> getAll() throws Exception {

		// debugging practice start
    	List<String> strList =  stringlistForDebug();
    	System.out.println("strList :" +strList);
    	logger.debug("inside getAl method strList::"+ strList);
    	
    	List<Integer> intList =  intlistForDebug();
    	System.out.println("intList :" +intList);
    	logger.debug("inside getAl method intList::"+ intList);


    	List<Long> longList = longListForDebug();
    	System.out.println("longList :" +longList);
    	logger.debug("inside getAl method longList::"+ longList);

    	
    	Map<String, Integer> returnMap = mapForDebug();
    	logger.debug("inside getAl method returnMap::"+ returnMap);
    	System.out.println("returnMap:" +returnMap);
    	
		// debugging practice ends here

    	OrderEntity ent = repo.findById(1L).get();/// for debgguging and see how it looks on hower also below line
	    if( ent == null) {
	    	throw new Exception("Daniel new exception no data in object from repo demo msg");
	    }
    	
    	
    	List<OrderEntity> entities = repo.findAll();
logger.debug("logger daniel repo.findall:: "+entities);
	    List<OrderDTO> dtoList = new ArrayList<>();

	    for (OrderEntity e : entities) {
	        //OrderDTO dto = mapper.toDto(e);
	        OrderDTO dto = new OrderDTO();
	        dto.setCustomerName(e.getCustomerName());
	        dto.setAmount(e.getAmount());
	        dto.setCreatedAt(e.getCreatedAt());
	        dto.setFilePath(e.getFilePath());
	        dto.setStatus(e.getStatus());
	        dtoList.add(dto);
	    }

	    return dtoList;
	}   
	
//	 public ByteArrayInputStream exportDownloadOrders() {
//
//	        List<OrderEntity> orders = repo.findAll();
//
//	        StringBuilder sb = new StringBuilder();
//
//	        // header row
//	        sb.append("ID,CustomerName,Amount,Status,FilePath,CreatedAt\n");
//
//	        for (OrderEntity o : orders) {
//	            sb.append(o.getId()).append(",")
//	              .append(o.getCustomerName()).append(",")
//	              .append(o.getAmount()).append(",")
//	              .append(o.getStatus()).append(",")
//	              .append(o.getFilePath()).append(",")
//	              .append(o.getCreatedAt()).append("\n");
//	        }
//
//	        return new ByteArrayInputStream(sb.toString().getBytes());
//	    }
//
//	 
//	 
	 
	 public ByteArrayInputStream exportDownloadToExcel() throws IOException {

	        List<OrderEntity> orders = repo.findAll();
		 	//List<OrderDTO> orders = getAll();						
	    	List<OrderEntity> entities = repo.findAll();

		    List<OrderDTO> dtoList = new ArrayList<>();

		    for (OrderEntity e : orders) {
		        OrderDTO dto = mapper.toDto(e);
		        dtoList.add(dto);
		    }
	        
	        Workbook workbook = new XSSFWorkbook();
	        Sheet sheet = workbook.createSheet("Orders");

	        // Header row
	        Row header = sheet.createRow(0);
	        header.createCell(0).setCellValue("ID");
	        header.createCell(1).setCellValue("Customer Name");
	        header.createCell(2).setCellValue("Amount");
	        header.createCell(3).setCellValue("Status");
	        header.createCell(4).setCellValue("File Path");
	        header.createCell(5).setCellValue("Created At");

	        // Data rows
	        int rowIdx = 1;
	        for (OrderEntity o : orders) {
	            Row row = sheet.createRow(rowIdx++);

	            row.createCell(0).setCellValue(o.getId());
	            row.createCell(1).setCellValue(o.getCustomerName());
	            row.createCell(2).setCellValue(o.getAmount());
	            row.createCell(3).setCellValue(o.getStatus());
	            row.createCell(4).setCellValue(o.getFilePath());
	            row.createCell(5).setCellValue(
	                o.getCreatedAt() != null ? o.getCreatedAt().toString() : ""
	            );
	        }

	        // Auto size columns
	        for (int i = 0; i < 6; i++) {
	            sheet.autoSizeColumn(i);
	        }

	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        workbook.write(out);
	        workbook.close();

	        return new ByteArrayInputStream(out.toByteArray());
	    }

	 public int uploadExcelAndSaveExcelData(MultipartFile file) throws Exception {

		    List<OrderEntity> list = new ArrayList<>();

		    if (file.isEmpty()) {
		        throw new IllegalArgumentException("File is empty");
		    }

		    if (!file.getOriginalFilename().endsWith(".xlsx")) {
		        throw new IllegalArgumentException("Only .xlsx files are allowed");
		    }

		    Workbook workbook = new XSSFWorkbook(file.getInputStream());
		    Sheet sheet = workbook.getSheetAt(0);

		    for (int i = 1; i <= sheet.getLastRowNum(); i++) { // start from 1 to skip header
		        Row row = sheet.getRow(i);
		        if (row == null) continue; // skip empty rows

		        OrderEntity o = new OrderEntity();

		        // Safely read cells
		        o.setCustomerName(getCellAsString(row.getCell(0)));
		        o.setAmount(getCellAsDouble(row.getCell(1)));
		        o.setStatus(getCellAsString(row.getCell(2)));
		        o.setFilePath(getCellAsString(row.getCell(3)));

		        // Handle date cell safely
		        String dtStr = getCellAsString(row.getCell(4));
		        if (!dtStr.isEmpty()) {
		            try {
		                o.setCreatedAt(LocalDateTime.parse(dtStr));
		            } catch (Exception e) {
		                // If parsing fails, fallback to now
		                o.setCreatedAt(LocalDateTime.now());
		            }
		        } else {
		            o.setCreatedAt(LocalDateTime.now());
		        }

		        list.add(o);
		    }

		    workbook.close();

		    repo.saveAll(list); // save all at once
		    return list.size();
		}

		// Helper: Convert any cell to String safely
		private String getCellAsString(Cell cell) {
		    if (cell == null) return "";

		    switch (cell.getCellType()) {
		        case STRING:
		            return cell.getStringCellValue();
		        case BOOLEAN:
		            return String.valueOf(cell.getBooleanCellValue());
		        case NUMERIC:
		            if (DateUtil.isCellDateFormatted(cell)) {
		                return cell.getLocalDateTimeCellValue().toString();
		            } else {
		                double val = cell.getNumericCellValue();
		                // Convert to integer string if whole number
		                if (val == Math.floor(val)) return String.valueOf((long) val);
		                else return String.valueOf(val);
		            }
		        case FORMULA:
		            return cell.getCellFormula();
		        case BLANK:
		        case _NONE:
		        case ERROR:
		        default:
		            return "";
		    }
		}

		// Helper: Convert any cell to Double safely
		private Double getCellAsDouble(Cell cell) {
		    if (cell == null) return 0.0;

		    switch (cell.getCellType()) {
		        case NUMERIC:
		            return cell.getNumericCellValue();
		        case STRING:
		            try {
		                return Double.parseDouble(cell.getStringCellValue());
		            } catch (NumberFormatException e) {
		                return 0.0;
		            }
		        case BOOLEAN:
		            return cell.getBooleanCellValue() ? 1.0 : 0.0;
		        default:
		            return 0.0;
		    }
		}
		
		public List<String> stringlistForDebug() {
			
			List<String> stringList =new ArrayList<>();
			stringList.add("daniel1");
			stringList.add("daniel2");
			stringList.add("daniel3");
			stringList.add("daniel4");
			stringList.add("daniel55");
			stringList.add("daniel6");
			stringList.add("daniel7");
			stringList.add("daniel8");
			stringList.add("daniel9");
	
			return stringList; 
		}
			
		public List<Integer> intlistForDebug() {
			
			List<Integer> intList =new ArrayList<>();
			intList.add(1);
			intList.add(2);
			intList.add(3);
			intList.add(4);
			intList.add(5);
			intList.add(6); 
			
			return intList;
	
		}
			
		public List<Long> longListForDebug() { 
			
			List<Long> longList =new ArrayList<>();
			longList.add(1l);
			longList.add(2l);
			longList.add(3l);
			longList.add(4l);
			longList.add(5l);
			longList.add(6l);
	
			return longList;		
		}
				
	public Set<Integer> setForDebug( ) {   
	
		Set<Integer> intSet = new HashSet<Integer>();
		intSet.add(1);
		intSet.add(2);
		intSet.add(3);
		intSet.add(4);
		intSet.add(5);
		intSet.add(6);
		intSet.add(7);

		return intSet;
		
	} 	
	
	public Map<String, Integer> mapForDebug() {
		
		
		Map<String, Integer> map = new HashMap<>();
		map.put("Daniel1", 1);
		map.put("Daniel2", 2);
		map.put("Daniel3", 3);
		map.put("Daniel4", 4);
		map.put("Daniel5", 5);
		map.put("Daniel6", 6);
		map.put("Daniel7", 7);
		map.put("Daniel8", 8);
		
		
		return map;	
	}

	 public void sendEmail(String to, String subject, String text) {

		 try {  
		
			 String emailSentFrom = "daniel.quadras401@gmail.com";
		        SimpleMailMessage message = new SimpleMailMessage();

		        message.setTo(to);
		        message.setSubject(subject);
		        message.setText(text);
		        message.setFrom(emailSentFrom);

//		        logger.debug("logger.debug to"+ to );
//		        logger.debug("logger.debug subject"+ subject );
//		        logger.debug("logger.debug text"+ text);
//		        logger.debug("logger.debug emailSentFrom"+ emailSentFrom);
		       
		        System.out.println("logger.debug to::  "+ to);
		        System.out.println("logger.debug subject:: "+ subject );
		        System.out.println("logger.debug text:: "+ text);
		        System.out.println("logger.debug emailSentFrom:: "+ emailSentFrom);

		        javaMailSender.send(message);

			 
		 }
		 catch (Exception e) {
			 System.out.println("eception inside sendmail::: "+e );
		 
		 }	
		}

	@Override
	public void writeDataToFile(String content) throws IOException {

		try { 
			Files.write(Paths.get("data.txt"), content.getBytes());
			//String content = "Hello this is a file write example";
		} 
         
		catch (Exception e) {
		System.out.println("Exception is:: "+e); 
		
		}
	}

	@Override
	public String writestaticDataIntoFile() {

		String content = "Daniel content which is to be written into an file";
		try {
			Files.write(Paths.get("data.txt"), content.getBytes());
		}
		
		catch (IOException e) {
			System.out.println("Error is:: "+e);		
			e.printStackTrace();
		}

		return "Daniel Data written into file sucessfully";
	}
	
	
	
	
	
	

	}

