import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import Validation from '../../../helpers/validation';
import {STEPPER_GLOBAL_OPTIONS} from '@angular/cdk/stepper';
import {Router} from "@angular/router";
import {TokenStorageService} from "../../../services/token-storage.service";
import Swal from 'sweetalert2';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.sass'],
  providers: [{
    provide: STEPPER_GLOBAL_OPTIONS, useValue: {showError: true}
  }]
})
export class RegisterComponent implements OnInit {

  form!: FormGroup;
  isLoggedIn = false;
  hide = true;

  get formArray(): AbstractControl | null { return this.form.get('formArray'); }

  constructor(private formBuilder: FormBuilder, private authService: AuthService,
              private router: Router, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      formArray: this.formBuilder.array([
        this.formBuilder.group({
          name: ['', Validators.required],
          surname: ['', Validators.required],
          email: ['', [Validators.required, Validators.email]],
          phone: ['', Validators.pattern('(\\+48)?\\s?[0-9]{3}\\s?[0-9]{3}\\s?[0-9]{3}')]
        }),
        this.formBuilder.group({
          postalCode: ['', [Validators.required, Validators.pattern('[0-9]{2}-[0-9]{3}')]],
          post: ['', Validators.required],
          city: ['', Validators.required],
          street: ['', Validators.required],
          buildingNumber: ['', [Validators.required, Validators.pattern('[0-9]+.*')]],
          doorNumber: ['', Validators.pattern('[0-9]+.*')]
        }),
        this.formBuilder.group({
            username: [
              '',
              [
                Validators.required,
                Validators.minLength(6),
                Validators.maxLength(20)
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
      ])
      });
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
      this.router.navigate(['/profile']).then(() => this.reloadPage());
    }
  }

  onSubmit(): void {

    this.authService.register({
      "username": this.formArray!.get([2])!.get('username')?.value,
      "email": this.formArray!.get([0])!.get('email')?.value,
      "password": this.formArray!.get([2])!.get('password')?.value,
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
        this.router.navigate(['/login']).then(() => this.showSuccess());
      },
      err => {
        if(err.error.message.includes("e-mail")){
          this.form.controls['formArray'].get([0])?.get('email')?.setErrors({'incorrect': true})
        } else if(err.error.message.includes("Nazwa")){
          this.form.controls['formArray'].get([2])?.get('username')?.setErrors({'incorrect': true})
        }
        Swal.fire({
          position: 'top-end',
          title: 'Rejestracja nie powiodła się',
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    );
  }

  showSuccess(): void {
    Swal.fire({
      position: 'top-end',
      title: 'Rejestracja przebiegła pomyślnie!',
      text: 'Możesz zalogować się na swoje konto',
      icon: 'success',
      showConfirmButton: false,
      timer: 6000
    })
  }

  reloadPage(): void {
    window.location.reload();
  }

}
