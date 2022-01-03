import {ComponentFixture, TestBed} from '@angular/core/testing';

import {OfferDetailsDialogComponent} from './offer-details-dialog.component';
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {OfferInfo} from "../../../../models/book-offers/OfferInfo";
import {OfferListItem} from "../../../../models/book-offers/OfferListItem";
import {OfferDetails} from "../../../../models/book-offers/OfferDetails";
import {EBookLabel} from "../../../../enums/EBookLabel";
import {EBookStatus} from "../../../../enums/EBookStatus";

describe('OfferDetailsDialogComponent', () => {
  let component: OfferDetailsDialogComponent;
  let fixture: ComponentFixture<OfferDetailsDialogComponent>;
  let basic: OfferListItem = {
    id: 1,
    title: "",
    author: "",
    image: null
  }
  let details: OfferDetails = {
    publisher: "",
    yearOfPublication: 2021,
    description: "",
    localization: "",
    label: EBookLabel.PERMANENT_SWAP,
    status: EBookStatus.AVAILABLE,
    owner: "",
    categories: [],
    hasOfferFromUser: false,
    requestedBooks: []
  }
  let data: OfferInfo = {
    offerBasics: basic,
    offerDetails: details,
    hasBooksForSwap: true
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OfferDetailsDialogComponent ],
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
    fixture = TestBed.createComponent(OfferDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
