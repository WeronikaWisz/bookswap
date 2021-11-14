import {EBookStatus} from "../../enums/EBookStatus";
import {EBookLabel} from "../../enums/EBookLabel";

export interface BookFilter{
  titles: string[];
  authors: string[];
  publishers: string[];
  yearOfPublicationFrom: string;
  yearOfPublicationTo: string;
  categories: string[];
  status?: EBookStatus;
  label?: EBookLabel;
}
