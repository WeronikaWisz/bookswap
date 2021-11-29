import {EBookLabel} from "../../enums/EBookLabel";
import {EBookStatus} from "../../enums/EBookStatus";
import {RequestBook} from "./RequestBook";

export interface OfferDetails{
  publisher: string;
  yearOfPublication: number;
  description: string;
  label: EBookLabel;
  status: EBookStatus;
  owner: string;
  localization: string;
  categories: string[];
  hasOfferFromUser: boolean;
  requestedBooks: RequestBook[];
}
