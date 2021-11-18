import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {BookData} from "../models/user-books/BookData";
import {EBookStatus} from "../enums/EBookStatus";
import {BookListItem} from "../models/user-books/BookListItem";
import {BookDetails} from "../models/user-books/BookDetails";
import {BookFilter} from "../models/user-books/BookFilter";
import {FilterHints} from "../models/user-books/FilterHints";

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

  loadBooks(status: EBookStatus): Observable<BookListItem[]> {
    return this.http.get<BookListItem[]>(USER_BOOK_API + 'books', {
      params: new HttpParams().set('status', EBookStatus[status])
    });
  }

  getBookDetail(bookId: number): Observable<BookDetails>{
    return this.http.get<BookDetails>(USER_BOOK_API + 'book-details/' + bookId);
  }

  getBook(bookId: number): Observable<BookData>{
    return this.http.get<BookData>(USER_BOOK_API + 'book/' + bookId);
  }

  loadFilteredBook(bookFilter: BookFilter): Observable<BookListItem[]>{
    return this.http.post<BookListItem[]>(USER_BOOK_API + 'books/filter', JSON.stringify(bookFilter), httpOptions);
  }

  loadHintsForFilter(status: EBookStatus): Observable<FilterHints>{
    return this.http.get<FilterHints>(USER_BOOK_API + 'filter-hints', {
      params: new HttpParams().set('status', EBookStatus[status])
    });
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
