import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FilterOffersDialogComponent} from './filter-offers-dialog.component';
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {FilterHints} from "../../../../models/book-offers/FilterHints";
import {OfferFilter} from "../../../../models/book-offers/OfferFilter";
import {FilterDialogData} from "../../../../models/book-offers/FilterDialogData";
import {EBookLabel} from "../../../../enums/EBookLabel";

describe('FilterOffersDialogComponent', () => {
  let component: FilterOffersDialogComponent;
  let fixture: ComponentFixture<FilterOffersDialogComponent>;
  let hints: FilterHints = {
    titles: [],
    authors: [],
    publishers: [],
    categories: [],
    localization: [],
    owners: []
  }
  let filter: OfferFilter = {
    titles: [],
    authors: [],
    publishers: [],
    categories: [],
    yearOfPublicationTo: "",
    yearOfPublicationFrom: "",
    localization: [],
    owners: [],
    label: EBookLabel.PERMANENT_SWAP
  }
  let data: FilterDialogData = {
    filterHints: hints,
    bookFilter: filter
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FilterOffersDialogComponent ],
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
    fixture = TestBed.createComponent(FilterOffersDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
