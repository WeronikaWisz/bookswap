import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {UpdateUserData} from "../../../../models/manage-users/UpdateUserData";
import {AbstractControl, FormBuilder, FormGroup, Validators} from "@angular/forms";
import Swal from "sweetalert2";
import {UsersService} from "../../../../services/manage-users/users.service";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-update-profile-dialog',
  templateUrl: './update-profile-dialog.component.html',
  styleUrls: ['./update-profile-dialog.component.sass']
})
export class UpdateProfileDialogComponent implements OnInit {

  form!: FormGroup;
  dataChanged = false;

  get formArray(): AbstractControl | null { return this.form.get('formArray'); }


  constructor(
    public dialogRef: MatDialogRef<UpdateProfileDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UpdateUserData, private formBuilder: FormBuilder,
    private usersService: UsersService, private translate: TranslateService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      formArray: this.formBuilder.array([
        this.formBuilder.group({
          name: [this.data.name, Validators.required],
          surname: [this.data.surname, Validators.required],
          email: [this.data.email, [Validators.required, Validators.email]],
          phone: [this.data.phone ? this.data.phone : '', Validators.pattern('(\\+48)?\\s?[0-9]{3}\\s?[0-9]{3}\\s?[0-9]{3}')]
        }),
        this.formBuilder.group({
          postalCode: [this.data.postalCode, [Validators.required, Validators.pattern('[0-9]{2}-[0-9]{3}')]],
          post: [this.data.post, Validators.required],
          city: [this.data.city, Validators.required],
          street: [this.data.street, Validators.required],
          buildingNumber: [this.data.buildingNumber, [Validators.required, Validators.pattern('[0-9]+.*')]],
          doorNumber: [this.data.doorNumber ? this.data.doorNumber : '', Validators.pattern('[0-9]+.*')]
        })
      ])
    });
  }

  saveData(){
    this.usersService.updateUserProfileData({
      "email": this.formArray!.get([0])!.get('email')?.value,
      "name": this.formArray!.get([0])!.get('name')?.value,
      "surname": this.formArray!.get([0])!.get('surname')?.value,
      "phone": this.formArray!.get([0])!.get('phone')?.value,
      "postalCode": this.formArray!.get([1])!.get('postalCode')?.value,
      "post": this.formArray!.get([1])!.get('post')?.value,
      "city": this.formArray!.get([1])!.get('city')?.value,
      "street": this.formArray!.get([1])!.get('street')?.value,
      "buildingNumber": this.formArray!.get([1])!.get('buildingNumber')?.value,
      "doorNumber": this.formArray!.get([1])!.get('doorNumber')?.value
    }).subscribe(
      data => {
        console.log(data);
        this.dataChanged = true
        this.form.markAsPristine();
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("manage-users.profile.update-success"),
          icon: 'success',
          showConfirmButton: false
        })
      },
      err => {
        Swal.fire({
          position: 'top-end',
          title: this.getTranslateMessage("manage-users.profile.update-error"),
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    );
  }

  getTranslateMessage(key: string): string{
    let message = "";
    this.translate.get(key).subscribe(data =>
      message = data
    );
    return message;
  }

}
