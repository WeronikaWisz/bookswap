import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseOffersComponent } from './browse-offers.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {MatDialogModule} from "@angular/material/dialog";

describe('BrowseOffersComponent', () => {
  let component: BrowseOffersComponent;
  let fixture: ComponentFixture<BrowseOffersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseOffersComponent ],
      imports: [HttpClientTestingModule, RouterTestingModule,
        TranslateModule.forRoot(), MatDialogModule]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BrowseOffersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
