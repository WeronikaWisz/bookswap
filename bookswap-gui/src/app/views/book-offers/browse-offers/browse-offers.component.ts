import {Component, OnInit} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {ActivatedRoute, Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import Swal from "sweetalert2";
import {OfferFilter} from "../../../models/book-offers/OfferFilter";
import {OfferListItem} from "../../../models/book-offers/OfferListItem";
import {FilterHints} from "../../../models/book-offers/FilterHints";
import {BookOffersService} from "../../../services/book-offers.service";
import {FilterOffersDialogComponent} from "./filter-offers-dialog/filter-offers-dialog.component";
import {OfferDetailsDialogComponent} from "./offer-details-dialog/offer-details-dialog.component";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-browse-offers',
  templateUrl: './browse-offers.component.html',
  styleUrls: ['./browse-offers.component.sass']
})
export class BrowseOffersComponent implements OnInit {

  offerFilter: OfferFilter = {
    authors: [],
    categories: [],
    publishers: [],
    titles: [],
    yearOfPublicationFrom: '',
    yearOfPublicationTo: '',
    label: 0,
    localization: [],
    owners: []
  }
  isLoggedIn = false;
  offers: OfferListItem[] = [];
  availableOffersCount: number = 0;

  filterHintsLoaded = false;
  hints?: FilterHints;

  selectedTabIndex = 0;

  emptySearchList = false;

  constructor(public dialog: MatDialog, private router: Router, private route: ActivatedRoute,
              private translate: TranslateService, private bookOffersService : BookOffersService,
              private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
    this.checkIfFromSwapRequestView();
  }

  checkIfFromSwapRequestView(){
    this.route.params
      .subscribe(
        params => {
          console.log(params);
          if (params.username){
            this.offerFilter.owners = [params.username]
          }
          if(params.label){
            this.offerFilter.label = params.label
            this.selectedTabIndex = params.label
          }
          this.loadFilterOffers();
        }
      );
  }

  public onCardClick(idx: number){
    console.log(idx);
    this.bookOffersService.getOfferDetail(idx)
      .subscribe(
        data => {
          console.log(data);
          const dialogRef = this.dialog.open(OfferDetailsDialogComponent, {
            data: {
              offerBasics: this.offers.find(element => element.id == idx),
              offerDetails: data,
              hasBooksForSwap: this.availableOffersCount
            }
          });
          dialogRef.afterClosed().subscribe(result => {
            console.log(result);
            if(result){
              let index = this.offers.findIndex(offer => offer.id === idx);
              this.offers.splice(index, 1);
              this.availableOffersCount -= 1;
            }
          });
        },
        err => {
          let message = "";
          this.translate.get("book-offers.browse-offers.load-book-error").subscribe(data =>
            message = data
          );
          Swal.fire({
            position: 'top-end',
            title: message,
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      )
  }

  openFilter(){
    if(!this.filterHintsLoaded){
      this.bookOffersService.loadHintsForFilter()
        .subscribe(
          data => {
            console.log(data);
            this.hints = data;
            this.filterHintsLoaded = true;
            this.openFilterDialog();
          })
    } else {
      this.openFilterDialog();
    }
  }

  openFilterDialog(): void {
    const dialogRef = this.dialog.open(FilterOffersDialogComponent, {
      data: {
        filterHints: this.hints,
        bookFilter: this.offerFilter
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log(result);
      if(result) {
        this.offerFilter = result;
        this.loadFilterOffers();
      }
    });
  }

  loadFilterOffers(){
    this.emptySearchList = false;
    this.bookOffersService.loadFilteredOffers({
      authors: this.offerFilter.authors,
      categories: this.offerFilter.categories,
      publishers: this.offerFilter.publishers,
      titles: this.offerFilter.titles,
      yearOfPublicationFrom: this.offerFilter.yearOfPublicationFrom ?
        this.offerFilter.yearOfPublicationFrom : null!,
      yearOfPublicationTo: this.offerFilter.yearOfPublicationTo ?
        this.offerFilter.yearOfPublicationTo : null!,
      label: this.offerFilter.label,
      localization: this.offerFilter.localization,
      owners: this.offerFilter.owners
    })
      .subscribe(
        data => {
          console.log(data);
          this.offers = data.offersList;
          this.availableOffersCount = data.availableOffersCount;
          if(this.offers.length == 0){
            this.emptySearchList = true;
          }
        },
        err => {
          let message = "";
          this.translate.get("book-offers.browse-offers.load-error").subscribe(data =>
            message = data
          );
          Swal.fire({
            position: 'top-end',
            title: message,
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      )
  }

  reloadPage(): void {
    window.location.reload();
  }

  onTabChange(event: any){
    console.log(event);
    this.offerFilter.label = event.index;
    this.loadFilterOffers();
  }

}
