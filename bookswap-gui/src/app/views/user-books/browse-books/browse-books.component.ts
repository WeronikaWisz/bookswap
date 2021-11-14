import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {FiltersDialogComponent} from './filters-dialog/filters-dialog.component'
import {BookFilter} from "../../../models/user-books/BookFilter";
import {Router} from "@angular/router";
import {UserBookService} from "../../../services/user-book.service";
import {TokenStorageService} from "../../../services/token-storage.service";
import {EBookStatus} from "../../../enums/EBookStatus";
import Swal from "sweetalert2";
import {BookListItem} from "../../../models/user-books/BookListItem";
import {BookDetailsDialogComponent} from "./book-details-dialog/book-details-dialog.component";

@Component({
  selector: 'app-browse-books',
  templateUrl: './browse-books.component.html',
  styleUrls: ['./browse-books.component.sass']
})
export class BrowseBooksComponent implements OnInit {

  bookFilter: BookFilter = new class implements BookFilter {
    author: string = '';
    categories: string[] = [];
    publisher: string = '';
    title: string = '';
    yearOfPublicationFrom: string = '';
    yearOfPublicationTo: string = '';
  }
  isLoggedIn = false;
  books: BookListItem[] = [];
  bookCount: number = 0;

  constructor(public dialog: MatDialog, private router: Router,
              private userBookService : UserBookService, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
    this.loadBooks(EBookStatus.AVAILABLE);
  }

  public onCardClick(idx: number){
    console.log(idx);
    this.userBookService.getBook(idx)
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

  openFilter(): void {
    const dialogRef = this.dialog.open(FiltersDialogComponent, {
      data: this.bookFilter
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log(result);
      if(result) {
        this.bookFilter = result;
      }
    });
  }

  loadBooks(status: EBookStatus){
    this.userBookService.loadBooks(status)
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
    );
  }

  reloadPage(): void {
    window.location.reload();
  }

}
