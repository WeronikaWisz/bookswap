import {ComponentFixture, TestBed} from '@angular/core/testing';

import {BookDetailsDialogComponent} from './book-details-dialog.component';
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {EBookLabel} from "../../../../enums/EBookLabel";
import {EBookStatus} from "../../../../enums/EBookStatus";
import {BookListItem} from "../../../../models/user-books/BookListItem";
import {BookInfo} from "../../../../models/user-books/BookInfo";
import {BookDetails} from "../../../../models/user-books/BookDetails";

describe('BookDetailsDialogComponent', () => {
  let component: BookDetailsDialogComponent;
  let fixture: ComponentFixture<BookDetailsDialogComponent>;
  let basic: BookListItem = {
    id: 1,
    title: "",
    author: "",
    image: null,
    label: EBookLabel.PERMANENT_SWAP
  }
  let details: BookDetails = {
    publisher: "",
    yearOfPublication: 2021,
    description: "",
    label: EBookLabel.PERMANENT_SWAP,
    status: EBookStatus.AVAILABLE,
    categories: []
  }
  let data: BookInfo = {
    bookBasics: basic,
    bookDetails: details
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BookDetailsDialogComponent ],
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule,
        TranslateModule.forRoot(), MatDialogModule],
      providers: [
        { provide: MatDialogRef, useValue: {}
        },
        { provide: MAT_DIALOG_DATA, useValue: data }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BookDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
