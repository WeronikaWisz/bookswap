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
import {TranslateService} from "@ngx-translate/core";
import {PageEvent} from "@angular/material/paginator";

@Component({
  selector: 'app-browse-swaps',
  templateUrl: './browse-swaps.component.html',
  styleUrls: ['./browse-swaps.component.sass']
})
export class BrowseSwapsComponent implements OnInit {

  isLoggedIn = false;
  isPermanentSwaps = true;
  title = '';
  swapsCount: number = 0;
  swaps: SwapListItem[] = [];
  currentTab = 0;
  swapStatus: ESwapStatus[] = [ESwapStatus.IN_PROGRESS, ESwapStatus.BOOK_1_CONFIRMED, ESwapStatus.BOOK_2_CONFIRMED]
  label: EBookLabel = EBookLabel.PERMANENT_SWAP;

  emptySearchList = false;

  totalSwapsLength = 0;
  pageSize = 10;
  pageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 20];

  constructor(private router: Router, private tokenStorage: TokenStorageService, public dialog: MatDialog,
              private bookSwapsService : BookSwapsService, private route: ActivatedRoute,
              private translate: TranslateService) { }

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
            this.title = this.getTranslateMessage("book-swaps.browse-swaps.temporary-title")
            this.label = EBookLabel.TEMPORARY_SWAP
          } else {
            this.title = this.getTranslateMessage("book-swaps.browse-swaps.permanent-title")
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
    this.pageIndex = 0;
    this.getSwaps();
  }

  getBookLabel(label: EBookLabel): string{
    let labelS = label.valueOf() as unknown as string;
    if(labelS === EBookLabel[EBookLabel.PERMANENT_SWAP]){
      return this.getTranslateMessage("book-swaps.browse-swaps.label-permanent-short")
    }
    if(labelS === EBookLabel[EBookLabel.TEMPORARY_SWAP]){
      return this.getTranslateMessage("book-swaps.browse-swaps.label-permanent-short")
    }
    return ''
  }

  getSwapStatus(status: ESwapStatus, ifCurrentUserConfirmed: boolean, statusForCurrentUserBook: boolean, label: EBookLabel) {
    let statusS = status.valueOf() as unknown as string;
    if(statusS === ESwapStatus[ESwapStatus.IN_PROGRESS]){
      return this.getTranslateMessage("book-swaps.browse-swaps.waiting-receive")
    } else if(statusS === ESwapStatus[ESwapStatus.BOOK_1_CONFIRMED] || statusS === ESwapStatus[ESwapStatus.BOOK_2_CONFIRMED]){
      if(statusForCurrentUserBook){
        if(ifCurrentUserConfirmed){
          return this.getTranslateMessage("book-swaps.browse-swaps.waiting-receive")
        } else {
          return this.getTranslateMessage("book-swaps.browse-swaps.received")
        }
      } else {
        if(ifCurrentUserConfirmed){
          return this.getTranslateMessage("book-swaps.browse-swaps.received")
        } else {
          return this.getTranslateMessage("book-swaps.browse-swaps.waiting-receive")
        }
      }
    } else if(statusS === ESwapStatus[ESwapStatus.BOTH_CONFIRMED]){
      return this.getTranslateMessage("book-swaps.browse-swaps.waiting-return")
    } else if(statusS === ESwapStatus[ESwapStatus.BOOK_1_RETURNED] || statusS === ESwapStatus[ESwapStatus.BOOK_2_RETURNED]){
      if(statusForCurrentUserBook){
        if(ifCurrentUserConfirmed){
          return this.getTranslateMessage("book-swaps.browse-swaps.returned")
        } else {
          return this.getTranslateMessage("book-swaps.browse-swaps.waiting-return")
        }
      } else {
        if(ifCurrentUserConfirmed){
          return this.getTranslateMessage("book-swaps.browse-swaps.waiting-return")
        } else {
          return this.getTranslateMessage("book-swaps.browse-swaps.returned")
        }
      }
    } else {
      let labelS = label.valueOf() as unknown as string;
      if(labelS === EBookLabel[EBookLabel.PERMANENT_SWAP]){
        return this.getTranslateMessage("book-swaps.browse-swaps.received")
      }
      if(labelS === EBookLabel[EBookLabel.TEMPORARY_SWAP]){
        return this.getTranslateMessage("book-swaps.browse-swaps.returned")
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
    this.emptySearchList = false;
    console.log(this.label)
    this.swaps = [];
    this.swapsCount = 0;
    this.totalSwapsLength = 0;
    this.bookSwapsService.getSwaps({
      swapStatus: this.swapStatus,
      bookLabel: this.label
    }, this.pageIndex, this.pageSize)
      .subscribe(
        data => {
          console.log(data)
          this.swaps = data.swapsList
          this.swapsCount = data.totalSwapsLength;
          this.totalSwapsLength = data.totalSwapsLength;
          if(data.swapsList.length == 0){
            this.emptySearchList = true;
          }
        },
        err => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("book-swaps.browse-swaps.load-swap-error"),
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
            this.swapsCount -= 1;
          } else {
            this.swaps.splice(index, 1, data);
          }
        },
        err => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("book-swaps.browse-swaps.confirm-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      )
  }

  getTranslateMessage(key: string): string{
    let message = "";
    this.translate.get(key).subscribe(data =>
      message = data
    );
    return message;
  }

  pageChanged(event: PageEvent) {
    console.log({ event });
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.getSwaps();
  }

}
