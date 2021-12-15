import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {OfferDetails} from "../models/book-offers/OfferDetails";
import {OfferFilter} from "../models/book-offers/OfferFilter";
import {FilterHints} from "../models/book-offers/FilterHints";
import {OffersResponse} from "../models/book-offers/OffersResponse";
import {BooksForSwap} from "../models/book-offers/BooksForSwap";
import {SwapRequestListItem} from "../models/book-offers/SwapRequestListItem";
import {SwapRequestFilter} from "../models/book-offers/SwapRequestFilter";
import {RequestsResponse} from "../models/book-offers/RequestsResponse";

const BOOK_OFFERS_API = 'http://localhost:8080/book-offers/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class BookOffersService {

  constructor(private http: HttpClient) { }

  getOfferDetail(offerId: number): Observable<OfferDetails>{
    return this.http.get<OfferDetails>(BOOK_OFFERS_API + 'offer-details/' + offerId);
  }

  loadFilteredOffers(offerFilter: OfferFilter, pageIndex: number, pageSize: number): Observable<OffersResponse>{
    return this.http.post<OffersResponse>(BOOK_OFFERS_API + `offers/filter?page=${pageIndex}&size=${pageSize}`, JSON.stringify(offerFilter), httpOptions);
  }

  loadHintsForFilter(): Observable<FilterHints>{
    return this.http.get<FilterHints>(BOOK_OFFERS_API + 'filter-hints');
  }

  sendSwapRequest(bookForSwap: BooksForSwap): Observable<any>{
    return this.http.post(BOOK_OFFERS_API + 'swap-request', JSON.stringify(bookForSwap), httpOptions);
  }

  getSentRequests(swapRequestFilter: SwapRequestFilter, pageIndex: number, pageSize: number): Observable<RequestsResponse>{
    console.log(swapRequestFilter)
    return this.http.post<RequestsResponse>(BOOK_OFFERS_API + `sent-requests?page=${pageIndex}&size=${pageSize}`, JSON.stringify(swapRequestFilter), httpOptions);
  }

  getReceivedRequests(swapRequestFilter: SwapRequestFilter, pageIndex: number, pageSize: number): Observable<RequestsResponse>{
    console.log(swapRequestFilter)
    return this.http.post<RequestsResponse>(BOOK_OFFERS_API + `received-requests?page=${pageIndex}&size=${pageSize}`, JSON.stringify(swapRequestFilter), httpOptions);
  }

  cancelSwapRequest(swapRequestId: number): Observable<any>{
    return this.http.delete(BOOK_OFFERS_API + 'swap-request/cancel/' + swapRequestId);
  }

  denySwapRequest(swapRequestId: number): Observable<any>{
    return this.http.delete(BOOK_OFFERS_API + 'swap-request/deny/' + swapRequestId);
  }

}
