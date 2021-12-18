import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseBooksComponent } from './browse-books.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {TranslateModule} from "@ngx-translate/core";
import {MatDialogModule} from "@angular/material/dialog";

describe('BrowseBooksComponent', () => {
  let component: BrowseBooksComponent;
  let fixture: ComponentFixture<BrowseBooksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseBooksComponent ],
      imports: [HttpClientTestingModule, RouterTestingModule,
        TranslateModule.forRoot(), MatDialogModule]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BrowseBooksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
