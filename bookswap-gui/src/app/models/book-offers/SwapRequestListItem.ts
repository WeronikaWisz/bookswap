import {ERequestStatus} from "../../enums/ERequestStatus";
import {EBookLabel} from "../../enums/EBookLabel";
import {EBookStatus} from "../../enums/EBookStatus";

export interface SwapRequestListItem{
  id: number;
  bookTitle: string;
  bookAuthor: string;
  bookLabel: EBookLabel;
  bookStatus: EBookStatus;
  bookImage: any;
  sender: string;
  owner: string;
  requestStatus: ERequestStatus;
}
