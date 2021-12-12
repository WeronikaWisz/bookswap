package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.dtos.userbooks.BookData;
import com.bookswap.bookswapapp.dtos.userbooks.BookFilter;
import com.bookswap.bookswapapp.dtos.userbooks.BookListItem;
import com.bookswap.bookswapapp.dtos.userbooks.FilterHints;
import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.bookswap.bookswapapp.exception.ApiExpectationFailedException;
import com.bookswap.bookswapapp.exception.ApiForbiddenException;
import com.bookswap.bookswapapp.exception.ApiNotFoundException;
import com.bookswap.bookswapapp.helpers.FilterHelper;
import com.bookswap.bookswapapp.helpers.ImageHelper;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        book.setImage(ImageHelper.compressBytes(image.getBytes()));
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
        book.setImage(ImageHelper.compressBytes(image.getBytes()));
    }

    @Transactional
    public void updateBook(Long id, BookData bookData, List<String> categories, EBookLabel label){
        updateBookInfo(id, bookData, categories, label);
    }

    private Book updateBookInfo(Long id, BookData bookData, List<String> categories, EBookLabel label){
        Book book = getBook(id);

        User user = getCurrentUser();

        if(!book.getUser().getUsername().equals(user.getUsername())){
            throw new ApiForbiddenException("exception.bookNotBelongToUser");
        }

        if(!book.getStatus().equals(EBookStatus.AVAILABLE)){
            throw new ApiExpectationFailedException("exception.cannotEditNotAvailableBook");
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
            if (!FilterHelper.containsIgnoreCaseAndTrim(categories, category.getName())) {
                book.removeCategory(category);
        }});
        for(String name: categories){
            Category category = getCategory(name);
            if(!FilterHelper.containsIgnoreCaseAndTrim(book.getCategories().stream().map(Category::getName).collect(Collectors.toList()), name)) {
                book.addCategory(category);
            }
        }
        return book;
    }

    public Book getBook(Long id){
        return bookRepository.findById(id).orElseThrow(
                () -> new ApiNotFoundException("exception.bookNotFound")
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
            bookList = bookList.stream().filter(book -> FilterHelper.containsIgnoreCaseAndTrim(bookFilter.getAuthors(), book.getAuthor()))
                    .collect(Collectors.toList());
        }
        if(!bookFilter.getTitles().isEmpty()){
            bookList = bookList.stream().filter(book -> FilterHelper.containsIgnoreCaseAndTrim(bookFilter.getTitles(), book.getTitle()))
                    .collect(Collectors.toList());
        }
        if(!bookFilter.getPublishers().isEmpty()){
            bookList = bookList.stream().filter(book -> FilterHelper.containsIgnoreCaseAndTrim(bookFilter.getPublishers(), book.getPublisher()))
                    .collect(Collectors.toList());
        }
        if(!bookFilter.getCategories().isEmpty()){
            bookList = bookList.stream().filter(book -> FilterHelper.categoriesMatches(bookFilter.getCategories(), book))
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
        return bookListToBookListItem(bookList
                .stream().sorted(Comparator.comparing(Book::getCreationDate).reversed()).collect(Collectors.toList()));
    }

    public FilterHints loadFilterHints(){
        List<Book> bookList = bookRepository
                .findBookByUser(getCurrentUser()).orElse(Collections.emptyList());
        FilterHints filterHints = new FilterHints();
        filterHints.setTitles(bookList.stream().map(Book::getTitle).distinct().collect(Collectors.toList()));
        filterHints.setAuthors(bookList.stream().map(Book::getAuthor).distinct().collect(Collectors.toList()));
        filterHints.setPublishers(bookList.stream().map(Book::getPublisher).distinct().collect(Collectors.toList()));
        filterHints.setCategories(FilterHelper.getCategoriesFromBooks(bookList));
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
                bookListItem.setImage(ImageHelper.decompressBytes(book.getImage()));
            }
            bookListItems.add(bookListItem);
        }
        return bookListItems;
    }

}
