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
    return (this.getStatusValue() !== 0 || this.requestAlreadySend);
  }

  setBookToSwap(){
    if(this.data.offerDetails.hasOfferFromUser){
      this.form.get("requestedBooksCtrl")?.setValue(this.data.offerDetails.requestedBooks[0].id);
      this.form.get("requestedBooksCtrl")?.updateValueAndValidity();
    }
  }

  getStatusName(value: number): string{
    if(value === 0){
      return this.getTranslateMessage("book-offers.browse-offers.status-available")
    }
    if(value === 1){
      return this.getTranslateMessage("book-offers.browse-offers.status-permanent")
    }
    if(value === 2){
      return this.getTranslateMessage("book-offers.browse-offers.status-temporary")
    }
    return ''
  }

  getStatusValue(): number{
    let status = this.data.offerDetails.status.valueOf() as unknown as string;
    if(status === EBookStatus[EBookStatus.AVAILABLE]){
      return 0
    }
    if(status === EBookStatus[EBookStatus.PERMANENT_SWAP]){
      return 1
    }
    if(status === EBookStatus[EBookStatus.TEMPORARY_SWAP]){
      return 2
    }
    return 0
  }

  getLabel(): string{
    let label = this.data.offerDetails.label.valueOf() as unknown as string;
    if(label === EBookLabel[EBookLabel.PERMANENT_SWAP]){
      return this.getTranslateMessage("book-offers.browse-offers.label-permanent-short")
    }
    if(label === EBookLabel[EBookLabel.TEMPORARY_SWAP]){
      return this.getTranslateMessage("book-offers.browse-offers.label-temporary-short")
    }
    return ''
  }

  sendSwapRequest(){
    console.log("sendRequestSwap")
    console.log(this.data.offerBasics.id)
    this.bookOffersService.sendSwapRequest({
      requestedBookId: this.data.offerBasics.id,
      userBookIdForSwap: null!
    }).subscribe(
      data => {
        console.log(data);
        this.requestAlreadySend = true;
        this.disableSelect();
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("book-offers.browse-offers.request-success"),
          icon: 'success',
          showConfirmButton: false
        })
      },
      err => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("book-offers.browse-offers.request-error"),
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
    this.bookOffersService.sendSwapRequest({
      requestedBookId: this.data.offerBasics.id,
      userBookIdForSwap: this.form.get("requestedBooksCtrl")?.value ? this.form.get("requestedBooksCtrl")?.value : null!
    }).subscribe(
      data => {
        console.log(data);
        this.requestAlreadySend = true;
        this.disableSelect()
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("book-offers.browse-offers.swap-success-title"),
          text: this.getTranslateMessage("book-offers.browse-offers.swap-success-text"),
          icon: 'success',
          showConfirmButton: false
        })
      },
      err => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("book-offers.browse-offers.swap-error"),
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
          Swal.fire({
            position: 'top-end',
            title: this.getTranslateMessage("book-offers.browse-offers.load-book-data-error"),
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      )
  }

  checkIfAvailable(){
    if(this.getStatusValue() !== 0){
      Swal.fire({
        position: 'top-end',
        title: this.getTranslateMessage("book-offers.browse-offers.error-swapped-title"),
        text: this.getTranslateMessage("book-offers.browse-offers.error-swapped-text"),
        icon: 'info',
        showConfirmButton: false
      })
    }
  }

  disableSelect(){
    this.form.get("requestedBooksCtrl")?.disable();
  }

  getTranslateMessage(key: string): string{
    let message = "";
    this.translate.get(key).subscribe(data =>
      message = data
    );
    return message;
  }

}
