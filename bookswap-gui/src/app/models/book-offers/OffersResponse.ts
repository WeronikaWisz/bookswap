import {OfferListItem} from "./OfferListItem";

export interface OffersResponse{
  offersList: OfferListItem[];
  availableOffersCount: number;
  totalOffersLength: number;
}
