import { TestBed } from '@angular/core/testing';

import { BookSwapsService } from './book-swaps.service';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('BookSwapsService', () => {
  let service: BookSwapsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(BookSwapsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
