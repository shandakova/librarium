package com.librarium.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Book {
    private VolumeInfo volumeInfo;

    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Название: ").append(volumeInfo.getTitle()).append("\n");
        sb.append("Автор: ");
        if (volumeInfo.getAuthors() != null) {
            for (String a : volumeInfo.getAuthors()) {
                sb.append(a).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
        }

        sb.append("\nЖанр: ");
        if (volumeInfo.getCategories() != null) {
            sb.append(volumeInfo.getCategories().get(0)).append("\n");
        }
        sb.append("Год: ");
        if (volumeInfo.getPublishedDate() != null) {
            sb.append(volumeInfo.getPublishedDate(), 0, 4).append("\n");
        }
        sb.append("ISBN: ");
        if (volumeInfo.getIndustryIdentifiers() != null && volumeInfo.getIndustryIdentifiers().get(0).get("type").startsWith("ISBN")) {
            sb.append(volumeInfo.getIndustryIdentifiers().get(0).get("identifier")).append("\n");
        }
        sb.append("Аннотация: \n");
        if (volumeInfo.getDescription() != null) {
            String[] arr = volumeInfo.getDescription().split(" ");
            StringBuilder sb1 = new StringBuilder();
            for (String s : arr) {
                sb1.append(s).append(" ");
                if (sb1.length() > 120) {
                    sb.append(sb1.toString()).append("\n");
                    sb1.delete(0, sb1.length());
                }
            }
            sb.append(sb1.toString()).append("\n");
        }
        return sb.toString();
    }
}
