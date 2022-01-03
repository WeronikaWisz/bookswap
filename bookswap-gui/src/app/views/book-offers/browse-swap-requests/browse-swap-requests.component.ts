import {Component, OnInit} from '@angular/core';
import {TokenStorageService} from "../../../services/token-storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {BookOffersService} from "../../../services/book-offers.service";
import {ERequestStatus} from "../../../enums/ERequestStatus";
import {EBookLabel} from "../../../enums/EBookLabel";
import {SwapRequestListItem} from "../../../models/book-offers/SwapRequestListItem";
import Swal from "sweetalert2";
import {EBookStatus} from "../../../enums/EBookStatus";
import {TranslateService} from "@ngx-translate/core";
import {RequestStatus} from "../../../models/book-offers/RequestStatus";
import {Label} from "../../../models/book-offers/Label";
import {PageEvent} from "@angular/material/paginator";

@Component({
  selector: 'app-sent-offers',
  templateUrl: './browse-swap-requests.component.html',
  styleUrls: ['./browse-swap-requests.component.sass']
})
export class BrowseSwapRequestsComponent implements OnInit {

  isLoggedIn = false;
  offersCount: number = 0;
  swapRequests: SwapRequestListItem[] = [];
  requestStatus: ERequestStatus[] = [ERequestStatus.WAITING];

  bookLabel?: EBookLabel;
  selectedRequestStatus?: ERequestStatus;

  currentTab = 0;

  isSentOffers = true;
  title = "";

  statuses: RequestStatus[] = []

  labels: Label[] = []

  emptySearchList = false;

  totalRequestsLength = 0;
  pageSize = 10;
  pageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 20];

  constructor(private router: Router, private tokenStorage: TokenStorageService, private translate: TranslateService,
              private bookOffersService : BookOffersService, private route: ActivatedRoute) {
    this.statuses = [
      {
        status: ERequestStatus.ACCEPTED,
        name: this.getTranslateMessage("book-offers.browse-swap-requests.accepted")
      },
      {
        status: ERequestStatus.DENIED,
        name: this.getTranslateMessage("book-offers.browse-swap-requests.denied")
      },
      {
        status: ERequestStatus.CANCELED,
        name: this.getTranslateMessage("book-offers.browse-swap-requests.canceled")
      }];
    this.labels = [
      {
        label: EBookLabel.PERMANENT_SWAP,
        name: this.getTranslateMessage("book-offers.browse-swap-requests.label-permanent")
      },
      {
        label: EBookLabel.TEMPORARY_SWAP,
        name: this.getTranslateMessage("book-offers.browse-swap-requests.label-temporary")
      }];
  }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
    this.checkIfSendOrReceived()
  }

  checkIfSendOrReceived(){
    this.route.params
      .subscribe(
        params => {
          console.log(params);
          this.isSentOffers = params.direction === 'sent';
          if(!this.isSentOffers){
            this.title = this.getTranslateMessage("book-offers.browse-swap-requests.received-title");
          } else {
            this.title = this.getTranslateMessage("book-offers.browse-swap-requests.send-title");
          }
          this.getRequests();
        }
      );
  }

  onTabChange(event: any){
    console.log(event);
    this.currentTab = event.index;
    if(event.index === 0){
      this.requestStatus = [ERequestStatus.WAITING];
    } else {
      if (this.selectedRequestStatus) {
        this.requestStatus = [this.selectedRequestStatus]
      } else {
        this.requestStatus = [ERequestStatus.ACCEPTED, ERequestStatus.DENIED, ERequestStatus.CANCELED];
      }
    }
    this.pageIndex = 0;
    this.getRequests();
  }

  reloadPage(): void {
    window.location.reload();
  }

  getRequests() {
    this.emptySearchList = false;
    this.swapRequests = [];
    this.offersCount = 0;
    this.totalRequestsLength = 0;
    if (this.isSentOffers) {
      this.bookOffersService.getSentRequests({
        requestStatus: this.requestStatus,
        bookLabel: this.bookLabel != undefined ? this.bookLabel : null!
      }, this.pageIndex, this.pageSize).subscribe(data => {
          console.log(data)
          this.swapRequests = data.requestsList
          this.offersCount = data.totalRequestsLength;
          this.totalRequestsLength = data.totalRequestsLength;
          this.checkIfEmptyRequestList(data.requestsList.length);
        },
        err => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("book-offers.browse-swap-requests.load-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      )
    } else {
      this.bookOffersService.getReceivedRequests({
        requestStatus: this.requestStatus,
        bookLabel: this.bookLabel != undefined ? this.bookLabel : null!
      }, this.pageIndex, this.pageSize).subscribe(data => {
          console.log(data)
          this.swapRequests = data.requestsList
          this.offersCount = data.totalRequestsLength;
          this.totalRequestsLength = data.totalRequestsLength;
          this.checkIfEmptyRequestList(data.requestsList.length);
        },
        err => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("book-offers.browse-swap-requests.load-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      )
    }
  }

  checkIfEmptyRequestList(booksLength: number){
    if(booksLength == 0){
      this.emptySearchList = true;
    }
  }

  getBookStatus(status: EBookStatus): string{
    let statusS = status.valueOf() as unknown as string;
    if(statusS === EBookStatus[EBookStatus.AVAILABLE]){
      return this.getTranslateMessage("book-offers.browse-swap-requests.status-available")
    }
    if(statusS === EBookStatus[EBookStatus.PERMANENT_SWAP]){
      return this.getTranslateMessage("book-offers.browse-swap-requests.status-permanent")
    }
    if(statusS === EBookStatus[EBookStatus.TEMPORARY_SWAP]){
      return this.getTranslateMessage("book-offers.browse-swap-requests.status-temporary")
    }
    return ''
  }

  getBookLabel(label: EBookLabel): string{
    let labelS = label.valueOf() as unknown as string;
    if(labelS === EBookLabel[EBookLabel.PERMANENT_SWAP]){
      return this.getTranslateMessage("book-offers.browse-swap-requests.label-permanent-short")
    }
    if(labelS === EBookLabel[EBookLabel.TEMPORARY_SWAP]){
      return this.getTranslateMessage("book-offers.browse-swap-requests.label-temporary-short")
    }
    return ''
  }

  getRequestStatus(status: ERequestStatus): string{
    let statusS = status.valueOf() as unknown as string;
    if(statusS === ERequestStatus[ERequestStatus.WAITING]){
      return this.getTranslateMessage("book-offers.browse-swap-requests.waiting-status")
    }
    if(statusS === ERequestStatus[ERequestStatus.ACCEPTED]){
      return this.getTranslateMessage("book-offers.browse-swap-requests.accepted")
    }
    if(statusS === ERequestStatus[ERequestStatus.CANCELED]){
      return this.getTranslateMessage("book-offers.browse-swap-requests.canceled")
    }
    if(statusS === ERequestStatus[ERequestStatus.DENIED]){
      return this.getTranslateMessage("book-offers.browse-swap-requests.denied")
    }
    return ''
  }

  isRequestWaiting(status: ERequestStatus): boolean{
    let statusS = status.valueOf() as unknown as string;
    return statusS === ERequestStatus[ERequestStatus.WAITING];
  }

  cancelSwapRequest(swapRequestId: number){
    this.bookOffersService.cancelSwapRequest(swapRequestId)
      .subscribe(
        data => {
          console.log(data);
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("book-offers.browse-swap-requests.cancel-success"),
            text: this.getTranslateMessage("book-offers.browse-swap-requests.go-to-history"),
            icon: 'success',
            showConfirmButton: false
          })
          let index = this.swapRequests.findIndex(swapRequest => swapRequest.id === swapRequestId);
          this.swapRequests.splice(index, 1);
          this.offersCount -= 1;
        },
        err => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("book-offers.browse-swap-requests.cancel-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      )
  }

  denySwapRequest(swapRequestId: number){
    this.bookOffersService.denySwapRequest(swapRequestId)
      .subscribe(
        data => {
          console.log(data);
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("book-offers.browse-swap-requests.deny-success"),
            text: this.getTranslateMessage("book-offers.browse-swap-requests.go-to-history"),
            icon: 'success',
            showConfirmButton: false
          })
          let index = this.swapRequests.findIndex(swapRequest => swapRequest.id === swapRequestId);
          this.swapRequests.splice(index, 1);
          this.offersCount -= 1;
        },
        err => {
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("book-offers.browse-swap-requests.deny-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      )
  }

  goToBrowseBooks(username: string, label: EBookLabel){
    let labelS = label.valueOf() as unknown as string;
    if(labelS === EBookLabel[EBookLabel.PERMANENT_SWAP]){
      this.router.navigate(['/browse-offers', username, 0]);
    } else {
      this.router.navigate(['/browse-offers', username, 1]);
    }
  }

  changeSelectedRequestStatus(){
    if(this.currentTab === 1) {
      if (this.selectedRequestStatus) {
        this.requestStatus = [this.selectedRequestStatus]
      } else {
        this.requestStatus = [ERequestStatus.ACCEPTED, ERequestStatus.DENIED, ERequestStatus.CANCELED];
      }
      this.pageIndex = 0;
      this.getRequests()
    }
  }

  changeBookLabel(){
    this.pageIndex = 0;
    this.getRequests()
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
    this.getRequests();
  }

}
