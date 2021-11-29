import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseSwapsComponent } from './browse-swaps.component';

describe('BrowseSwapsComponent', () => {
  let component: BrowseSwapsComponent;
  let fixture: ComponentFixture<BrowseSwapsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseSwapsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BrowseSwapsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
