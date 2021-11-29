import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import {BookSwapsService} from "../../../services/book-swaps.service";
import {ESwapStatus} from "../../../enums/ESwapStatus";
import {SwapListItem} from "../../../models/book-swaps/SwapListItem";
import {EBookLabel} from "../../../enums/EBookLabel";
import Swal from "sweetalert2";
import {MatDialog} from "@angular/material/dialog";
import {UserAddressDialogComponent} from "./user-address-dialog/user-address-dialog.component";
import {EBookStatus} from "../../../enums/EBookStatus";

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
  currentTab = 0;
  swapStatus: ESwapStatus[] = [ESwapStatus.IN_PROGRESS, ESwapStatus.BOOK_1_CONFIRMED, ESwapStatus.BOOK_2_CONFIRMED]
  label: EBookLabel = EBookLabel.PERMANENT_SWAP;

  constructor(private router: Router, private tokenStorage: TokenStorageService, public dialog: MatDialog,
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
          this.isPermanentSwaps = params.label === 'permanent';
          if(!this.isPermanentSwaps){
            this.title = "Wymiany tymczasowe"
            this.label = EBookLabel.TEMPORARY_SWAP
          } else {
            this.title = "Wymiany stałe"
            this.label = EBookLabel.PERMANENT_SWAP
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
    } else if(!this.isPermanentSwaps && event.index === 1){
      this.swapStatus = [ESwapStatus.BOTH_CONFIRMED, ESwapStatus.BOOK_1_RETURNED, ESwapStatus.BOOK_2_RETURNED];
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
          return 'Oczekuje na odiór'
        } else {
          return 'Odebrana'
        }
      } else {
        if(ifCurrentUserConfirmed){
          return 'Odebrana'
        } else {
          return 'Oczekuje na odiór'
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

  isSwapCompleted(status: ESwapStatus): boolean {
    let statusS = status.valueOf() as unknown as string;
    return statusS === ESwapStatus[ESwapStatus.COMPLETED];
  }

  getOtherUserAddress(username: string){
    this.dialog.open(UserAddressDialogComponent, {
      maxWidth: '650px',
      data: username
    });
  }

  getSwaps(){
    console.log(this.label)
    this.swaps = [];
    this.swapsCount = 0;
    this.bookSwapsService.getSwaps({
      swapStatus: this.swapStatus,
      bookLabel: this.label
    })
      .subscribe(
        data => {
          console.log(data)
          this.swaps = data
          this.swapsCount = data.length;
        },
        err => {
          Swal.fire({
            position: 'top-end',
            title: 'Nie można załadować wymian',
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      )
  }

  confirmBookDelivery(id: number){
    this.bookSwapsService.confirmBookDelivery(id)
      .subscribe(
        data => {
          let statusS = data.swapStatus.valueOf() as unknown as string;
          let index = this.swaps.findIndex(swap => swap.id === id);
          if(statusS === ESwapStatus[ESwapStatus.BOTH_CONFIRMED] || statusS === ESwapStatus[ESwapStatus.COMPLETED]){
            this.swaps.splice(index, 1);
          } else {
            this.swaps.splice(index, 1, data);
          }
        },
        err => {
          Swal.fire({
            position: 'top-end',
            title: 'Nie można zatwierdzić odbioru',
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      )
  }

}
