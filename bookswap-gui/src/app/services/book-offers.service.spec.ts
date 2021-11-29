import { TestBed } from '@angular/core/testing';

import { BookOffersService } from './book-offers.service';

describe('BookOffersService', () => {
  let service: BookOffersService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookOffersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
