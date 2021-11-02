import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import Validation from '../helpers/validation';
import {STEPPER_GLOBAL_OPTIONS} from '@angular/cdk/stepper';

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

  get formArray(): AbstractControl | null { return this.form.get('formArray'); }

  constructor(private formBuilder: FormBuilder, private authService: AuthService) { }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      formArray: this.formBuilder.array([
        this.formBuilder.group({
          name: ['', Validators.required],
          surname: ['', Validators.required],
          email: ['', [Validators.required, Validators.email]],
          phone: ['', [Validators.required, Validators.pattern('(\\+48)?\\s?[0-9]{3}\\s?[0-9]{3}\\s?[0-9]{3}')]]
        }),
        this.formBuilder.group({
          postalCode: ['', [Validators.required, Validators.pattern('[0-9]{2}-[0-9]{3}')]],
          post: ['', Validators.required],
          city: ['', Validators.required],
          street: ['', Validators.required],
          buildingNumber: ['', Validators.required],
          doorNumber: ['', Validators.required]
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
  }

  // onSubmit(): void {
  //   const { username, email, password } = this.form;
  //
  //   this.authService.register(username, email, password).subscribe(
  //     data => {
  //       console.log(data);
  //       this.isSuccessful = true;
  //       this.isSignUpFailed = false;
  //     },
  //     err => {
  //       this.errorMessage = err.error.message;
  //       this.isSignUpFailed = true;
  //     }
  //   );
  // }

}
