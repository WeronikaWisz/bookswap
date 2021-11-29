import {BookListItem} from "./BookListItem";
import {BookDetails} from "./BookDetails";

export interface BookInfo{
  bookBasics: BookListItem;
  bookDetails: BookDetails;
}
