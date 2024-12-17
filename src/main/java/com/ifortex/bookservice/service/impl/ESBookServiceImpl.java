package com.ifortex.bookservice.service.impl;

import com.ifortex.bookservice.dto.SearchCriteria;
import com.ifortex.bookservice.model.Book;
import com.ifortex.bookservice.repository.impl.BookRepositoryImpl;
import com.ifortex.bookservice.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

// Attention! It is FORBIDDEN to make any changes in this file!
@Service
public class ESBookServiceImpl implements BookService {

    @Autowired
    private BookRepositoryImpl bookRepository;

    @Override
    public Map<String, Long> getBooks() {
        return bookRepository.getCountOFBooks();
    }

    @Override
    public List<Book> getAllByCriteria(SearchCriteria searchCriteria) {
        return bookRepository.findByCriteria(searchCriteria);
    }
}
