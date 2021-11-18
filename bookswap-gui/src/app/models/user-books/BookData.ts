import {EBookLabel} from "../../enums/EBookLabel";

export interface BookData {
  title: string;
  author: string;
  publisher: string;
  yearOfPublication: string;
  description: string;
  categories: string[];
  label: EBookLabel;
}
