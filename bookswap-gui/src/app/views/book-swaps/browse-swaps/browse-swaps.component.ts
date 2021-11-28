import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {BookSwapsService} from "../../../services/book-swaps.service";
import {ESwapStatus} from "../../../enums/ESwapStatus";
import {SwapListItem} from "../../../models/book-swaps/SwapListItem";
import {EBookLabel} from "../../../enums/EBookLabel";

// export interface SwapStatus{
//   status: ESwapStatus,
//   name: string
// }

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
  swaps: SwapListItem[] = [];
  // selectedSwapStatus?: ESwapStatus;
  currentTab = 0;
  swapStatus: ESwapStatus[] = [ESwapStatus.IN_PROGRESS, ESwapStatus.BOOK_1_CONFIRMED, ESwapStatus.BOOK_2_CONFIRMED]

  // statuses: SwapStatus[] = [
  //   {status: ESwapStatus.BOOK_1_CONFIRMED, name: "Wysłane przeze mnie"},
  //   {status: ESwapStatus.BOOK_2_CONFIRMED, name: "Otrzymane"}]

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

  getBookLabel(label: EBookLabel): string{
    let labelS = label.valueOf() as unknown as string;
    if(labelS === EBookLabel[EBookLabel.PERMANENT_SWAP]){
      return 'Stała'
    }
    if(labelS === EBookLabel[EBookLabel.TEMPORARY_SWAP]){
      return 'Tymczasowa'
    }
    return ''
  }

  getSwapStatus(status: ESwapStatus, ifCurrentUserConfirmed: boolean, statusForCurrentUserBook: boolean, label: EBookLabel) {
    let statusS = status.valueOf() as unknown as string;
    if(statusS === ESwapStatus[ESwapStatus.IN_PROGRESS]){
      return 'Oczekuje na odbiór'
    } else if(statusS === ESwapStatus[ESwapStatus.BOOK_1_CONFIRMED] || statusS === ESwapStatus[ESwapStatus.BOOK_2_CONFIRMED]){
      if(statusForCurrentUserBook){
        if(ifCurrentUserConfirmed){
          return 'Odebrana'
        } else {
          return 'Oczekuje na odiór'
        }
      } else {
        if(ifCurrentUserConfirmed){
          return 'Oczekuje na odiór'
        } else {
          return 'Odebrana'
        }
      }
    } else if(statusS === ESwapStatus[ESwapStatus.BOTH_CONFIRMED]){
      return 'Oczekuje na zwrot'
    } else if(statusS === ESwapStatus[ESwapStatus.BOOK_1_RETURNED] || statusS === ESwapStatus[ESwapStatus.BOOK_2_RETURNED]){
      if(statusForCurrentUserBook){
        if(ifCurrentUserConfirmed){
          return 'Zwrócona'
        } else {
          return 'Oczekuje na zwrot'
        }
      } else {
        if(ifCurrentUserConfirmed){
          return 'Oczekuje na zwrot'
        } else {
          return 'Zwrócona'
        }
      }
    } else {
      let labelS = label.valueOf() as unknown as string;
      if(labelS === EBookLabel[EBookLabel.PERMANENT_SWAP]){
        return 'Odebrana'
      }
      if(labelS === EBookLabel[EBookLabel.TEMPORARY_SWAP]){
        return 'Zwrócona'
      }
      return ''
    }
  }

  isSwapNotCompleted(status: ESwapStatus): boolean {
    let statusS = status.valueOf() as unknown as string;
    return statusS === ESwapStatus[ESwapStatus.COMPLETED];
  }

  // changeSelectedSwapStatus(){
  //   if(this.currentTab === 0) {
  //     if (this.selectedSwapStatus) {
  //       this.swapStatus = [this.selectedSwapStatus]
  //     } else {
  //       this.swapStatus = [ESwapStatus.IN_PROGRESS, ESwapStatus.BOOK_1_CONFIRMED, ESwapStatus.BOOK_2_CONFIRMED];
  //     }
  //     this.getSwaps()
  //   }
  // }

  getSwaps(){

  }

}
