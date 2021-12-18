import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FiltersDialogComponent } from './filters-dialog.component';
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {FilterDialogData} from "../../../../models/user-books/FilterDialogData";
import {FilterHints} from "../../../../models/user-books/FilterHints";
import {BookFilter} from "../../../../models/user-books/BookFilter";
import {MatAutocompleteModule} from "@angular/material/autocomplete";

describe('FiltersDialogComponent', () => {
  let component: FiltersDialogComponent;
  let fixture: ComponentFixture<FiltersDialogComponent>;
  let hints: FilterHints = {
    titles: [],
    authors: [],
    publishers: [],
    categories: []
  }
  let filter: BookFilter = {
    titles: [],
    authors: [],
    publishers: [],
    categories: [],
    yearOfPublicationTo: "",
    yearOfPublicationFrom: ""
  }
  let data: FilterDialogData = {
    filterHints: hints,
    bookFilter: filter
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FiltersDialogComponent ],
      imports: [ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule,
        TranslateModule.forRoot(), MatDialogModule, MatAutocompleteModule],
      providers: [
        { provide: MatDialogRef, useValue: {}
        },
        { provide: MAT_DIALOG_DATA, useValue: data }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FiltersDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
