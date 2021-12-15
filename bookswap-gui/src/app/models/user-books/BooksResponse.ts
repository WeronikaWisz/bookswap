import {BookListItem} from "./BookListItem";

export interface BooksResponse{
  booksList: BookListItem[];
  totalBooksLength: number;
}
