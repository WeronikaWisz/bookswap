import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseSwapsComponent } from './browse-swaps.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {MatDialogModule} from "@angular/material/dialog";

describe('BrowseSwapsComponent', () => {
  let component: BrowseSwapsComponent;
  let fixture: ComponentFixture<BrowseSwapsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseSwapsComponent ],
      imports: [HttpClientTestingModule, RouterTestingModule,
        TranslateModule.forRoot(), MatDialogModule]
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
