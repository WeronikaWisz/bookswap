import {OfferListItem} from "./OfferListItem";
import {OfferDetails} from "./OfferDetails";

export interface OfferInfo{
  offerBasics: OfferListItem;
  offerDetails: OfferDetails;
  hasBooksForSwap: boolean;
}
