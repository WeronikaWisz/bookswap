import { TestBed } from '@angular/core/testing';

import { BookOffersService } from './book-offers.service';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('BookOffersService', () => {
  let service: BookOffersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(BookOffersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
