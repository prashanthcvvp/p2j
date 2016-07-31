package com.p2j;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptException;
import org.ghost4j.display.PageRaster;
import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.RendererException;
import org.ghost4j.renderer.SimpleRenderer;
import org.ghost4j.util.ImageUtil;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PdftoJpg {

	private static String alphabets = "abcdefghijklmnopqrstuvwxyz";
//	String base_dir= "/home/p2j-test";
	String base_dir= "/home/prashanth/Desktop/Sample";

	/****************************************************************************************************************************/

	public String getRandomName() {
		Random rand = new Random();
		return String.valueOf(alphabets.charAt(
										       rand.nextInt(alphabets.length())))
										     + String.valueOf(alphabets.charAt(rand.nextInt(alphabets.length())))
										     + String.valueOf(alphabets.charAt(rand.nextInt(alphabets.length())));

	}
	/****************************************************************************************************************************/
	public ResponseEntity<InputStreamResource> process(MultipartFile file){
		
		String name = getRandomName();
		saveFile(base_dir + "/" + name+ ".pdf", name, file);
		
		convertPDFToJPGGhost(base_dir + "/" + name + ".pdf");
		
		Zipfolder(base_dir + "/" + name + ".zip");
		
		return readZipfolder(base_dir + "/" + name + ".zip");
	}
	/****************************************************************************************************************************/
	public void saveFile(String path, String name, MultipartFile file){
		
		try {
			File pdf_file=new File(path);
			file.transferTo(pdf_file);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/****************************************************************************************************************************/
	public void convertPDFToJPGGhost(String src) {
		System.out.println(src);
		File pdfFile = new File(src);
		try {
			String jpg_path=src.split(".pdf")[0]; 
			
			
			File img_dir = new File(jpg_path);
			if (!img_dir.exists()) {
				img_dir.mkdirs();
			}
			
			Ghostscript gs = Ghostscript.getInstance();
			
			synchronized (gs) {
				
			
				PDFDocument document = new PDFDocument();
				
				
				document.load(pdfFile);
				SimpleRenderer renderer = new SimpleRenderer();
				renderer.setMaxProcessCount(1);
				
				System.out.println(renderer.getProcessCount());
				
				// set resolution (in DPI)
				renderer.setResolution(300);
				List<PageRaster> images;
	
				int pageCount = document.getPageCount();
				
				List<Image> imagesRender=null;

				int pageInterval = 5;
				if (pageCount > pageInterval) {
					for (int page = 0; page < pageCount; page = (page + pageInterval + 1)) {
						images = renderer.remoteRender(document, page, page + pageInterval);
						
						imagesRender = ImageUtil.convertPageRastersToImages(images);
						
						System.out.println("Processed till Page Number "+ page);
//						for (int i = 0; i < imagesRender.size(); i++) {
//							ImageIO.write((RenderedImage) imagesRender.get(i), "jpg",new File(jpg_path +"/"+ page + "" + (i + 1)+ ".jpg"));
//						}
						images.clear();
						System.gc();
					}
				} else {
	
					images = renderer.run(document, 0, pageCount);
					
					imagesRender = ImageUtil.convertPageRastersToImages(images);
					for (int i = 0; i < imagesRender.size(); i++) {
						
						ImageIO.write((RenderedImage) imagesRender.get(i), "jpg",new File(jpg_path +"/" + (i + 1) + ".jpg"));
					}
				}
				gs.exit();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RendererException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (GhostscriptException e) {
			e.printStackTrace();
		}
		pdfFile.delete();
	}

	/****************************************************************************************************************************/
	public void Zipfolder(String path) {
		try {
			String dir_path = path.split(".zip")[0];
			System.out.println("Dir path " + dir_path);
			
			FileOutputStream f_out = new FileOutputStream(new File(path));
			ZipOutputStream zip_out = new ZipOutputStream(f_out);
			
			File dir = new File(dir_path);
			File[] file_list = dir.listFiles();
			
			for (File file : file_list) {
				FileInputStream each_file_is = new FileInputStream(file);
				byte[] buffer = new byte[(int) file.length()];

				zip_out.putNextEntry(new ZipEntry(file.getName()));
				each_file_is.read(buffer, 0, buffer.length);
				
				zip_out.write(buffer, 0, buffer.length);
				each_file_is.close();
				file.delete();
			}
			zip_out.flush();
			zip_out.close();
			
			dir.delete();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	
	/**
	 * @return **************************************************************************************************************************/
	public ResponseEntity<InputStreamResource> readZipfolder(String path) {
		ResponseEntity<InputStreamResource> responseEntity=null;
		File zipFile = new File(path);
		try {
			
			
			HttpHeaders respHeaders = new HttpHeaders();
		    respHeaders.setContentLength(zipFile.length());
		    respHeaders.setContentDispositionFormData("attachment", "fileNameIwant.pdf");
			
		    InputStreamResource isr = new InputStreamResource(new FileInputStream(zipFile));
			
		    responseEntity  = new ResponseEntity<InputStreamResource>(isr, 
		    														  respHeaders, 
		    														  HttpStatus.OK);
		    
		} catch (FileNotFoundException e) {
			System.out.println("Could not read zip file " + e);
		}
		zipFile.delete();
		return responseEntity;
	}
	
	/************************************************************************************************************/
	public void getMemoryStatus(){
		Runtime runTime = Runtime.getRuntime();
		
		System.out.println("Total Memory Allocated "+ (runTime.totalMemory() /1024));
		System.out.println("max Memory Allocated "+ (runTime.maxMemory() /1024));
		System.out.println("Total Memory Allocated "+ (runTime.freeMemory() /1024));
	}
	
	
}
