import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {NewBook} from "../models/user-books/NewBook";

const USER_BOOK_API = 'http://localhost:8080/user-books/';

// const httpOptions = {
//   headers: new HttpHeaders({ 'Content-Type': 'application/json' })
// };

@Injectable({
  providedIn: 'root'
})
export class UserBookService {

  constructor(private http: HttpClient) { }

  addBook(newBook: NewBook, image: any): Observable<any> {
    const formData = new FormData();
    formData.append("image", image);
    const blobNewBook = new Blob([JSON.stringify(newBook)], {
      type: 'application/json'
    })
    formData.append('info', blobNewBook);
    console.log(newBook)
    return this.http.post(USER_BOOK_API + 'book', formData);
  }

}
