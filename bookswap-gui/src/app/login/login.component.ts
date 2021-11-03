import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { TokenStorageService } from '../services/token-storage.service';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import Swal from "sweetalert2";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.sass']
})
export class LoginComponent implements OnInit {

  form!: FormGroup;
  isLoggedIn = false;
  roles: string[] = [];
  hide = true;

  constructor(private formBuilder: FormBuilder,
              private route: ActivatedRoute,
              private router: Router,
              private authService: AuthService, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
      this.roles = this.tokenStorage.getUser().roles;
      this.router.navigate(['/profile']).then(() => this.reloadPage());
    }
  }

  onSubmit(): void {

    this.authService.login({
      "username": this.form.get('username')?.value,
      "password": this.form.get('password')?.value
    }).subscribe(
      data => {
        this.tokenStorage.saveToken(data.accessToken);
        this.tokenStorage.saveUser(data);

        this.isLoggedIn = true;
        this.roles = this.tokenStorage.getUser().roles;
        this.router.navigate(['/my-books']).then(() => this.reloadPage());
      },
      err => {
        this.form.controls['username'].setErrors({'incorrect': true});
        this.form.controls['password'].setErrors({'incorrect': true});
        Swal.fire({
          position: 'top-end',
          title: 'Logowanie nie powiodło się',
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    );
  }

  reloadPage(): void {
    window.location.reload();
  }

}
