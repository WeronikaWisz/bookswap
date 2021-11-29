import {ESwapStatus} from "../../enums/ESwapStatus";
import {EBookLabel} from "../../enums/EBookLabel";

export interface SwapFilter{
  swapStatus: ESwapStatus[];
  bookLabel: EBookLabel;
}
