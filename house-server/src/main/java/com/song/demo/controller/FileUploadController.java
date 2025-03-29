package com.song.demo.controller;

import cn.hutool.core.net.URLDecoder;
import com.song.demo.common.BizException;
import com.song.demo.common.ResultData;
import com.song.demo.common.ReturnCode;
import com.song.demo.util.FileUploadUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/file")
public class FileUploadController {

    @Value("${file.upload.base-dir}")
    private String baseDir;

    @Value("${file.upload.access-path}")
    private String accessPath;

    private final FileUploadUtil fileUploadUtil;

    public FileUploadController(FileUploadUtil fileUploadUtil) {
        this.fileUploadUtil = fileUploadUtil;
    }

    @PostMapping("/upload")
    public List<String> handleFileUpload(@RequestParam("files") MultipartFile[] files) throws IOException {
        List<String> fileUrls = fileUploadUtil.uploadFiles(files);
        return fileUrls;
    }

    @GetMapping("/file/{*relativePath}")
    public ResponseEntity<ResultData<Resource>> getFile(@PathVariable String relativePath) {
        try {
            // 1. 路径解码和安全处理
            String decodedPath = URLDecoder.decode(relativePath, StandardCharsets.UTF_8);
            String accessPrefix = accessPath.replace("/**", "");

            // 2. 验证路径格式
            if (!decodedPath.startsWith(accessPrefix)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResultData.fail(ReturnCode.C400.getCode(), "路径格式错误"));
            }

            // 3. 提取实际文件名
            String fileName = decodedPath.substring(accessPrefix.length()).replaceFirst("^/", "");

            // 4. 构建安全路径
            Path filePath = Paths.get(baseDir, fileName).normalize();
            Path basePath = Paths.get(baseDir).toAbsolutePath().normalize();

            // 5. 路径安全检查
//            if (!filePath.startsWith(basePath)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResultData.fail(ReturnCode.C403.getCode(), "没有权限访问该文件"));
//            }

            // 6. 加载文件资源
            Resource resource = new UrlResource(filePath.toUri());

            // 7. 验证资源是否存在
            if (resource.exists() && resource.isReadable()) {
                // 8. 自动识别Content-Type
                String contentType = MediaTypeFactory.getMediaType(fileName)
                        .map(MediaType::toString)
                        .orElse("application/octet-stream");

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(ResultData.success(resource));
            } else {
//                return ResponseEntity.notFound().body(ResultData.fail(ReturnCode.C404.getCode(), "文件未找到"));
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResultData.fail(ReturnCode.C400.getCode(), "URL格式错误"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ResultData.fail(ReturnCode.C500.getCode(), "服务器内部错误"));
        }
        return null;
    }
}
