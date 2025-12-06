package org.mystore.dtos;

import lombok.Data;

@Data
public class SortParam {
    private  String  sortCriteria;
    private SortType sortType;
}
