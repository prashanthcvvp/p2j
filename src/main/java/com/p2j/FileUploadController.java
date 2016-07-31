package com.p2j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {

	@Autowired
	private PdftoJpg pdfBox;
	
	
	
	@RequestMapping(value = "/convert", method = RequestMethod.GET)
	public @ResponseBody String uploadInfo() {
		return "Website Under Construction for P2J test";
	}

	
	@RequestMapping(value = "/PDFServlet", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> handleFileUpload(@RequestParam("name") String name,
																			  @RequestParam("file") MultipartFile file) {
		return pdfBox.process(file);
	}
}
