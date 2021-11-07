import { Component, OnInit } from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import { FiltersDialogComponent } from './filters-dialog/filters-dialog.component'
import {BookFilter} from "../models/user-books/BookFilter";

@Component({
  selector: 'app-user-books',
  templateUrl: './user-books.component.html',
  styleUrls: ['./user-books.component.sass']
})
export class UserBooksComponent implements OnInit {

  bookFilter: BookFilter = new class implements BookFilter {
    author: string = '';
    categories: string[] = [];
    publisher: string = '';
    title: string = '';
    yearOfPublicationFrom: string = '';
    yearOfPublicationTo: string = '';
  }

  constructor(public dialog: MatDialog) { }

  ngOnInit(): void {
  }

  public onCardClick(evt: MouseEvent){
    console.log(evt);
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

}
