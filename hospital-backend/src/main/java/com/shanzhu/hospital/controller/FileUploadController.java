package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.utils.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("upload")
public class FileUploadController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.base-url}")
    private String baseUrl;

    /**
     * 上传图片
     */
    @PostMapping("/image")
    public R uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return R.error("文件不能为空");
            }

            // 检查文件类型
            if (!FileUploadUtil.isImage(file)) {
                return R.error("只能上传图片文件");
            }

            // 检查文件大小（限制2MB）
            long fileSize = file.getSize();
            if (fileSize > 2 * 1024 * 1024) {
                return R.error("图片大小不能超过2MB");
            }

            // 上传文件
            String fileName = FileUploadUtil.uploadFile(file, uploadPath);
            String fileUrl = baseUrl + fileName;

            log.info("图片上传成功: {}", fileName);

            // 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("fileUrl", fileUrl);
            result.put("originalName", file.getOriginalFilename());
            result.put("fileSize", String.valueOf(fileSize));

            return R.ok("上传成功", result);

        } catch (IOException e) {
            log.error("图片上传失败", e);
            return R.error("上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("系统错误", e);
            return R.error("系统错误");
        }
    }

    /**
     * 删除图片
     */
    @DeleteMapping("/image")
    public R deleteImage(@RequestParam("fileName") String fileName) {
        try {
            String filePath = uploadPath + fileName;
            File file = new File(filePath);

            if (file.exists()) {
                if (file.delete()) {
                    log.info("删除图片成功: {}", fileName);
                    return R.ok("删除成功");
                } else {
                    return R.error("删除失败");
                }
            } else {
                return R.error("文件不存在");
            }
        } catch (Exception e) {
            log.error("删除图片失败", e);
            return R.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 上传富文本编辑器中的图片（用于资讯内容）
     */
    @PostMapping("/editor")
    public R uploadEditorImage(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return R.error("文件不能为空");
            }

            // 检查文件类型
            if (!FileUploadUtil.isImage(file)) {
                return R.error("只能上传图片文件");
            }

            // 检查文件大小（限制5MB）
            long fileSize = file.getSize();
            if (fileSize > 5 * 1024 * 1024) {
                return R.error("图片大小不能超过5MB");
            }

            // 上传文件
            String fileName = FileUploadUtil.uploadFile(file, uploadPath);
            String fileUrl = baseUrl + fileName;

            log.info("编辑器图片上传成功: {}", fileName);

            // 返回富文本编辑器要求的格式
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("alt", file.getOriginalFilename());
            result.put("href", fileUrl);

            return R.ok("上传成功", result);

        } catch (IOException e) {
            log.error("编辑器图片上传失败", e);
            return R.error("上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("系统错误", e);
            return R.error("系统错误");
        }
    }

    /**
     * 测试上传目录
     */
    @GetMapping("/test")
    public R testUpload() {
        File dir = new File(uploadPath);
        Map<String, Object> result = new HashMap<>();
        result.put("uploadPath", uploadPath);
        result.put("baseUrl", baseUrl);
        result.put("dirExists", dir.exists());
        result.put("dirPath", dir.getAbsolutePath());

        if (!dir.exists()) {
            if (dir.mkdirs()) {
                result.put("created", true);
            } else {
                result.put("created", false);
            }
        }

        return R.ok("测试成功", result);
    }
}