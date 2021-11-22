import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {EBookLabel} from "../enums/EBookLabel";
import {OfferDetails} from "../models/book-offers/OfferDetails";
import {OfferFilter} from "../models/book-offers/OfferFilter";
import {FilterHints} from "../models/book-offers/FilterHints";
import {OffersResponse} from "../models/book-offers/OffersResponse";
import {BooksForSwap} from "../models/book-offers/BooksForSwap";
import {SwapRequestListItem} from "../models/book-offers/SwapRequestListItem";
import {SwapRequestFilter} from "../models/book-offers/SwapRequestFilter";

const BOOK_OFFERS_API = 'http://localhost:8080/book-offers/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class BookOffersService {

  constructor(private http: HttpClient) { }

  loadOffers(label: EBookLabel): Observable<OffersResponse> {
    return this.http.get<OffersResponse>(BOOK_OFFERS_API + 'offers', {
      params: new HttpParams().set('label', EBookLabel[label])
    });
  }

  getOfferDetail(offerId: number): Observable<OfferDetails>{
    return this.http.get<OfferDetails>(BOOK_OFFERS_API + 'offer-details/' + offerId);
  }

  loadFilteredOffers(offerFilter: OfferFilter): Observable<OffersResponse>{
    return this.http.post<OffersResponse>(BOOK_OFFERS_API + 'offers/filter', JSON.stringify(offerFilter), httpOptions);
  }

  loadHintsForFilter(): Observable<FilterHints>{
    return this.http.get<FilterHints>(BOOK_OFFERS_API + 'filter-hints');
  }

  sendSwapRequest(bookForSwap: BooksForSwap): Observable<any>{
    return this.http.post(BOOK_OFFERS_API + 'swap-request', JSON.stringify(bookForSwap), httpOptions);
  }

  getSentRequests(swapRequestFilter: SwapRequestFilter): Observable<SwapRequestListItem[]>{
    return this.http.post<SwapRequestListItem[]>(BOOK_OFFERS_API + 'sent-requests', JSON.stringify(swapRequestFilter), httpOptions);
  }

  getReceivedRequests(swapRequestFilter: SwapRequestFilter): Observable<SwapRequestListItem[]>{
    return this.http.post<SwapRequestListItem[]>(BOOK_OFFERS_API + 'received-requests', JSON.stringify(swapRequestFilter), httpOptions);
  }

  cancelSwapRequest(swapRequestId: number): Observable<any>{
    return this.http.delete(BOOK_OFFERS_API + 'swap-request/' + swapRequestId);
  }

}
