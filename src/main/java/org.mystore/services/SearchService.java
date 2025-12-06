package org.mystore.services;

import org.mystore.dtos.SortParam;
import org.mystore.dtos.SortType;
import org.mystore.models.Product;
import org.mystore.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {
    @Autowired
    ProductRepository productRepository;

    public Page<Product> searchProducts(String query, Integer pageNumber, Integer pageSize, List<SortParam> sortParams) {

//        Sort sort=Sort.by("price").ascending().and(Sort.by("id").descending());

        Sort sort=null;
        if(!sortParams.isEmpty()){
           if(sortParams.get(0).getSortType().equals(SortType.ASC))
               sort=Sort.by(sortParams.get(0).getSortCriteria());
           else
               sort=Sort.by(sortParams.get(0).getSortCriteria()).descending();

        }

        for (int i = 1; i <sortParams.size() ; i++) {
            if(sortParams.get(i).getSortType().equals(SortType.ASC))
                sort=Sort.by(sortParams.get(i).getSortCriteria());
            else
                sort=Sort.by(sortParams.get(i).getSortCriteria()).descending();

        }


        return productRepository.findByNameEquals(query, PageRequest.of(pageNumber, pageSize,sort));
    }
}
