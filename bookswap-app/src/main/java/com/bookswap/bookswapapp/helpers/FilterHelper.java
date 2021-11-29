package com.bookswap.bookswapapp.helpers;

import com.bookswap.bookswapapp.models.Book;
import com.bookswap.bookswapapp.models.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FilterHelper {

    public static boolean containsIgnoreCaseAndTrim(List<String> list, String value) {
        for (String item : list) {
            if (item.trim().equalsIgnoreCase(value.trim())) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getCategoriesFromBooks(List<Book> bookList){
        List<String> categories= new ArrayList<>();
        for(Book book: bookList){
            categories.addAll(book.getCategories().stream()
                    .map(Category::getName).collect(Collectors.toList()));
        }
        return categories.stream().distinct().collect(Collectors.toList());
    }

    public static boolean localizationMatches(List<String> localizations, Book book){
        for(String localization: localizations){
            String[] address = localization.split(", ");
            String postalCode = address[1].substring(0, 6);
            String post = address[1].substring(7).trim();
            if(book.getUser().getCity().equals(address[0]) && book.getUser().getPostalCode().equals(postalCode)
                    && book.getUser().getPost().equals(post)){
                return true;
            }
        }
        return false;
    }

    public static boolean categoriesMatches(List<String> categories, Book book){
        return !Collections.disjoint(
                categories.stream()
                        .map(cat -> cat.trim().toLowerCase(Locale.ROOT)).collect(Collectors.toList()),
                book.getCategories().stream()
                        .map(cat -> cat.getName().trim().toLowerCase(Locale.ROOT)).collect(Collectors.toList()));
    }

}
