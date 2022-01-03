import {Component, Inject, OnInit} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {BookInfo} from "../../../../models/user-books/BookInfo";
import {EBookStatus} from "../../../../enums/EBookStatus";
import {EBookLabel} from "../../../../enums/EBookLabel";
import {Router} from "@angular/router";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-book-details-dialog',
  templateUrl: './book-details-dialog.component.html',
  styleUrls: ['./book-details-dialog.component.sass']
})
export class BookDetailsDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<BookDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: BookInfo,
    private router: Router, private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  getStatusName(value: number): string{
    if(value === 0){
      return this.getTranslateMessage("user-books.browse-books.status-available")
    }
    if(value === 1){
      return this.getTranslateMessage("user-books.browse-books.status-permanent")
    }
    if(value === 2){
      return this.getTranslateMessage("user-books.browse-books.status-temporary")
    }
    return ''
  }

  getStatusValue(): number{
    let status = this.data.bookDetails.status.valueOf() as unknown as string;
    if(status === EBookStatus[EBookStatus.AVAILABLE]){
      return 0
    }
    if(status === EBookStatus[EBookStatus.PERMANENT_SWAP]){
      return 1
    }
    if(status === EBookStatus[EBookStatus.TEMPORARY_SWAP]){
      return 2
    }
    return 0
  }

  getLabel(): string{
    let label = this.data.bookDetails.label.valueOf() as unknown as string;
    if(label === EBookLabel[EBookLabel.PERMANENT_SWAP]){
      return this.getTranslateMessage("user-books.browse-books.label-permanent-short")
    }
    if(label === EBookLabel[EBookLabel.TEMPORARY_SWAP]){
      return this.getTranslateMessage("user-books.browse-books.label-temporary-short")
    }
    return ''
  }

  goToEditBook(): void {
    this.router.navigate(['/edit-book', this.data.bookBasics.id]).then(r => this.onNoClick());
  }

  getTranslateMessage(key: string): string{
    let message = "";
    this.translate.get(key).subscribe(data =>
      message = data
    );
    return message;
  }

}
