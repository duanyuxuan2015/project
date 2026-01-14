package com.example.ecommerce.member.controller;

import com.example.ecommerce.member.annotation.OperationLog;
import com.example.ecommerce.member.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    /**
     * 上传头像
     *
     * @param file 文件
     * @return 文件URL
     */
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @OperationLog(
            module = "文件",
            operationType = "UPLOAD",
            description = "上传头像"
    )
    public ApiResponse<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        // 1. 验证文件是否为空
        if (file.isEmpty()) {
            return ApiResponse.error("4000", "文件不能为空");
        }

        // 2. 验证文件大小(≤2MB)
        long maxSize = 2 * 1024 * 1024; // 2MB
        if (file.getSize() > maxSize) {
            return ApiResponse.error("4001", "文件大小不能超过2MB");
        }

        // 3. 验证文件类型(JPG/PNG)
        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            return ApiResponse.error("4002", "只支持JPG和PNG格式的图片");
        }

        try {
            // 4. 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 5. 创建目录: uploads/avatars/2026/01/14/
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fullUploadPath = uploadPath + "/avatars/" + datePath;
            Path path = Paths.get(fullUploadPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            // 6. 保存文件
            String filePath = fullUploadPath + "/" + filename;
            File destFile = new File(filePath);
            file.transferTo(destFile);

            // 7. 返回文件URL
            String fileUrl = "/uploads/avatars/" + datePath + "/" + filename;

            Map<String, String> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("filename", filename);

            log.info("头像上传成功: filename={}, size={}", filename, file.getSize());

            return ApiResponse.success(data);

        } catch (IOException e) {
            log.error("头像上传失败", e);
            return ApiResponse.error("9003", "文件上传失败");
        }
    }
}
