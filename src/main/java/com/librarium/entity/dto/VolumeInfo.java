package com.librarium.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VolumeInfo {
    private String title;
    private List<String> authors;
    private List<String> categories;
    private String publishedDate;
    private List<Map<String, String>> industryIdentifiers;
    private String description;
}
