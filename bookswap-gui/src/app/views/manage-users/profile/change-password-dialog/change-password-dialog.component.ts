import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MatDialogRef} from "@angular/material/dialog";
import {UsersService} from "../../../../services/manage-users/users.service";
import Validation from "../../../../helpers/validation";
import Swal from "sweetalert2";

@Component({
  selector: 'app-change-password-dialog',
  templateUrl: './change-password-dialog.component.html',
  styleUrls: ['./change-password-dialog.component.sass']
})
export class ChangePasswordDialogComponent implements OnInit {

  form!: FormGroup;
  hide = true;

  constructor(
    public dialogRef: MatDialogRef<ChangePasswordDialogComponent>,
    private formBuilder: FormBuilder,
    private usersService: UsersService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
        oldPassword: [
          '',
          [
            Validators.required
          ]
        ],
        password: [
          '',
          [
            Validators.required,
            Validators.minLength(12),
            Validators.maxLength(40)
          ]
        ],
        confirmPassword: ['', Validators.required]
      },
      {
        validators: [Validation.match('password', 'confirmPassword')]
      }
    )
  }

  onNoClick() {
    this.dialogRef.close();
  }

  changePassword(){
    this.usersService.changePassword({
      "oldPassword": this.form.get('oldPassword')?.value,
      "newPassword": this.form.get('password')?.value,
    }).subscribe(
      data => {
        console.log(data);
        Swal.fire({
          position: 'top-end',
          title: 'Pomyślnie zmieniono hasło',
          icon: 'success',
          showConfirmButton: false
        })
        this.form.reset();
        this.form.markAsUntouched();
      },
      err => {
        Swal.fire({
          position: 'top-end',
          title: 'Nie udało się zmienić hasła',
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    );
  }

}
