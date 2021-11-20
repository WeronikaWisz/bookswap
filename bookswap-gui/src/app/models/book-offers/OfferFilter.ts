import {EBookLabel} from "../../enums/EBookLabel";

export interface OfferFilter {
  titles: string[];
  authors: string[];
  publishers: string[];
  yearOfPublicationFrom: string;
  yearOfPublicationTo: string;
  categories: string[];
  localization: string[];
  label: EBookLabel;
  owners: string[];
}
