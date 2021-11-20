import {EBookLabel} from "../../enums/EBookLabel";

export interface OfferDetails{
  publisher: string;
  yearOfPublication: number;
  description: string;
  label: EBookLabel;
  owner: string;
  categories: string[];
}
