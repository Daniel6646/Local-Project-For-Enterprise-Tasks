package com.debuglab.service;

import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.debuglab.dto.OrderDTO;

public interface OrderService {

    OrderDTO create (OrderDTO dto) throws Exception;

    OrderDTO get(Long id);

    Page<OrderDTO> list(String status, int page, int size);

    void delete(Long id);

    String uploadFile(Long id, MultipartFile file);

    byte[] downloadFile(Long id);

	 List<OrderDTO> getAll() throws Exception;
	 
//	 public ByteArrayInputStream exportDownloadOrders();
	 
	 public ByteArrayInputStream exportDownloadToExcel() throws IOException;
	 
	 public int uploadExcelAndSaveExcelData(MultipartFile file) throws Exception ;

	void sendEmail(String to, String subject, String body);

	void writeDataToFile(String content) throws IOException;

	String writestaticDataIntoFile();
}

