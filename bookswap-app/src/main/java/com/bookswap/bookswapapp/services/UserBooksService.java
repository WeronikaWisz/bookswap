package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.dtos.userbooks.BookData;
import com.bookswap.bookswapapp.dtos.userbooks.BookFilter;
import com.bookswap.bookswapapp.dtos.userbooks.BookListItem;
import com.bookswap.bookswapapp.dtos.userbooks.FilterHints;
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
import java.util.*;
import java.util.stream.Collectors;
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
    public void addBook(MultipartFile image, Book book, List<String> categories, EBookLabel label) throws IOException {
        book.setImage(compressBytes(image.getBytes()));
        addBookInfo(book, categories, label);
    }

    @Transactional
    public void addBook(Book book, List<String> categories, EBookLabel label){
        addBookInfo(book, categories, label);
    }

    private void addBookInfo(Book book, List<String> categories, EBookLabel label){
        book.setCreationDate(LocalDateTime.now());
        book.setLabel(label);
        book.setStatus(EBookStatus.AVAILABLE);
        User user = getCurrentUser();
        book.setUser(user);
        bookRepository.save(book);
        for(String name: categories){
            Category category = getCategory(name);
            book.addCategory(category);
        }
    }

    @Transactional
    public void updateBook(Long id, MultipartFile image, BookData bookData, List<String> categories, EBookLabel label) throws IOException {
        Book book = updateBookInfo(id, bookData, categories, label);
        book.setImage(compressBytes(image.getBytes()));
    }

    @Transactional
    public void updateBook(Long id, BookData bookData, List<String> categories, EBookLabel label){
        updateBookInfo(id, bookData, categories, label);
    }

    private Book updateBookInfo(Long id, BookData bookData, List<String> categories, EBookLabel label){
        Book book = getBook(id);

        User user = getCurrentUser();

        if(!book.getUser().getUsername().equals(user.getUsername())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Book does not belong to user");
        }

        if(!book.getStatus().equals(EBookStatus.AVAILABLE)){
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Cannot edit book that is not available");
        }

        book.setUpdateDate(LocalDateTime.now());
        if(!Objects.equals(book.getTitle(), bookData.getTitle())){
            book.setTitle(bookData.getTitle());
        }
        if(!Objects.equals(book.getAuthor(), bookData.getAuthor())){
            book.setAuthor(bookData.getAuthor());
        }
        if(!Objects.equals(book.getPublisher(), bookData.getPublisher())){
            book.setPublisher(bookData.getPublisher());
        }
        if(!Objects.equals(book.getYearOfPublication(), bookData.getYearOfPublication())){
            book.setYearOfPublication(bookData.getYearOfPublication());
        }
        if(bookData.getDescription() != null && !Objects.equals(book.getDescription(), bookData.getDescription())){
            book.setDescription(bookData.getDescription());
        }
        if(book.getLabel() != label) {
            book.setLabel(label);
        }
        book.getCategories().forEach(category -> {
            if (!containsIgnoreCaseAndTrim(categories, category.getName())) {
                book.removeCategory(category);
        }});
        for(String name: categories){
            Category category = getCategory(name);
            if(!containsIgnoreCaseAndTrim(book.getCategories().stream().map(Category::getName).collect(Collectors.toList()), name)) {
                book.addCategory(category);
            }
        }
        return book;
    }


    public List<BookListItem> loadBooks(EBookStatus status){
        List<Book> bookList = bookRepository
                .findBookByStatusAndUser(status, getCurrentUser()).orElse(Collections.emptyList());
        return bookListToBookListItem(bookList);
    }

    public Book getBook(Long id){
        return bookRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found")
        );
    }

    public List<BookListItem> filterBooks(BookFilter bookFilter){
        boolean isLabelNull = (bookFilter.getLabel() == null);
        boolean isStatusNull = (bookFilter.getStatus() == null);
        List<Book> bookList;
        if(!isLabelNull && !isStatusNull){
            bookList = bookRepository.findBookByStatusAndLabelAndUser(bookFilter.getStatus(),
                    bookFilter.getLabel(), getCurrentUser()).orElse(Collections.emptyList());
        } else if(!isLabelNull) {
            bookList = bookRepository.findBookByLabelAndUser(bookFilter.getLabel(), getCurrentUser())
                    .orElse(Collections.emptyList());
        } else if(!isStatusNull) {
            bookList = bookRepository.findBookByStatusAndUser(bookFilter.getStatus(), getCurrentUser())
                    .orElse(Collections.emptyList());
        } else {
            bookList = bookRepository.findBookByUser(getCurrentUser()).orElse(Collections.emptyList());
        }
        if(!bookFilter.getAuthors().isEmpty()){
            bookList = bookList.stream().filter(book -> containsIgnoreCaseAndTrim(bookFilter.getAuthors(), book.getAuthor()))
                    .collect(Collectors.toList());
        }
        if(!bookFilter.getTitles().isEmpty()){
            bookList = bookList.stream().filter(book -> containsIgnoreCaseAndTrim(bookFilter.getTitles(), book.getTitle()))
                    .collect(Collectors.toList());
        }
        if(!bookFilter.getPublishers().isEmpty()){
            bookList = bookList.stream().filter(book -> containsIgnoreCaseAndTrim(bookFilter.getPublishers(), book.getPublisher()))
                    .collect(Collectors.toList());
        }
        if(!bookFilter.getCategories().isEmpty()){
            bookList = bookList.stream().filter(book -> !Collections.disjoint(
                    bookFilter.getCategories().stream()
                            .map(cat -> cat.trim().toLowerCase(Locale.ROOT)).collect(Collectors.toList()),
                            book.getCategories().stream()
                                    .map(cat -> cat.getName().trim().toLowerCase(Locale.ROOT)).collect(Collectors.toList())))
                    .collect(Collectors.toList());
        }
        if(bookFilter.getYearOfPublicationFrom() != null && !bookFilter.getYearOfPublicationFrom().equals("")){
            bookList = bookList.stream().filter(book -> book.getYearOfPublication() >= Integer.parseInt(bookFilter.getYearOfPublicationFrom()))
                    .collect(Collectors.toList());
        }
        if(bookFilter.getYearOfPublicationTo() != null && !bookFilter.getYearOfPublicationTo().equals("")){
            bookList = bookList.stream().filter(book -> book.getYearOfPublication() <= Integer.parseInt(bookFilter.getYearOfPublicationTo()))
                    .collect(Collectors.toList());
        }
        return bookListToBookListItem(bookList);
    }

    public FilterHints loadFilterHints(EBookStatus status){
        List<Book> bookList = bookRepository
                .findBookByStatusAndUser(status, getCurrentUser()).orElse(Collections.emptyList());
        FilterHints filterHints = new FilterHints();
        filterHints.setTitles(bookList.stream().map(Book::getTitle).distinct().collect(Collectors.toList()));
        filterHints.setAuthors(bookList.stream().map(Book::getAuthor).distinct().collect(Collectors.toList()));
        filterHints.setPublishers(bookList.stream().map(Book::getPublisher).distinct().collect(Collectors.toList()));
        List<String> categories= new ArrayList<>();
        for(Book book: bookList){
            categories.addAll(book.getCategories().stream()
                    .map(Category::getName).collect(Collectors.toList()));
        }
        filterHints.setCategories(categories.stream().distinct().collect(Collectors.toList()));
        return filterHints;
    }

    public List<String> loadCategoriesNames(){
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(Category::getName).collect(Collectors.toList());
    }

    private Category getCategory(String name){
        Optional<Category> categoryOpt = categoryRepository
                .findCategoryByName(name.trim().toLowerCase(Locale.ROOT));
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

    private static byte[] decompressBytes(byte[] data) {
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

    private List<BookListItem> bookListToBookListItem(List<Book> bookList){
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

    private boolean containsIgnoreCaseAndTrim(List<String> list, String value) {
        for (String item : list) {
            if (item.trim().equalsIgnoreCase(value.trim())) {
                return true;
            }
        }
        return false;
    }
}
