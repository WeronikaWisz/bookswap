import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseSwapRequestsComponent } from './browse-swap-requests.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {MatDialogModule} from "@angular/material/dialog";

describe('BrowseSwapRequestsComponent', () => {
  let component: BrowseSwapRequestsComponent;
  let fixture: ComponentFixture<BrowseSwapRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseSwapRequestsComponent ],
      imports: [HttpClientTestingModule, RouterTestingModule,
        TranslateModule.forRoot(), MatDialogModule]
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
