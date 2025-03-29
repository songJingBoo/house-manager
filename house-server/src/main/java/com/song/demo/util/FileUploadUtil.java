package com.song.demo.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Component
public class FileUploadUtil {

    @Value("${file.upload.base-dir}")
    private String baseDir;

    @Value("${file.upload.access-path}")
    private String accessPath;

    @PostConstruct
    public void init() {
        // 初始化存储目录
        File dir = new File(baseDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Failed to create upload directory");
            }
        }

        // 处理访问路径前缀
        accessPath = accessPath.replace("/**", "");
    }

    public List<String> uploadFiles(MultipartFile[] files) throws IOException {
        List<String> filePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                filePaths.add(uploadFile(file));
            }
        }
        return filePaths;
    }

    private String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileExt = getFileExtension(originalFilename);
        String storageName = generateFileName(fileExt);

        Path targetPath = Paths.get(baseDir, storageName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return accessPath + "/" + storageName;
    }

    private String generateFileName(String fileExt) {
        return UUID.randomUUID() + "." + fileExt;
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
