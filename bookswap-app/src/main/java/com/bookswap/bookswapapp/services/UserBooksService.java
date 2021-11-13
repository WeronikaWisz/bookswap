package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.dtos.userbooks.BookListItem;
import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.bookswap.bookswapapp.models.Book;
import com.bookswap.bookswapapp.models.Category;
import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.repositories.BookRepository;
import com.bookswap.bookswapapp.repositories.CategoryRepository;
import com.bookswap.bookswapapp.repositories.UserRepository;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class UserBooksService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserBooksService.class);

    @Autowired
    public UserBooksService(BookRepository bookRepository, CategoryRepository categoryRepository,
                            UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addBook(MultipartFile image, Book book, List<String> categories, String label) throws IOException {
        book.setImage(compressBytes(image.getBytes()));
        addBookInfo(book, categories, label);
    }

    @Transactional
    public void addBook(Book book, List<String> categories, String label){
        addBookInfo(book, categories, label);
    }

    private void addBookInfo(Book book, List<String> categories, String label){
        book.setCreationDate(LocalDateTime.now());
        book.setLabel(EBookLabel.valueOf(label));
        book.setStatus(EBookStatus.AVAILABLE);
        User user = getCurrentUser();
        book.setUser(user);
        bookRepository.save(book);
        for(String name: categories){
            Category category = getCategory(name);
            book.addCategory(category);
        }
    }

    public List<Category> getCategories(){
        return categoryRepository.findAll();
    }

    public List<BookListItem> loadBooks(EBookStatus status){
        List<Book> bookList = bookRepository
                .findBookByStatusAndUser(status, getCurrentUser()).orElse(Collections.emptyList());
        List<BookListItem> bookListItems = new ArrayList<>();
        for(Book book: bookList){
            BookListItem bookListItem = new BookListItem(book.getId(), book.getTitle(),
                    book.getAuthor(), book.getLabel());
            if(book.getImage() != null) {
                bookListItem.setImage(decompressBytes(book.getImage()));
            }
            bookListItems.add(bookListItem);
        }
        return bookListItems;
    }

    public Book getBook(Long id){
        return bookRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found")
        );
    }

    private Category getCategory(String name){
        Optional<Category> categoryOpt = categoryRepository.findCategoryByName(name);
        if(categoryOpt.isPresent()){
            return categoryOpt.get();
        } else {
            Category category = new Category();
            category.setName(name);
            categoryRepository.save(category);
            return category;
        }
    }

    private byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            logger.error("Error compressing the image: ", e.getMessage());
        }
        return outputStream.toByteArray();
    }

    public static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException ioe) {
            logger.error("Error compressing the image: ", ioe.getMessage());
        } catch ( DataFormatException e) {
            logger.error("Error formatting the image: ", e.getMessage());
        }
        return outputStream.toByteArray();
    }

    private User getCurrentUser(){
        UserDetailsI userDetails = (UserDetailsI) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));
    }
}
