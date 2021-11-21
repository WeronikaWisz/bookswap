import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilterOffersDialogComponent } from './filter-offers-dialog.component';

describe('FilterOffersDialogComponent', () => {
  let component: FilterOffersDialogComponent;
  let fixture: ComponentFixture<FilterOffersDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FilterOffersDialogComponent ]
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
