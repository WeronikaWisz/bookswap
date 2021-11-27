import {Component, OnInit} from '@angular/core';
import {TokenStorageService} from "../../../services/token-storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {BookOffersService} from "../../../services/book-offers.service";
import {ERequestStatus} from "../../../enums/ERequestStatus";
import {EBookLabel} from "../../../enums/EBookLabel";
import {SwapRequestListItem} from "../../../models/book-offers/SwapRequestListItem";
import Swal from "sweetalert2";
import {EBookStatus} from "../../../enums/EBookStatus";
import {Label} from "../../user-books/add-book/add-book.component";

export interface RequestStatus{
  status: ERequestStatus,
  name: string
}

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
  title = "Wysłane ofery wymiany";

  statuses: RequestStatus[] = [{status: ERequestStatus.ACCEPTED, name: "Zaakceptowana"},
    {status: ERequestStatus.DENIED, name: "Odrzucona"},
    {status: ERequestStatus.CANCELED, name: "Odwołana"}]

  labels: Label[] = [{label: EBookLabel.PERMANENT_SWAP, name: "Wymiana stała"},
    {label: EBookLabel.TEMPORARY_SWAP, name: "Wymiana tymczasowa"}]

  constructor(private router: Router, private tokenStorage: TokenStorageService,
              private bookOffersService : BookOffersService, private route: ActivatedRoute) { }

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
            this.title = "Otrzymane ofery wymiany"
          } else {
            this.title = "Wysłane ofery wymiany"
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
      this.requestStatus = [ERequestStatus.ACCEPTED, ERequestStatus.DENIED, ERequestStatus.CANCELED];
    }
    this.getRequests();
  }

  reloadPage(): void {
    window.location.reload();
  }

  getRequests() {
    this.swapRequests = [];
    this.offersCount = 0;
    if (this.isSentOffers) {
      this.bookOffersService.getSentRequests({
        requestStatus: this.requestStatus,
        bookLabel: this.bookLabel != undefined ? this.bookLabel : null!
      }).subscribe(data => {
          console.log(data)
          this.swapRequests = data
          this.offersCount = data.length;
        },
        err => {
          Swal.fire({
            position: 'top-end',
            title: 'Nie można załadować propozycji',
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
      }).subscribe(data => {
          console.log(data)
          this.swapRequests = data
          this.offersCount = data.length;
        },
        err => {
          Swal.fire({
            position: 'top-end',
            title: 'Nie można załadować propozycji',
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      )
    }
  }

  getBookStatus(status: EBookStatus): string{
    let statusS = status.valueOf() as unknown as string;
    if(statusS === EBookStatus[EBookStatus.AVAILABLE]){
      return 'Dostępna'
    }
    if(statusS === EBookStatus[EBookStatus.PERMANENT_SWAP]){
      return 'Wymieniona'
    }
    if(statusS === EBookStatus[EBookStatus.TEMPORARY_SWAP]){
      return 'Na wymianie tymczasowej'
    }
    return ''
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

  getRequestStatus(status: ERequestStatus): string{
    let statusS = status.valueOf() as unknown as string;
    if(statusS === ERequestStatus[ERequestStatus.WAITING]){
      return 'Oczekująca'
    }
    if(statusS === ERequestStatus[ERequestStatus.ACCEPTED]){
      return 'Zaakceptowana'
    }
    if(statusS === ERequestStatus[ERequestStatus.CANCELED]){
      return 'Odwołana'
    }
    if(statusS === ERequestStatus[ERequestStatus.DENIED]){
      return 'Odrzucona'
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
            title: 'Pomyśnie odwołanno ofertę',
            text: 'Możesz znaleźć ją w ofertach historycznych',
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
            title: 'Nie można odwołać oferty',
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
            title: 'Pomyśnie odrzucono ofertę',
            text: 'Możesz znaleźć ją w ofertach historycznych',
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
            title: 'Nie można odrzucić oferty',
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
      this.getRequests()
    }
  }

  changeBookLabel(){
    this.getRequests()
  }

}
