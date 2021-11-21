import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfferDetailsDialogComponent } from './offer-details-dialog.component';

describe('OfferDetailsDialogComponent', () => {
  let component: OfferDetailsDialogComponent;
  let fixture: ComponentFixture<OfferDetailsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OfferDetailsDialogComponent ]
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
