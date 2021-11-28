import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {SwapListItem} from "../models/book-swaps/SwapListItem";
import {ESwapStatus} from "../enums/ESwapStatus";
import {SwapFilter} from "../models/book-swaps/SwapFilter";

const BOOK_OFFERS_API = 'http://localhost:8080/book-swaps/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class BookSwapsService {

  constructor(private http: HttpClient) { }

  getSwaps(swapFilter: SwapFilter): Observable<SwapListItem[]>{
    return this.http.post<SwapListItem[]>(BOOK_OFFERS_API + 'swaps', JSON.stringify(swapFilter), httpOptions);
  }

}
