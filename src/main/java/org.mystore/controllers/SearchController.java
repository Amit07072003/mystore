package org.mystore.controllers;

import org.mystore.dtos.SearchRequestDto;
import org.mystore.models.Product;
import org.mystore.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/search")
@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping
    public Page<Product> searchProducts(@RequestBody SearchRequestDto searchRequestDto) {

        return searchService.searchProducts(searchRequestDto.getQuery(),searchRequestDto.getPageNumber(),searchRequestDto.getPageSize(),searchRequestDto.getSortParams());


    }
}
