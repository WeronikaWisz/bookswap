import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceivedOffersComponent } from './received-offers.component';

describe('ReceivedOffersComponent', () => {
  let component: ReceivedOffersComponent;
  let fixture: ComponentFixture<ReceivedOffersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReceivedOffersComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReceivedOffersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
