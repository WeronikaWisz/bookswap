import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseSwapRequestsComponent } from './browse-swap-requests.component';

describe('BrowseSwapRequestsComponent', () => {
  let component: BrowseSwapRequestsComponent;
  let fixture: ComponentFixture<BrowseSwapRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseSwapRequestsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BrowseSwapRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
