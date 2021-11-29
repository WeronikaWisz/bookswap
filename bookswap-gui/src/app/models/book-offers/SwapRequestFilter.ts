import {ERequestStatus} from "../../enums/ERequestStatus";
import {EBookLabel} from "../../enums/EBookLabel";

export interface SwapRequestFilter{
  requestStatus: ERequestStatus[];
  bookLabel: EBookLabel;
}
