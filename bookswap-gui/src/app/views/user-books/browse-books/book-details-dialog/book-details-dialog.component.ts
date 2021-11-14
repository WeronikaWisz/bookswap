import {Component, Inject, OnInit} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {BookInfo} from "../../../../models/user-books/BookInfo";
import {EBookStatus} from "../../../../enums/EBookStatus";
import {EBookLabel} from "../../../../enums/EBookLabel";

@Component({
  selector: 'app-book-details-dialog',
  templateUrl: './book-details-dialog.component.html',
  styleUrls: ['./book-details-dialog.component.sass']
})
export class BookDetailsDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<BookDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: BookInfo,
  ) {}

  ngOnInit(): void {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  getStatus(): string{
    let status = this.data.bookDetails.status.valueOf() as unknown as string;
    if(status === EBookStatus[EBookStatus.AVAILABLE]){
      return 'Dostępna'
    }
    if(status === EBookStatus[EBookStatus.PERMANENT_SWAP]){
      return 'Wymieniona'
    }
    if(status === EBookStatus[EBookStatus.TEMPORARY_SWAP]){
      return 'Na wymianie tymczasowej'
    }
    return ''
  }

  getLabel(): string{
    let label = this.data.bookDetails.label.valueOf() as unknown as string;
    if(label === EBookLabel[EBookLabel.PERMANENT_SWAP]){
      return 'Stała'
    }
    if(label === EBookLabel[EBookLabel.TEMPORARY_SWAP]){
      return 'Tymczasowa'
    }
    return ''
  }

}
