import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {EBookLabel} from "../../../../enums/EBookLabel";
import {OfferInfo} from "../../../../models/book-offers/OfferInfo";
import {EBookStatus} from "../../../../enums/EBookStatus";
import Swal from "sweetalert2";
import {BookOffersService} from "../../../../services/book-offers.service";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-offer-details-dialog',
  templateUrl: './offer-details-dialog.component.html',
  styleUrls: ['./offer-details-dialog.component.sass']
})
export class OfferDetailsDialogComponent implements OnInit {

  form!: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<OfferDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: OfferInfo, private formBuilder: FormBuilder,
    private router: Router, private bookOffersService : BookOffersService
  ) {}

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      requestedBooksCtrl: [[]]
    });
    console.log("aa")
    console.log(this.data.offerDetails.requestedBooks[0])
    if(this.data.offerDetails.hasOfferFromUser){
      this.form.get("requestedBooksCtrl")?.setValue(this.data.offerDetails.requestedBooks[0].id);
      this.form.get("requestedBooksCtrl")?.updateValueAndValidity();
    }
    if(this.getStatus() !== 'Dostępna'){
      Swal.fire({
        position: 'top-end',
        title: 'Książka przed chwilą została wymieniona',
        text: 'Nie jest możliwe złożenie oferty',
        icon: 'info',
        showConfirmButton: false
      })
    }
  }

  onNoClick(): void {
    this.dialogRef.close();
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
    this.bookOffersService.sendSwapRequest({
      requestedBookId: this.data.offerBasics.id,
      userBookIdForSwap: null!
    }).subscribe(
      data => {
        console.log(data);
      },
      err => {
        Swal.fire({
          position: 'top-end',
          title: 'Nie można wysłać propozycji wymiany',
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
      },
      err => {
        Swal.fire({
          position: 'top-end',
          title: 'Nie można wymienić wybranych książek',
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
        },
        err => {
          Swal.fire({
            position: 'top-end',
            title: 'Nie można załadować aktualnych informacji o książce',
            text: err.error.message,
            icon: 'error',
            showConfirmButton: false
          })
        }
      )
  }

}
