import {EBookLabel} from "../../enums/EBookLabel";

export interface BookListItem{
  id: number;
  title: string;
  author: string;
  image: any;
  label: EBookLabel;
}
