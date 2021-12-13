import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {EBookLabel} from "../../../../enums/EBookLabel";
import {OfferInfo} from "../../../../models/book-offers/OfferInfo";
import {EBookStatus} from "../../../../enums/EBookStatus";
import Swal from "sweetalert2";
import {BookOffersService} from "../../../../services/book-offers.service";
import {FormBuilder, FormGroup} from "@angular/forms";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-offer-details-dialog',
  templateUrl: './offer-details-dialog.component.html',
  styleUrls: ['./offer-details-dialog.component.sass']
})
export class OfferDetailsDialogComponent implements OnInit {

  form!: FormGroup;
  requestAlreadySend = false;

  constructor(
    public dialogRef: MatDialogRef<OfferDetailsDialogComponent>, private translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) public data: OfferInfo, private formBuilder: FormBuilder,
    private router: Router, private bookOffersService : BookOffersService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      requestedBooksCtrl: [[]]
    });
    this.setBookToSwap();
    this.checkIfAvailable();
  }

  onNoClick(): boolean {
    return (this.getStatus() !== 'Dostępna' || this.requestAlreadySend);
  }

  setBookToSwap(){
    if(this.data.offerDetails.hasOfferFromUser){
      this.form.get("requestedBooksCtrl")?.setValue(this.data.offerDetails.requestedBooks[0].id);
      this.form.get("requestedBooksCtrl")?.updateValueAndValidity();
    }
  }

  getStatus(): string{
    let status = this.data.offerDetails.status.valueOf() as unknown as string;
    if(status === EBookStatus[EBookStatus.AVAILABLE]){
      return 'Dostępna'
    }
    if(status === EBookStatus[EBookStatus.PERMANENT_SWAP]){
      return 'Wymieniona'
    }
    if(status === EBookStatus[EBookStatus.TEMPORARY_SWAP]){
      return 'Na wymianie tymczasowej'
    }
    return ''
  }

  getLabel(): string{
    let label = this.data.offerDetails.label.valueOf() as unknown as string;
    if(label === EBookLabel[EBookLabel.PERMANENT_SWAP]){
      return 'Stała'
    }
    if(label === EBookLabel[EBookLabel.TEMPORARY_SWAP]){
      return 'Tymczasowa'
    }
    return ''
  }

  sendSwapRequest(){
    console.log("sendRequestSwap")
    console.log(this.data.offerBasics.id)
    let message = "";
    this.bookOffersService.sendSwapRequest({
      requestedBookId: this.data.offerBasics.id,
      userBookIdForSwap: null!
    }).subscribe(
      data => {
        console.log(data);
        this.requestAlreadySend = true;
        this.disableSelect();
        this.translate.get("book-offers.browse-offers.request-success").subscribe(data =>
          message = data
        );
        Swal.fire({
          position: 'top-end',
          title: message,
          icon: 'success',
          showConfirmButton: false
        })
      },
      err => {
        this.translate.get("book-offers.browse-offers.request-error").subscribe(data =>
          message = data
        );
        Swal.fire({
          position: 'top-end',
          title: message,
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
        this.reloadBookDetails()
      }
    )
  }

  sendSwap(){
    console.log("sendSwap")
    let message = "";
    this.bookOffersService.sendSwapRequest({
      requestedBookId: this.data.offerBasics.id,
      userBookIdForSwap: this.form.get("requestedBooksCtrl")?.value ? this.form.get("requestedBooksCtrl")?.value : null!
    }).subscribe(
      data => {
        console.log(data);
        this.requestAlreadySend = true;
        this.disableSelect()
        this.translate.get("book-offers.browse-offers.swap-success-title").subscribe(data =>
          message = data
        );
        let message2 = "";
        this.translate.get("book-offers.browse-offers.swap-success-text").subscribe(data =>
          message2 = data
        );
        Swal.fire({
          position: 'top-end',
          title: message,
          text: message2,
          icon: 'success',
          showConfirmButton: false
        })
      },
      err => {
        this.translate.get("book-offers.browse-offers.swap-error").subscribe(data =>
          message = data
        );
        Swal.fire({
          position: 'top-end',
          title: message,
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
        this.reloadBookDetails()
      }
    )
  }

  reloadBookDetails(){
    this.bookOffersService.getOfferDetail(this.data.offerBasics.id)
      .subscribe(
        data => {
          this.data.offerDetails = data
          this.setBookToSwap();
          this.checkIfAvailable();
        },
        err => {
          let message = "";
          this.translate.get("book-offers.browse-offers.load-book-data-error").subscribe(data =>
            message = data
          );
          Swal.fire({
            position: 'top-end',
            title: message,
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      )
  }

  checkIfAvailable(){
    if(this.getStatus() !== 'Dostępna'){
      let messageTitle = "";
      let messageText = "";
      this.translate.get("book-offers.browse-offers.error-swapped-title").subscribe(data =>
        messageTitle = data
      );
      this.translate.get("book-offers.browse-offers.error-swapped-text").subscribe(data =>
        messageText = data
      );
      Swal.fire({
        position: 'top-end',
        title: messageTitle,
        text: messageText,
        icon: 'info',
        showConfirmButton: false
      })
    }
  }

  disableSelect(){
    this.form.get("requestedBooksCtrl")?.disable();
  }

}
