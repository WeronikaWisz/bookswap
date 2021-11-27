import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

const BOOK_OFFERS_API = 'http://localhost:8080/book-swaps/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class BookSwapsService {

  constructor(private http: HttpClient) { }


}
