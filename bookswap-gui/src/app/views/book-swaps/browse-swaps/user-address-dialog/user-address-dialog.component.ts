import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import Swal from "sweetalert2";
import {ProfileData} from "../../../../models/manage-users/ProfileData";
import {BookSwapsService} from "../../../../services/book-swaps.service";

@Component({
  selector: 'app-user-address-dialog',
  templateUrl: './user-address-dialog.component.html',
  styleUrls: ['./user-address-dialog.component.sass']
})
export class UserAddressDialogComponent implements OnInit {

  profileData?: ProfileData;

  constructor(
    public dialogRef: MatDialogRef<UserAddressDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: string, private bookSwapService: BookSwapsService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.bookSwapService.getUserAddressByUsername(this.data)
      .subscribe(
      data => {
        console.log(data)
        this.profileData = data;
      }, err => {
        Swal.fire({
          position: 'top-end',
          title: 'Pobranie danych użytkownika nie powiodło się',
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    )
  }

  onNoClick(){
    this.dialogRef.close();
  }

}
