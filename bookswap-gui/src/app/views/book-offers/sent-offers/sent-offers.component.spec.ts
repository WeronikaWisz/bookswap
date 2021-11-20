import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SentOffersComponent } from './sent-offers.component';

describe('SentOffersComponent', () => {
  let component: SentOffersComponent;
  let fixture: ComponentFixture<SentOffersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SentOffersComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SentOffersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
