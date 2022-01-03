import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {BookData} from "../models/user-books/BookData";
import {BookDetails} from "../models/user-books/BookDetails";
import {BookFilter} from "../models/user-books/BookFilter";
import {FilterHints} from "../models/user-books/FilterHints";
import {BooksResponse} from "../models/user-books/BooksResponse";

const USER_BOOK_API = 'http://localhost:8080/user-books/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class UserBookService {

  constructor(private http: HttpClient) { }

  addBook(newBook: BookData, image: any): Observable<any> {
    const formData = new FormData();
    formData.append("image", image);
    const blobNewBook = new Blob([JSON.stringify(newBook)], {
      type: 'application/json'
    })
    formData.append('info', blobNewBook);
    console.log(newBook)
    return this.http.post(USER_BOOK_API + 'book', formData);
  }

  getBookDetail(bookId: number): Observable<BookDetails>{
    return this.http.get<BookDetails>(USER_BOOK_API + 'book-details/' + bookId);
  }

  getBook(bookId: number): Observable<BookData>{
    return this.http.get<BookData>(USER_BOOK_API + 'book/' + bookId);
  }

  loadFilteredBook(bookFilter: BookFilter, pageIndex: number, pageSize: number): Observable<BooksResponse>{
    return this.http.post<BooksResponse>(USER_BOOK_API + `books/filter?page=${pageIndex}&size=${pageSize}`, JSON.stringify(bookFilter), httpOptions);
  }

  loadHintsForFilter(): Observable<FilterHints>{
    return this.http.get<FilterHints>(USER_BOOK_API + 'filter-hints');
  }

  loadAllCategoryNames(): Observable<string[]>{
    return this.http.get<string[]>(USER_BOOK_API + 'categories');
  }

  updateBook(updateBook: BookData, image: any, id: number): Observable<any> {
    const formData = new FormData();
    formData.append("image", image);
    const blobNewBook = new Blob([JSON.stringify(updateBook)], {
      type: 'application/json'
    })
    formData.append('info', blobNewBook);
    return this.http.put(USER_BOOK_API + `book/${id}`, formData);
  }

}
