import { Component, OnInit } from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import Swal from "sweetalert2";
import {OfferFilter} from "../../../models/book-offers/OfferFilter";
import {OfferListItem} from "../../../models/book-offers/OfferListItem";
import {FilterHints} from "../../../models/book-offers/FilterHints";
import {BookOffersService} from "../../../services/book-offers.service";
import {EBookLabel} from "../../../enums/EBookLabel";

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

  constructor(public dialog: MatDialog, private router: Router,
              private bookOffersService : BookOffersService, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
    this.loadOffers(EBookLabel.PERMANENT_SWAP);
  }

  public onCardClick(idx: number){
    // console.log(idx);
    // this.userBookService.getBookDetail(idx)
    //   .subscribe(
    //     data => {
    //       console.log(data);
    //       const dialogRef = this.dialog.open(BookDetailsDialogComponent, {
    //         data: {
    //           bookBasics: this.books.find(element => element.id == idx),
    //           bookDetails: data
    //         }
    //       });
    //       dialogRef.afterClosed().subscribe(result => {
    //         console.log(result);
    //       });
    //     },
    //     err => {
    //       Swal.fire({
    //         position: 'top-end',
    //         title: 'Nie można załadować informacji o książce',
    //         text: err.error.message,
    //         icon: 'error',
    //         showConfirmButton: false
    //       })
    //     }
    //   )
  }

  openFilter(){
    // if(!this.filterHintsLoaded){
    //   this.userBookService.loadHintsForFilter(EBookStatus.AVAILABLE)
    //     .subscribe(
    //       data => {
    //         console.log(data);
    //         this.hints = data;
    //         this.filterHintsLoaded = true;
    //         this.openFilterDialog();
    //       })
    // } else {
    //   this.openFilterDialog();
    // }
  }

  // openFilterDialog(): void {
  //   const dialogRef = this.dialog.open(FiltersDialogComponent, {
  //     data: {
  //       filterHints: this.hints,
  //       bookFilter: this.bookFilter
  //     }
  //   });
  //
  //   dialogRef.afterClosed().subscribe(result => {
  //     console.log(result);
  //     if(result) {
  //       this.bookFilter = result;
  //       this.loadFilterBooks();
  //     }
  //   });
  // }

  loadFilterOffers(){
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
        },
        err => {
          Swal.fire({
            position: 'top-end',
            title: 'Nie można załadować książek',
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      )
  }

  loadOffers(label: EBookLabel){
    this.bookOffersService.loadOffers(label)
      .subscribe(
        data => {
          console.log(data);
          this.offers = data.offersList;
          this.availableOffersCount = data.availableOffersCount;
        },
        err => {
          Swal.fire({
            position: 'top-end',
            title: 'Nie można załadować książek',
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      );
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
