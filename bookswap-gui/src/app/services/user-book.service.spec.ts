import { TestBed } from '@angular/core/testing';

import { UserBookService } from './user-book.service';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('UserService', () => {
  let service: UserBookService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(UserBookService);

  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
