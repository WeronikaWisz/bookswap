import { Component, OnInit } from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import { FiltersDialogComponent } from './filters-dialog/filters-dialog.component'

@Component({
  selector: 'app-user-books',
  templateUrl: './user-books.component.html',
  styleUrls: ['./user-books.component.sass']
})
export class UserBooksComponent implements OnInit {

  title: string = '';
  author: string = '';
  publisher: string = '';
  yearOfPublication: string = '';
  categories: string[] = [];

  constructor(public dialog: MatDialog) { }

  ngOnInit(): void {
  }

  public onCardClick(evt: MouseEvent){
    console.log(evt);
  }

  openFilter(): void {
    const dialogRef = this.dialog.open(FiltersDialogComponent, {
      width: '250px',
      data: {
        title: this.title,
        author: this.author,
        publisher: this.publisher,
        yearOfPublication: this.yearOfPublication,
        categories: this.categories
      },
    });

    dialogRef.afterClosed().subscribe(result => {
      // this.animal = result;
    });
  }

}
