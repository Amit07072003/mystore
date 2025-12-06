package org.mystore.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchRequestDto {
    private String query;
    private Integer pageNumber;
    private Integer pageSize;
List<SortParam> sortParams=new ArrayList<>();

}
