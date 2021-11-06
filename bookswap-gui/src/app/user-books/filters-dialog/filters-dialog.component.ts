import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { BookFilter } from "../../models/user-books/BookFilter";

@Component({
  selector: 'app-filters-dialog',
  templateUrl: './filters-dialog.component.html',
  styleUrls: ['./filters-dialog.component.sass']
})
export class FiltersDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<FiltersDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: BookFilter,
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit(): void {
  }

}
