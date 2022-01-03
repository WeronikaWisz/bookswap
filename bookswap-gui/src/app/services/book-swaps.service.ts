import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {SwapListItem} from "../models/book-swaps/SwapListItem";
import {SwapFilter} from "../models/book-swaps/SwapFilter";
import {ProfileData} from "../models/manage-users/ProfileData";
import {SwapsResponse} from "../models/book-swaps/SwapsResponse";

const BOOK_OFFERS_API = 'http://localhost:8080/book-swaps/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class BookSwapsService {

  constructor(private http: HttpClient) { }

  getSwaps(swapFilter: SwapFilter, pageIndex: number, pageSize: number): Observable<SwapsResponse>{
    return this.http.post<SwapsResponse>(BOOK_OFFERS_API + `swaps?page=${pageIndex}&size=${pageSize}`, JSON.stringify(swapFilter), httpOptions);
  }

  getUserAddressByUsername(username: string): Observable<ProfileData>{
    return this.http.get<ProfileData>(BOOK_OFFERS_API + 'user-address/' + username);
  }

  confirmBookDelivery(swapId: number):Observable<SwapListItem>{
    return this.http.put<SwapListItem>(BOOK_OFFERS_API + 'swap/confirm/' + swapId, httpOptions);
  }

}
