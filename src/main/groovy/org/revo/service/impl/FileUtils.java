package org.revo.service.impl;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by ashraf on 3/3/2016.
 */
public class FileUtils {

    static public String UploadFile(MultipartFile file) {
        InputStream is;
        FileOutputStream os;
        try {
            is = file.getInputStream();
            String name = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString().replaceAll("-", "") + "." + file.getOriginalFilename().split("\\.")[1];
            os = new FileOutputStream(name);
            int read;
            byte b[] = new byte[1024];
            while ((read = is.read(b)) != -1) {
                os.write(b, 0, read);
            }
            is.close();
            os.close();
            return name;
        } catch (IOException ex) {
            return null;
        }
    }

    static public ResponseEntity<InputStreamResource> DownloadFile(String name) throws IOException {
        ClassPathResource pdfFile = new ClassPathResource("files" + File.separator + name);
        MediaType type = MediaType.parseMediaType(new MimetypesFileTypeMap().getContentType(pdfFile.getFile()));
        return ResponseEntity
                .ok().header("Content-Disposition", "inline; filename=" + name)
                .contentLength(pdfFile.contentLength()).contentType(type)
                .body(new InputStreamResource(pdfFile.getInputStream()));
    }

}
