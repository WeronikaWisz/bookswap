import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {BookSwapsService} from "../../../services/book-swaps.service";
import {SwapRequestListItem} from "../../../models/book-offers/SwapRequestListItem";
import {ESwapStatus} from "../../../enums/ESwapStatus";

export interface SwapStatus{
  status: ESwapStatus,
  name: string
}

@Component({
  selector: 'app-browse-swaps',
  templateUrl: './browse-swaps.component.html',
  styleUrls: ['./browse-swaps.component.sass']
})
export class BrowseSwapsComponent implements OnInit {

  isLoggedIn = false;
  isPermanentSwaps = true;
  title = 'Wymiany stałe';
  swapsCount: number = 0;
  swaps: SwapRequestListItem[] = [];
  selectedSwapStatus?: ESwapStatus;
  currentTab = 0;
  swapStatus: ESwapStatus[] = [ESwapStatus.IN_PROGRESS, ESwapStatus.BOOK_1_CONFIRMED, ESwapStatus.BOOK_2_CONFIRMED]

  statuses: SwapStatus[] = [
    {status: ESwapStatus.BOOK_1_CONFIRMED, name: "Wysłane przeze mnie"},
    {status: ESwapStatus.BOOK_2_CONFIRMED, name: "Otrzymane"}]

  constructor(private router: Router, private tokenStorage: TokenStorageService,
              private bookSwapsService : BookSwapsService, private route: ActivatedRoute,) { }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
    this.checkIfPermanentOrTemporary()
  }

  checkIfPermanentOrTemporary(){
    this.route.params
      .subscribe(
        params => {
          console.log(params);
          this.isPermanentSwaps = params.direction === 'permanent';
          if(!this.isPermanentSwaps){
            this.title = "Wymiany tymczasowe"
          } else {
            this.title = "Wymiany stałe"
          }
          this.getSwaps();
        }
      );
  }

  reloadPage(): void {
    window.location.reload();
  }

  onTabChange(event: any){
    console.log(event);
    this.currentTab = event.index;
    if(event.index === 0){
      this.swapStatus = [ESwapStatus.IN_PROGRESS, ESwapStatus.BOOK_1_CONFIRMED, ESwapStatus.BOOK_2_CONFIRMED];
    } else {
      this.swapStatus = [ESwapStatus.COMPLETED];
    }
    this.getSwaps();
  }

  changeSelectedSwapStatus(){
    if(this.currentTab === 0) {
      if (this.selectedSwapStatus) {
        this.swapStatus = [this.selectedSwapStatus]
      } else {
        this.swapStatus = [ESwapStatus.IN_PROGRESS, ESwapStatus.BOOK_1_CONFIRMED, ESwapStatus.BOOK_2_CONFIRMED];
      }
      this.getSwaps()
    }
  }

  getSwaps(){

  }

}
