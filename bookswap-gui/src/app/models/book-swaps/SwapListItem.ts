import {EBookLabel} from "../../enums/EBookLabel";
import {ESwapStatus} from "../../enums/ESwapStatus";

export interface SwapListItem {
  id: number;
  currentUserBookTitle: string;
  currentUserBookAuthor: string;
  currentUserBookLabel: EBookLabel;
  currentUserBookImage: any;
  otherUserBookTitle: string;
  otherUserBookAuthor: string;
  otherUserBookLabel: EBookLabel;
  otherUserBookImage: any;
  otherUsername: string;
  swapStatus: ESwapStatus;
  ifCurrentUserConfirmed: boolean;
}
