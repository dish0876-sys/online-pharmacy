package com.pharmacy.onlinepharmacy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    // Берем путь из файла application.properties/yaml
    @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * Метод для сохранения файла на диск.
     * @param file Файл, который пришел от клиента
     * @return Строка с путем к сохраненному файлу
     */
    public String saveFile(MultipartFile file) {
        try {
            // 1. Получаем путь к папке (uploads/prescriptions/)
            Path root = Paths.get(uploadDir);

            // 2. Если папки еще нет, создаем её
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            // 3. Генерируем уникальное имя файла.
            // UUID нужен, чтобы файлы "рецепт.jpg" от разных людей не перезаписывали друг друга
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // 4. Копируем содержимое файла в нужную папку
            Files.copy(file.getInputStream(), root.resolve(fileName));

            // 5. Возвращаем полный путь, чтобы сохранить его в БД
            return uploadDir + fileName;

        } catch (IOException e) {
            // Если произошла ошибка, выбрасываем исключение
            throw new RuntimeException("Ошибка при сохранении файла: " + e.getMessage(), e);
        }
    }
}