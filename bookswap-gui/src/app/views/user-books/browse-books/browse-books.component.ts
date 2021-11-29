import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {FiltersDialogComponent} from './filters-dialog/filters-dialog.component'
import {BookFilter} from "../../../models/user-books/BookFilter";
import {Router} from "@angular/router";
import {UserBookService} from "../../../services/user-book.service";
import {TokenStorageService} from "../../../services/token-storage.service";
import Swal from "sweetalert2";
import {BookListItem} from "../../../models/user-books/BookListItem";
import {BookDetailsDialogComponent} from "./book-details-dialog/book-details-dialog.component";
import {FilterHints} from "../../../models/user-books/FilterHints";

@Component({
  selector: 'app-browse-books',
  templateUrl: './browse-books.component.html',
  styleUrls: ['./browse-books.component.sass']
})
export class BrowseBooksComponent implements OnInit {

  bookFilter: BookFilter = {
    authors: [],
    categories: [],
    publishers: [],
    titles: [],
    yearOfPublicationFrom: '',
    yearOfPublicationTo: '',
    label: undefined,
    status: 0
  }
  isLoggedIn = false;
  books: BookListItem[] = [];
  bookCount: number = 0;

  filterHintsLoaded = false;
  hints?: FilterHints;

  constructor(public dialog: MatDialog, private router: Router,
              private userBookService : UserBookService, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
    this.loadFilterBooks();
  }

  public onCardClick(idx: number){
    console.log(idx);
    this.userBookService.getBookDetail(idx)
      .subscribe(
        data => {
          console.log(data);
          const dialogRef = this.dialog.open(BookDetailsDialogComponent, {
            data: {
              bookBasics: this.books.find(element => element.id == idx),
              bookDetails: data
            }
          });
          dialogRef.afterClosed().subscribe(result => {
            console.log(result);
          });
        },
        err => {
          Swal.fire({
            position: 'top-end',
            title: 'Nie można załadować informacji o książce',
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      )
  }

  openFilter(){
    if(!this.filterHintsLoaded){
      this.userBookService.loadHintsForFilter()
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
    const dialogRef = this.dialog.open(FiltersDialogComponent, {
      data: {
        filterHints: this.hints,
        bookFilter: this.bookFilter
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log(result);
      if(result) {
        this.bookFilter = result;
        this.loadFilterBooks();
      }
    });
  }

  loadFilterBooks(){
    this.userBookService.loadFilteredBook({
      authors: this.bookFilter.authors,
      categories: this.bookFilter.categories,
      publishers: this.bookFilter.publishers,
      titles: this.bookFilter.titles,
      yearOfPublicationFrom: this.bookFilter.yearOfPublicationFrom ?
        this.bookFilter.yearOfPublicationFrom : null!,
      yearOfPublicationTo: this.bookFilter.yearOfPublicationTo ?
        this.bookFilter.yearOfPublicationTo : null!,
      label: null!,
      status: this.bookFilter.status
    })
      .subscribe(
        data => {
          console.log(data);
          this.books = data;
          this.bookCount = this.books.length;
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

  reloadPage(): void {
    window.location.reload();
  }

  onTabChange(event: any){
    console.log(event);
    if(event.index === 1){
      this.bookFilter.status = 2;
    } else if (event.index === 2){
      this.bookFilter.status = 1;
    } else {
      this.bookFilter.status = 0;
    }
    this.loadFilterBooks();
  }

}
