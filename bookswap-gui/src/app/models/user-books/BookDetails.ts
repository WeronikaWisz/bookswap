import {EBookStatus} from "../../enums/EBookStatus";
import {EBookLabel} from "../../enums/EBookLabel";

export interface BookDetails{
  publisher: string;
  yearOfPublication: number;
  description: string;
  status: EBookStatus;
  label: EBookLabel;
  categories: string[];
}
