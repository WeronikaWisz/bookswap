import { TestBed } from '@angular/core/testing';

import { BookSwapsService } from './book-swaps.service';

describe('BookSwapsService', () => {
  let service: BookSwapsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookSwapsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
