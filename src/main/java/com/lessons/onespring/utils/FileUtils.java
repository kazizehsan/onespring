package com.lessons.onespring.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtils {
    private static final Map<String, String> extensionContentTypeMap;

    static {
        extensionContentTypeMap = new HashMap<>();
        extensionContentTypeMap.put("jpg", "image/jpeg");
        extensionContentTypeMap.put("jpeg", "image/jpeg");
        extensionContentTypeMap.put("png", "image/png");
    }

    public static String getContentTypeFromExtension(String extension) {
        String key = "";
        for (Map.Entry<String, String> entry : extensionContentTypeMap.entrySet()) {
            if ((entry.getKey().equalsIgnoreCase(extension))) {
                key = entry.getValue();
            }
        }
        return key;
    }

    public static boolean isValidExtension(MultipartFile file, List<String> validExtensions) {
        if (file == null) return false;
        String fileName = file.getOriginalFilename();
        return FileUtils.isValidExtension(fileName, validExtensions);
    }

    public static boolean isValidExtension(String fileName, List<String> validExtensions) {
        return FilenameUtils.isExtension(fileName.toLowerCase(), validExtensions);
    }

    public static boolean isValidContentTypeByExtension(MultipartFile file, List<String> validExtensions) {
        if (file == null) return false;
        List<String> contentTypes = new ArrayList<>();
        for (String validExtension : validExtensions) {
            String contentType = getContentTypeFromExtension(validExtension);
            contentTypes.add(contentType);
        }
        return isValidContentType(file, contentTypes);
    }

    public static boolean isValidContentType(MultipartFile file, List<String> validContentTypes) {
        if (file == null) return false;
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        for (String validContentType : validContentTypes) {
            if (contentType.equalsIgnoreCase(validContentType)) {
                return true;
            }
        }
        return false;
    }
}
